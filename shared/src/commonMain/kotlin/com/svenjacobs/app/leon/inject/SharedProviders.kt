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
package com.svenjacobs.app.leon.inject

import com.svenjacobs.app.leon.core.domain.Cleaner
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerRepository
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizersCollection
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.AllSanitizers
import com.svenjacobs.app.leon.db.AppDatabase
import com.svenjacobs.app.leon.db.HistoryDao
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

/** Providers that are the same on every platform, contributed to each platform's graph. */
@ContributesTo(AppScope::class)
interface SharedProviders {

    @Provides fun provideSanitizers(): SanitizersCollection = AllSanitizers

    @Provides
    fun provideCleaner(sanitizers: SanitizersCollection, repository: SanitizerRepository): Cleaner =
        Cleaner(sanitizers, repository)

    @Provides fun provideHistoryDao(db: AppDatabase): HistoryDao = db.historyDao()
}
