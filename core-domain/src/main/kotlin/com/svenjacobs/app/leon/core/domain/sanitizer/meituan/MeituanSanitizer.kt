package com.svenjacobs.app.leon.core.domain.sanitizer.meituan_dianping

import android.content.Context
import com.svenjacobs.app.leon.core.common.domain.matchesDomain
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class MeituanDianpingSanitizer : Sanitizer {

    override val id = SanitizerId("meituan_dianping")

    override fun getMetadata(context: Context) =
        Sanitizer.Metadata(name = "美团 / 大众点评")

    override fun matchesDomain(input: String): Boolean =
        input.matchesDomain("meituan.com") ||
        input.matchesDomain("meituan.cn") ||
        input.matchesDomain("dianping.com") ||
        input.matchesDomain("meituan.net")

    override fun invoke(input: String): String {
        return input
            // 移除常见跟踪参数（可扩展）
            .replace(Regex("[?&]utm_[^&]*"), "")
            .replace(Regex("[?&]from=[^&]*"), "")
            .replace(Regex("[?&]source=[^&]*"), "")
            .replace(Regex("[?&]channel=[^&]*"), "")
            .replace(Regex("[?&]refer=[^&]*"), "")
            .replace(Regex("[?&]wm=[^&]*"), "")
            .replace(Regex("[?&]c=[^&]*"), "")
            .replace(Regex("[?&]wx_?[^&]*"), "") // 部分链接可能混入微信参数
            .removeSuffix("?")
            .removeSuffix("&")
    }
}