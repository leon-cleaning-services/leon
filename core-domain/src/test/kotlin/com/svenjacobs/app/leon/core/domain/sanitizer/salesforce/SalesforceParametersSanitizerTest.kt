/*
 * Léon - The URL Cleaner
 * Copyright (C) 2024 Sven Jacobs
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
package com.svenjacobs.app.leon.core.domain.sanitizer.salesforce

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class SalesforceParametersSanitizerTest :
    WordSpec({
        val sanitizer = SalesforceParametersSanitizer()

        "invoke" should
            {
                "remove sfmc_activityid parameter as per #302" {
                    sanitizer(
                        "https://www.geox.com/it-IT/uomo/scarpe/stivaletti/?sfmc_activityid=a5542c58-11be-4f33-8dd5-5e0ebeae30f2"
                    ) shouldBe "https://www.geox.com/it-IT/uomo/scarpe/stivaletti/"
                }
            }
    })
