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
 * The two directories Léon stores its files in on desktop: [data] for the Room database, [config]
 * for the DataStore preference files. On macOS both point at the same directory.
 */
data class DesktopDirs(val data: File, val config: File)

/**
 * Per the XDG Base Directory Specification, a variable that is set but empty, or set to a relative
 * path, must be treated as unset:
 * https://specifications.freedesktop.org/basedir-spec/latest/#variables
 */
private fun xdgEnv(name: String, env: (String) -> String?): String? =
    env(name)?.takeIf { it.isNotEmpty() && File(it).isAbsolute }

/**
 * The same "set but empty means unset" rule for the Windows variables, so an empty `%APPDATA%`
 * cannot silently turn into a relative `Leon` directory in the working directory. Absoluteness is
 * deliberately not checked here: `File.isAbsolute` judges a Windows path by the *host* JVM's rules
 * and would report `false` for `C:\...` everywhere except Windows.
 */
private fun windowsEnv(name: String, env: (String) -> String?): String? =
    env(name)?.takeIf { it.isNotEmpty() }

/**
 * Resolves the per-OS directory Léon stores its database (`leon.db`) in.
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
            File(windowsEnv("LOCALAPPDATA", env) ?: "$userHome\\AppData\\Local", "Leon")

        osName.startsWith("Mac") -> File(userHome, "Library/Application Support/Leon")

        else -> File(xdgEnv("XDG_DATA_HOME", env) ?: "$userHome/.local/share", "leon")
    }

/**
 * Resolves the per-OS directory Léon stores its DataStore preference files
 * (`settings.preferences_pb`, `sanitizers.preferences_pb`) in.
 *
 * On macOS this is the same directory as [desktopDataDir] - `~/Library/Preferences` is reserved for
 * plists written through NSUserDefaults, not app-managed files. On Windows this is `Roaming` rather
 * than `Local`, since per-user configuration belongs there.
 */
fun desktopConfigDir(
    osName: String = System.getProperty("os.name"),
    userHome: String = System.getProperty("user.home"),
    env: (String) -> String? = System::getenv,
): File =
    when {
        osName.startsWith("Windows") ->
            File(windowsEnv("APPDATA", env) ?: "$userHome\\AppData\\Roaming", "Leon")

        osName.startsWith("Mac") -> File(userHome, "Library/Application Support/Leon")

        else -> File(xdgEnv("XDG_CONFIG_HOME", env) ?: "$userHome/.config", "leon")
    }

/**
 * Migrates DataStore preference files from [data] to [config], for installs that predate the
 * data/config split (everything used to live in [data]). No-op when [data] and [config] are the
 * same directory (macOS), when [config] already has a `settings.preferences_pb`, or when [data] has
 * none to move. A failed move is swallowed per file - it must never prevent startup.
 */
fun migrateConfigFiles(data: File, config: File) {
    if (data.absolutePath == config.absolutePath) return
    if (File(config, "settings.preferences_pb").exists()) return
    if (!File(data, "settings.preferences_pb").exists()) return

    for (name in listOf("settings.preferences_pb", "sanitizers.preferences_pb")) {
        runCatching { File(data, name).takeIf { it.exists() }?.renameTo(File(config, name)) }
    }
}
