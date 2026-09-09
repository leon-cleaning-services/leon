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

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import java.io.File
import kotlin.io.path.createTempDirectory

class DataDirTest :
    WordSpec({
        "desktopDataDir" should
            {
                "use %LOCALAPPDATA%\\Leon on Windows when set" {
                    desktopDataDir(
                        osName = "Windows 11",
                        userHome = "C:\\Users\\sven",
                        env = {
                            if (it == "LOCALAPPDATA") "C:\\Users\\sven\\AppData\\Local" else null
                        },
                    ) shouldBe File("C:\\Users\\sven\\AppData\\Local", "Leon")
                }

                "fall back to userHome\\AppData\\Local\\Leon on Windows when LOCALAPPDATA is unset" {
                    desktopDataDir(
                        osName = "Windows 11",
                        userHome = "C:\\Users\\sven",
                        env = { null },
                    ) shouldBe File("C:\\Users\\sven\\AppData\\Local", "Leon")
                }

                "use Library/Application Support/Leon on macOS" {
                    desktopDataDir(
                        osName = "Mac OS X",
                        userHome = "/Users/sven",
                        env = { null },
                    ) shouldBe File("/Users/sven/Library/Application Support/Leon")
                }

                "use \$XDG_DATA_HOME/leon on Linux when set" {
                    desktopDataDir(
                        osName = "Linux",
                        userHome = "/home/sven",
                        env = { if (it == "XDG_DATA_HOME") "/home/sven/.data" else null },
                    ) shouldBe File("/home/sven/.data", "leon")
                }

                "fall back to userHome/.local/share/leon on Linux when XDG_DATA_HOME is unset" {
                    desktopDataDir(
                        osName = "Linux",
                        userHome = "/home/sven",
                        env = { null },
                    ) shouldBe File("/home/sven", ".local/share/leon")
                }

                "fall back to userHome/.local/share/leon on Linux when XDG_DATA_HOME is empty" {
                    desktopDataDir(
                        osName = "Linux",
                        userHome = "/home/sven",
                        env = { if (it == "XDG_DATA_HOME") "" else null },
                    ) shouldBe File("/home/sven", ".local/share/leon")
                }

                "fall back to userHome/.local/share/leon on Linux when XDG_DATA_HOME is relative" {
                    desktopDataDir(
                        osName = "Linux",
                        userHome = "/home/sven",
                        env = { if (it == "XDG_DATA_HOME") "relative/path" else null },
                    ) shouldBe File("/home/sven", ".local/share/leon")
                }
            }

        "desktopConfigDir" should
            {
                "use %APPDATA%\\Leon on Windows when set" {
                    desktopConfigDir(
                        osName = "Windows 11",
                        userHome = "C:\\Users\\sven",
                        env = {
                            if (it == "APPDATA") "C:\\Users\\sven\\AppData\\Roaming" else null
                        },
                    ) shouldBe File("C:\\Users\\sven\\AppData\\Roaming", "Leon")
                }

                "fall back to userHome\\AppData\\Roaming\\Leon on Windows when APPDATA is unset" {
                    desktopConfigDir(
                        osName = "Windows 11",
                        userHome = "C:\\Users\\sven",
                        env = { null },
                    ) shouldBe File("C:\\Users\\sven\\AppData\\Roaming", "Leon")
                }

                "use Library/Application Support/Leon on macOS" {
                    desktopConfigDir(
                        osName = "Mac OS X",
                        userHome = "/Users/sven",
                        env = { null },
                    ) shouldBe File("/Users/sven/Library/Application Support/Leon")
                }

                "use \$XDG_CONFIG_HOME/leon on Linux when set" {
                    desktopConfigDir(
                        osName = "Linux",
                        userHome = "/home/sven",
                        env = { if (it == "XDG_CONFIG_HOME") "/home/sven/.conf" else null },
                    ) shouldBe File("/home/sven/.conf", "leon")
                }

                "fall back to userHome/.config/leon on Linux when XDG_CONFIG_HOME is unset" {
                    desktopConfigDir(
                        osName = "Linux",
                        userHome = "/home/sven",
                        env = { null },
                    ) shouldBe File("/home/sven", ".config/leon")
                }

                "fall back to userHome/.config/leon on Linux when XDG_CONFIG_HOME is empty" {
                    desktopConfigDir(
                        osName = "Linux",
                        userHome = "/home/sven",
                        env = { if (it == "XDG_CONFIG_HOME") "" else null },
                    ) shouldBe File("/home/sven", ".config/leon")
                }

                "fall back to userHome/.config/leon on Linux when XDG_CONFIG_HOME is relative" {
                    desktopConfigDir(
                        osName = "Linux",
                        userHome = "/home/sven",
                        env = { if (it == "XDG_CONFIG_HOME") "relative/path" else null },
                    ) shouldBe File("/home/sven", ".config/leon")
                }
            }

        "migrateConfigFiles" should
            {
                "move settings.preferences_pb and sanitizers.preferences_pb from data to config" {
                    val data = createTempDirectory("leon-data").toFile()
                    val config = createTempDirectory("leon-config").toFile()
                    File(data, "settings.preferences_pb").writeText("settings")
                    File(data, "sanitizers.preferences_pb").writeText("sanitizers")

                    migrateConfigFiles(data, config)

                    File(data, "settings.preferences_pb").exists() shouldBe false
                    File(data, "sanitizers.preferences_pb").exists() shouldBe false
                    File(config, "settings.preferences_pb").readText() shouldBe "settings"
                    File(config, "sanitizers.preferences_pb").readText() shouldBe "sanitizers"
                }

                "leave an already-migrated install untouched" {
                    val data = createTempDirectory("leon-data").toFile()
                    val config = createTempDirectory("leon-config").toFile()
                    File(data, "settings.preferences_pb").writeText("old")
                    File(config, "settings.preferences_pb").writeText("current")

                    migrateConfigFiles(data, config)

                    File(data, "settings.preferences_pb").readText() shouldBe "old"
                    File(config, "settings.preferences_pb").readText() shouldBe "current"
                }

                "be a no-op when data and config are the same directory" {
                    val dir = createTempDirectory("leon-macos").toFile()
                    File(dir, "settings.preferences_pb").writeText("settings")

                    migrateConfigFiles(dir, dir)

                    File(dir, "settings.preferences_pb").readText() shouldBe "settings"
                }

                "be a no-op when there is nothing to move" {
                    val data = createTempDirectory("leon-data").toFile()
                    val config = createTempDirectory("leon-config").toFile()

                    migrateConfigFiles(data, config)

                    File(config, "settings.preferences_pb").exists() shouldBe false
                }
            }
    })
