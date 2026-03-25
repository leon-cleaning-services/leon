package com.svenjacobs.app.leon.core.domain.sanitizer.xiaohongshu

import android.content.Context
import com.svenjacobs.app.leon.core.common.domain.matchesDomain
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class XiaohongshuSanitizer : Sanitizer() {
    override val id = SanitizerId("xiaohongshu")

    override val parameters = setOf(
        "xhslink", "from", "share_id", "share_user_id", "wx_fakeid", "scene", "is_share"
    )

    override fun getMetadata(context: Context) =
        Sanitizer.Metadata(name = "小红书")

    override fun matchesDomain(input: String) =
        input.matchesDomain("xiaohongshu.com") || input.matchesDomain("xhslink.com")
}
