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
package com.svenjacobs.app.leon.core.domain.sanitizer.pinduoduo

import android.content.Context
import com.svenjacobs.app.leon.core.common.domain.matchesDomain // 扩展函数
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class PddSanitizer : Sanitizer {

    override val id = SanitizerId("pinduoduo")

    override fun getMetadata(context: Context) = Sanitizer.Metadata(name = "拼多多")

    override fun matchesDomain(input: String): Boolean =
        input.matchesDomain("pinduoduo.com") ||
            input.matchesDomain("pdd.com") ||
            input.matchesDomain("yangkeduo.com") // 覆盖 mobile.yangkeduo.com 及其它子域名

    override fun invoke(input: String): String {
        return input
            .replace(Regex("&pid=[^&]*"), "")
            .replace(Regex("&share_uin=[^&]*"), "")
            .replace(Regex("&track_id=[^&]*"), "")
            .replace(Regex("&goods_sign=[^&]*"), "")
            .removeSuffix("?")
    }
}
