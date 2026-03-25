package com.svenjacobs.app.leon.core.domain.sanitizer.jd

import android.content.Context
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class JdSanitizer : Sanitizer {

    override val id = SanitizerId("jd")

    override fun getMetadata(context: Context) =
        Sanitizer.Metadata(name = "JD")

    override fun matchesDomain(input: String) = input.contains("jd.com")

    override fun invoke(input: String): String {
        return input.replace(Regex("[?&](utm_source|utm_medium|utm_campaign|share)=[^&]*"), "")
            .removeSuffix("?")
    }
}
