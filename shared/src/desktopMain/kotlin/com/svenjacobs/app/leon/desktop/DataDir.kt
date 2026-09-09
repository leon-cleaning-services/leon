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
package com.svenjacobs.app.leon.desktop

import java.io.File

/**
 * Resolves the per-OS directory Léon stores its database and preferences in.
 *
 * The Linux fallback is byte-identical to the path Léon has always used there, so existing installs
 * keep their data with no migration.
 */
fun desktopDataDir(
    osName: String = System.getProperty("os.name"),
    userHome: String = System.getProperty("user.home"),
    env: (String) -> String? = System::getenv,
): File =
    when {
        osName.startsWith("Windows") ->
            File(env("LOCALAPPDATA") ?: "$userHome\\AppData\\Local", "Leon")

        osName.startsWith("Mac") -> File(userHome, "Library/Application Support/Leon")

        else -> File(env("XDG_DATA_HOME") ?: "$userHome/.local/share", "leon")
    }
