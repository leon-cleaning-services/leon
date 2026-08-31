# Léon – Copilot Instructions

## Project Overview

**Léon – The URL Cleaner** is an Android application (minSdk 23, Kotlin) that removes tracking
and other unwanted parameters from URLs before sharing. It integrates into Android's standard
sharing mechanism and is also meant as a blueprint for modern Android development.

## Module Structure

```
leon/
├── app/                  # Android application module (UI, DI bootstrap)
└── core-domain/          # URL model, sanitizer catalog, Cleaner
```

- **`app`** – Activities, Jetpack Compose screens, ViewModels, DataStore managers, and
  `ContainerInitializer` (plugs the app's `SanitizerRepository` into `DomainContainer`).
- **`core-domain`** – Everything about cleaning a URL: the `Url` model, `Match`, `Rule`, `Change`,
  `Cleaner`, and the sanitizer catalog under
  `com.svenjacobs.app.leon.core.domain.sanitizer.catalog/`.

`core-domain` contains **no Android and no `java.*` API** and depends on no service locator, so it
can be lifted out into a standalone Kotlin library — for a command line cleaner, for example. Keep
it that way; this check must stay empty:

```bash
grep -rn "^import android\|^import androidx\|^import java\.\|^import javax\." core-domain/src/main
```

## How Cleaning Works

A sanitizer never modifies a URL. It **describes** what it would do, and `Cleaner` applies
the description. That is what lets the UI list every proposed change and let the user decline it.

- `Url` – the parsed URL (scheme, host, port, path, parameters, fragment). Nothing is normalized, so
  `Url.parse(s).toString() == s`.
- `Sanitizer` – a **data class**, not an interface: `match` says which URLs it applies to, `rules`
  say what it would do to them.
- `Change` – one proposed modification, carrying the id of the sanitizer that proposed it.
- `Cleaner` – parses each URL out of the text, collects the changes, applies the ones the
  user has not declined, and serializes the result.

## Commit Messages & PR Titles

Commits and PR titles **must** follow the
[Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) specification.

Format: `<type>[optional scope]: <description>`

Common types: `feat`, `fix`, `docs`, `chore`, `refactor`, `test`, `style`, `perf`, `ci`.

Examples:

- `feat(sanitizer): add Google Analytics sanitizer`
- `fix(cleaner): handle URLs without query parameters`
- `docs: update README with new configuration options`

The **PR title must be identical** to the commit message that implements the requested changes.

## Pull Request Labels

Every pull request **must** have exactly one label that reflects the nature of the change:

| Label           | When to use                                                      |
|-----------------|------------------------------------------------------------------|
| `feature`       | A new feature or enhancement                                     |
| `bug`           | A bug fix                                                        |
| `chore`         | Maintenance tasks, dependency updates, build changes, CI changes |
| `documentation` | Documentation-only changes                                       |
| `refactor`      | Code refactoring without behavior changes                        |

## Pull Request State

After all work is done, the pull request **must** be marked as **"ready for review"**. This signals
that the implementation is complete and the PR is ready for human review.

## Pull Request Description

Keep the description **concise**. It tells a reviewer what changed and why — it does not restate
the diff.

- Start with a **TL;DR** section of at most three sentences. If the whole description is that short
  anyway, drop the heading and just write those sentences.
- Add further sections only when they carry information the TL;DR cannot, for example notable
  implementation decisions, trade-offs, follow-ups or screenshots for UI changes.
- If the pull request contains testable functionality, add a **Testing** section that explains what
  needs to be tested and how: which URL to share, which screen to open, which Gradle task to run.
- Omit the Testing section for changes that cannot be verified by hand, such as documentation-only
  or build configuration changes.

Example:

````markdown
## TL;DR

Adds a sanitizer that removes `example_` tracking parameters from `example.com` URLs.

## Testing

1. Share `https://www.example.com/path?example_ref=abc&keep=123` with Léon.
2. The cleaned URL must no longer contain `example_ref`, while `keep=123` is preserved.
3. Run `./gradlew :core-domain:test`.
````

## Closing Keywords

If a pull request implements a feature request or fixes a bug that originates from a GitHub issue,
include
a [closing keyword](https://docs.github.com/en/get-started/writing-on-github/working-with-advanced-formatting/using-keywords-in-issues-and-pull-requests)
in the PR description so the issue is automatically closed when the PR is merged.

Supported keywords: `Closes`, `Fixes`, `Resolves` (case-insensitive).

Example PR description:

```
Closes #42
```

or inline:

```
This PR adds support for removing `utm_` parameters from all URLs.

Closes #42
```

## Code Review

When reviewing a pull request, every **review comment**, **summary comment** and **reply** to a
remark **must** end with a note stating that it was written by an AI agent, naming the harness and
the model used.

Put the note on its own last line, in italics:

````markdown
The regex also matches `example_` inside the path, not just in the query string. Consider anchoring
it to `?` or `&`.

_Written by Claude Code (Claude Opus 5)_
````

Always name the harness and model actually in use — the line above is only an example.

## Code Style

- **Language**: Kotlin only.
- **Indentation**: spaces (size 4), as configured in `.editorconfig`.
- **Formatting**: enforced by [Spotless](https://github.com/diffplug/spotless)
  (`./gradlew spotlessCheck` / `./gradlew spotlessApply`). Code style is `kotlinlang`.
- **Trailing commas**: allowed (and preferred) on both declaration and call sites.
- **License header**: every `.kt` file must start with the GPL-3.0 license header (see below).

### License Header

```kotlin
/*
 * Léon - The URL Cleaner
 * Copyright (C) <year> Sven Jacobs
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
```

## Adding a New Sanitizer

A sanitizer is data. There is no class to write and no string resource to add.

### 1. Add the entry

Add a `val` to the vendor's file in
`core-domain/src/main/kotlin/com/svenjacobs/app/leon/core/domain/sanitizer/catalog/`, creating
`<Vendor>.kt` if it does not exist yet:

```kotlin
val ExampleSanitizer =
    Sanitizer(
        id = SanitizerId("example"),
        name = "Example",
        rules = persistentListOf(Rule.RemoveParameters("example_.*")),
        // Omit `match` entirely when the sanitizer applies to every URL:
        match = persistentListOf(Match(HostMatch.Domain("example.com"))),
    )
```

`name` is the English display name. Brand names are shown as they are; for a *descriptive* name that
should be translated, add a string resource to `app/src/main/res/values*/strings.xml` and an entry to
`TRANSLATED_NAMES` in `app/src/main/kotlin/com/svenjacobs/app/leon/sanitizer/SanitizerNames.kt`.

**`match` — which URLs the sanitizer applies to** (a URL matching *any* entry is sanitized):

- `Match()` – every URL. This is the default, so leave `match` out.
- `Match(HostMatch.Domain("example.com"))` – exactly that host, an optional `www.` aside. It does
  **not** cover subdomains: if the site serves URLs from `shop.example.com`, this silently never
  fires there.
- `Match(HostMatch.Subdomains("example.com"))` – the host and every subdomain of it. Prefer this
  whenever the site uses subdomains at all.
- `Match(HostMatch.Pattern("(?:open\\.)?example\\.com"))` – a regex matching the **complete** host.
- Any of them takes `pathPrefix = "/redirect"` to additionally require a path.

Leaving `match` out is right for a **tracking product** rather than a site: Google Analytics
(`utm_*`), Webtrekk (`wt_*`), Adobe Marketo (`mkt_*`), Salesforce (`sfmc_*`) and session ids all
appear on other people's sites, and that is exactly the point of them.

The test for whether that is safe is the *parameter names*, not the vendor. Those sanitizers can
apply everywhere because their names are vendor-namespaced — nobody else's site uses `utm_source` or
`wt_mc` to mean something of its own. A sanitizer whose parameters are ordinary words (`from`,
`source`, `channel`, `id`, `ref`) must carry a `match`, however famous the vendor is: without one it
strips those from every URL on the internet, and the damage is invisible until somebody shares an
unrelated link that happened to use one of those words.

Remove only the parameters that **belong to this sanitizer's own vendor**. `utm_*` is Google
Analytics's and is already handled by `GoogleAnalyticsSanitizer`; repeating it in another
sanitizer's rules is duplication, not thoroughness.

**`rules` — what it would do:**

- `Rule.RemoveParameters("utm_.*")` – removes every parameter whose name the regex matches
  completely; `negate = true` removes everything *but* those.
- `Rule.RemoveEmptyParameters` – removes parameters with an empty value.
- `Rule.RemoveFragment("Echobox=.*")` – removes the fragment, optionally only a matching one.
- `Rule.RewriteHost(pattern, replacement)` / `Rule.RewritePath(pattern, replacement)` – rewrite a
  component when the pattern matches it completely; `$1`, `$2` … reference its groups. Pass
  `from = Source.Path` to build one part of the URL out of another, as
  `open.substack.com/pub/<publication>` needs.
- `Rule.Follow(from, steps)` – replaces the URL with the one hidden in `from`, after running it
  through `steps`. This covers every redirect wrapper, however deeply it buries its target:
  - `from` is `Source.Parameter("url|q")`, `Source.Path`, `Source.Host` or `Source.Fragment`.
  - `steps` are `Decode.Capture(pattern, replacement)` (the replacement defaults to the first
    group, and may build a whole URL out of it), `Decode.PercentDecode`, `Decode.Base64Decode`
    and `Decode.JsonField(key)`, applied in order.
  - `dropParameters = true` discards the query of the *target*, for wrappers which append their
    tracking to it rather than to themselves.

Regexes are held as **strings**, never as `Regex`, so that the catalog stays plain data.

### 2. Register it

Add the `val` to `AllSanitizers` in `catalog/Catalog.kt`, keeping the list alphabetical.

A `SanitizerId` is persisted — it is the DataStore key (`sanitizer_<id>`) behind the switch in
Settings. **Never change or reuse the id of an existing sanitizer**: renaming one silently resets
whether the user had it turned off, and reusing one inherits that setting.

## Unit Tests

Tests live in the corresponding `src/test` source set, mirroring the production package structure.

- **Framework**: [Kotest](https://kotest.io/) with `WordSpec` style.
- **Assertions**: `io.kotest.matchers.shouldBe`.
- **No Android framework** dependencies in `core-domain` tests – sanitizers are plain data.
- A sanitizer's test extends `SanitizerSpec`, which wires up a `Cleaner` holding nothing but that
  one sanitizer and exposes `clean(url)` and `matches(url)`. Going through the `Cleaner` is what
  makes the test meaningful: a sanitizer is data, and the `Cleaner` is what interprets it — so
  `match` is honoured, exactly as it is in the app.

### Test template

```kotlin
package com.svenjacobs.app.leon.core.domain.sanitizer.example

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.ExampleSanitizer
import io.kotest.matchers.shouldBe

class ExampleSanitizerTest :
    SanitizerSpec(
        ExampleSanitizer,
        {
            "clean" should {

                "remove example_ parameters" {
                    clean(
                        "https://www.example.com/path?example_ref=abc&keep=123",
                    ) shouldBe "https://www.example.com/path?keep=123"
                }
            }

            // Include this block only when `match` is given:
            "matches" should {

                "match example.com" { matches("https://example.com") shouldBe true }

                "not match a lookalike host" {
                    matches("https://example.com.evil.com") shouldBe false
                }
            }
        },
    )
```

Every new sanitizer **must** have a corresponding `*Test` class that covers:

1. Cleaning at least one realistic URL.
2. `matches` (positive *and* negative cases) whenever `match` is given. Include a lookalike host
   such as `example.com.evil.com` among the negative cases, and a real subdomain of the site if it
   serves URLs from one — `Domain` does not cover subdomains, `Subdomains` does.

## After Generating Code

After generating or modifying any Kotlin code, always run the formatter to ensure consistent code
style:

```bash
./gradlew spotlessApply
```

## Running Tests & Lint

```bash
# Run unit tests for the core-domain module
./gradlew :core-domain:test

# Lint (check formatting)
./gradlew spotlessCheck

# Auto-format
./gradlew spotlessApply
```
