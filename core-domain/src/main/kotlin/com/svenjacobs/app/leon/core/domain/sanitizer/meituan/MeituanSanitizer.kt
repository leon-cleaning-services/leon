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
package com.svenjacobs.app.leon.core.domain.sanitizer.meituan_dianping

import android.content.Context
import com.svenjacobs.app.leon.core.common.domain.matchesDomain
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class MeituanDianpingSanitizer : Sanitizer {

    override val id = SanitizerId("meituan_dianping")

    override fun getMetadata(context: Context) = Sanitizer.Metadata(name = "美团 / 大众点评")

    override fun matchesDomain(input: String): Boolean =
        input.matchesDomain("meituan.com") ||
            input.matchesDomain("meituan.cn") ||
            input.matchesDomain("dianping.com") ||
            input.matchesDomain("meituan.net")

    override fun invoke(input: String): String {
        return input
            // 移除常见跟踪参数（可扩展）
            .replace(Regex("[?&]utm_[^&]*"), "")
            .replace(Regex("[?&]from=[^&]*"), "")
            .replace(Regex("[?&]source=[^&]*"), "")
            .replace(Regex("[?&]channel=[^&]*"), "")
            .replace(Regex("[?&]refer=[^&]*"), "")
            .replace(Regex("[?&]wm=[^&]*"), "")
            .replace(Regex("[?&]c=[^&]*"), "")
            .replace(Regex("[?&]wx_?[^&]*"), "") // 部分链接可能混入微信参数
            .removeSuffix("?")
            .removeSuffix("&")
    }
}
