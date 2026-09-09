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
package com.svenjacobs.app.leon.core.domain.sanitizer.substack

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.Substack
import io.kotest.matchers.shouldBe

class SubstackTest :
    SanitizerSpec(
        Substack,
        {
            "clean" should
                {
                    "remove parameters from substack.com" {
                        clean(
                            "https://substack.com/@sebastianbarros/note/c-190523061?r=c0obe&utm_source=notes-share-action&utm_medium=web"
                        ) shouldBe "https://substack.com/@sebastianbarros/note/c-190523061"
                    }

                    "remove parameters from publication subdomains" {
                        clean(
                            "https://fosspost.substack.com/p/open-up-your-android-smartphone?r=c0obe&utm_campaign=email-post&utm_medium=email&triedRedirect=true"
                        ) shouldBe "https://fosspost.substack.com/p/open-up-your-android-smartphone"
                    }

                    "keep the fragment which references a section or comment" {
                        clean(
                            "https://fosspost.substack.com/p/open-up-your-android-smartphone?r=c0obe#comment-12345"
                        ) shouldBe
                            "https://fosspost.substack.com/p/open-up-your-android-smartphone#comment-12345"
                    }

                    "rewrite open.substack.com to the publication of the article" {
                        clean(
                            "https://open.substack.com/pub/fosspost/p/open-up-your-android-smartphone?utm_campaign=post-expanded-share&utm_medium=web"
                        ) shouldBe "https://fosspost.substack.com/p/open-up-your-android-smartphone"
                    }

                    "rewrite open.substack.com of a newsletter mail" {
                        clean(
                            "https://open.substack.com/pub/presspublish/p/practical-customising-your-substack-3f7?r=20ql4m&showWelcomeOnShare=true&triedRedirect=true"
                        ) shouldBe
                            "https://presspublish.substack.com/p/practical-customising-your-substack-3f7"
                    }

                    "rewrite open.substack.com without an article" {
                        clean("https://open.substack.com/pub/fosspost") shouldBe
                            "https://fosspost.substack.com"
                    }

                    "keep the article reference of app-link URLs of a newsletter mail" {
                        clean(
                            "https://substack.com/app-link/post?publication_id=162759&post_id=178435284&utm_campaign=email-post-title&isFreemail=true&r=c0obe&token=eyJ1c2VyX2lkIjoxfQ&utm_medium=email&triedRedirect=true"
                        ) shouldBe
                            "https://substack.com/app-link/post?publication_id=162759&post_id=178435284"
                    }

                    "keep the article reference of app-link URLs shared from the app" {
                        clean(
                            "https://substack.com/app-link/post?publication_id=89120&post_id=209203336&action=share&triggerShare=true&isFreemail=true&r=1a2b3c&token=eyJzdWIiOiJwb3N0LXJlYWN0aW9uIn0.c2lnbmF0dXJl"
                        ) shouldBe
                            "https://substack.com/app-link/post?publication_id=89120&post_id=209203336"
                    }

                    "keep the note reference of app-link URLs" {
                        clean(
                            "https://substack.com/app-link/note?note_id=c-190523061&r=c0obe&token=eyJhIjoxfQ"
                        ) shouldBe "https://substack.com/app-link/note?note_id=c-190523061"
                    }

                    "not leave an empty query when an app-link URL has no reference" {
                        clean(
                            "https://substack.com/app-link/post?token=eyJ1c2VyX2lkIjoxfQ&r=c0obe"
                        ) shouldBe "https://substack.com/app-link/post"
                    }

                    "leave redirect links untouched" {
                        clean("https://substack.com/redirect/2f9a1c/?j=eyJ1IjoiYWJjIn0") shouldBe
                            "https://substack.com/redirect/2f9a1c/?j=eyJ1IjoiYWJjIn0"
                    }

                    "keep a port while removing parameters" {
                        clean("https://substack.com:443/p/some-article?r=c0obe") shouldBe
                            "https://substack.com:443/p/some-article"
                    }

                    "leave an already clean URL untouched" {
                        clean(
                            "https://fosspost.substack.com/p/open-up-your-android-smartphone"
                        ) shouldBe "https://fosspost.substack.com/p/open-up-your-android-smartphone"
                    }

                    "be idempotent" {
                        val once =
                            clean(
                                "https://open.substack.com/pub/fosspost/p/open-up-your-android-smartphone?r=c0obe"
                            )
                        clean(once) shouldBe once
                    }
                }

            "matches" should
                {
                    "match substack.com" { matches("https://substack.com") shouldBe true }

                    "match open.substack.com" { matches("https://open.substack.com") shouldBe true }

                    "match publication subdomains" {
                        matches("https://fosspost.substack.com/p/article") shouldBe true
                    }

                    "match hosts with a port" {
                        matches("https://substack.com:443/p/article") shouldBe true
                    }

                    "not match domains which merely end with substack.com" {
                        matches("https://notsubstack.com/p/article") shouldBe false
                    }

                    "not match domains which merely start with substack.com" {
                        matches("https://substack.com.example.com/p/article") shouldBe false
                    }
                }
        },
    )
