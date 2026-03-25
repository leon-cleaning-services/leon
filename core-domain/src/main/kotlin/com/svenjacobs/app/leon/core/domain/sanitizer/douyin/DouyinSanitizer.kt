package com.svenjacobs.app.leon.core.domain.douyin

import android.content.Context

class DouyinSanitizer : Sanitizer {

    override val id = SanitizerId("douyin")

    override fun getMetadata(context: Context) =
        Sanitizer.Metadata(name = "抖音")

    override fun matchesDomain(input: String) =
        input.contains("douyin.com") || input.contains("v.douyin.com")

    override fun invoke(input: String): String {
        return input.replace(Regex("[?&](sec_uid|dt_dapp|enter_from|share_source|share_link_id|previous_page)=[^&]*"), "")
            .removeSuffix("?")
    }
}
