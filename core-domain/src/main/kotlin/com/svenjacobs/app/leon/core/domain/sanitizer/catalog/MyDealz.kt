/*
 * Léon - The URL Cleaner
 * Copyright (C) 2026 Sven Jacobs
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
package com.svenjacobs.app.leon.core.domain.sanitizer.catalog

import com.svenjacobs.app.leon.core.domain.sanitizer.HostMatch
import com.svenjacobs.app.leon.core.domain.sanitizer.Match
import com.svenjacobs.app.leon.core.domain.sanitizer.Rule
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId
import kotlinx.collections.immutable.persistentListOf

val MyDealzParameters =
    Sanitizer(
        id = SanitizerId("mydealz_parameters"),
        name = "MyDealz Parameters",
        rules = persistentListOf(Rule.RemoveParameters(".*")),
        match =
            persistentListOf(
                Match(HostMatch.Subdomains("mydealz.de")),
                Match(HostMatch.Subdomains("chollometro.com")),
                Match(HostMatch.Subdomains("dealabs.com")),
                Match(HostMatch.Subdomains("desidime.com")),
                Match(HostMatch.Subdomains("hotukdeals.com")),
                Match(HostMatch.Subdomains("nl.pepper.com")),
                Match(HostMatch.Subdomains("pepper.it")),
                Match(HostMatch.Subdomains("pepper.pl")),
                Match(HostMatch.Subdomains("pepper.ru")),
                Match(HostMatch.Subdomains("promodescuentos.com")),
                Match(HostMatch.Subdomains("pelando.com.br")),
                Match(HostMatch.Subdomains("preisjaeger.at")),
            ),
    )

val MyDealzRedirects =
    Sanitizer(
        id = SanitizerId("mydealz_redirects"),
        name = "MyDealz Redirects",
        rules = persistentListOf(Rule.RewritePath("/share-deal-from-app/(.+)", "/deals/a-$1")),
        match =
            persistentListOf(
                Match(HostMatch.Subdomains("mydealz.de")),
                Match(HostMatch.Subdomains("chollometro.com")),
                Match(HostMatch.Subdomains("dealabs.com")),
                Match(HostMatch.Subdomains("desidime.com")),
                Match(HostMatch.Subdomains("hotukdeals.com")),
                Match(HostMatch.Subdomains("nl.pepper.com")),
                Match(HostMatch.Subdomains("pepper.it")),
                Match(HostMatch.Subdomains("pepper.pl")),
                Match(HostMatch.Subdomains("pepper.ru")),
                Match(HostMatch.Subdomains("promodescuentos.com")),
                Match(HostMatch.Subdomains("pelando.com.br")),
                Match(HostMatch.Subdomains("preisjaeger.at")),
            ),
    )
