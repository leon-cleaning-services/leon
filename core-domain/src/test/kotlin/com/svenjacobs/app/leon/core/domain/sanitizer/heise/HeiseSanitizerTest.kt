/*
 * Léon - The URL Cleaner
 * Copyright (C) 2023 Sven Jacobs
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
package com.svenjacobs.app.leon.core.domain.sanitizer.heise

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.Heise
import io.kotest.matchers.shouldBe

class HeiseSanitizerTest :
    SanitizerSpec(
        Heise,
        {
            "clean" should
                {
                    "clean heise.de URLs" {
                        clean(
                            "https://www.heise.de/news/Boom-bei-Balkonkraftwerken-Bereits-mehr-als-" +
                                "300-000-in-Betrieb-9324094.html?wt_mc=rss.red.ho.ho.rdf.beitrag.beitrag"
                        ) shouldBe
                            "https://www.heise.de/news/Boom-bei-Balkonkraftwerken-Bereits-mehr-als" +
                                "-300-000-in-Betrieb-9324094.html"
                    }
                }

            "matches" should { "match heise.de" { matches("https://heise.de") shouldBe true } }
        },
    )
