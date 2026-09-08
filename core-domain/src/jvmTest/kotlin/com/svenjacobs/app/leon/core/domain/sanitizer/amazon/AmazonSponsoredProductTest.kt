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
package com.svenjacobs.app.leon.core.domain.sanitizer.amazon

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.AmazonSponsoredProduct
import io.kotest.matchers.shouldBe

class AmazonSponsoredProductTest :
    SanitizerSpec(
        AmazonSponsoredProduct,
        {
            "clean" should
                {
                    "extract product link from sponsored product click URL" {
                        clean(
                            "https://www.amazon.it/sspa/click?ie=UTF8&spc=MTo3MTk4NjU4MzU5NTEyNTQxOjE3NTE2" +
                                "MDkwMDM6c3BfcGhvbmVfZGV0YWlsX3RoZW1hdGljOjMwMDU0OTczNzczNDkzMjowOjA6Og==&" +
                                "url=%2FHP-OmniBook-14-fe1000sl-Snapdragon-X1P-42-100%2Fdp%2FB0DBVFPSGT%2Fref%" +
                                "3Dpd_aw_subss_hxwSS2_sspa_mw_detail_m_sccl_1%2F260-2153257-8850102%3Fpd_rd_w%" +
                                "3DTmRV2%26content-id%3Damzn1.sym.194ef91c-49b2-42b3-829e-26a1075c2a4c%26pf_rd" +
                                "_p%3D194ef91c-49b2-42b3-829e-26a1075c2a4c%26pf_rd_r%3D8YMAXQKR0FR9JGR0TZTN%26" +
                                "pd_rd_wg%3DZ9yiT%26pd_rd_r%3D8bded4b2-92aa-488f-a6af-d80d19f6f9c2%26pd_rd_i%3D" +
                                "B0DBVFPSGT%26psc%3D1%26sp_csd%3Dd2lkZ2V0TmFtZT1zcF9waG9uZV9kZXRhaWxfdGhlbWF0aW" +
                                "M%3D"
                        ) shouldBe "https://www.amazon.it/dp/B0DBVFPSGT/"
                    }

                    "work on other Amazon domains" {
                        clean(
                            "https://www.amazon.de/sspa/click?url=%2Ffoo%2Fdp%2FB123456789%2Fref%3Dabc"
                        ) shouldBe "https://www.amazon.de/dp/B123456789/"
                    }
                }

            "matches" should
                {
                    "match Amazon sponsored product click link" {
                        matches("https://www.amazon.it/sspa/click?url=%2Fdp%2FB0DBVFPSGT") shouldBe
                            true
                    }

                    "not match sponsored product link inside another URL" {
                        matches(
                            "https://evil.com/?u=https://amazon.it/sspa/click?url=%2Fdp%2FB0DBVFPSGT"
                        ) shouldBe false
                    }

                    "not match other Amazon paths" {
                        matches("https://www.amazon.it/dp/B0DBVFPSGT/") shouldBe false
                    }
                }
        },
    )
