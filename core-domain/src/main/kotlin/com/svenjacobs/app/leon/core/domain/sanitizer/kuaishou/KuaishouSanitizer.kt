package com.svenjacobs.app.leon.core.domain.sanitizer.kuaishou

import android.content.Context
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class KuaishouSanitizer : Sanitizer {

    override val id = SanitizerId("kuaishou")

    override fun getMetadata(context: Context) =
        Sanitizer.Metadata(name = "Kuaishou")

    override fun matchesDomain(input: String) =
        input.contains("kuaishou.com") || input.contains("v.kuaishou.com")

    override fun invoke(input: String): String {
        return input.replace(Regex("[?&](share|userId|photoId)=[^&]*"), "")
            .removeSuffix("?")
    }
}
