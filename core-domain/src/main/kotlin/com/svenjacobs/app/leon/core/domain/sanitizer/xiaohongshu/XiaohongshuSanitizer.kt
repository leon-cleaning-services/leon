package com.svenjacobs.app.leon.core.domain.sanitizer.xiaohongshu

import android.content.Context
import com.svenjacobs.app.leon.core.common.domain.matchesDomain  // 扩展函数
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class XiaohongshuSanitizer : Sanitizer {

    override val id = SanitizerId("xiaohongshu")

    override fun getMetadata(context: Context) =
        Sanitizer.Metadata(name = "小红书")

    override fun matchesDomain(input: String): Boolean =
        input.matchesDomain("xiaohongshu.com") || input.matchesDomain("xhslink.com")

    override fun invoke(input: String): String {
        return input
            .replace(Regex("&xhslink=[^&]*"), "")
            .replace(Regex("&from=[^&]*"), "")
            .replace(Regex("&share_id=[^&]*"), "")
            .replace(Regex("&share_user_id=[^&]*"), "")
            .replace(Regex("&wx_fakeid=[^&]*"), "")
            .replace(Regex("&scene=[^&]*"), "")
            .replace(Regex("&is_share=[^&]*"), "")
            .removeSuffix("?")
    }
}