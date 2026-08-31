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
package com.svenjacobs.app.leon.core.domain.sanitizer.yandex

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.Yandex
import io.kotest.matchers.shouldBe

class YandexTest :
    SanitizerSpec(
        Yandex,
        {
            "clean" should
                {
                    "clean yandex.com URLs" {
                        clean(
                            "https://yandex.com/search/?text=test&lr=103769&search_source=yacom_desktop_common"
                        ) shouldBe "https://yandex.com/search/?text=test"
                    }

                    "clean ya.ru URLs" {
                        clean(
                            "https://ya.ru/search/?text=test&lr=103769&search_source=yacom_desktop_common"
                        ) shouldBe "https://ya.ru/search/?text=test"
                    }
                }

            "matches" should
                {
                    "match yandex.com" { matches("https://yandex.com") shouldBe true }

                    "match ya.ru" { matches("https://ya.ru") shouldBe true }
                }
        },
    )
