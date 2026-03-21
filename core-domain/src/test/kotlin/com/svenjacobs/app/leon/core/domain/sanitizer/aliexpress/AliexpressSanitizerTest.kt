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
package com.svenjacobs.app.leon.core.domain.sanitizer.aliexpress

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class AliexpressSanitizerTest :
    WordSpec({
        val sanitizer = AliexpressSanitizer()

        "invoke" should
            {
                "remove all parameters from AliExpress article URL" {
                    val result =
                        sanitizer(
                            "https://m.de.aliexpress.com/item/32948511896.html?ug_edm_item_id=32948" +
                                "511896&pdp_npi=2%40dis%21EUR%21%E2%82%AC%2024%2C58%21%E2%82%AC%2014%2C" +
                                "50%21%21%21%21%21%402102fea916677108936606992d1f0c%2112000027801501608" +
                                "%21edm&edm_click_module=item_detail&tracelog=rowan&rowan_id1=aeug_edm_" +
                                "24677_1_de_DE_2022-11-05&rowan_msg_id=8681biz_pay_after_purchase%3A0%3" +
                                "A0_572584174%248a93ce02da764ff48f65112ca837f7df&ck=in_edm_other&gatewa" +
                                "yAdapt=gloPc2deuMsite"
                        )

                    result shouldBe "https://m.de.aliexpress.com/item/32948511896.html"
                }
            }

        "matchesDomain" should
            {
                "match aliexpress.com domains" {
                    sanitizer.matchesDomain("aliexpress.com/item/12345") shouldBe true
                    sanitizer.matchesDomain("m.de.aliexpress.com/item/12345") shouldBe true
                    sanitizer.matchesDomain("de.aliexpress.com/item/12345") shouldBe true
                    sanitizer.matchesDomain("es.aliexpress.com/item/12345") shouldBe true
                }
            }
    })
