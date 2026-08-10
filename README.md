# CraftLanka - Mobile Application

Welcome to the CraftLanka repository! This guide provides complete setup instructions, quality standards, build configurations, and contribution guidelines to ensure smooth development and prevent pipeline failures.

---

## Table of Contents
1. [Quick Start & Local Setup](#-quick-start--local-setup)
2. [Conventional Commits Standard](#-conventional-commits-standard)
3. [Code Formatting & Quality Checks](#-code-formatting--quality-checks)
4. [Known Build Configurations & Fixes](#-known-build-configurations--fixes)
5. [Git & PR Workflow](#-git--pr-workflow)

---

## Quick Start & Local Setup

### Prerequisites
* **Android Studio**: Jellyfish (2023.3.1) or newer
* **JDK**: Version 17 (Temurin recommended)
* **Android SDK**: `compileSdk = 35`, `minSdk = 24`

### Firebase Configuration (`google-services.json`)
The `app/google-services.json` file is omitted from Git version control for security.

* **For local development:** Obtain the official `google-services.json` file from the repository maintainer and place it directly inside the `app/` folder (`app/google-services.json`).
* **In GitHub Actions (CI):** The pipeline automatically checks for the `GOOGLE_SERVICES_JSON` repository secret. If absent, it automatically generates a mock config so build and test workflows complete without crashing.

---

## Conventional Commits Standard

All commit messages in this repository **must** follow the [Conventional Commits](https://www.conventionalcommits.org/) specification to ensure clear git history and automated changelog compatibility.

### Commit Format
```text
<type>(<scope>): <short summary>

[optional body]

# Conventional Commits Standard

All commit messages in this repository **must** follow the [Conventional Commits](https://www.conventionalcommits.org/) specification to ensure clear git history and automated changelog compatibility.

### Supported Types

| Type | Description | Example |
| :--- | :--- | :--- |
| `feat` | A new feature for the user | `feat(auth): add google sign-in button` |
| `fix` | A bug fix | `fix(deps): force core-ktx to 1.13.1` |
| `ci` | Changes to CI configuration files or scripts | `ci(devops): update android-ci workflow` |
| `chore` | Build process or auxiliary tool/library updates | `chore(init): setup gradle wrapper` |
| `docs` | Documentation-only changes | `docs(readme): add developer setup guide` |
| `style` | Formatting or lint fixes (no production code change) | `style(spotless): format kotlin files` |
| `refactor` | Code change that neither fixes a bug nor adds a feature | `refactor(ui): extract product card item` |
| `test` | Adding missing tests or correcting existing tests | `test(unit): add product repository tests` |

---

## Code Formatting & Quality Checks

This project uses **Spotless** integrated with **ktlint** (version `1.2.1`) to enforce official Kotlin coding standards.

### 1. Auto-Fix Formatting
Run the auto-formatter task in your terminal before committing to fix code style errors automatically:

```bash
./gradlew spotlessApply

### 2. Run Full CI Validation Locally

Run the exact check suite executed by GitHub Actions to confirm your branch will pass CI:

```bash
./gradlew spotlessCheck lintDebug testDebugUnitTest assembleDebug

## Known Build Configurations & Fixes

To prevent Android Gradle Plugin (AGP `8.5.2`) version check errors when targeting `compileSdk = 35`, the following configurations are enforced in the repository:

### 1. Locked `androidx.core` Dependencies

* **`gradle/libs.versions.toml`**: `coreKtx` is locked to **`1.13.1`**. Versions `1.16.0+` or `1.19.0+` require AGP `8.6.0+` / API 37 and will fail the build.
* **`app/build.gradle.kts`**: Force resolution strategy is enabled to prevent transitive dependencies from upgrading `androidx.core`:

```kotlin
configurations.all {
    resolutionStrategy {
        force("androidx.core:core:1.13.1")
        force("androidx.core:core-ktx:1.13.1")
    }
}
```

### Suppressed AAR Metadata Check

AGP 8.5.2 throws metadata compatibility failures when compiled against `compileSdk = 35`. This task is disabled in `app/build.gradle.kts`:

```kotlin
tasks.matching { it.name.contains("checkDebugAarMetadata") }.configureEach {
    enabled = false
}
```

## Git & PR Workflow

### 1. **Branch Creation**

Always create feature branches off `develop`:

```bash
git checkout develop
git pull origin develop
git checkout -b feature/your-feature-description
```

### 2. **Commit & Format**

Format your code and commit using Conventional Commits:

```bash
./gradlew spotlessApply
git add .
git commit -m "feat(ui): add product list layout"
```

### 3. **Push & Pull Request**

Push your branch and open a Pull Request targeting `develop`:

```bash
git push origin feature/your-feature-description
```