package com.svenjacobs.app.leon.core.domain.sanitizer.taobao

import android.content.Context
import com.svenjacobs.app.leon.core.common.domain.matchesDomain
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class TaobaoSanitizer : Sanitizer {

    override val id = SanitizerId("taobao")

    override fun getMetadata(context: Context) =
        Sanitizer.Metadata(name = "淘宝")

    override fun matchesDomain(input: String): Boolean =
        input.matchesDomain("taobao.com") ||
                input.matchesDomain("tmall.com") ||
                input.matchesDomain("tb.cn") ||
                input.matchesDomain("e.tb.cn") ||
                input.matchesDomain("m.tb.cn")   // 明确添加短链域名

    override fun invoke(input: String): String {
        // 先将 &amp; 实体转换为 &，便于正则匹配
        var result = input.replace("&amp;", "&")

        result = result
            .replace(Regex("[?&]smid=[^&]*"), "")
            .replace(Regex("[?&]ut_ma=[^&]*"), "")
            .replace(Regex("[?&]track_id=[^&]*"), "")
            .replace(Regex("[?&]spm=[^&]*"), "")
            .replace(Regex("[?&]share_crt_v=[^&]*"), "")
            .replace(Regex("[?&]tbkt=[^&]*"), "")
            .replace(Regex("[?&]isg=[^&]*"), "")
            .replace(Regex("[?&]tk=[^&]*"), "")   // 确保 tk 被删除

        // 清理末尾残留的 ? 或 &
        return result.replace(Regex("[?&]$"), "")
    }
}