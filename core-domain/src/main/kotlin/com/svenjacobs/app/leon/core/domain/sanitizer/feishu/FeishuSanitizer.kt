package com.svenjacobs.app.leon.core.domain.sanitizer.feishu_dingtalk

import android.content.Context
import android.net.Uri
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class FeishuDingtalkSanitizer : Sanitizer {

    override val id = SanitizerId("feishu_dingtalk")

    override fun getMetadata(context: Context) =
        Sanitizer.Metadata(name = "飞书 / 钉钉")

    override fun matchesDomain(input: String): Boolean {
        val host = runCatching { Uri.parse(input).host }.getOrNull() ?: return false
        return host == "feishu.cn" || host == "feishu.net" ||
               host == "dingtalk.com" || host == "dingtalk.cn" ||
               host.endsWith(".feishu.cn") || host.endsWith(".feishu.net") ||
               host.endsWith(".dingtalk.com") || host.endsWith(".dingtalk.cn")
    }

    override fun invoke(input: String): String {
        return try {
            val uri = Uri.parse(input)
            val builder = uri.buildUpon().clearQuery()

            // 保留所有非跟踪参数
            val keepParams = mutableMapOf<String, String>()
            for (name in uri.queryParameterNames) {
                if (name !in setOf("from", "scene", "channel", "source", "refer")) {
                    keepParams[name] = uri.getQueryParameter(name) ?: ""
                }
            }
            for ((name, value) in keepParams) {
                builder.appendQueryParameter(name, value)
            }
            builder.build().toString()
        } catch (e: Exception) {
            input
        }
    }
}