package com.svenjacobs.app.leon.core.domain.sanitizer.douyin

import android.content.Context
import com.svenjacobs.app.leon.core.common.domain.matchesDomain  // 扩展函数
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class DouyinSanitizer : Sanitizer {

    override val id = SanitizerId("douyin")

    override fun getMetadata(context: Context) =
        Sanitizer.Metadata(name = "抖音")

    override fun matchesDomain(input: String): Boolean =
        input.matchesDomain("douyin.com") || input.matchesDomain("v.douyin.com")

    override fun invoke(input: String): String {
        return input
            .replace(Regex("&sec_uid=[^&]*"), "")
            .replace(Regex("&dt_dapp=[^&]*"), "")
            .replace(Regex("&enter_from=[^&]*"), "")
            .replace(Regex("&share_source=[^&]*"), "")
            .replace(Regex("&share_link_id=[^&]*"), "")
            .replace(Regex("&previous_page=[^&]*"), "")
            .removeSuffix("?")
    }
}