package com.svenjacobs.app.leon.core.domain.sanitizer.weibo

import android.content.Context
import com.svenjacobs.app.leon.core.common.domain.matchesDomain  // 扩展函数
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class WeiboSanitizer : Sanitizer {

    override val id = SanitizerId("weibo")

    override fun getMetadata(context: Context) =
        Sanitizer.Metadata(name = "微博")

    // 使用扩展函数 matchesDomain，避免递归调用
    override fun matchesDomain(input: String): Boolean =
        input.matchesDomain("weibo.com") || input.matchesDomain("m.weibo.cn")

    override fun invoke(input: String): String {
        return input
            .replace(Regex("&from=[^&]*"), "")
            .replace(Regex("&refer=[^&]*"), "")
            .replace(Regex("&share_token=[^&]*"), "")
            .removeSuffix("?")
    }
}