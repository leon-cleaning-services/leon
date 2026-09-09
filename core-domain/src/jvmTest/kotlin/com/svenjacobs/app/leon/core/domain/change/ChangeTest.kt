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
package com.svenjacobs.app.leon.core.domain.change

import com.svenjacobs.app.leon.core.domain.change.Change.Action.RemoveParameter
import com.svenjacobs.app.leon.core.domain.change.Change.Action.Replace
import com.svenjacobs.app.leon.core.domain.change.Change.Action.SetComponent
import com.svenjacobs.app.leon.core.domain.change.Change.Component
import com.svenjacobs.app.leon.core.domain.url.Url
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class ChangeTest :
    WordSpec({
        fun url(value: String) = requireNotNull(Url.parse(value))

        "apply" should
            {
                "return the same instance for no actions" {
                    val url = url("https://example.com/path?a=1")

                    url.apply(emptyList()) shouldBe url
                }

                "remove a parameter" {
                    url("https://example.com/?a=1&b=2")
                        .apply(listOf(RemoveParameter(Url.Parameter("a", "1"))))
                        .toString() shouldBe "https://example.com/?b=2"
                }

                "remove only the first of two equal parameters" {
                    url("https://example.com/?a=1&a=1")
                        .apply(listOf(RemoveParameter(Url.Parameter("a", "1"))))
                        .toString() shouldBe "https://example.com/?a=1"
                }

                "ignore removal of a parameter which is not present" {
                    url("https://example.com/?a=1")
                        .apply(listOf(RemoveParameter(Url.Parameter("b", "2"))))
                        .toString() shouldBe "https://example.com/?a=1"
                }

                "drop the query section once the last parameter is removed" {
                    url("https://example.com/path?a=1#anchor")
                        .apply(listOf(RemoveParameter(Url.Parameter("a", "1"))))
                        .toString() shouldBe "https://example.com/path#anchor"
                }

                "set the host" {
                    url("https://music.youtube.com/watch?v=1")
                        .apply(
                            listOf(
                                SetComponent(
                                    Component.HOST,
                                    from = "music.youtube.com",
                                    to = "youtube.com",
                                )
                            )
                        )
                        .toString() shouldBe "https://youtube.com/watch?v=1"
                }

                "set the path" {
                    url("https://example.com/a/b/c")
                        .apply(listOf(SetComponent(Component.PATH, from = "/a/b/c", to = "/c")))
                        .toString() shouldBe "https://example.com/c"
                }

                "remove the fragment" {
                    url("https://example.com/article#Echobox=123")
                        .apply(
                            listOf(
                                SetComponent(Component.FRAGMENT, from = "Echobox=123", to = null)
                            )
                        )
                        .toString() shouldBe "https://example.com/article"
                }

                "replace the whole URL" {
                    url("https://www.google.com/url?q=x")
                        .apply(
                            listOf(
                                Replace(
                                    from = url("https://www.google.com/url?q=x"),
                                    to = url("https://example.com/article"),
                                )
                            )
                        )
                        .toString() shouldBe "https://example.com/article"
                }

                "apply actions in order" {
                    url("https://example.com/a/b?x=1&y=2")
                        .apply(
                            listOf(
                                RemoveParameter(Url.Parameter("x", "1")),
                                SetComponent(Component.PATH, from = "/a/b", to = "/b"),
                                RemoveParameter(Url.Parameter("y", "2")),
                            )
                        )
                        .toString() shouldBe "https://example.com/b"
                }
            }
    })
