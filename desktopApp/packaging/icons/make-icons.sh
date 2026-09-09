#!/usr/bin/env sh
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

# Regenerates leon.png, leon.ico and leon.icns from etc/ic_launcher.svg.
# Run it and commit the result whenever the SVG changes:
#
#     ./desktopApp/packaging/icons/make-icons.sh
#
# Requires ImageMagick 7 (`magick`) built against librsvg — the default on Fedora and Debian.
# On macOS `realpath` needs coreutils from Homebrew, as in bin/promote-beta.sh.

set -eu

DIR=$(dirname "$(realpath "$0")")
SRC="$DIR/../../../etc/ic_launcher.svg"

command -v magick > /dev/null || {
    echo "magick not found - install ImageMagick 7" >&2
    exit 1
}

render() { # size, target file
    # -strip drops the date:create/date:modify tEXt chunks ImageMagick otherwise embeds, which
    # would make every run produce a byte-different PNG (and therefore .icns) for the same SVG.
    magick -background none "$SRC" -resize "$1x$1" -strip "$2"
}

render 512 "$DIR/leon.png"
magick -background none "$SRC" -resize 256x256 \
    -define icon:auto-resize=256,128,64,48,32,16 "$DIR/leon.ico"

# A four byte big-endian integer. Octal escapes, because \xHH is not POSIX printf.
be32() {
    printf "\\$(printf '%03o' $(( ($1 >> 24) & 255 )))\\$(printf '%03o' $(( ($1 >> 16) & 255 )))\\$(printf '%03o' $(( ($1 >> 8) & 255 )))\\$(printf '%03o' $(( $1 & 255 )))"
}

TMP=$(mktemp -d)
trap 'rm -rf "$TMP"' EXIT

# ic07-ic10 are the plain sizes, ic11-ic14 the @2x variants macOS uses on Retina displays.
for entry in ic07:128 ic08:256 ic09:512 ic10:1024 ic11:32 ic12:64 ic13:256 ic14:512; do
    type=${entry%:*}
    render "${entry#*:}" "$TMP/$type.png"
    printf '%s' "$type" >> "$TMP/chunks"
    be32 $(( $(wc -c < "$TMP/$type.png") + 8 )) >> "$TMP/chunks"
    cat "$TMP/$type.png" >> "$TMP/chunks"
done

{
    printf 'icns'
    be32 $(( $(wc -c < "$TMP/chunks") + 8 ))
    cat "$TMP/chunks"
} > "$DIR/leon.icns"

echo "wrote leon.png, leon.ico and leon.icns from $SRC"
