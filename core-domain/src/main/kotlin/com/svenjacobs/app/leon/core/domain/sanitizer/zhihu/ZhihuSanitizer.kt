package com.svenjacobs.app.leon.core.domain.sanitizer.zhihu

import android.content.Context
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class ZhihuSanitizer : Sanitizer {

    override val id = SanitizerId("zhihu")

    override fun getMetadata(context: Context) =
        Sanitizer.Metadata(name = "Zhihu")

    override fun matchesDomain(input: String) = input.contains("zhihu.com")

    override fun invoke(input: String): String {
        return input.replace(Regex("[?&](utm_source|share_redirect)=[^&]*"), "")
            .removeSuffix("?")
    }
}
