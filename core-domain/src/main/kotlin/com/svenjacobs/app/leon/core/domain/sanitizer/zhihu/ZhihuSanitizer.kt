package com.svenjacobs.app.leon.core.domain.sanitizer.zhihu

import android.content.Context
import com.svenjacobs.app.leon.core.common.domain.matchesDomain  // 扩展函数
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class ZhihuSanitizer : Sanitizer {

    override val id = SanitizerId("zhihu")

    override fun getMetadata(context: Context) =
        Sanitizer.Metadata(name = "知乎")

    // 使用扩展函数 matchesDomain，避免与自身递归冲突
    override fun matchesDomain(input: String): Boolean = input.matchesDomain("zhihu.com")

    override fun invoke(input: String): String {
        return input
            .replace(Regex("&utm_source=[^&]*"), "")
            .replace(Regex("&share_redirect=[^&]*"), "")
            .replace(Regex("&share_code=[^&]*"), "")
            .removeSuffix("?")
    }
}