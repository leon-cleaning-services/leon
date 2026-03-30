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
package com.svenjacobs.app.leon.core.domain.sanitizer.weibo

import android.content.Context
import com.svenjacobs.app.leon.core.common.domain.matchesDomain // 扩展函数
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class WeiboSanitizer : Sanitizer {

    override val id = SanitizerId("weibo")

    override fun getMetadata(context: Context) = Sanitizer.Metadata(name = "微博")

    // 使用扩展函数 matchesDomain，避免递归调用
    override fun matchesDomain(input: String): Boolean =
        input.matchesDomain("weibo.com") || input.matchesDomain("m.weibo.cn")

    override fun invoke(input: String): String {
        return input
            .replace(Regex("&from=[^&]*"), "")
            .replace(Regex("&refer=[^&]*"), "")
            .replace(Regex("&share_token=[^&]*"), "")
            .removeSuffix("?")
    }
}
