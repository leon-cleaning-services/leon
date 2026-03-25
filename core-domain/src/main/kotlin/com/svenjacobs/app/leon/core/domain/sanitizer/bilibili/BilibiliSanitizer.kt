package com.svenjacobs.app.leon.core.domain.sanitizer.bilibili

import android.content.Context
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class BilibiliSanitizer : Sanitizer {

    override val id = SanitizerId("bilibili")

    override fun getMetadata(context: Context) =
        Sanitizer.Metadata(name = "Bilibili")

    override fun matchesDomain(input: String) = input.contains("bilibili.com")

    override fun invoke(input: String): String {
        return input.replace(Regex("[?&](vd_source|from|seid|share_source|copy_link)=[^&]*"), "")
            .removeSuffix("?")
    }
}
