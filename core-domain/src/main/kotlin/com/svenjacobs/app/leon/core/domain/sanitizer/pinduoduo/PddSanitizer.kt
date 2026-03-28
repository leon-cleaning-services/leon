package com.svenjacobs.app.leon.core.domain.sanitizer.pinduoduo

import android.content.Context
import com.svenjacobs.app.leon.core.common.domain.matchesDomain  // 扩展函数
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class PddSanitizer : Sanitizer {

    override val id = SanitizerId("pinduoduo")

    override fun getMetadata(context: Context) =
        Sanitizer.Metadata(name = "拼多多")

    override fun matchesDomain(input: String): Boolean =
        input.matchesDomain("pinduoduo.com") || input.matchesDomain("pdd.com")

    override fun invoke(input: String): String {
        return input
            .replace(Regex("&pid=[^&]*"), "")
            .replace(Regex("&share_uin=[^&]*"), "")
            .replace(Regex("&track_id=[^&]*"), "")
            .replace(Regex("&goods_sign=[^&]*"), "")
            .removeSuffix("?")
    }
}