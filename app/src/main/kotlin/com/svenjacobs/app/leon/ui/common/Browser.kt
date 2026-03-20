package com.svenjacobs.app.leon.ui.common

import android.content.Context
import android.content.Intent
import android.net.Uri

fun isDefaultBrowser(context: Context): Boolean {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://"))
    val activity = intent.resolveActivity(context.packageManager)
    return activity?.packageName?.startsWith("com.svenjacobs.app.leon") ?: false
}
