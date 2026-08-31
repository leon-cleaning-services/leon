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
package com.svenjacobs.app.leon.core.domain.sanitizer

import com.svenjacobs.app.leon.core.domain.change.apply
import com.svenjacobs.app.leon.core.domain.sanitize
import com.svenjacobs.app.leon.core.domain.url.Url
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentListOf

class RuleTest :
    WordSpec({
        /** Applies [rule] to [url] and returns the resulting URL, for readable assertions. */
        fun Rule.clean(url: String): String {
            val parsed = requireNotNull(Url.parse(url))
            return parsed.apply(sanitize(parsed)).toString()
        }

        "RemoveParameters" should
            {
                "remove the parameters matched by the name" {
                    Rule.RemoveParameters("utm_.*")
                        .clean("https://example.com/?utm_source=x&utm_medium=y&page=2") shouldBe
                        "https://example.com/?page=2"
                }

                "require the name to match completely" {
                    Rule.RemoveParameters("ref")
                        .clean("https://example.com/?ref=1&referrer=2") shouldBe
                        "https://example.com/?referrer=2"
                }

                "remove every parameter for a catch-all name" {
                    Rule.RemoveParameters(".*").clean("https://example.com/path?a=1&b=2") shouldBe
                        "https://example.com/path"
                }

                "keep the fragment when removing every parameter" {
                    Rule.RemoveParameters(".*")
                        .clean("https://example.com/path?a=1#anchor") shouldBe
                        "https://example.com/path#anchor"
                }

                "remove everything but the named parameters when negated" {
                    Rule.RemoveParameters("(id|story_fbid)", negate = true)
                        .clean("https://example.com/?id=1&utm_source=x&story_fbid=2") shouldBe
                        "https://example.com/?id=1&story_fbid=2"
                }

                "do nothing when there are no parameters" {
                    Rule.RemoveParameters(".*")
                        .sanitize(url("https://example.com/path"))
                        .shouldBeEmpty()
                }
            }

        "RemoveEmptyParameters" should
            {
                "remove parameters with an empty value" {
                    Rule.RemoveEmptyParameters.clean("https://example.com/?a=&b=2&c=") shouldBe
                        "https://example.com/?b=2"
                }

                "keep a parameter without a value" {
                    Rule.RemoveEmptyParameters.clean("https://example.com/?flag") shouldBe
                        "https://example.com/?flag"
                }
            }

        "RemoveFragment" should
            {
                "remove any fragment when no pattern is given" {
                    Rule.RemoveFragment().clean("https://example.com/a#anything") shouldBe
                        "https://example.com/a"
                }

                "remove only a matching fragment" {
                    val rule = Rule.RemoveFragment("Echobox=.*")

                    rule.clean("https://example.com/a#Echobox=123") shouldBe "https://example.com/a"
                    rule.clean("https://example.com/a#section") shouldBe
                        "https://example.com/a#section"
                }

                "do nothing when there is no fragment" {
                    Rule.RemoveFragment().sanitize(url("https://example.com/a")).shouldBeEmpty()
                }
            }

        "RewriteHost" should
            {
                "rewrite a matching host" {
                    Rule.RewriteHost("music\\.youtube\\.com", "youtube.com")
                        .clean("https://music.youtube.com/watch?v=1") shouldBe
                        "https://youtube.com/watch?v=1"
                }

                "replace the host exactly once for a catch-all pattern" {
                    Rule.RewriteHost(".*", "www.google.com")
                        .clean("https://maps.google.com/maps") shouldBe
                        "https://www.google.com/maps"
                }

                "substitute groups" {
                    Rule.RewriteHost("open\\.(.+)", "$1")
                        .clean("https://open.example.com/p") shouldBe "https://example.com/p"
                }

                "do nothing when the pattern does not cover the complete host" {
                    Rule.RewriteHost("youtube\\.com", "example.com")
                        .sanitize(url("https://music.youtube.com/"))
                        .shouldBeEmpty()
                }
            }

        "RewritePath" should
            {
                "reduce a path to a captured group" {
                    Rule.RewritePath("/.+(/p/[0-9A-Z]+).*", "$1")
                        .clean("https://newegg.com/some-product-name/p/N82E168") shouldBe
                        "https://newegg.com/p/N82E168"
                }

                "strip a suffix" {
                    Rule.RewritePath("(.*?)/Ipd:.*", "$1")
                        .clean("https://example.com/video/Ipd:tracking") shouldBe
                        "https://example.com/video"
                }

                "do nothing when the path does not match" {
                    Rule.RewritePath("/.+(/p/[0-9A-Z]+).*", "$1")
                        .sanitize(url("https://newegg.com/cart"))
                        .shouldBeEmpty()
                }
            }

        "Follow" should
            {
                "replace the URL with the percent-decoded parameter" {
                    Rule.Follow(Source.Parameter("q"), persistentListOf(Decode.PercentDecode))
                        .clean(
                            "https://www.google.com/url?q=https%3A%2F%2Fexample.com%2Fa&usg=x"
                        ) shouldBe "https://example.com/a"
                }

                "accept any of several parameter names" {
                    val rule =
                        Rule.Follow(
                            Source.Parameter("url|q"),
                            persistentListOf(Decode.PercentDecode),
                        )

                    rule.clean("https://g.com/url?url=https%3A%2F%2Fexample.com") shouldBe
                        "https://example.com"
                    rule.clean("https://g.com/url?q=https%3A%2F%2Fexample.com") shouldBe
                        "https://example.com"
                }

                "do nothing when the source is missing" {
                    Rule.Follow(Source.Parameter("q"), persistentListOf(Decode.PercentDecode))
                        .sanitize(url("https://www.google.com/url?usg=x"))
                        .shouldBeEmpty()
                }

                "do nothing when the source does not contain a URL" {
                    Rule.Follow(Source.Parameter("q"), persistentListOf(Decode.PercentDecode))
                        .sanitize(url("https://www.google.com/url?q=hello"))
                        .shouldBeEmpty()
                }

                "capture the target out of the path" {
                    Rule.Follow(
                            Source.Path,
                            persistentListOf(Decode.Capture("RU=([^/]+)"), Decode.PercentDecode),
                        )
                        .clean(
                            "https://search.aol.com/click/_ylt=abc/RV=2/RU=https%3a%2f%2fexample" +
                                ".com%2fa/RK=0"
                        ) shouldBe "https://example.com/a"
                }

                "do nothing when a capture finds nothing" {
                    Rule.Follow(Source.Path, persistentListOf(Decode.Capture("RU=([^/]+)")))
                        .sanitize(url("https://search.aol.com/click/no-match-here"))
                        .shouldBeEmpty()
                }

                "build the target from a capture" {
                    Rule.Follow(
                            Source.Path,
                            persistentListOf(
                                Decode.Capture("/(.+)", "https://www.youtube.com/watch?v=$1")
                            ),
                        )
                        .clean("https://youtu.be/5HaUOgW5BlA") shouldBe
                        "https://www.youtube.com/watch?v=5HaUOgW5BlA"
                }

                "decode a base64 encoded JSON payload" {
                    // {"$android_url":"https://example.com/post?id=1"}
                    val payload = "eyIkYW5kcm9pZF91cmwiOiJodHRwczovL2V4YW1wbGUuY29tL3Bvc3Q/aWQ9MSJ9"

                    Rule.Follow(
                            Source.Parameter("data"),
                            persistentListOf(Decode.Base64Decode, Decode.JsonField("\$android_url")),
                        )
                        .clean("https://shared.example.com/a?data=$payload") shouldBe
                        "https://example.com/post?id=1"
                }

                "do nothing when the payload is not base64" {
                    Rule.Follow(Source.Parameter("data"), persistentListOf(Decode.Base64Decode))
                        .sanitize(url("https://example.com/a?data=not!base64"))
                        .shouldBeEmpty()
                }

                "do nothing when the JSON field is absent" {
                    // {"other":"x"}
                    Rule.Follow(
                            Source.Parameter("data"),
                            persistentListOf(Decode.Base64Decode, Decode.JsonField("\$android_url")),
                        )
                        .sanitize(url("https://example.com/a?data=eyJvdGhlciI6IngifQ=="))
                        .shouldBeEmpty()
                }

                "drop the parameters of the target when asked to" {
                    val target = "https%3A%2F%2Fexample.com%2Fa%3Fref%3Dnewsletter"

                    Rule.Follow(
                            Source.Parameter("u"),
                            persistentListOf(Decode.PercentDecode),
                            dropParameters = true,
                        )
                        .clean("https://click.example.com/x?u=$target") shouldBe
                        "https://example.com/a"
                }

                "keep the parameters of the target by default" {
                    val target = "https%3A%2F%2Fexample.com%2Fa%3Fref%3Dnewsletter"

                    Rule.Follow(Source.Parameter("u"), persistentListOf(Decode.PercentDecode))
                        .clean("https://click.example.com/x?u=$target") shouldBe
                        "https://example.com/a?ref=newsletter"
                }
            }

        "RewriteHost from another part of the URL" should
            {
                "take the new host out of the path" {
                    Rule.RewriteHost("/pub/([^/]+)(?:/.*)?", "$1.substack.com", from = Source.Path)
                        .clean("https://open.substack.com/pub/fosspost/p/article") shouldBe
                        "https://fosspost.substack.com/pub/fosspost/p/article"
                }

                "do nothing when the source does not match completely" {
                    Rule.RewriteHost("/pub/([^/]+)", "$1.substack.com", from = Source.Path)
                        .sanitize(url("https://open.substack.com/p/article"))
                        .shouldBeEmpty()
                }

                "do nothing when the host already holds the result" {
                    Rule.RewriteHost("/pub/([^/]+)(?:/.*)?", "$1.substack.com", from = Source.Path)
                        .sanitize(url("https://fosspost.substack.com/pub/fosspost/p/a"))
                        .shouldBeEmpty()
                }
            }
    })

private fun url(value: String) = requireNotNull(Url.parse(value))
