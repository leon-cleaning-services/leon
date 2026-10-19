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
package com.svenjacobs.app.leon.core.domain.sanitizer.tiktok

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.TiktokShortLink
import io.kotest.matchers.shouldBe

class TiktokShortLinkTest :
    SanitizerSpec(
        TiktokShortLink,
        {
            "clean" should
                {
                    "replace a resolved short link with its target" {
                        clean(
                            "https://www.tiktok.com/t/ZT8abc123/",
                            resolved =
                                mapOf(
                                    "https://www.tiktok.com/t/ZT8abc123/" to
                                        "https://www.tiktok.com/@user/video/7300000000000000000",
                                ),
                        ) shouldBe "https://www.tiktok.com/@user/video/7300000000000000000"
                    }

                    "replace a resolved vm.tiktok.com short link with its target" {
                        clean(
                            "https://vm.tiktok.com/ZMabc123/",
                            resolved =
                                mapOf(
                                    "https://vm.tiktok.com/ZMabc123/" to
                                        "https://www.tiktok.com/@user/video/7300000000000000000",
                                ),
                        ) shouldBe "https://www.tiktok.com/@user/video/7300000000000000000"
                    }

                    "keep a short link which was not resolved" {
                        clean("https://www.tiktok.com/t/ZT8abc123/?_t=8abc") shouldBe
                            "https://www.tiktok.com/t/ZT8abc123/?_t=8abc"
                    }
                }

            "isShortLink" should
                {
                    "recognize tiktok.com/t/" {
                        isShortLink("https://www.tiktok.com/t/ZT8abc123/") shouldBe true
                        isShortLink("https://tiktok.com/t/ZT8abc123") shouldBe true
                    }

                    "recognize tt.site/t/" {
                        isShortLink("https://tt.site/t/AbCd12345") shouldBe true
                    }

                    "recognize vm. and vt.tiktok.com" {
                        isShortLink("https://vm.tiktok.com/ZMabc123/") shouldBe true
                        isShortLink("https://vt.tiktok.com/ZSabc123/") shouldBe true
                    }

                    "recognize a code with a hyphen or an underscore" {
                        isShortLink("https://vm.tiktok.com/ZS923LxwaARJm-5DUYW/") shouldBe true
                        isShortLink("https://www.tiktok.com/t/ZT8a_b-c12") shouldBe true
                    }

                    "recognize the older codes" {
                        isShortLink("https://www.tiktok.com/t/ZTRmqkW4N") shouldBe true
                        isShortLink("https://vm.tiktok.com/TTPdhG8EbL/") shouldBe true
                    }

                    "not consider a video a short link" {
                        isShortLink(
                            "https://www.tiktok.com/@user/video/7300000000000000000",
                        ) shouldBe false
                    }

                    "not consider other TikTok pages short links" {
                        isShortLink("https://www.tiktok.com/explore") shouldBe false
                        isShortLink("https://www.tiktok.com/t/") shouldBe false
                        isShortLink("https://vm.tiktok.com/") shouldBe false
                    }
                }

            "matches" should
                {
                    "match the short link hosts" {
                        matches("https://www.tiktok.com/t/ZT8abc123/") shouldBe true
                        matches("https://tt.site/t/AbCd12345") shouldBe true
                        matches("https://vm.tiktok.com/ZMabc123/") shouldBe true
                        matches("https://vt.tiktok.com/ZSabc123/") shouldBe true
                    }

                    "not match a lookalike host" {
                        matches("https://tiktok.com.evil.com/t/ZT8abc123") shouldBe false
                        matches("https://vm.tiktok.com.evil.com/ZMabc123") shouldBe false
                    }

                    "not match tiktok.com outside of /t/" {
                        matches("https://www.tiktok.com/@user/video/7300000000000000000") shouldBe
                            false
                    }
                }
        },
    )
