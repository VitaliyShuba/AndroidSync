# AndroidSync — Feature Roadmap

A two-way bidirectional file sync Android app. Users select folders on their phone, and the app syncs them with Google Drive (including background auto-sync and conflict resolution).

## Progress

- [x] **Feature 1**: Folder Selection & Persistence
- [x] **Feature 2**: Local File Scanning & Indexing
- [ ] **Feature 3**: Google Drive Auth & API Access
- [ ] **Feature 4**: One-Way Upload Sync (Local → Drive)
- [ ] **Feature 5**: Two-Way Sync & Conflict Resolution
- [ ] **Feature 6**: Background Periodic Sync (WorkManager)
- [ ] **Feature 7**: Real-Time File Change Detection
- [ ] **Feature 8**: Sync Dashboard, Error Handling & Polish

---

## Feature 1: Folder Selection & Persistence — DONE (PR #2)

**User sees:** "Add Folder" button → system folder picker → selected folders listed on main screen with remove option. Persists across restarts.

**Technical:**
- SAF `ACTION_OPEN_DOCUMENT_TREE` + `takePersistableUriPermission()` for persistent access
- Room database with `SyncFolder` entity (URI, display name, enabled toggle)
- KSP plugin for Room annotation processing
- `FolderListScreen` + `FolderListViewModel`

**Packages:** `data/local/db/`, `data/repository/`, `ui/screen/folderlist/`

---

## Feature 2: Local File Scanning & Indexing — DONE (PR #3)

**User sees:** Tap a folder → navigates to detail screen showing all files. Files listed with name, size, and last modified date. "Scan" button triggers rescan with progress indicator. File count badges on folder cards.

**Technical:**
- `DocumentsContract` cursor queries for performant recursive SAF tree traversal (O(n), not O(n²))
- `FileMetadata` Room entity with ForeignKey CASCADE to SyncFolder
- Directories stored with `isDirectory = true` for future Drive folder mirroring
- Navigation Compose with `NavHost` (folder list → folder detail routes)
- Room v1→v2 migration

**Packages:** `data/local/scanner/`, `ui/screen/folderdetail/`

---

## Feature 3: Google Drive Auth & API Access

**User sees:** "Sign in with Google" button. After auth, shows connected account email + sign-out option. Verifies connection by listing Drive root.

**Technical:**
- Credential Manager (`androidx.credentials`) + Google Identity for sign-in
- Google Drive REST API v3 (`com.google.api-services-drive`) — required over deprecated GDAA since we need access to all files, not just app-created ones
- Token storage via EncryptedSharedPreferences
- INTERNET permission
- Requires Google Cloud Console OAuth 2.0 client ID setup (manual step)

**New packages:** `data/remote/auth/`, `data/remote/drive/`, `ui/screen/settings/`

**New dependencies:** google-api-services-drive, google-api-client-android, credential manager, google identity

---

## Feature 4: One-Way Upload Sync (Local → Drive)

**User sees:** "Sync Now" button on a folder. Uploads files to a mirrored "AndroidSync" folder on Drive. Progress bar + "Synced" status badges per file.

**Technical:**
- Drive folder mirroring — recreates local directory structure on Drive
  - `Drive.Files.create()` with `mimeType = "application/vnd.google-apps.folder"` for directories
  - `Drive.Files.create()` with `MediaContent` for files
- Resumable/chunked uploads via `MediaHttpUploader` for large files
- `SyncEngine` class: scan → diff → upload → update Room
- Add `driveFileId` + `driveModifiedTime` to `FileMetadata`
- Local is source of truth (no conflict handling yet)

**New packages:** `data/remote/drive/`, `data/sync/`, `data/sync/model/`

---

## Feature 5: Two-Way Sync & Conflict Resolution

**User sees:** Files added/modified/deleted on Drive sync back to phone. Conflicts show a dialog: "Keep local" / "Keep remote" / "Keep both". Default strategy configurable in settings.

**Technical:**
- **Three-way diff with sync anchors** — Room stores last-synced state (local + remote timestamps/hashes at time of last successful sync)
  - Only local changed → upload
  - Only remote changed → download
  - Both changed → conflict
  - Same hash → converged, no action
