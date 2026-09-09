# Development

How to build, release and maintain the Play Store listing of **Léon – The URL Cleaner**.
For the code conventions — sanitizers, ViewModels, tests, formatting — see
[AGENTS.md](AGENTS.md).

## Building the App

```bash
# Run the core-domain unit tests
./gradlew :core-domain:jvmTest

# Run the shared module's unit tests (ViewModels, Room DAO) on the JVM
./gradlew :shared:desktopTest

# Check formatting
./gradlew spotlessCheck

# Auto-format
./gradlew spotlessApply
```

## Desktop

Léon also runs as a Compose Desktop application, built from the `desktopApp` module against the
same `shared` code as Android. It packages as `.deb`, `.rpm` and `.AppImage` on Linux, `.msi` on
Windows and an arm64-only `.dmg` on macOS.

```bash
# Run it locally
./gradlew :desktopApp:run

# Package for the host OS; jpackage only ever builds for the OS and architecture it runs on
./gradlew :desktopApp:packageDeb    # Linux, needs dpkg + fakeroot, see below
./gradlew :desktopApp:packageRpm    # Linux, needs rpmbuild
./gradlew :desktopApp:packageMsi    # Windows, needs the WiX Toolset (bundled on windows-latest)
./gradlew :desktopApp:packageDmg    # macOS

# AppImage: build the distributable, then wrap it
./gradlew :desktopApp:createDistributable
bash desktopApp/packaging/appimage.sh
```

That is why CI runs a matrix rather than a single job — see below. The packaged files land under
`desktopApp/build/compose/binaries/main/{deb,rpm,msi,dmg}/` and, for the AppImage,
`desktopApp/build/Leon-<version>-x86_64.AppImage`. Install the `.deb` with
`sudo apt install ./desktopApp/build/compose/binaries/main/deb/leon_*.deb` and run `leon`, or a URL
as its argument.

On Fedora, `packageDeb` needs `dpkg` and `fakeroot`, `packageRpm` needs `rpm-build`; none of that is
on a stock Fedora Atomic/Kinoite host, so run those two tasks inside a Distrobox:

```bash
distrobox create --yes --name leon-pkg --image quay.io/fedora/fedora-toolbox:44
distrobox enter leon-pkg -- sudo dnf install -y rpm-build dpkg fakeroot
distrobox enter leon-pkg -- env JAVA_HOME="$HOME/.gradle/jdks/eclipse_adoptium-21-amd64-linux.2" \
    ./gradlew --no-daemon :desktopApp:packageDeb :desktopApp:packageRpm
distrobox rm --force leon-pkg
```

**Set `JAVA_HOME` explicitly**, or jpackage fails with the thoroughly misleading
`Error: Invalid or unsupported type: [deb]` even though `dpkg-deb` is right there on the `PATH`.
The cause is that a `java` exported from another Distrobox is a shim which re-enters *that*
container, so Gradle — and the jpackage it spawns — runs somewhere the packaging tools were never
installed. Pointing `JAVA_HOME` at a JDK directory keeps everything inside `leon-pkg`. Distrobox
shares `$HOME`, so the toolchain JDK Gradle already downloaded is reachable as shown; check
`ls ~/.gradle/jdks` for the exact directory name. `--no-daemon` stops a daemon outliving the
container it was started in.

The AppImage needs nothing beyond `curl` and runs fine on the host — `appimage.sh` invokes
`appimagetool` with `--appimage-extract-and-run`, since there is no FUSE inside a container or on
GitHub runners.

Desktop data (the Room database and DataStore preference files) lives per OS: `%LOCALAPPDATA%\Leon`
on Windows, `~/Library/Application Support/Leon` on macOS, and `$XDG_DATA_HOME/leon` (falling back
to `~/.local/share/leon`) on Linux. The Linux path is unchanged from before per-OS data directories
existed, so existing Linux installs keep their data.

