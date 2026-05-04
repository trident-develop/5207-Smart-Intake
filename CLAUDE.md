# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project status

This is a freshly-scaffolded Android Studio project. `MainActivity.onCreate` calls `setContent { SmartIntakeTheme { } }` with an **empty** content lambda — there is no UI, no domain code, and no architecture beyond the Compose theme files. Treat any feature work as greenfield: there are no established patterns in this repo to follow yet, so prefer Android/Compose idioms over inventing structure.

## Build & run

Gradle wrapper is checked in. Run everything from the repo root.

- Assemble debug APK: `./gradlew :app:assembleDebug`
- Install on a connected device/emulator: `./gradlew :app:installDebug`
- Unit tests (`app/src/test`, JUnit4): `./gradlew :app:testDebugUnitTest`
- Single unit test: `./gradlew :app:testDebugUnitTest --tests "com.xd.smartintake.ExampleUnitTest.addition_isCorrect"`
- Instrumented tests (`app/src/androidTest`, requires running device/emulator): `./gradlew :app:connectedDebugAndroidTest`
- Lint: `./gradlew :app:lintDebug` (report at `app/build/reports/lint-results-debug.html`)
- Clean: `./gradlew clean`

## Toolchain & versions (non-obvious)

Versions are pinned in `gradle/libs.versions.toml` (version catalog) — change them there, not inline in `build.gradle.kts`.

- **AGP 9.1.1 + Kotlin 2.2.10**: bleeding edge. AGP 9 uses the new `compileSdk { version = release(36) { minorApiLevel = 1 } }` DSL — do not "fix" it back to `compileSdk = 36`.
- **Kotlin Compose Compiler plugin** (`org.jetbrains.kotlin.plugin.compose`) is what enables Compose; there is no separate `composeOptions { kotlinCompilerExtensionVersion = ... }` block, and adding one will conflict.
- **Java 11** source/target — keep new code compatible.
- **minSdk 24, targetSdk 36, Compose BOM 2026.02.01.** All Compose libraries (`androidx.compose.ui`, `material3`, etc.) get their versions from the BOM, so add new Compose deps without a version.
- Plugin block uses `alias(libs.plugins.android.application)` / `alias(libs.plugins.kotlin.compose)` — only those two are applied. Adding kapt/ksp/Hilt/etc. requires both a catalog entry and an `alias(...)` here.

## Structure

Single-module project: root → `:app` (`app/build.gradle.kts`). Application id and namespace are both `com.xd.smartintake`.

- `app/src/main/java/com/xd/smartintake/MainActivity.kt` — entry point; `enableEdgeToEdge()` is called, so any future scaffolding needs to handle insets (use `Scaffold` + `innerPadding`, don't draw under system bars without applying `WindowInsets`).
- `app/src/main/java/com/xd/smartintake/ui/theme/` — `Theme.kt` (`SmartIntakeTheme`, dynamic color enabled by default on Android 12+), `Color.kt`, `Type.kt`. Wrap all Composables in `SmartIntakeTheme` for previews.
- `app/src/main/res/` — standard Android resources. App label is `@string/app_name`.

## Gemini / on-device AI tooling note

`local.properties` exists (gitignored) and may contain SDK paths or developer-specific keys; never commit it. There is no AI/ML SDK wired up yet despite the project name.
