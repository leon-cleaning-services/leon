package com.svenjacobs.app.leon.core.domain.sanitizer.jd

import android.content.Context
import com.svenjacobs.app.leon.core.common.domain.matchesDomain
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class JdSanitizer : Sanitizer {

    override val id = SanitizerId("jd")

    override fun getMetadata(context: Context) =
        Sanitizer.Metadata(name = "京东")

    override fun matchesDomain(input: String): Boolean =
        input.matchesDomain("jd.com") || input.matchesDomain("3.cn")   // 添加京东短链域名

    override fun invoke(input: String): String {
        // 将 &amp; 实体转换为 &，以便正则匹配
        var result = input.replace("&amp;", "&")

        result = result
            .replace(Regex("[?&]utm_source=[^&]*"), "")
            .replace(Regex("[?&]utm_medium=[^&]*"), "")
            .replace(Regex("[?&]utm_campaign=[^&]*"), "")
            .replace(Regex("[?&]share=[^&]*"), "")
            .replace(Regex("[?&]jkl=[^&]*"), "")   // 现在可以匹配 ?jkl= 或 &jkl=

        // 清理末尾残留的 ? 或 &
        return result.replace(Regex("[?&]$"), "")
    }
}