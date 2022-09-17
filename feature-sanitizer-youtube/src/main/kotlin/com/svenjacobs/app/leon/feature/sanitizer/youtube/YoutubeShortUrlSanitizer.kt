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

package com.svenjacobs.app.leon.feature.sanitizer.youtube

import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import javax.inject.Inject

class YoutubeShortUrlSanitizer @Inject constructor() : Sanitizer {

	override fun invoke(input: String): String {
		val videoId = VIDEO_ID_REGEX.matchEntire(input)?.groupValues?.getOrNull(1)
			?: throw IllegalArgumentException("Could not extract video ID from youtu.be URL")
		return "https://www.youtube.com/watch?v=$videoId"
	}

	private companion object {
		val VIDEO_ID_REGEX = Regex("(?:https?://)?youtu\\.be/(.+)\$")
	}
}
