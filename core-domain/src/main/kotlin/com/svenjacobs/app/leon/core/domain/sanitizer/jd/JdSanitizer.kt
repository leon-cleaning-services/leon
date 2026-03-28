package com.svenjacobs.app.leon.core.domain.sanitizer.jd

import android.content.Context
import com.svenjacobs.app.leon.core.common.domain.matchesDomain  // 扩展函数
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class JdSanitizer : Sanitizer {

    override val id = SanitizerId("jd")

    override fun getMetadata(context: Context) =
        Sanitizer.Metadata(name = "京东")

    // 使用扩展函数 matchesDomain
    override fun matchesDomain(input: String): Boolean = input.matchesDomain("jd.com")

    override fun invoke(input: String): String {
        return input
            .replace(Regex("&utm_source=[^&]*"), "")
            .replace(Regex("&utm_medium=[^&]*"), "")
            .replace(Regex("&utm_campaign=[^&]*"), "")
            .replace(Regex("&share=[^&]*"), "")
            .removeSuffix("?")
    }
}