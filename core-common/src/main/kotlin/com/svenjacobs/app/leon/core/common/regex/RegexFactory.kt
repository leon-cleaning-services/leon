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
package com.svenjacobs.app.leon.core.common.regex

object RegexFactory {

    /** This regex matches all parameters of a URL, so everything starting at "?". */
    val AllParameters = Regex("\\?.*")

    /**
     * Returns a Regex which matches a certain parameter.
     *
     * For example `ofParameter("abc")` returns a regex string which matches `?abc=` or `&abc=`.
     *
     * @param parameter Parameter prefix
     */
    @Suppress("RegExpUnnecessaryNonCapturingGroup")
    fun ofParameter(parameter: String): Regex = Regex("[?&](?:$parameter)=([^&#]*)")

    /**
     * Returns a Regex which matches a certain parameter prefix.
     *
     * For example `ofWildcardParameter("abc_")` returns a regex string which matches `?abc_x=`,
     * `&abc_y=`, `&abc_zzz=` et cetera.
     *
     * @param parameter Parameter prefix
     */
    @Suppress("RegExpUnnecessaryNonCapturingGroup")
    fun ofWildcardParameter(parameter: String): Regex = Regex("[?&](?:$parameter)[^=]*=([^&#]*)")

    /**
     * Returns a Regex which removes all parameters except the specified one.
     *
     * Use regular expression group and OR (`|`) to include several parameters, for example `(a|b)`.
     *
     * @param parameter Parameter to exclude
     */
    fun exceptParameter(parameter: String): Regex = Regex("[?&](?!$parameter=)[^&]+")
}
