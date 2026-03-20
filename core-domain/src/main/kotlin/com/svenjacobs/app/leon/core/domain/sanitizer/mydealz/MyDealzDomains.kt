package com.svenjacobs.app.leon.core.domain.sanitizer.mydealz

internal class MyDealzDomains {
    companion object {
        internal val DOMAINS_REGEX = Regex(
            "www\\.mydealz\\.de|mydealz\\.de|" +
                "chollometro\\.com|dealabs\\.com|desidime\\.com|hotukdeals\\.com|nl\\.pepper\\.com|" +
                "pepper\\.it|pepper\\.pl|pepper\\.ru|promodescuentos\\.com|pelando\\.com\\.br|" +
                "preisjaeger\\.at",
        )
    }
}
