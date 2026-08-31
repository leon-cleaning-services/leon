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
package com.svenjacobs.app.leon.core.domain.sanitizer.meta

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.MetaAd
import io.kotest.matchers.shouldBe

class MetaAdSanitizerTest :
    SanitizerSpec(
        MetaAd,
        {
            "clean" should
                {
                    "remove ad_id, adset_id, campaign_id, gc_id, h_ad_id and placement parameters" {
                        clean(
                            "https://www.example.com/path?ad_id=1&adset_id=2&campaign_id=3&gc_id=4&h_ad_id=5&placement=6&keep=123"
                        ) shouldBe "https://www.example.com/path?keep=123"
                    }
                }
        },
    )
