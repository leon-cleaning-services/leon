/*
 * Léon - The URL Cleaner
 * Copyright (C) 2023 Sven Jacobs
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.svenjacobs.app.leon.core.domain.sanitizer.mydealz

internal class MyDealzDomains {
    companion object {

        /**
         * Regular expression of all domains of the MyDealz network, including their subdomains.
         *
         * Meant to be passed to `matchesDomainRegex`, which anchors the expression at the start of
         * the URL and requires it to cover the complete host.
         */
        internal const val DOMAINS_REGEX =
            "(?:[^./?#:]+\\.)*(?:" +
                "mydealz\\.de|chollometro\\.com|dealabs\\.com|desidime\\.com|hotukdeals\\.com|" +
                "nl\\.pepper\\.com|pepper\\.it|pepper\\.pl|pepper\\.ru|promodescuentos\\.com|" +
                "pelando\\.com\\.br|preisjaeger\\.at" +
                ")"
    }
}
