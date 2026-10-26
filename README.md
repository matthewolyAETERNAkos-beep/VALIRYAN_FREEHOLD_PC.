# VALIRYAN FREEHOLD

A starter native Android browser project built with Kotlin and Android WebView. It has a custom dark/ivory home screen, address/search bar, basic navigation, download handoff to Android DownloadManager, PDF opening through an installed viewer, theme toggle, basic private-session toggle, and placeholder DIY/bookmark flows.

## Build

Requirements: Android Studio (current stable), Android SDK Platform 35, JDK 17.

1. Extract this folder.
2. Open the `VALIRYAN-FREEHOLD` folder in Android Studio.
3. Allow Gradle sync and install any requested SDK components.
4. Run on an Android device or emulator (min SDK 26 / Android 8.0).
5. To build an APK: **Build → Build Bundle(s) / APK(s) → Build APK(s)**. Android Studio will show the output path.
6. For GitHub: create a repository and upload the project. The included workflow installs Gradle and attempts a debug APK build on pushes and pull requests. This starter ZIP does not include the generated Gradle wrapper JAR/scripts; Android Studio/CI uses installed Gradle.

## Feature status (read carefully)

- **Native Android WebView browsing:** implemented as a basic single-WebView browser.
- **Downloads:** ordinary downloadable links are handed to Android `DownloadManager`. This is not a general-purpose video extractor. Many streaming sites use segmented streams, DRM, signed URLs, or JavaScript players and cannot be downloaded by this simple listener.
- **PDF reader:** opens selected PDF files in an installed PDF app. An embedded PDF reader is not included.
- **Themes:** two basic themes are included.
- **DIY modules:** currently a design/format guide and placeholder catalog, not a live plugin runtime.
- **Bookmarks:** HTML picker is present, but HTML parsing and bookmark storage/import are not yet implemented.
- **Private tabs:** this starter has a basic private-session toggle, not a proper isolated multi-tab architecture. It clears some WebView data, but should not be considered strong anti-forensics.
- **Ad/tracker blocking:** NOT implemented. The app currently does not filter network requests. WebView alone does not provide Brave-equivalent blocking. A maintained content-blocking engine and filter-list update strategy must be integrated before advertising this as an ad-blocking browser.
- **Multiple tabs:** not implemented; the current “＋ Tab” control returns to the home page.

## Security notes

This is a starter project, not a Brave-equivalent production browser. Do not use it for sensitive browsing until private-session isolation, safe browsing, download validation, content filtering, certificate/error handling, and dependency updates are reviewed. It restricts page loads to HTTP/HTTPS and disables local file access from web content.

## Roadmap

1. Add a real tab model (one WebView per tab) with isolated private profiles.
2. Add persistent bookmarks/history and Netscape HTML bookmark import.
3. Integrate a maintained request/content filter engine and updateable filter lists.
4. Add a dedicated PDF renderer or library.
5. Add explicit, user-initiated video download support for sources where downloading is authorized and technically available.
6. Add JSON theme-pack import with schema validation and a trusted DIY module registry.