- Drive change detection via `Drive.Files.list()` with `modifiedTime`, `md5Checksum`, `trashed`
- Download via `Drive.Files.get(fileId).executeMediaAsInputStream()`
- Write to local via SAF `contentResolver.openOutputStream()`
- New files from Drive: `DocumentsContract.createDocument()` to create locally
- `ConflictResolutionDialog` composable
- DataStore for sync preferences (conflict strategy)
- **Deletion safety:** sync anchor distinguishes "remotely deleted" vs "new local file"
- SHA-256 content hashing computed lazily (only when timestamps suggest conflict)

**New packages:** `data/sync/conflict/`, `data/settings/`, `ui/components/`

**New dependencies:** DataStore preferences

---

## Feature 6: Background Periodic Sync (WorkManager)

**User sees:** App syncs automatically at configured interval (15min–4hrs) even when closed. Notification shows sync progress. Settings: interval, Wi-Fi only, battery constraints.

**Technical:**
- `PeriodicWorkRequestBuilder` with network/battery constraints
  - `NetworkType.CONNECTED` (or `UNMETERED` for Wi-Fi only)
  - `requiresBatteryNotLow(true)`
- `SyncWorker` (CoroutineWorker) runs SyncEngine for all enabled folders
- Foreground service promotion via `setForeground(ForegroundInfo(...))` for long syncs
- `android:foregroundServiceType="dataSync"` + `FOREGROUND_SERVICE_DATA_SYNC` permission
- `koin-androidx-workmanager` for DI in workers
- Settings: sync interval selector, Wi-Fi only toggle, master auto-sync on/off

**New packages:** `data/sync/worker/`, `data/sync/notification/`

**New dependencies:** koin-androidx-workmanager

---

## Feature 7: Real-Time File Change Detection

**User sees:** Modifying a file in any app triggers sync within seconds. Toast: "Change detected, syncing..." Toggleable in settings.

**Technical:**
- `ContentObserver` on SAF tree URIs (not FileObserver — doesn't work with SAF)
- `FileWatcherService` foreground service hosting observers
- Coroutine-based debounce (2-3s) to coalesce rapid change events
- `OneTimeWorkRequest` with `ExistingWorkPolicy.REPLACE` per folder
- Periodic sync (Feature 6) acts as safety net for unreliable ContentObserver on some OEMs
- **Limitation:** SAF ContentObserver is not perfectly reliable on all devices

**New packages:** `data/sync/watcher/`

---

## Feature 8: Sync Dashboard, Error Handling & Polish

**User sees:** Home dashboard with all folders, last sync times, pending/synced/conflict counts, Drive quota. Sync history log. Clear error messages with retry actions. Bottom navigation (Dashboard / Folders / Settings).

**Technical:**
- `SyncLog` Room entity for sync history (timestamp, counts, errors, duration)
- Retry with exponential backoff for transient Drive API errors (429, 500, 503)
- Error categorization: NETWORK, AUTH_EXPIRED, QUOTA_EXCEEDED, FILE_TOO_LARGE, PERMISSION_DENIED, SAF_ACCESS_LOST
- SAF permission validation via `persistedUriPermissions`
- Drive quota via `Drive.About.get()`
- Navigation Compose bottom bar with `AppNavGraph` (Dashboard / Folders / Settings)
- Material3 polish: empty states, loading skeletons, pull-to-refresh, animated transitions

**New packages:** `ui/screen/dashboard/`, `ui/screen/synclog/`, `ui/navigation/`, `data/sync/error/`

---

## Key Architectural Decisions

| Decision | Rationale |
|----------|-----------|
| **Drive REST API v3** over GDAA | GDAA only accesses app-created files; we need full Drive access for two-way sync |
| **SAF** over direct file paths | Scoped storage (Android 10+) restricts direct paths; SAF is forward-compatible |
| **SHA-256 content hashing** | Timestamps alone are unreliable (clock skew, timezone, SAF granularity); hash is ground truth |
| **Three-way sync anchors** | Correctly distinguishes "new local file" from "remotely deleted file" |
| **ContentObserver** over FileObserver | FileObserver doesn't work with SAF URIs |
| **DocumentsContract** over DocumentFile | `DocumentFile.listFiles()` is O(n²); cursor queries are O(n) |

## Critical Files Modified Across Features

- `gradle/libs.versions.toml` — dependencies added per feature
- `app/build.gradle.kts` — plugins and dependency blocks
- `AndroidManifest.xml` — permissions and service declarations accumulate
- `di/AppModule.kt` — expanded per feature with new components
- `App.kt` — Koin init, later WorkManager custom factory
- `data/local/db/AppDatabase.kt` — entities and migrations grow per feature
