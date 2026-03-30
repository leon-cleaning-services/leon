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
package com.svenjacobs.app.leon.core.domain.sanitizer.taobao

import android.content.Context
import com.svenjacobs.app.leon.core.common.domain.matchesDomain
import com.svenjacobs.app.leon.core.common.regex.RegexFactory
import com.svenjacobs.app.leon.core.domain.R
import com.svenjacobs.app.leon.core.domain.sanitizer.RegexSanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class TaobaoSanitizer : RegexSanitizer(
    regex = RegexFactory.ofParameter("smid|ut_ma|track_id|spm|share_crt_v|tbkt|isg|tk")
) {

    override val id = SanitizerId("taobao")

    override fun getMetadata(context: Context) = Sanitizer.Metadata(
        name = context.getString(R.string.sanitizer_taobao_name)
    )

    override fun matchesDomain(input: String): Boolean =
        input.matchesDomain("taobao.com") ||
            input.matchesDomain("tmall.com") ||
            input.matchesDomain("tb.cn") ||
            input.matchesDomain("e.tb.cn") ||
            input.matchesDomain("m.tb.cn")

    override fun invoke(input: String): String {
        // 将 &amp; 实体转换为 & 以便正则匹配
        val cleaned = input.replace("&amp;", "&")
        // 调用基类移除指定参数，并清理末尾残留的 ? 或 &
        return super.invoke(cleaned).replace(Regex("[?&]$"), "")
    }
}