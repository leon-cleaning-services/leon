package com.svenjacobs.app.leon.core.domain.sanitizer.xiaohongshu

import android.content.Context
import com.svenjacobs.app.leon.core.common.domain.matchesDomain
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class XiaohongshuSanitizer : Sanitizer {

    override val id = SanitizerId("xiaohongshu")

    override fun getMetadata(context: Context) =
        Sanitizer.Metadata(name = "小红书")

    override fun matchesDomain(input: String): Boolean =
        input.matchesDomain("xiaohongshu.com") || input.matchesDomain("xhslink.com")

    override fun invoke(input: String): String {
        // 直接截取 '?' 之前的部分，去掉所有查询参数
        return input.substringBefore('?')
    }
}