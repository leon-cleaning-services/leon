/*
 * Léon - The URL Cleaner
 * Copyright (C) 2021 Sven Jacobs
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

import com.svenjacobs.app.leon.startup.StethoHelper
import com.svenjacobs.app.leon.startup.StethoHelperImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.MainScope

object ApplicationModule {

    @Module
    @InstallIn(SingletonComponent::class)
    object Providers {

        @Provides
        @ApplicationCoroutineScope
        fun provideApplicationCoroutineScope() = MainScope()
    }

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class Bindings {

        @Binds
        abstract fun bindStethoHelper(stethoHelperImpl: StethoHelperImpl): StethoHelper
    }
}
