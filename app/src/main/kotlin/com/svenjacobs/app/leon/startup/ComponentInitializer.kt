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

package com.svenjacobs.app.leon.startup

import android.content.Context
import com.svenjacobs.app.leon.core.domain.inject.AppComponent
import com.svenjacobs.app.leon.feature.sanitizer.amazon.AmazonProductSanitizerRegistration
import com.svenjacobs.app.leon.feature.sanitizer.amazon.AmazonSanitizerRegistration
import com.svenjacobs.app.leon.feature.sanitizer.amazon.smile.AmazonSmileSanitizerRegistration
import com.svenjacobs.app.leon.feature.sanitizer.aol.search.AolSearchSanitizerRegistration
import com.svenjacobs.app.leon.feature.sanitizer.emptyparameters.EmptyParametersSanitizerRegistration
import com.svenjacobs.app.leon.feature.sanitizer.facebook.FacebookSanitizerRegistration
import com.svenjacobs.app.leon.feature.sanitizer.flipkart.FlipkartSanitizerRegistration
import com.svenjacobs.app.leon.feature.sanitizer.ga.GoogleAnalyticsSanitizerRegistration
import com.svenjacobs.app.leon.feature.sanitizer.google.search.GoogleSearchSanitizerRegistration
import com.svenjacobs.app.leon.feature.sanitizer.instagram.InstagramSanitizerRegistration
import com.svenjacobs.app.leon.feature.sanitizer.netflix.NetflixSanitizerRegistration
import com.svenjacobs.app.leon.feature.sanitizer.sessionids.SessionIdsSanitizerRegistration
import com.svenjacobs.app.leon.feature.sanitizer.spotify.SpotifySanitizerRegistration
import com.svenjacobs.app.leon.feature.sanitizer.twitter.TwitterSanitizerRegistration
import com.svenjacobs.app.leon.feature.sanitizer.webtrekk.WebtrekkSanitizerRegistration
import com.svenjacobs.app.leon.feature.sanitizer.yahoo.search.YahooSearchSanitizerRegistration
import com.svenjacobs.app.leon.feature.sanitizer.youtube.YoutubeRedirectSanitizerRegistration
import com.svenjacobs.app.leon.feature.sanitizer.youtube.YoutubeShortUrlSanitizerRegistration
import com.svenjacobs.app.leon.sanitizer.SanitizerRepositoryImpl
import kotlinx.collections.immutable.persistentListOf

class ComponentInitializer : DistinctInitializer<Unit> {

	override fun create(context: Context) {
		AppComponent.setup(
			appContext = context,
			sanitizerRepositoryProvider = { SanitizerRepositoryImpl() },
			sanitizerRegistrations = persistentListOf(
				AmazonProductSanitizerRegistration(),
				AmazonSanitizerRegistration(),
				AmazonSmileSanitizerRegistration(),
				AolSearchSanitizerRegistration(),
				EmptyParametersSanitizerRegistration(),
				FacebookSanitizerRegistration(),
				FlipkartSanitizerRegistration(),
				GoogleAnalyticsSanitizerRegistration(),
				GoogleSearchSanitizerRegistration(),
				InstagramSanitizerRegistration(),
				NetflixSanitizerRegistration(),
				SessionIdsSanitizerRegistration(),
				SpotifySanitizerRegistration(),
				TwitterSanitizerRegistration(),
				WebtrekkSanitizerRegistration(),
				YahooSearchSanitizerRegistration(),
				YoutubeRedirectSanitizerRegistration(),
				YoutubeShortUrlSanitizerRegistration(),
			),
		)
	}
}
