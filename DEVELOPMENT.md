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

Desktop data follows the XDG Base Directory Specification on Linux, and the equivalent platform
conventions on macOS and Windows, split into a data directory (the Room database) and a config
directory (the DataStore preference files):

- **Data**: `%LOCALAPPDATA%\Leon` on Windows, `~/Library/Application Support/Leon` on macOS,
  `$XDG_DATA_HOME/leon` (falling back to `~/.local/share/leon`) on Linux.
- **Config**: `%APPDATA%\Leon` on Windows, `~/Library/Application Support/Leon` on macOS (same
  directory as data - macOS keeps app-managed files together), `$XDG_CONFIG_HOME/leon` (falling
  back to `~/.config/leon`) on Linux.

The Linux data path is unchanged from before per-OS data directories existed, so existing Linux
installs keep their database; on first run the app moves the preference files that used to live
alongside it into the new config directory.

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

Renovate's weekly lock file maintenance does the same (see [Dependency Updates](#dependency-updates)),
so the same two blocks are worth a look in its pull requests.

## Dependency Updates

[Renovate](https://docs.renovatebot.com/) keeps the dependencies up to date, configured in
[`renovate.json5`](renovate.json5). Every Monday before 4 AM (UTC) it opens
pull requests for the Gradle version catalog and plugins, the Gradle wrapper, GitHub Actions, the
`Gemfile.lock` and the `flake.lock`. Every entry in `libs.versions.toml` sharing a `version.ref`
moves in one pull request. Its Dependency Dashboard issue lists every pending and ignored update,
and lets you trigger one ahead of schedule.

Renovate does not regenerate `aboutlibraries.json`. When one of its pull requests changes a
dependency the app ships, check out the branch and regenerate it (see
[AGENTS.md](AGENTS.md#gotchas-from-the-kmp-migration)) before merging.

## Releasing

Releases are driven by [release-please](https://github.com/googleapis/release-please)
([`.github/workflows/release-please.yml`](.github/workflows/release-please.yml), configured in
`release-please-config.json` and `.release-please-manifest.json`). On every push to `main` it keeps
a release pull request open, collecting the commits since the last release. Every conventional
commit type (`feat`, `fix`, `perf`, `refactor`, `docs`, `test`, `chore`/`ci`/`build`) opens or
updates that PR.

The release PR bumps `versionName` in [`version.properties`](version.properties); the workflow's
`bump-version-code` job then increments `versionCode` on the PR branch. Only the major number
counts: the app shows a plain `versionName` (`69`, `70`, …), while releases and tags carry the semver
form release-please insists on (`v70.0.0`). Never edit `version.properties` by hand.

To release, **squash-merge** the release PR. release-please then creates the GitHub release and its
`vXX.0.0` tag, and hands it to [`.github/workflows/deploy.yml`](.github/workflows/deploy.yml), a
reusable workflow whose jobs run like this:

```
package-desktop ─┐
build-android ─┬─┴─> github-release
               └───> play-store
```

`github-release` attaches the APKs, the AAB and the desktop packages to it. `deploy.yml`
can also be started by hand (`workflow_dispatch`) with a `tag` input, for re-running a release from
its release commit.

release-please finds the previous release by that tag, so never delete or rename a `vXX.0.0` tag:
the next release PR would list every change since the one before it again. (`v68` and older, and
`v69`, predate this scheme; `v69.0.0` was added afterwards as the starting point.)

The release PR only runs CI if it is created with a token other than `GITHUB_TOKEN`, so the
workflow mints a short-lived token from a GitHub App owned by the organization, installed on this
repository only, with Contents, Pull requests and Issues read/write permissions and no webhook. The
repository needs its Client ID as the variable `RELEASE_APP_CLIENT_ID` and a private key of it as
the secret `RELEASE_APP_PRIVATE_KEY`.

`build-android` decrypts the signing secrets and runs `assembleRelease bundleRelease`, in parallel
with the desktop matrix. `play-store` picks up the resulting AAB and runs

```bash
bundle exec fastlane deploy
```

That lane reads `versionCode` out of `version.properties` and uploads the AAB to the **production**
track as a completed release, together with the store listing screenshots. It builds the AAB itself
when run locally; on CI, `SKIP_GRADLE_BUILD=true` tells it to use the one `build-android` already
produced.

The GitHub release does not wait for the Play Store upload — it needs only the two artifact jobs.

The lane deliberately skips the store listing texts, changelogs and the icon/feature graphic
(`skip_upload_metadata`, `skip_upload_changelogs`, `skip_upload_images`). The texts are uploaded by
the `store_listing` lane instead (see [Play Store Listing](#play-store-listing)); changelogs and
the icon/feature graphic are maintained in the Play Console.

### F-Droid

F-Droid's build recipe, `metadata/com.svenjacobs.app.leon.yml` in the separate
[fdroiddata](https://gitlab.com/fdroid/fdroiddata) repository, uses `UpdateCheckMode: Tags` and
`AutoUpdateMode: Version`, so every new `vXX.0.0` tag becomes a new F-Droid build. Because the version
lives in `version.properties` and no longer in `androidApp/build.gradle.kts`, the recipe needs

```yaml
UpdateCheckData: version.properties|versionCode=(\d+)|.|versionName=(\d+)
```

Without `UpdateCheckData`, `checkupdates` looks for `versionCode` in `build.gradle.kts` and silently
ignores new tags. Tags from before `version.properties` existed are simply skipped.

The recipe's `prebuild: sed -i -e '/foojay/d' ../settings.gradle.kts` line needs no change: it
still strips the Foojay resolver plugin before F-Droid's offline build, and `gradle assembleRelease`
still only matches the `:androidApp` task — `shared` and `desktopApp` are never built there, so no
Skiko (Compose Desktop's rendering backend) binaries are ever downloaded on the F-Droid builder.

## Play Store Listing

Everything the `store_listing` lane uploads lives in `fastlane/metadata/android/`. Upload it without
publishing a release:

```bash
bundle exec fastlane store_listing
```

Do a dry run first — it validates the whole edit against Google Play and never commits it:

```bash
SUPPLY_VALIDATE_ONLY=true bundle exec fastlane store_listing
```

The lane uploads the texts of every language and the screenshots, but never the icon, the feature
graphic or changelogs.

### Texts

Every language the app is translated into has a folder named by its **Google Play** language code,
which is not always the Android one:

| Folder  | App resources | Language             |
|---------|---------------|----------------------|
| `en-US` | `values`      | English (default)    |
| `de-DE` | `values-de`   | German               |
| `ja-JP` | `values-ja`   | Japanese             |
| `pl-PL` | `values-pl`   | Polish               |
| `ru-RU` | `values-ru`   | Russian              |
| `vi`    | `values-vi`   | Vietnamese           |
| `zh-CN` | `values-zh`   | Chinese (Simplified) |

Each folder holds the following files, within Google Play's limits:

| File                    | Limit           |
|-------------------------|-----------------|
| `title.txt`             | 30 characters   |
| `short_description.txt` | 80 characters   |
| `full_description.txt`  | 4000 characters |

The title is the app's own `scaffold_title` string in that language. When the English text
changes, update the other languages with it — here, or on Weblate, which also creates the folder of
a new language (see [Translations](#translations)). F-Droid reads the same folders for its listing.

supply replaces the texts of every language that has a folder. A missing file leaves that field
as it is on Google Play, and a language without a folder is not touched at all — deleting a folder
does **not** remove the translation from the store listing; that has to be done in the Play
Console.

### Screenshots

#### Where they live

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

#### Updating them

1. Take the new screenshots and put them in `docs/screenshots`, keeping the
   `<mode>-<device>-<n>-<screen>.png` naming used there. That folder is the source of truth for
   the README.
2. Copy them into the three `images/` folders above under their `NN_<screen>_<mode>.png` names.
3. Upload them with the `store_listing` lane (see [Play Store Listing](#play-store-listing)).

For each device type that has local images, supply makes Google Play hold **exactly the local
screenshots, in their order**. Both lanes set `sync_image_upload: true`, so supply compares SHA-256
checksums: it keeps the remote screenshots that match the local ones in sequence, and deletes and
re-uploads everything from the first difference on. Changing `03_history_light.png` therefore
re-uploads screenshots 3 to 6, and unchanged screenshots are not uploaded at all — which also means
the `deploy` lane only touches them when they actually changed. Google Play stores the uploaded
files byte for byte, so the checksums do match. Device types with no local folder are left
untouched.

#### Requirements and gotchas

Google Play's
[asset requirements](https://support.google.com/googleplay/android-developer/answer/9866151)
for screenshots: 24-bit PNG without alpha or JPEG, up to 8 MB each, each side between 320 px and
3840 px, and the longer side at most twice the shorter one.

The current phone screenshots are 1280 × 2856, a ratio of 2.23:1 and therefore over that last
limit — as were the 1080 × 2280 ones before them. Modern phones are simply this tall. If an
upload is ever rejected for it, pad the phone images to 1428 × 2856.

Two more things to keep in mind:

- **`More than one release found in this track`** — the `store_listing` lane looks up the
  production track's current release even though it does not touch it. If production holds more
  than one release (a staged rollout next to the live one, say), pass the live `version_code`
  explicitly.
- **Only `en-US` has an `images/` folder**, so supply never touches the screenshots of any other
  language, and Google Play shows the English ones there. The screenshots show an English UI,
  which is why they are only maintained under `en-US`.

## Translations

Translations have two sources, and Weblate is not the only one:

- **This repository.** A change that adds or rewrites a string still translates it into the
  existing languages, as it always has (see [AGENTS.md](AGENTS.md)).
- **[Hosted Weblate](https://hosted.weblate.org/projects/leon-url-cleaner/)** (project
  `leon-url-cleaner`, gratis under its Libre plan), where translators improve the existing
  languages and add new ones.

English is the source language and is only ever edited in this repository; Weblate does not let
translators change it. Weblate is connected through the Hosted Weblate GitHub App, installed on
the `leon-cleaning-services` organization with access to this repository only. It tells Weblate
about every push to `main`, so a string added here shows up there straight away; no repository
webhook is needed.

### Components

| Component    | Files                                                         | Weblate file format             |
|--------------|---------------------------------------------------------------|---------------------------------|
| `app`        | `shared/src/commonMain/composeResources/values-*/strings.xml` | Compose Multiplatform Resource  |
| `android`    | `androidApp/src/main/res/values-*/strings.xml`                | Android String Resource         |
| `play-store` | `fastlane/metadata/android/*`                                 | App store metadata files        |

`app` owns the clone of this repository and uses the "GitHub (via Weblate GitHub app)" backend; the
other two are linked to it (`weblate://leon-url-cleaner/app`) and take its repository, branch and
push settings. All three are licensed GPL-3.0-or-later.

Things that are easy to get wrong when changing the setup:

- **`app` must stay "Compose Multiplatform Resource"**, never "Android String Resource". The
  Android format escapes `'` and `"` with a backslash, which Compose resources print literally
  (see the KMP gotchas in [AGENTS.md](AGENTS.md#gotchas-from-the-kmp-migration)).
- **Language aliases** on the project (`en_US:en,de_DE:de,ja_JP:ja,pl_PL:pl,ru_RU:ru,zh:zh_Hans,zh_CN:zh_Hans`)
  merge the Play Store's `de-DE` with the app's `de`, so each language is one language on Weblate
  rather than two.
- **Language code styles** decide how Weblate names the files of a new language: `android` for
  `app` and `android` (`values-pt-rBR`), `googleplay` for `play-store` (`pt-BR`). A new language
  needs its `androidApp` folder as well — `generateLocaleConfig` builds the in-app language list
  from it.
- The Play Store source strings carry `max-length:30`, `max-length:80` and `max-length:4000`
  flags, Google Play's limits for the title, short and full description.

### Pull requests from Weblate

Weblate pushes translations through the GitHub App and opens a pull request against `main`. Every
message template starts with `chore(i18n):`, and the "Squash Git commits" add-on squashes all
pending changes into one commit named `chore(i18n): update translations from Weblate` — the same
as the pull request title. The `app` component's "Pull request labels" setting
(`merge_request_labels`) gives every pull request the `chore` label.

Squash-merge them like every other pull request.

Because both sources edit the same files, Weblate rebases its pending translations onto every push
to `main`. That only fails when both changed the *same* string; Weblate then stops pushing and shows
a merge-conflict alert on the project. To avoid it, merge an open Weblate pull request before
changing that language's existing translations here; adding new strings rarely conflicts.