None of the installers are signed. On macOS, Gatekeeper blocks a plain double-click; open the app
with right-click → Open, or clear the quarantine flag with
`xattr -d com.apple.quarantine /Applications/Leon.app`. On Windows, SmartScreen shows a warning;
choose *More info → Run anyway*. The `.dmg` is arm64-only — there is no Intel macOS build.

CI (`.github/workflows/package-desktop.yml`, called from both `build.yml` and `deploy.yml`) builds
all five packages on every PR across an `ubuntu-latest` / `windows-latest` / `macos-latest` matrix;
`deploy.yml` attaches all of them to the GitHub release.

### Desktop icons

`etc/ic_launcher.svg` is the source of truth for Léon's mark on the desktop, the same as it is for
the Android launcher icon. `desktopApp/packaging/icons/leon.png`, `leon.ico` and `leon.icns` are
generated from it and committed, since jpackage needs one format per OS (`.png` for Linux, `.ico`
for Windows, `.icns` for macOS) and the build itself does no rasterizing. Regenerate and commit all
three whenever the SVG changes:

```bash
./desktopApp/packaging/icons/make-icons.sh
```

The script needs ImageMagick 7 (`magick`) and nothing else. Android's mipmaps under
`androidApp/src/main/res/mipmap-*/` are not covered — those stay Android Studio's Image Asset job.

## Secrets

