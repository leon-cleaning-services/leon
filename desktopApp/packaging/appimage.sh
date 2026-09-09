#!/usr/bin/env bash
#
# Léon - The URL Cleaner
# Copyright (C) 2026 Sven Jacobs
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#

# Builds desktopApp/build/Leon-<version>-x86_64.AppImage from the distributable produced by
# `./gradlew :desktopApp:createDistributable`. Run from the repo root:
#
#     bash desktopApp/packaging/appimage.sh
#
# Requires curl to fetch appimagetool, plus GNU grep. bash rather than sh because of `pipefail`,
# which is not POSIX and fails outright under dash - Ubuntu's /bin/sh.
#
# --appimage-extract-and-run is used both to run appimagetool and, when smoke-testing the result,
# to run the AppImage itself: CI runners have no FUSE.

set -euo pipefail

DIR=$(dirname "$(realpath "$0")")
ROOT=$(realpath "$DIR/../..")
cd "$ROOT"

APPDIR="desktopApp/build/AppDir"
APP="desktopApp/build/compose/binaries/main/app/leon"

# Same source as desktopApp/build.gradle.kts, and the same ".0.0" suffix, so the AppImage carries
# the identical version to the deb, rpm, msi and dmg.
VERSION="$(grep -oP 'versionName = "\K\d+' androidApp/build.gradle.kts).0.0"

[ -d "$APP" ] || {
    echo "$APP not found - run ./gradlew :desktopApp:createDistributable first" >&2
    exit 1
}

rm -rf "$APPDIR"
mkdir -p "$APPDIR"
cp -a "$APP/." "$APPDIR/"

cp "$DIR/com.svenjacobs.app.leon.desktop" "$APPDIR/"
cp "$DIR/icons/leon.png" "$APPDIR/com.svenjacobs.app.leon.png"

cat > "$APPDIR/AppRun" << 'EOF'
#!/bin/sh
HERE="$(dirname "$(readlink -f "$0")")"
exec "$HERE/bin/leon" "$@"
EOF
chmod +x "$APPDIR/AppRun"

APPIMAGETOOL="desktopApp/build/appimagetool-x86_64.AppImage"
if [ ! -x "$APPIMAGETOOL" ]; then
    curl -L -o "$APPIMAGETOOL" \
        https://github.com/AppImage/appimagetool/releases/download/continuous/appimagetool-x86_64.AppImage
    chmod +x "$APPIMAGETOOL"
fi

OUT="desktopApp/build/Leon-$VERSION-x86_64.AppImage"
rm -f "$OUT"
ARCH=x86_64 "$APPIMAGETOOL" --appimage-extract-and-run "$APPDIR" "$OUT"

echo "wrote $OUT"
