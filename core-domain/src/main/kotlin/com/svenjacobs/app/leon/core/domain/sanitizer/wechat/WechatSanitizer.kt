package com.svenjacobs.app.leon.core.domain.sanitizer.wechat

import android.content.Context
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class WechatSanitizer : Sanitizer {

    override val id = SanitizerId("wechat")

    override fun getMetadata(context: Context) =
        Sanitizer.Metadata(name = "WeChat")

    override fun matchesDomain(input: String) =
        input.contains("weixin.qq.com") || input.contains("url.cn")

    override fun invoke(input: String): String {
        return input.replace(Regex("[?&](__biz|mid|idx|sn|scene|wx_header|from)=[^&]*"), "")
            .removeSuffix("?")
    }
}