Three files are encrypted with [git-secret](https://github.com/sobolevn/git-secret) and are
**not** in the repository in plain text:

| File                              | Used for                        |
|-----------------------------------|---------------------------------|
| `upload-keystore.jks`             | Signing the release bundle      |
| `signing.properties`              | Keystore credentials            |
| `google-play-service-account.json`| Google Play API authentication  |

Decrypt them once, with the GPG key of a maintainer imported:

```bash
git secret reveal -f
```

All three are git-ignored in their decrypted form, so they cannot be committed by accident.

## Fastlane Setup

Fastlane is a Ruby bundle. `Gemfile` declares it, `Gemfile.lock` pins the exact versions of it and
its ~100 dependencies, so everybody — and CI — runs the same fastlane.

Install it **into the project** rather than into the system gem home:

```bash
bundle config set --local path vendor/bundle
bundle update
```

`bundle config set --local` writes `.bundle/config`, so this is a one-off — every later
`bundle` command in this repository picks the path up on its own.

Installing into the project keeps the bundle out of the system gem home, which a package manager
such as Homebrew also manages. Writing into a gem home that is owned by a package manager tends to
fail outright on permissions, and where it succeeds it puts the packages the package manager
installed globally and the ones fastlane needs in each other's way. A project-local path avoids
that entirely, and matches what CI does. Both `/.bundle` and `/vendor/bundle` are git-ignored.

### Updating Fastlane

```bash
bundle update
git diff Gemfile.lock
```

Commit the resulting `Gemfile.lock`. Watch two blocks in the diff:

- `BUNDLED WITH` — becomes whatever bundler you ran locally. CI installs the bundler named here,
  so a jump to a new major version is a deliberate decision, not a side effect.
- `PLATFORMS` — bundler may add your machine's platform (for example `arm64-darwin-25`). CI runs
  on `x86_64-linux`, which must stay in the list.

## Releasing

Releases are automated. Pushing a `v*` tag triggers
[`.github/workflows/deploy.yml`](.github/workflows/deploy.yml), which decrypts the secrets and runs

```bash
bundle exec fastlane deploy
```

That lane reads `versionCode` out of `androidApp/build.gradle.kts`, builds `assembleRelease` and
`bundleRelease`, and uploads the AAB to the **production** track as a completed release, together
with the store listing screenshots.

The lane deliberately skips metadata, changelogs and the icon/feature graphic
(`skip_upload_metadata`, `skip_upload_changelogs`, `skip_upload_images`); those are maintained in
the Play Console.

### Releasing after the KMP migration

The [KMP migration](https://github.com/svenjacobs/leon) renamed the Android application module
from `app` to `androidApp`. Play Store and F-Droid builds both keep working — Fastlane and the two
workflows were repointed at `androidApp/` — with **one exception that needs a manual step before
the next tag**: F-Droid's build recipe, `metadata/com.svenjacobs.app.leon.yml` in the separate
[fdroiddata](https://gitlab.com/fdroid/fdroiddata) repository, still says `subdir: app`. Submit a
merge request there changing the **last** build block to `subdir: androidApp` before tagging the
first release after this migration — F-Droid's `AutoUpdateMode: Version` copies that last build
block for every new tag, so it keeps using the stale `subdir` (and fails to find a
`build.gradle.kts` there) until the recipe is edited.

The recipe's `prebuild: sed -i -e '/foojay/d' ../settings.gradle.kts` line needs no change: it
still strips the Foojay resolver plugin before F-Droid's offline build, and `gradle assembleRelease`
still only matches the `:androidApp` task — `shared` and `desktopApp` are never built there, so no
Skiko (Compose Desktop's rendering backend) binaries are ever downloaded on the F-Droid builder.

## Play Store Screenshots

### Where they live

Screenshots are **copied** into the folder layout supply expects, not referenced from
`docs/screenshots`:

```
fastlane/metadata/android/en-US/images/
├── phoneScreenshots/       # smartphone
├── sevenInchScreenshots/   # foldable
└── tenInchScreenshots/     # tablet
```

Google Play knows only `phoneScreenshots`, `sevenInchScreenshots`, `tenInchScreenshots`,
`tvScreenshots` and `wearScreenshots` — there is no foldable category, so the foldable screenshots
go into `sevenInchScreenshots`.

Each folder holds the same six images, ordered as light/dark pairs per screen:

```
01_main_light.png      04_history_dark.png
02_main_dark.png       05_settings_light.png
03_history_light.png   06_settings_dark.png
```

supply uploads the files of a folder in plain lexical order, which is what the numeric prefix is
for — it *is* the order they appear in on the store listing. Keep the prefixes zero-padded and
contiguous when adding or removing a screen.

The settings-detail screenshots in `docs/screenshots` are intentionally **not** part of the
listing.

### Updating them

1. Take the new screenshots and put them in `docs/screenshots`, keeping the
   `<mode>-<device>-<n>-<screen>.png` naming used there. That folder is the source of truth for
   the README.
2. Copy them into the three `images/` folders above under their `NN_<screen>_<mode>.png` names.
3. Upload them without publishing a release:

   ```bash
   bundle exec fastlane screenshots
   ```

For each device type that has local images, supply **deletes every screenshot Google Play currently
holds for that type** and then uploads the local ones. Device types with no local folder are left
untouched, as is everything else in the listing.

Do a dry run first — it validates the whole edit against Google Play and never commits it:

```bash
SUPPLY_VALIDATE_ONLY=true bundle exec fastlane screenshots
```

Once the screenshots are live and only one of them changes, `SUPPLY_SYNC_IMAGE_UPLOAD=true`
compares SHA-256 checksums and re-uploads only what actually differs.

### Requirements and gotchas

Google Play's
[asset requirements](https://support.google.com/googleplay/android-developer/answer/9866151)
for screenshots: 24-bit PNG without alpha or JPEG, up to 8 MB each, each side between 320 px and
3840 px, and the longer side at most twice the shorter one.

The current phone screenshots are 1280 × 2856, a ratio of 2.23:1 and therefore over that last
limit — as were the 1080 × 2280 ones before them. Modern phones are simply this tall. If an
upload is ever rejected for it, pad the phone images to 1428 × 2856.

Two more things to keep in mind:

- **`More than one release found in this track`** — the `screenshots` lane looks up the production
  track's current release even though it does not touch it. If production holds more than one
  release (a staged rollout next to the live one, say), pass the live `version_code` explicitly.
- **The `de-DE` listing has no `images/` folder**, so supply never touches its screenshots. The
  screenshots show an English UI, which is why they are only maintained under `en-US`.
