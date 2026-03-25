package com.svenjacobs.app.leon.core.domain.sanitizer.pinduoduo

import android.content.Context
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class PddSanitizer : Sanitizer {

    override val id = SanitizerId("pinduoduo")

    override fun getMetadata(context: Context) =
        Sanitizer.Metadata(name = "Pinduoduo")

    override fun matchesDomain(input: String) = input.contains("pinduoduo.com")

    override fun invoke(input: String): String {
        return input.replace(Regex("[?&](pid|share_uin|track_id|goods_sign)=[^&]*"), "")
            .removeSuffix("?")
    }
}
