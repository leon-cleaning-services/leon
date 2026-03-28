package com.svenjacobs.app.leon.core.domain.sanitizer.taobao

import android.content.Context
import com.svenjacobs.app.leon.core.common.domain.matchesDomain  // 扩展函数
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class TaobaoSanitizer : Sanitizer {

    override val id = SanitizerId("taobao")

    override fun getMetadata(context: Context) =
        Sanitizer.Metadata(name = "淘宝")

    override fun matchesDomain(input: String): Boolean =
        input.matchesDomain("taobao.com") || input.matchesDomain("tmall.com")

    override fun invoke(input: String): String {
        return input
            .replace(Regex("&smid=[^&]*"), "")
            .replace(Regex("&ut_ma=[^&]*"), "")
            .replace(Regex("&track_id=[^&]*"), "")
            .replace(Regex("&spm=[^&]*"), "")
            .replace(Regex("&share_crt_v=[^&]*"), "")
            .replace(Regex("&tbkt=[^&]*"), "")
            .replace(Regex("&isg=[^&]*"), "")
            .removeSuffix("?")
    }
}