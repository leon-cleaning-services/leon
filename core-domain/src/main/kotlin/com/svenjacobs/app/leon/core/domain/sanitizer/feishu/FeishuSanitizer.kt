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
package com.svenjacobs.app.leon.core.domain.sanitizer.feishu_dingtalk

import android.content.Context
import android.net.Uri
import com.svenjacobs.app.leon.core.common.regex.RegexFactory
import com.svenjacobs.app.leon.core.domain.R
import com.svenjacobs.app.leon.core.domain.sanitizer.RegexSanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class FeishuDingtalkSanitizer : RegexSanitizer(
    regex = RegexFactory.ofParameter("from|scene|channel|source|refer")
) {

    override val id = SanitizerId("feishu_dingtalk")

    override fun getMetadata(context: Context) = Sanitizer.Metadata(
        name = context.getString(R.string.sanitizer_feishu_dingtalk_name)
    )

    override fun matchesDomain(input: String): Boolean {
        val host = runCatching { Uri.parse(input).host }.getOrNull() ?: return false
        return host == "feishu.cn" ||
            host == "feishu.net" ||
            host == "dingtalk.com" ||
            host == "dingtalk.cn" ||
            host.endsWith(".feishu.cn") ||
            host.endsWith(".feishu.net") ||
            host.endsWith(".dingtalk.com") ||
            host.endsWith(".dingtalk.cn")
    }

    // 无需覆写 invoke，基类会自动移除匹配的正则部分
}