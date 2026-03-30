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
package com.svenjacobs.app.leon.core.domain.sanitizer.douyin

import android.content.Context
import com.svenjacobs.app.leon.core.common.domain.matchesDomain
import com.svenjacobs.app.leon.core.domain.R
import com.svenjacobs.app.leon.core.domain.sanitizer.RegexSanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class DouyinSanitizer : RegexSanitizer(
    regex = Regex("(?i)(/Ipd:.*|/lpD:.*|/9@4\\.com.*|/\\d+/\\d+.*)$")
) {

    override val id = SanitizerId("douyin")

    override fun getMetadata(context: Context) = Sanitizer.Metadata(
        name = context.getString(R.string.sanitizer_douyin_name)
    )

    override fun matchesDomain(input: String): Boolean =
        input.matchesDomain("douyin.com") ||
            input.matchesDomain("v.douyin.com") ||
            input.matchesDomain("iesdouyin.com")

    override fun invoke(input: String): String {
        // 1. 将 &nbsp; 实体转换为空格（预处理）
        val cleaned = input.replace("&nbsp;", " ")

        // 2. 提取第一个 http/https URL，直到遇到 ? 或空白符
        val urlPattern = Regex("https?://[^?\\s]+")
        val url = urlPattern.find(cleaned)?.value ?: cleaned

        // 3. 使用基类移除末尾的口令后缀
        return super.invoke(url)
    }
}