# AndroidSync — Project Context for Claude Code

## What is this project?
A two-way bidirectional Android file sync app. Users select folders on their phone, and the app syncs them with **Google Drive** — including background auto-sync, recursive folder support, and conflict resolution.

## Architecture

**Single-module app** with clean package structure:

```
com.example.androidsync/
├── App.kt                          # Application class, Koin startup
├── MainActivity.kt                 # NavHost with Compose navigation
├── data/
│   ├── local/
│   │   ├── db/                     # Room database
│   │   │   ├── AppDatabase.kt     # DB class with migrations
│   │   │   ├── entity/            # SyncFolder, FileMetadata
│   │   │   └── dao/               # SyncFolderDao, FileMetadataDao
│   │   └── scanner/               # LocalFileScanner (SAF traversal)
│   └── repository/                # SyncFolderRepository, FileRepository
├── di/
│   └── AppModule.kt               # Koin DI module
└── ui/
    ├── screen/
    │   ├── folderlist/            # Folder list screen + ViewModel
    │   └── folderdetail/          # Folder detail screen + ViewModel
    └── theme/                     # Material3 theme, colors, typography
```

## Tech Stack
- **Kotlin 2.0.21** with Compose compiler plugin
- **Jetpack Compose** (BOM 2024.12.01) + **Material3** with dynamic color
- **Koin 4.0.2** for dependency injection
- **Room 2.6.1** with KSP 2.0.21-1.0.28 for persistence
- **Navigation Compose 2.8.5** for screen routing
- **DocumentFile / DocumentsContract** for SAF folder access
- compileSdk 35, minSdk 26, targetSdk 35, Java 17

## Key Patterns
- **MVVM** — ViewModels expose `StateFlow`, screens collect with `collectAsStateWithLifecycle()`
- **Repository pattern** — Repositories wrap DAOs and scanners, injected via Koin
- **SAF (Storage Access Framework)** — `ACTION_OPEN_DOCUMENT_TREE` with `takePersistableUriPermission()` for persistent folder access
- **DocumentsContract cursor queries** — used instead of `DocumentFile.listFiles()` for O(n) vs O(n²) performance
- **Room migrations** — explicit `Migration` objects in `AppDatabase.Companion`, registered in Koin via `.addMigrations()`
- **Foreign keys with CASCADE** — deleting a SyncFolder auto-deletes its FileMetadata entries
- **Koin DSL** — `singleOf()`, `viewModelOf()`, `androidContext()` for Context-dependent classes

## Database Schema (v2)
- **sync_folders** — id, uri, displayName, addedAt, isEnabled
- **file_metadata** — id, folderId (FK→sync_folders CASCADE), documentUri (unique), relativePath, fileName, sizeBytes, lastModified, mimeType, isDirectory

## Navigation Routes
- `"folders"` — FolderListScreen
- `"folders/{folderId}"` — FolderDetailScreen (Long arg)

## Build & CI
- Gradle 8.9 with version catalog (`gradle/libs.versions.toml`)
- GitHub Actions workflow: `.github/workflows/build.yml` — builds debug APK on push/PR, uploads artifact
- No Android SDK in this remote environment — builds only succeed in CI or local Android Studio

## Development Workflow
- **Always create a new branch off `main`** for each feature
- Branch naming: `feature/<descriptive-name>`
- Create PR to `main` when feature is complete
- `main` is the protected default branch

## Roadmap
See `ROADMAP.md` for the full 8-feature plan. Currently completed:
- Feature 1: Folder Selection & Persistence (PR #2)
- Feature 2: Local File Scanning & Indexing (PR #3)

Next up: Feature 3 (Google Drive Auth & API Access)

## Important Technical Notes
- **SAF URIs** are stored as strings in Room; always parse with `Uri.parse()` before use
- **FileObserver does NOT work with SAF** — use ContentObserver on tree URIs instead
- **Drive REST API v3** is required (not deprecated GDAA) because we need access to all files, not just app-created ones
- **Three-way sync anchors** will be needed for bidirectional sync to distinguish "new file" from "remotely deleted"
- **SHA-256 hashing** is deferred to Feature 5 (expensive, only needed for conflict detection)
- The `isDirectory` field in FileMetadata exists so Feature 4 can mirror folder structure on Drive
