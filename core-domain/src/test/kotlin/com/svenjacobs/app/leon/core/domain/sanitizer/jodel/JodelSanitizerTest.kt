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
package com.svenjacobs.app.leon.core.domain.sanitizer.jodel

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class JodelSanitizerTest :
    WordSpec({
        "invoke" should
            {
                "extract Jodel sharing URL" {
                    val sanitizer = JodelSanitizer()
                    val result =
                        sanitizer(
                            "http://shared.jodel.com/a/key_live_abZZZZgPxyz82xxxxAAAAdefghi4YYY1" +
                                "?%24identity_id=123456789012345678&feature=shared_post&campaign=image" +
                                "_DE_FrontPage&type=0&duration=0&source=android&data=eyIkY2Fub25pY2FsX" +
                                "2lkZW50aWZpZXIiOiJzYWpcL2JhZGMwZmZlZWJhZGMwZmZlZTAxMjM0NSIsIiRwdWJsaW" +
                                "NseV9pbmRleGFibGUiOiJ0cnVlIiwicG9zdElkIjoiYmFkYzBmZmVlYmFkYzBmZmVlMDE" +
                                "yMzQ1IiwiJGRlc2t0b3BfdXJsIjoiaHR0cHM6XC9cL3NoYXJlLmpvZGVsLmNvbVwvcG9z" +
                                "dD9wb3N0SWQ9YmFkYzBmZmVlYmFkYzBmZmVlMDEyMzQ1IiwicmVmZXJyZXJfaWQiOiJhY" +
                                "mNkZWYwMTIzNDU2Nzg5ZGVhZGMwZGUiLCIkYW5kcm9pZF91cmwiOiJodHRwczpcL1wvc2" +
                                "hhcmUuam9kZWwuY29tXC9wb3N0P3Bvc3RJZD1iYWRjMGZmZWViYWRjMGZmZWUwMTIzNDU" +
                                "iLCIkaW9zX3VybCI6Imh0dHBzOlwvXC9zaGFyZS5qb2RlbC5jb21cL3Bvc3Q%2FcG9zdE" +
                                "lkPWJhZGMwZmZlZWJhZGMwZmZlZTAxMjM0NSJ9?channel=copy"
                        )

                    result shouldBe "https://share.jodel.com/post?postId=badc0ffeebadc0ffee012345"
                }
            }
    })
