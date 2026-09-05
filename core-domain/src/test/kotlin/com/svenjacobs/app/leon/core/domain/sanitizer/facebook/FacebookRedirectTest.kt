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
package com.svenjacobs.app.leon.core.domain.sanitizer.facebook

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.FacebookRedirect
import io.kotest.matchers.shouldBe

class FacebookRedirectTest :
    SanitizerSpec(
        FacebookRedirect,
        {
            "clean" should
                {
                    "extract target from l.php outgoing link warning" {
                        clean(
                            "https://lm.facebook.com/l.php?u=https%3A%2F%2Fwww.latinapress.it%2Flatina%2F" +
                                "panico-al-liceo-dante-alighieri-di-latina-sasso-lanciato-da-un-palazzo-colpi" +
                                "sce-studentessa-durante-la-lezione%2F%3Ffbclid%3DIwQ0xDSwKhKW9leHRuA2FlbQIxM" +
                                "QABHhokO35LR-vCEC4f00kC0H2Z6WLrY5Kises22Eacg9fpbfxXW7RJOrbTL_iG_aem_6d8C6jDZJ" +
                                "aGqRXbV19zsvA&h=AT0j5ZjKdut-FmFM-Cfrt57PKS76sok0qtsnLizEx5A2r6ABMyPTS9BXJixYy" +
                                "CdjHIC8V9aJOj6JlD79CtQ38sTQcm4QExXUFY1jCQgeYgzu8023sT8EqlPKS2xJwabIFrnuRWC02f" +
                                "aBikjd"
                        ) shouldBe
                            "https://www.latinapress.it/latina/panico-al-liceo-dante-alighieri-di-latina-sa" +
                                "sso-lanciato-da-un-palazzo-colpisce-studentessa-durante-la-lezione/?fbclid=I" +
                                "wQ0xDSwKhKW9leHRuA2FlbQIxMQABHhokO35LR-vCEC4f00kC0H2Z6WLrY5Kises22Eacg9fpbfx" +
                                "XW7RJOrbTL_iG_aem_6d8C6jDZJaGqRXbV19zsvA"
                    }

                    "extract target from flx/warn external link warning" {
                        clean(
                            "https://m.facebook.com/flx/warn/?u=https%3A%2F%2Fwww.focus.de%2Fgesundheit%2F" +
                                "ratgeber%2Fapotheker-warnt-vor-natuerlichem-antibiotikum-wer-es-nimmt-wird-z" +
                                "um-schlumpf-91fca548.html%3Ffbclid%3DIwZXh0bgNhZW0CMTEAc3J0YwZhcHBfaWQMMzUwN" +
                                "jg1NTMxNzI4AAEeJJ4U1iY04LKeknOI58w3czBdQECMoGyKXS6rGaT8urwE53c73uUMH6zYdKc_ae" +
                                "m_rHr5SbyaWkdVG0cHPQYr4g&h=AT3cDUVol511LB_DioYWzyRaZ7XpHvTfhDxNiKTfUH5GKkFYsJ" +
                                "iWaLhWIsERxwPuoyLqG-e6fyHnYBoMgtU0Z-u5ePXLykEkA9gjN5klYbAqaN14w23Ll_MQU7aXmZL" +
                                "QTIfdiLsfmlVcLPxqV4Rt1r2msm4MLK3h&_rdr"
                        ) shouldBe
                            "https://www.focus.de/gesundheit/ratgeber/apotheker-warnt-vor-natuerlichem-anti" +
                                "biotikum-wer-es-nimmt-wird-zum-schlumpf-91fca548.html?fbclid=IwZXh0bgNhZW0CM" +
                                "TEAc3J0YwZhcHBfaWQMMzUwNjg1NTMxNzI4AAEeJJ4U1iY04LKeknOI58w3czBdQECMoGyKXS6rGa" +
                                "T8urwE53c73uUMH6zYdKc_aem_rHr5SbyaWkdVG0cHPQYr4g"
                    }
                }

            "matches" should
                {
                    "match l.php on any facebook.com subdomain" {
                        matches(
                            "https://lm.facebook.com/l.php?u=https%3A%2F%2Fexample.com"
                        ) shouldBe true
                        matches(
                            "https://www.facebook.com/l.php?u=https%3A%2F%2Fexample.com"
                        ) shouldBe true
                    }

                    "match flx/warn on any facebook.com subdomain" {
                        matches(
                            "https://m.facebook.com/flx/warn/?u=https%3A%2F%2Fexample.com"
                        ) shouldBe true
                    }

                    "not match a lookalike host" {
                        matches(
                            "https://facebook.com.evil.com/l.php?u=https%3A%2F%2Fexample.com"
                        ) shouldBe false
                    }

                    "not match other facebook.com paths" {
                        matches("https://www.facebook.com/reel/123") shouldBe false
                    }
                }
        },
    )
