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

package com.svenjacobs.app.leon.core.domain.sanitizer.youtube

import android.content.Context
import com.svenjacobs.app.leon.core.domain.R
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class YoutubeShortUrlSanitizer : Sanitizer {

    override val id = SanitizerId("youtube_short_url")

    override fun getMetadata(context: Context) = Sanitizer.Metadata(
        name = context.getString(R.string.sanitizer_youtube_short_url_name),
    )

    override fun matchesDomain(input: String) = DOMAIN_REGEX.containsMatchIn(input)

    override fun invoke(input: String): String {
        val videoId = VIDEO_ID_REGEX.matchEntire(input)?.groupValues?.getOrNull(1)
            ?: throw IllegalArgumentException("Could not extract video ID from youtu.be URL")
        return "https://www.youtube.com/watch?v=$videoId"
    }

    private companion object {
        private val DOMAIN_REGEX = Regex("youtu\\.be")
        private val VIDEO_ID_REGEX = Regex("(?:https?://)?youtu\\.be/(.+)\$")
    }
}
