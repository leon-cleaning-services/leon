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
            }
    })
