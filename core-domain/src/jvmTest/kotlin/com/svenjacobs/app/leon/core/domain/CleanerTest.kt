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
package com.svenjacobs.app.leon.core.domain

import com.svenjacobs.app.leon.core.domain.change.Change
import com.svenjacobs.app.leon.core.domain.sanitizer.HostMatch
import com.svenjacobs.app.leon.core.domain.sanitizer.Match
import com.svenjacobs.app.leon.core.domain.sanitizer.Rule
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerRepository
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.AllSanitizers
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.GoogleSearch
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.Tiktok
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.TiktokShortLink
import com.svenjacobs.app.leon.core.domain.url.Url
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf

class CleanerTest :
    WordSpec(
        {
            val repository = mockk<SanitizerRepository>()

            /** A sanitizer which removes exactly one parameter, from every URL. */
            fun fake(name: String, parameter: String) =
                Sanitizer(
                    id = SanitizerId(name),
                    name = name,
                    rules = persistentListOf(Rule.RemoveParameters(parameter)),
                )

            val service =
                Cleaner(
                    sanitizers =
                        persistentListOf(
                            fake("fake1", "paramA"),
                            fake("fake2", "paramB"),
                            fake("fake3", "paramC"),
                        ),
                    repository = repository,
                )

            beforeEach {
                clearMocks(repository)

                coEvery { repository.isEnabled(SanitizerId(any())) } returns true
            }

            "clean" should
                {
                    "remove tracking parameters" {
                        val result = service.clean("https://www.some.site/?paramA=A&paramB=B")

                        result.cleanedText shouldBe "https://www.some.site/"
                        result.urls shouldHaveSize 1
                        result.urls.first().cleaned.toString() shouldBe "https://www.some.site/"
                    }

                    "keep non-tracking parameters" {
                        val result =
                            service.clean("https://www.some.site/?paramA=A&paramB=B&page=1&q=query")

                        result.cleanedText shouldBe "https://www.some.site/?page=1&q=query"
                        result.urls shouldHaveSize 1
                        result.urls.first().cleaned.toString() shouldBe
                            "https://www.some.site/?page=1&q=query"
                    }

                    "keep anchor tags after cleaned parameters" {
                        val result =
                            service.clean("https://www.some.site/?paramA=A&paramB=B#anchor")

                        result.cleanedText shouldBe "https://www.some.site/#anchor"
                        result.urls.first().cleaned.toString() shouldBe
                            "https://www.some.site/#anchor"
                    }

                    "clean multiple URLs in text" {
                        val result =
                            service.clean(
                                "Hey, I would like to share this " +
                                    "https://www.some.site/?paramA=A&paramB=B link as well as this " +
                                    "https://www.some2.site?paramC=C link :)",
                            )

                        result.cleanedText shouldBe
                            "Hey, I would like to share this " +
                                "https://www.some.site/ link as well as this https://www.some2.site link :)"
                        result.urls shouldHaveSize 2
                        result.urls[0].cleaned.toString() shouldBe "https://www.some.site/"
                        result.urls[1].cleaned.toString() shouldBe "https://www.some2.site"
                    }

                    "URL decode when enabled" {
                        val result =
                            service.clean(
                                text = "https://www.some.site/Hello%2FWorld?paramA=A&paramB=B",
                                decodeUrl = true,
                            )

                        result.cleanedText shouldBe "https://www.some.site/Hello%2FWorld"
                    }

                    "URL decode keeps %20 intact but decodes UTF-8 text" {
                        val result =
                            service.clean(
                                text = "https://www.some.site/?q=Hello%20M%C3%BCnchen&paramA=A",
                                decodeUrl = true,
                            )

                        result.cleanedText shouldBe "https://www.some.site/?q=Hello%20München"
                    }

                    "URL decode reveals an encoded URL inside a parameter value" {
                        val result =
                            service.clean(
                                text =
                                    "https://www.some.site/gp/r.html?" +
                                        "U=https%3A%2F%2Fwww.other.site%2Fdp%2FB01%3Fref%3Dx",
                                decodeUrl = true,
                            )

                        result.cleanedText shouldBe
                            "https://www.some.site/gp/r.html?U=https://www.other.site/dp/B01?ref=x"
                    }

                    "URL decode keeps an encoded parameter separator inside a value" {
                        val result =
                            service.clean(
                                text = "https://www.some.site/?q=a%26b%3Dc&paramA=A",
                                decodeUrl = true,
                            )

                        result.cleanedText shouldBe "https://www.some.site/?q=a%26b=c"
                    }

                    "repeat cleaning until iteration doesn't yield new value" {
                        val googleService =
                            Cleaner(
                                sanitizers = persistentListOf(GoogleSearch),
                                repository = repository,
                            )

                        googleService
                            .clean(
                                text =
                                    "https://www.google.com/url?q=https://www.google.com/url?rct%3Dj%26sa%" +
                                        "3Dt%26url%3Dhttps://www.inferse.com/268929/prime-day-2022-phone-deals-al" +
                                        "l-the-best-amazon-prime-early-access-phone-deals-bgr/%26ct%3Dga%26cd%3DC" +
                                        "AEYACoUMTA3NzY4NTE0MDEwNTAyODc4MzMyHDMxNGQyNzdmOTRjOGU5MDE6Y29tOmVuOlVTO" +
                                        "lI%26usg%3DAOvVaw0ib2-8ukc8GkNkCxTAEkb6&source=gmail&ust=166582840354400" +
                                        "0&usg=AOvVaw0R6tQNh7vvWCHGibWcIz_k",
                            )
                            .cleanedText shouldBe
                            "https://www.inferse.com/268929/prime-day-2022-phone-deals" +
                                "-all-the-best-amazon-prime-early-access-phone-deals-bgr/"
                    }

                    "return result after max iterations" {
                        // Appends a path segment on every pass, so cleaning never settles by
                        // itself.
                        val endless =
                            Sanitizer(
                                id = SanitizerId("endless"),
                                name = "endless",
                                rules = persistentListOf(Rule.RewritePath("(.*)", "$1/x")),
                            )

                        Cleaner(sanitizers = persistentListOf(endless), repository = repository)
                            .clean("http://www.example.com")
                            .cleanedText shouldBe "http://www.example.com/x/x/x/x/x"
                    }
                }

            "short links" should
                {
                    val catalog = Cleaner(sanitizers = AllSanitizers, repository = repository)

                    val shortLink = "https://www.tiktok.com/t/ZT8abc123/?_t=8xyz&_r=1"
                    val target =
                        "https://www.tiktok.com/@user/video/7300000000000000000" +
                            "?is_from_webapp=1&sender_device=pc"
                    val resolved = mapOf(url(shortLink) to url(target))
                    val replace =
                        Change(
                            TiktokShortLink.id,
                            Change.Action.Replace(from = url(shortLink), to = url(target)),
                        )

                    "list the short links of a text" {
                        catalog.shortLinks(
                            "Look at https://www.tiktok.com/t/ZT8abc123/ and https://a.co/d/4X8EKfk",
                        ) shouldBe
                            listOf(
                                url("https://www.tiktok.com/t/ZT8abc123/"),
                                url("https://a.co/d/4X8EKfk"),
                            )
                    }

                    "list a short link which appears twice once" {
                        catalog.shortLinks("$shortLink $shortLink") shouldBe listOf(url(shortLink))
                    }

                    "not list URLs whose path is not a short link" {
                        catalog
                            .shortLinks(
                                "https://www.tiktok.com/@user/video/7300000000000000000 " +
                                    "https://www.reddit.com/r/fossdroid/comments/1abcdef/title/ " +
                                    "https://example.com/t/ZT8abc123",
                            )
                            .shouldBeEmpty()
                    }

                    "not list short links whose sanitizer is disabled" {
                        coEvery { repository.isEnabled(TiktokShortLink.id) } returns false

                        catalog
                            .shortLinks("$shortLink https://a.co/d/4X8EKfk")
                            .shouldContainExactly(url("https://a.co/d/4X8EKfk"))
                        catalog.isShortLink(url(shortLink)) shouldBe false
                    }

                    "list nothing for a text without URLs" {
                        catalog.shortLinks("no links here").shouldBeEmpty()
                        catalog.shortLinks(null).shouldBeEmpty()
                    }

                    "recognize a single short link" {
                        catalog.isShortLink(url(shortLink)) shouldBe true
                        catalog.isShortLink(url(target)) shouldBe false
                    }

                    "replace a resolved short link and clean its target" {
                        catalog.clean(shortLink, resolved = resolved).cleanedText shouldBe
                            "https://www.tiktok.com/@user/video/7300000000000000000"
                    }

                    "hand the target to the regular TikTok sanitizer on the next pass" {
                        // The shape a live vm.tiktok.com link redirected to, with an empty user
                        // name; u_code and _d are placeholders.
                        val shortLink = "https://vm.tiktok.com/ZS923LxwaARJm-5DUYW/"
                        val target =
                            "https://www.tiktok.com/@/video/7649011711130766612?_r=1&_d=placeholder" +
                                "&u_code=placeholder&share_item_id=7649011711130766612&timestamp=" +
                                "&utm_campaign=client_share&utm_source=short_fallback" +
                                "&share_app_id=1233"

                        Cleaner(
                                sanitizers = persistentListOf(Tiktok, TiktokShortLink),
                                repository = repository,
                            )
                            .clean(shortLink, resolved = mapOf(url(shortLink) to url(target)))
                            .cleanedText shouldBe
                            "https://www.tiktok.com/@/video/7649011711130766612"
                    }

                    "attribute the replacement to the short link's sanitizer" {
                        val result = catalog.clean(shortLink, resolved = resolved).urls.single()

                        result.available shouldContain replace
                        result.applied shouldContain replace
                    }

                    "keep the short link when the replacement is declined" {
                        val result =
                            catalog.clean(shortLink, declined = setOf(replace), resolved = resolved)

                        result.cleanedText shouldBe "https://www.tiktok.com/t/ZT8abc123/"
                        result.urls.single().available shouldContain replace
                    }

                    "clean a short link which was not resolved as before" {
                        catalog.clean(shortLink).cleanedText shouldBe
                            "https://www.tiktok.com/t/ZT8abc123/"
                    }

                    "replace only the short link a target was resolved for" {
                        catalog
                            .clean("$shortLink https://a.co/d/4X8EKfk", resolved = resolved)
                            .cleanedText shouldBe
                            "https://www.tiktok.com/@user/video/7300000000000000000 " +
                                "https://a.co/d/4X8EKfk"
                    }

                    "apply the other changes of a pass before the replacement" {
                        // The rewrite is proposed for the short link in the same pass as the
                        // replacement, and must not end up applied to the target.
                        val resolve =
                            Sanitizer(
                                id = SanitizerId("resolve"),
                                name = "resolve",
                                rules = persistentListOf(Rule.Resolve()),
                                match = persistentListOf(Match(HostMatch.Domain("s.example.com"))),
                            )
                        val rewrite =
                            Sanitizer(
                                id = SanitizerId("rewrite"),
                                name = "rewrite",
                                rules = persistentListOf(Rule.RewritePath("/abc", "/xyz")),
                            )

                        Cleaner(
                                sanitizers = persistentListOf(resolve, rewrite),
                                repository = repository,
                            )
                            .clean(
                                "https://s.example.com/abc",
                                resolved =
                                    mapOf(
                                        url("https://s.example.com/abc") to
                                            url("https://example.com/page"),
                                    ),
                            )
                            .cleanedText shouldBe "https://example.com/page"
                    }
                }
        },
    )

private fun url(value: String) = requireNotNull(Url.parse(value))
