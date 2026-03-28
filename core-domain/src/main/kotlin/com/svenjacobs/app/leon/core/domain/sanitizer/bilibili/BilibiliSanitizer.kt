package com.svenjacobs.app.leon.core.domain.sanitizer.bilibili

import android.content.Context
import com.svenjacobs.app.leon.core.common.domain.matchesDomain  // 扩展函数
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class BilibiliSanitizer : Sanitizer {

    override val id = SanitizerId("bilibili")

    override fun getMetadata(context: Context) =
        Sanitizer.Metadata(name = "哔哩哔哩")

    override fun matchesDomain(input: String): Boolean = input.matchesDomain("bilibili.com")

    override fun invoke(input: String): String {
        return input
            .replace(Regex("&vd_source=[^&]*"), "")
            .replace(Regex("&seid=[^&]*"), "")
            .replace(Regex("&from=[^&]*"), "")
            .replace(Regex("&share_source=[^&]*"), "")
            .replace(Regex("&copy_link=[^&]*"), "")
            .removeSuffix("?")
    }
}