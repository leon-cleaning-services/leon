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

package com.svenjacobs.app.leon.service

import com.svenjacobs.app.leon.domain.Defaults.Spotify
import com.svenjacobs.app.leon.test.MOCK_SANITIZER_STRATEGY_EXECUTOR
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.be
import io.kotest.matchers.should

class SpotifySanitizerTest : ShouldSpec({

    context("sanitize") {

        should("remove \"si\" parameter") {
            val result = MOCK_SANITIZER_STRATEGY_EXECUTOR.sanitize(
                Spotify,
                "https://open.spotify.com/album/5N2BIKomahKMAAirp8tiBN?si=BICcHVzTTqmqt82Y6f2e_A&utm_source=native-share-menu"
            )

            result.artifactsRemoved should be(1)
            result.output should be("https://open.spotify.com/album/5N2BIKomahKMAAirp8tiBN&utm_source=native-share-menu")
        }
    }
})
