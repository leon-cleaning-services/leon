/*
 * Léon - The URL Cleaner
 * Copyright (C) 2022 Sven Jacobs
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

import com.svenjacobs.app.leon.core.domain.sanitizer.Registrations
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerRegistration
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerRegistrations
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerRepository
import com.svenjacobs.app.leon.sanitizer.SanitizerRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.collections.immutable.toImmutableSet

@Module
@InstallIn(SingletonComponent::class)
object SanitizersModuleProviders {

	@Provides
	@Singleton
	@Registrations
	fun provideRegistrations(
		registrations: Set<@JvmSuppressWildcards SanitizerRegistration>,
	): SanitizerRegistrations = registrations.toImmutableSet()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class SanitizersModuleBindings {

	@Binds
	abstract fun bindSanitizerRepository(
		sanitizerRepositoryImpl: SanitizerRepositoryImpl,
	): SanitizerRepository
}
