package com.svenjacobs.app.leon.core.domain.sanitizer.weibo

import android.content.Context
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class WeiboSanitizer : Sanitizer {

    override val id = SanitizerId("weibo")

    override fun getMetadata(context: Context) =
        Sanitizer.Metadata(name = "Weibo")

    override fun matchesDomain(input: String) = input.contains("weibo.com")

    override fun invoke(input: String): String {
        return input.replace(Regex("[?&](from|refer|share_token)=[^&]*"), "")
            .removeSuffix("?")
    }
}
