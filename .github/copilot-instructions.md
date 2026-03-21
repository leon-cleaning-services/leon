# Léon – Copilot Instructions

## Project Overview

**Léon – The URL Cleaner** is an Android application (minSdk 23, Kotlin) that removes tracking
and other unwanted parameters from URLs before sharing. It integrates into Android's standard
sharing mechanism and is also meant as a blueprint for modern Android development.

## Module Structure

```
leon/
├── app/                  # Android application module (UI, DI bootstrap)
├── core-domain/          # Business logic: sanitizers, CleanerService, domain interfaces
└── core-common/          # Shared utilities: RegexFactory, domain extension functions
```

- **`app`** – Activities, Jetpack Compose screens, ViewModels, DataStore managers, and
  `ContainerInitializer` (registers all sanitizers in `DomainContainer`).
- **`core-domain`** – All `Sanitizer` implementations live here under
  `com.svenjacobs.app.leon.core.domain.sanitizer.<name>/`. Also contains `CleanerService` and the
  `SanitizerRepository` interface.
- **`core-common`** – `RegexFactory` and `matchesDomain`/`matchesDomainRegex` extension functions
  used by sanitizers.

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

### 1. Create the sanitizer class

Create a new package inside `core-domain`:

```
core-domain/src/main/kotlin/com/svenjacobs/app/leon/core/domain/sanitizer/<name>/
```

Extend `RegexSanitizer` for regex-based sanitizers:

```kotlin
package com.svenjacobs.app.leon.core.domain.sanitizer.example

import android.content.Context
import com.svenjacobs.app.leon.core.common.regex.RegexFactory
import com.svenjacobs.app.leon.core.domain.R
import com.svenjacobs.app.leon.core.domain.sanitizer.RegexSanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class ExampleSanitizer :
    RegexSanitizer(
        regex = RegexFactory.ofWildcardParameter("example_"),
    ) {

    override val id = SanitizerId("example")

    override fun getMetadata(context: Context) = Sanitizer.Metadata(
        name = context.getString(R.string.sanitizer_example_name),
    )

    // Override matchesDomain only when the sanitizer is domain-specific:
    override fun matchesDomain(input: String) = input.matchesDomain("example.com")
}
```

**`RegexFactory` helpers:**

- `RegexFactory.AllParameters` – matches everything from `?` onwards (removes all parameters).
- `RegexFactory.ofParameter("foo")` – matches exact parameter `foo`.
- `RegexFactory.ofWildcardParameter("foo_")` – matches any parameter starting with `foo_`.
- `RegexFactory.exceptParameter("(a|b)")` – matches all parameters *except* `a` or `b`
  (use `|` for alternation).

**`matchesDomain` helpers** (from `core-common`):

- `input.matchesDomain("example.com")` – exact domain match.
- `input.matchesDomainRegex("(open\\.)?example\\.com")` – regex domain match.

When `matchesDomain` is **not** overridden the sanitizer applies to **all** URLs.

### 2. Add a string resource

Add a string entry to `core-domain/src/main/res/values/strings.xml`:

```xml

<string name="sanitizer_example_name" translatable="false">Example</string>
```

Use `translatable="false"` for brand names; omit (or set `translatable="true"`) for descriptive
names that should be localised.

For every string marked as translatable, also add the corresponding translated entry to **all**
existing locale-specific `strings.xml` files. The `core-domain` module currently has translations
for `values-de/` (German) and `values-ru/` (Russian). The `app` module currently has translations
for `values-de/` (German), `values-pl/` (Polish), `values-ru/` (Russian), and `values-vi/`
(Vietnamese).

### 3. Register the sanitizer

Add an instance of the new sanitizer to the `persistentListOf(…)` in
`app/src/main/kotlin/com/svenjacobs/app/leon/startup/ContainerInitializer.kt`.

## Unit Tests

Tests live in the corresponding `src/test` source set, mirroring the production package structure.

- **Framework**: [Kotest](https://kotest.io/) with `WordSpec` style.
- **Assertions**: `io.kotest.matchers.shouldBe`.
- **No Android framework** dependencies in `core-domain` tests – sanitizers are plain Kotlin
  objects.

### Test template

```kotlin
package com.svenjacobs.app.leon.core.domain.sanitizer.example

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class ExampleSanitizerTest :
    WordSpec(
        {
            val sanitizer = ExampleSanitizer()

            "invoke" should {

                "remove example_ parameters" {
                    sanitizer(
                        "https://www.example.com/path?example_ref=abc&keep=123",
                    ) shouldBe "https://www.example.com/path&keep=123"
                }
            }

            // Include this block only when matchesDomain is overridden:
            "matchesDomain" should {

                "match example.com" {
                    sanitizer.matchesDomain("https://example.com") shouldBe true
                }

                "not match other.com" {
                    sanitizer.matchesDomain("https://other.com") shouldBe false
                }
            }
        },
    )
```

Every new sanitizer **must** have a corresponding `*Test` class that covers:

1. The `invoke` function with at least one realistic URL.
2. The `matchesDomain` function (positive *and* negative cases) whenever it is overridden.

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

# Run unit tests for the core-common module
./gradlew :core-common:test

# Lint (check formatting)
./gradlew spotlessCheck

# Auto-format
./gradlew spotlessApply
```
