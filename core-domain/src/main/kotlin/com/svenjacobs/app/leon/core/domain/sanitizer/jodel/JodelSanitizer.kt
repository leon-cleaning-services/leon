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
package com.svenjacobs.app.leon.core.domain.sanitizer.jodel

import android.content.Context
import com.svenjacobs.app.leon.core.common.url.decodeUrl
import com.svenjacobs.app.leon.core.domain.R
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.serialization.json.Json

class JodelSanitizer : Sanitizer {
    override val id = SanitizerId("jodel")

    override fun getMetadata(context: Context) =
        Sanitizer.Metadata(name = context.getString(R.string.sanitizer_jodel_name))

    @OptIn(ExperimentalEncodingApi::class)
    override fun invoke(input: String): String {
        val encoded = URL_REGEX.find(input)?.groupValues?.getOrNull(1) ?: return input
        val base64Data = decodeUrl(encoded)
        val jsonString = Base64.Default.decode(base64Data)
        val jsonMap = Json.decodeFromString<Map<String, String>>(jsonString.decodeToString())

        return jsonMap["\$android_url"] ?: input
    }

    override fun matchesDomain(input: String) = URL_REGEX.containsMatchIn(input)

    private companion object {
        private val URL_REGEX = Regex("^https?://shared\\.jodel\\.com/a/key_live_.*&data=([^?&]*)")
    }
}
