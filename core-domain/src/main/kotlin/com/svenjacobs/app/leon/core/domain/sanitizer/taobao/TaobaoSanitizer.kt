package com.svenjacobs.app.leon.core.domain.sanitizer.taobao

import android.content.Context
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class TaobaoSanitizer : Sanitizer {

    override val id = SanitizerId("taobao")

    override fun getMetadata(context: Context) =
        Sanitizer.Metadata(name = "Taobao")

    override fun matchesDomain(input: String) =
        input.contains("taobao.com") || input.contains("tmall.com")

    override fun invoke(input: String): String {
        return input.replace(Regex("[?&](smid|ut_ma|track_id|spm|share_crt_v|tbkt|isg)=[^&]*"), "")
            .removeSuffix("?")
    }
}
