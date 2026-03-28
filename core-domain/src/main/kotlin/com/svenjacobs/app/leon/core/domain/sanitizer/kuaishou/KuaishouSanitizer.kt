package com.svenjacobs.app.leon.core.domain.sanitizer.kuaishou

import android.content.Context
import com.svenjacobs.app.leon.core.common.domain.matchesDomain  // 扩展函数
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class KuaishouSanitizer : Sanitizer {

    override val id = SanitizerId("kuaishou")

    override fun getMetadata(context: Context) =
        Sanitizer.Metadata(name = "快手")

    // 使用扩展函数 matchesDomain 处理多个域名
    override fun matchesDomain(input: String): Boolean =
        input.matchesDomain("kuaishou.com") || input.matchesDomain("v.kuaishou.com")

    override fun invoke(input: String): String {
        return input
            .replace(Regex("&share=[^&]*"), "")
            .replace(Regex("&userId=[^&]*"), "")
            .replace(Regex("&photoId=[^&]*"), "")
            .removeSuffix("?")
    }
}