package com.svenjacobs.app.leon.core.domain.sanitizer.wechat

import android.content.Context
import com.svenjacobs.app.leon.core.common.domain.matchesDomain  // 扩展函数
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class WechatSanitizer : Sanitizer {

    override val id = SanitizerId("wechat")

    override fun getMetadata(context: Context) =
        Sanitizer.Metadata(name = "微信")

    override fun matchesDomain(input: String): Boolean =
        input.matchesDomain("weixin.qq.com") || input.matchesDomain("url.cn")

    override fun invoke(input: String): String {
        return input
            .replace(Regex("&__biz=[^&]*"), "")
            .replace(Regex("&mid=[^&]*"), "")
            .replace(Regex("&idx=[^&]*"), "")
            .replace(Regex("&sn=[^&]*"), "")
            .replace(Regex("&scene=[^&]*"), "")
            .replace(Regex("&wx_header=[^&]*"), "")
            .removeSuffix("?")
    }
}