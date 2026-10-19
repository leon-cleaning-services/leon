/*
 * Léon - The URL Cleaner
 * Copyright (C) 2026 Sven Jacobs
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.svenjacobs.app.leon.ui.shortlink.model

import com.svenjacobs.app.leon.core.domain.shortlink.Resolution
import com.svenjacobs.app.leon.core.domain.shortlink.ShortLinkResolver
import com.svenjacobs.app.leon.core.domain.url.Url
import com.svenjacobs.app.leon.network.resolveAll
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.selects.select

/** Where resolving the short links of one text stands, as far as the user is concerned. */
sealed interface ShortLinkState {

    /**
     * Nothing to show: the text has no short links, or they are dealt with — resolved, or left as
     * they are because the user cancelled or chose to continue without resolving.
     */
    data object Idle : ShortLinkState

    /** [urls] are being resolved right now. */
    data class Resolving(val urls: ImmutableList<Url>) : ShortLinkState {

        /** The hosts being contacted, each once. */
        val hosts: ImmutableList<String>
            get() = urls.map { it.host }.distinct().toImmutableList()
    }

    /** These short links could not be resolved, for the given reasons. The user decides next. */
    data class Failed(val failures: ImmutableMap<Url, Resolution.Reason>) : ShortLinkState

    /** The text contains short links, but resolving short links is turned off. */
    data object Disabled : ShortLinkState
}

/**
 * Whether the cleaned text shown along with this state is the one the user ends up with: nothing is
 * being resolved and no failure awaits a decision. Only then is it shared, opened, copied or
 * recorded.
 *
 * [ShortLinkState.Disabled] counts as settled: the user turned resolving off, so the short link
 * cleaned as it is *is* the result. The hint merely offers to turn resolving on.
 */
val ShortLinkState.isSettled: Boolean
    get() = this is ShortLinkState.Idle || this is ShortLinkState.Disabled

/** What the user can do about short links while they are being resolved, or have failed. */
enum class ShortLinkCommand {
    /** Stop resolving and clean the short links that are not resolved yet as they are. */
    Cancel,

    /** Resolve the short links which failed once more. */
    Retry,

    /** Give up on the short links which failed and clean them as they are. */
    Continue,
}

/**
 * The [state] of resolving one text's short links, and the [resolved] targets known so far, which
 * `Cleaner.clean` takes as its `resolved`.
 */
data class ShortLinkProgress(
    val state: ShortLinkState = ShortLinkState.Idle,
    val resolved: Map<Url, Url> = emptyMap(),
)

/**
 * Resolves the short links [urls], asking the user through [commands] what to do when that fails,
 * and reports every step to [onProgress] — which is always called from the calling coroutine, so it
 * may emit to a flow.
 *
 * A failure does not end resolving: the successes are kept, and the user either retries the
 * failures alone or continues without them. Cancelling keeps what was resolved before. Either way
 * this ends with [ShortLinkState.Idle] and returns every target that is known.
 *
 * [resume] picks up where an earlier, interrupted call left off for the same [urls]: it keeps the
 * targets already known, waits for the user again on a failure, and resolves nothing at all if
 * resolving was already over. Nothing which was resolved before is requested twice.
 *
 * A command is only taken while it means something — [ShortLinkCommand.Cancel] while resolving, the
 * others while a failure is shown. Anything left over in [commands] from before is discarded
 * whenever a new step is reported, so a late tap never decides about something the user has not
 * seen yet.
 */
suspend fun ShortLinkResolver.resolveInteractively(
    urls: List<Url>,
    commands: ReceiveChannel<ShortLinkCommand>,
    resume: ShortLinkProgress? = null,
    onProgress: suspend (ShortLinkProgress) -> Unit,
): Map<Url, Url> {
    val resolved = resume?.resolved.orEmpty().toMutableMap()

    if (resume?.state == ShortLinkState.Idle) {
        onProgress(resume)
        return resolved
    }

    var failures = (resume?.state as? ShortLinkState.Failed)?.failures.orEmpty()
    var pending = urls.filterNot { it in resolved }

    while (true) {
        if (failures.isEmpty()) {
            if (pending.isEmpty()) break

            commands.discardPending()
            onProgress(
                ShortLinkProgress(
                    ShortLinkState.Resolving(pending.toImmutableList()),
                    resolved.toMap(),
                ),
            )

            val results = resolveUnlessCancelled(pending, commands) ?: break
            results.forEach { (url, resolution) ->
                if (resolution is Resolution.Resolved) resolved[url] = resolution.to
            }
            failures =
                results
                    .mapNotNull { (url, resolution) ->
                        (resolution as? Resolution.Failed)?.let { url to it.reason }
                    }
                    .toMap()
            if (failures.isEmpty()) break
        }

        commands.discardPending()
        onProgress(
            ShortLinkProgress(ShortLinkState.Failed(failures.toImmutableMap()), resolved.toMap()),
        )

        when (commands.receiveAnyOf(ShortLinkCommand.Retry, ShortLinkCommand.Continue)) {
            ShortLinkCommand.Retry -> {
                pending = failures.keys.toList()
                failures = emptyMap()
            }
            else -> break
        }
    }

    val result = resolved.toMap()
    onProgress(ShortLinkProgress(ShortLinkState.Idle, result))
    return result
}

/** Resolves [urls], or returns `null` as soon as the user cancels. */
private suspend fun ShortLinkResolver.resolveUnlessCancelled(
    urls: List<Url>,
    commands: ReceiveChannel<ShortLinkCommand>,
): Map<Url, Resolution>? = coroutineScope {
    val resolving = async { resolveAll(urls) }
    val cancelled = async { commands.receiveAnyOf(ShortLinkCommand.Cancel) }

    select {
        resolving.onAwait { it }
        cancelled.onAwait { null }
    }
        .also { coroutineContext.cancelChildren() }
}

private suspend fun ReceiveChannel<ShortLinkCommand>.receiveAnyOf(
    vararg accepted: ShortLinkCommand,
): ShortLinkCommand {
    while (true) {
        val command = receive()
        if (command in accepted) return command
    }
}

private fun ReceiveChannel<ShortLinkCommand>.discardPending() {
    while (tryReceive().isSuccess) Unit
}
