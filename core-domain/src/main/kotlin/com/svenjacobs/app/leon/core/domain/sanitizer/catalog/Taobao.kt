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
package com.svenjacobs.app.leon.core.domain.sanitizer.catalog

import com.svenjacobs.app.leon.core.domain.sanitizer.HostMatch
import com.svenjacobs.app.leon.core.domain.sanitizer.Match
import com.svenjacobs.app.leon.core.domain.sanitizer.Rule
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId
import kotlinx.collections.immutable.persistentListOf

val TaobaoSanitizer =
    Sanitizer(
        id = SanitizerId("taobao"),
        name = "Taobao",
        rules =
            persistentListOf(
                Rule.RemoveParameters("smid|ut_ma|track_id|spm|share_crt_v|tbkt|isg|tk")
            ),
        match =
            persistentListOf(
                Match(HostMatch.Subdomains("taobao.com")),
                Match(HostMatch.Subdomains("tmall.com")),
                Match(HostMatch.Subdomains("tb.cn")),
            ),
    )
