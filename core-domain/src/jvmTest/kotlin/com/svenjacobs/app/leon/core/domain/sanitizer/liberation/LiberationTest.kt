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
package com.svenjacobs.app.leon.core.domain.sanitizer.liberation

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.Liberation
import io.kotest.matchers.shouldBe

class LiberationTest :
    SanitizerSpec(
        Liberation,
        {
            "clean" should
                {
                    "remove all parameters" {
                        clean(
                            "https://www.liberation.fr/lifestyle/consommation/on-a-teste-le-premier-velo-" +
                                "a-assistance-electrique-sans-batterie-un-entre-deux-pour-pedaler-leger-2025" +
                                "0905_5LMA42SQEBGURDQHUGEODXOP3E/?at_email_type=retention&actId=~a6Zgr-mXM-" +
                                "avkpSr6lbShiBgjb7Fy-bR-_bBxnYV8qMmlRT6xrLcvgEtun2B7RhPP7cWY116zwEgcHUYAUbk2" +
                                "W-gJLfIFrMQ5Hr2EHeR4QacHi97DydZQcA%3D%3D&actCampaignType=CAMPAIGN_MAIL&actS" +
                                "ource=559069"
                        ) shouldBe
                            "https://www.liberation.fr/lifestyle/consommation/on-a-teste-le-premier-velo-a" +
                                "-assistance-electrique-sans-batterie-un-entre-deux-pour-pedaler-leger-20250" +
                                "905_5LMA42SQEBGURDQHUGEODXOP3E/"
                    }
                }

            "matches" should
                {
                    "match liberation.fr" { matches("https://liberation.fr") shouldBe true }

                    "match www.liberation.fr" { matches("https://www.liberation.fr") shouldBe true }

                    "not match a lookalike host" {
                        matches("https://liberation.fr.evil.com") shouldBe false
                    }
                }
        },
    )
