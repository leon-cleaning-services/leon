/*
 * Léon - The URL Cleaner
 * Copyright (C) 2025 Sven Jacobs
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

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class SubstackSanitizerTest :
    WordSpec({
        val sanitizer = SubstackSanitizer()

        "invoke" should
            {
                "remove parameters from substack.com" {
                    sanitizer(
                        "https://substack.com/@sebastianbarros/note/c-190523061?r=c0obe&utm_source=notes-share-action&utm_medium=web"
                    ) shouldBe "https://substack.com/@sebastianbarros/note/c-190523061"
                }

                "remove parameters from publication subdomains" {
                    sanitizer(
                        "https://fosspost.substack.com/p/open-up-your-android-smartphone?r=c0obe&utm_campaign=email-post&utm_medium=email&triedRedirect=true"
                    ) shouldBe "https://fosspost.substack.com/p/open-up-your-android-smartphone"
                }

                "keep the fragment which references a section or comment" {
                    sanitizer(
                        "https://fosspost.substack.com/p/open-up-your-android-smartphone?r=c0obe#comment-12345"
                    ) shouldBe
                        "https://fosspost.substack.com/p/open-up-your-android-smartphone#comment-12345"
                }

                "rewrite open.substack.com to the publication of the article" {
                    sanitizer(
                        "https://open.substack.com/pub/fosspost/p/open-up-your-android-smartphone?utm_campaign=post-expanded-share&utm_medium=web"
                    ) shouldBe "https://fosspost.substack.com/p/open-up-your-android-smartphone"
                }

                "rewrite open.substack.com of a newsletter mail" {
                    sanitizer(
                        "https://open.substack.com/pub/presspublish/p/practical-customising-your-substack-3f7?r=20ql4m&showWelcomeOnShare=true&triedRedirect=true"
                    ) shouldBe
                        "https://presspublish.substack.com/p/practical-customising-your-substack-3f7"
                }

                "rewrite open.substack.com without an article" {
                    sanitizer("https://open.substack.com/pub/fosspost") shouldBe
                        "https://fosspost.substack.com"
                }

                "keep the article reference of app-link URLs of a newsletter mail" {
                    sanitizer(
                        "https://substack.com/app-link/post?publication_id=162759&post_id=178435284&utm_campaign=email-post-title&isFreemail=true&r=c0obe&token=eyJ1c2VyX2lkIjoxfQ&utm_medium=email&triedRedirect=true"
                    ) shouldBe
                        "https://substack.com/app-link/post?publication_id=162759&post_id=178435284"
                }

                "keep the article reference of app-link URLs shared from the app" {
                    sanitizer(
                        "https://substack.com/app-link/post?publication_id=89120&post_id=209203336&action=share&triggerShare=true&isFreemail=true&r=1a2b3c&token=eyJzdWIiOiJwb3N0LXJlYWN0aW9uIn0.c2lnbmF0dXJl"
                    ) shouldBe
                        "https://substack.com/app-link/post?publication_id=89120&post_id=209203336"
                }

                "keep the note reference of app-link URLs" {
                    sanitizer(
                        "https://substack.com/app-link/note?note_id=c-190523061&r=c0obe&token=eyJhIjoxfQ"
                    ) shouldBe "https://substack.com/app-link/note?note_id=c-190523061"
                }

                "not leave an empty query when an app-link URL has no reference" {
                    sanitizer(
                        "https://substack.com/app-link/post?token=eyJ1c2VyX2lkIjoxfQ&r=c0obe"
                    ) shouldBe "https://substack.com/app-link/post"
                }

                "leave an already clean URL untouched" {
                    sanitizer(
                        "https://fosspost.substack.com/p/open-up-your-android-smartphone"
                    ) shouldBe "https://fosspost.substack.com/p/open-up-your-android-smartphone"
                }

                "be idempotent" {
                    val once =
                        sanitizer(
                            "https://open.substack.com/pub/fosspost/p/open-up-your-android-smartphone?r=c0obe"
                        )
                    sanitizer(once) shouldBe once
                }
            }

        "matchesDomain" should
            {
                "match substack.com" {
                    sanitizer.matchesDomain("https://substack.com") shouldBe true
                }

                "match open.substack.com" {
                    sanitizer.matchesDomain("https://open.substack.com") shouldBe true
                }

                "match publication subdomains" {
                    sanitizer.matchesDomain("https://fosspost.substack.com/p/article") shouldBe true
                }

                "not match domains which merely end with substack.com" {
                    sanitizer.matchesDomain("https://notsubstack.com/p/article") shouldBe false
                }

                "not match domains which merely start with substack.com" {
                    sanitizer.matchesDomain("https://substack.com.example.com/p/article") shouldBe
                        false
                }
            }
    })
