# AGENT.md — ExpenseTracker Android Project

> This file provides context for AI agents working on this codebase.
> Read this before making any changes to the project.

---

## Project Overview

**ExpenseTracker** is a native Android application for personal finance management. It enables users to:
- Track income and expense transactions
- Organize spending by categories (user-defined or system-default)
- Set and monitor spending budgets per category
- View financial statistics (weekly, monthly, yearly, or custom date range) with charts
- Automatically capture transactions from banking notification listeners (Vietnamese banking apps)
- Manage their account profile and password

The app communicates with a remote REST API backend hosted at:
```
https://expensetracker-api-b1xz.onrender.com/
```

**Language:** Java (100% — no Kotlin)  
**Min SDK:** 24 (Android 7.0)  
**Target/Compile SDK:** 37  
**Version:** 1.0 (versionCode 1)

---

## Architecture

The project follows **Clean Architecture** with **MVVM** presentation pattern, organized into three distinct layers:

```
Presentation Layer  →  Domain Layer  →  Data Layer
(UI + ViewModel)       (Use Cases,        (Repository Impl,
                        Models,            Remote API,
                        Repo Interfaces)   Mappers, DataStore)
```

### Layer Responsibilities

| Layer | Package | Responsibility |
|---|---|---|
| **Presentation** | `presentation` | Activities, Fragments, BottomSheets, ViewModels, Adapters |
| **Domain** | `domain` | Business logic, Use Cases, Domain Models, Repository Interfaces |
| **Data** | `data` | API clients (Retrofit), DTOs, Repository Implementations, Mappers, DataStore |

### Key Architectural Decisions

- **No local database** — all data is fetched from the remote API; no Room or SQLite.
- **RxJava3** is used throughout the data and domain layers (`Single`, `Completable`, `Observable`). ViewModels subscribe on `Schedulers.io()` and observe on `AndroidSchedulers.mainThread()`.
- **LiveData** is used in ViewModels to expose state to the UI layer.
- **Resource wrapper** (`domain.common.Resource<T>`) is used as a sealed-class-like pattern in Java with states: `IDLE`, `LOADING`, `SUCCESS`, `ERROR`.
- **Hilt** (Dagger) handles all dependency injection. Every Activity, Fragment, Service, and ViewModel is `@AndroidEntryPoint` / `@HiltViewModel`.
- **Fragment management** in `MainActivity` uses hide/show (not replace) to preserve fragment state across navigation tab switches.

---

## Package Structure

```
com.example.expensetracker/
├── ExpenseTrackerApplication.java    # @HiltAndroidApp, lifecycle callbacks, security init
├── MainActivity.java                 # Main nav host, bottom navigation (custom), FAB
│
├── data/
│   ├── prefs/
│   │   └── TokenManager.java         # DataStore-backed encrypted token storage (RxJava3)
│   ├── remote/
│   │   ├── api/                      # Retrofit interfaces (AuthApi, TransactionApi, etc.)
│   │   ├── dto/                      # DTOs grouped by feature (auth, budgets, categories, …)
│   │   │   └── common/
│   │   │       ├── ApiResponseDto    # Generic API envelope: { success, data, message }
│   │   │       └── PagedResultDto    # Pagination wrapper: { items, totalCount, page, … }
│   │   ├── interceptor/
│   │   │   ├── AuthInterceptor.java  # Attaches Bearer token to all non-auth requests
│   │   │   └── TokenAuthenticator.java # OkHttp Authenticator — auto-refreshes token on 401
│   │   └── mapper/
│   │       ├── TransactionMapper.java
│   │       └── UserMapper.java
│   ├── repository/                   # Concrete implementations of domain repository interfaces
│   └── session/
│       └── SessionManager.java       # In-memory session state; publishes session-expired events
│
├── di/
│   ├── NetworkModule.java            # OkHttpClient, Retrofit, all API singletons; cert pinning
│   ├── RepositoryModule.java         # Binds domain interfaces → data implementations
│   ├── SecurityModule.java           # SecurityHelper, CryptoManager, DataStore bindings
│   ├── RefreshAuthApiQualifier.java  # Custom Hilt qualifier for the refresh-only Retrofit
│   └── RefreshOkHttpClientQualifier.java
│
├── domain/
│   ├── common/
│   │   └── Resource.java             # State wrapper: IDLE | LOADING | SUCCESS | ERROR
│   ├── model/                        # Pure domain models (no framework dependencies)
│   │   ├── Transaction.java
│   │   ├── Category.java
│   │   ├── Budget.java
│   │   ├── User.java
│   │   ├── TokenData.java
│   │   ├── StatisticsData.java
│   │   └── CategoryBreakdown.java
│   ├── repository/                   # Repository interfaces (contracts)
│   │   ├── AuthRepository.java
│   │   ├── TransactionRepository.java
│   │   ├── CategoryRepository.java
│   │   ├── BudgetRepository.java
│   │   ├── ReportRepository.java
│   │   └── UserRepository.java
│   └── usecase/                      # One class per use case, uses `execute(…)` method
│       ├── auth/     (Login, Register, Logout, RefreshToken)
│       ├── budget/   (Create, Delete, GetBudgets, Update)
│       ├── category/ (Create, Delete, GetCategories, Update)
│       ├── statistics/ (GetStatistics)
│       ├── transaction/ (Create, Delete, Filter, GetByDate, Update)
│       └── user/     (ChangePassword, GetProfile, UpdateProfile)
│
├── presentation/
│   ├── adapter/
│   │   ├── BudgetAdapter.java
│   │   ├── CategoryAdapter.java
│   │   ├── CategorySelectorAdapter.java
│   │   ├── CategoryIconAdapter.java
│   │   └── TransactionAdapter.java
│   ├── dialog/
│   │   └── ConfirmDeleteDialog.java
│   ├── ui/
│   │   ├── activity/
│   │   │   ├── SplashActivity.java         # LAUNCHER; checks session → routes to Login or Main
│   │   │   ├── LoginActivity.java
│   │   │   ├── RegisterActivity.java
│   │   │   ├── ProfileActivity.java
│   │   │   ├── UpdateProfileActivity.java
│   │   │   └── ChangePasswordActivity.java
│   │   ├── bottomsheet/                    # BottomSheetDialogFragments for CRUD and pickers
│   │   │   ├── AddEditTransactionBottomSheet.java
│   │   │   ├── AddEditCategoryBottomSheet.java
│   │   │   ├── AddEditBudgetBottomSheet.java
│   │   │   ├── DateRangePickerBottomSheet.java
│   │   │   ├── FilterTransactionBottomSheet.java
│   │   │   ├── FilterBottomSheet.java
│   │   │   ├── MonthPickerBottomSheet.java
│   │   │   ├── SelectCategoryBottomSheet.java
│   │   │   └── TransactionDetailBottomSheet.java
│   │   ├── calendar/
│   │   │   ├── DateTransactionItem.java
│   │   │   ├── TransactionCalendarView.java
│   │   │   └── TransactionCalendarViewAdapter.java
│   │   └── fragment/
│   │       ├── overview/
│   │       │   ├── OverviewFragment.java   # Summary cards + tab switcher (Overview/Statistics)
│   │       │   └── StatisticsFragment.java # Charts via MPAndroidChart
│   │       ├── transaction/
│   │       │   └── TransactionsFragment.java
│   │       ├── category/
│   │       │   └── CategoriesFragment.java
│   │       └── budget/
│   │           └── BudgetsFragment.java
│   └── viewmodel/
│       ├── LoginViewModel.java
│       ├── RegisterViewModel.java
│       ├── TransactionViewModel.java
│       ├── CategoryViewModel.java
│       ├── BudgetViewModel.java
│       ├── OverviewViewModel.java
│       ├── StatisticsViewModel.java
│       └── ProfileViewModel.java
│
├── security/
│   ├── CertificatePinningHelper.java   # OkHttp certificate pinning for the API host
│   ├── CryptoManager.java              # AES-256-GCM encryption via Google Tink + Android Keystore
│   ├── DebuggerDetectionHelper.java    # Detects debugger attachment; terminates app in release
│   ├── IdleTimeoutManager.java         # Auto-logout after 15 min idle; clears tokens
│   └── ScreenSecurityHelper.java       # Applies FLAG_SECURE to prevent screenshots
│
├── service/
│   ├── ExpenseNotificationListenerService.java  # Reads banking notifications, auto-creates transactions
│   ├── NotificationParser.java                  # Parses notification content per bank app
│   └── NotificationTransaction.java             # POJO for parsed notification data
│
└── util/
    ├── Constants.java          # BASE_URL only
    ├── CurrencyUtils.java      # Vietnamese currency formatting helpers
    ├── DateUtils.java          # Calendar ↔ API date string (yyyy-MM-dd) conversions
    ├── IconResolver.java       # Maps category icon name → drawable resource ID
    └── ValidationUtils.java    # Field validators + live validation binding helpers
```

---

## Key Dependencies & Libraries

| Library | Version | Purpose |
|---|---|---|
| **Hilt (Dagger)** | 2.59.2 | Dependency injection |
| **Retrofit 2** | 3.0.0 | HTTP REST client |
| **OkHttp 3** | 4.12.0 | HTTP client with logging and auth interceptors |
| **Gson Converter** | 3.0.0 | JSON ↔ object serialization (bundled with Retrofit) |
| **RxJava 3** | 3.1.12 | Reactive programming for async operations |
| **RxAndroid** | 3.0.2 | Android schedulers for RxJava |
| **DataStore (Preferences)** | 1.2.1 | Persistent key-value storage (replaces SharedPreferences) |
| **DataStore RxJava3** | 1.2.1 | RxJava3 bindings for DataStore |
| **Google Tink (Android)** | 1.23.0 | AES-256-GCM encryption backed by Android Keystore |
| **MPAndroidChart** | 3.1.0 | Pie charts, line charts for statistics (via JitPack) |
| **AndroidX AppCompat** | 1.8.0 | Base Activity/Fragment compatibility |
| **Material Components** | 1.14.0 | Material Design widgets (TextInputLayout, etc.) |
| **Lifecycle (ViewModel/LiveData)** | 2.11.0 | MVVM lifecycle-aware components |
| **RecyclerView** | 1.4.0 | List/grid rendering |
| **ConstraintLayout** | 2.2.2 | UI layout |
| **rootbeer-lib** | 0.1.2 | Root detection (declared, may be used in SecurityModule) |

---

## Common Commands

> All commands should be run from the project root: `D:\hoctap\Android\Projects\ExpenseTracker`

```powershell
# Build debug APK
.\gradlew assembleDebug

# Build release APK (requires keystore.properties to be configured)
.\gradlew assembleRelease

# Install debug build on connected device/emulator
.\gradlew installDebug

# Run unit tests
.\gradlew test

# Run instrumented (Android) tests
.\gradlew connectedAndroidTest

# Run security test build (release config + debuggable)
.\gradlew assembleSecurityTest

# Clean build artifacts
.\gradlew clean

# Check for dependency updates
.\gradlew dependencyUpdates

# Generate lint report
.\gradlew lint
```

---

## Build Types & Signing

| Build Type | Description |
|---|---|
| `debug` | Default debug build; security monitoring bypassed in app |
| `release` | Minified + shrunk; ProGuard enabled; security checks active |
| `securityTest` | Inherits release config but `isDebuggable = true` for penetration testing |

**Signing:** The keystore file is `expense-tracker-release.jks` at project root. Signing credentials are read from `keystore.properties` (git-ignored).

**ProGuard rules** are in `app/proguard-rules.pro`.

---

## Coding Conventions

Observed throughout the codebase:

1. **Language:** Pure Java — no Kotlin, no Kotlin extensions.
2. **Formatting:** Each statement and chained method call on its own line (vertically expanded style). Method arguments often each on their own line.
3. **Class design:** Utility and singleton classes use `private` constructors (`Constants`, `ValidationUtils`). Hilt singletons are annotated `@Singleton`.
4. **Hilt injection:** Constructor injection is preferred (`@Inject` on constructor). Field injection is used only in Android framework classes (Activities, Fragments, Services) where Hilt requires it.
5. **Immutability:** Domain models use `final` fields and no setters — values set only via constructor.
6. **Error handling in repositories:** All exceptions are caught inside `Single.create(emitter -> { ... })` and returned as `Resource.error(...)` — never propagated as RxJava errors to the ViewModel. Error messages are in Vietnamese.
7. **ViewModel RxJava pattern:** Use `CompositeDisposable` and `Disposable` tracking; cancel in-flight requests before starting new ones by calling `.dispose()` on the previous `Disposable`. Clean up in `onCleared()`.
8. **LiveData naming:** State LiveData fields are named with nouns (`transactionAction`, `deleteAction`, `loginState`). Reset methods are `resetXxx()`.
9. **Fragment navigation:** `MainActivity` hides/shows fragments (not replaces) using tags: `"overview"`, `"transactions"`, `"categories"`, `"budgets"`.
10. **UI strings:** All user-facing messages are in Vietnamese. Use Vietnamese for any new error messages or Toast text.
11. **Null checks:** Explicit null checks before using API responses (`response.isSuccessful() && response.body() != null && response.body().getData() != null`).
12. **Logging:** `Log.d/e/w` calls are always guarded by `if (BuildConfig.DEBUG)` in production code paths.
13. **`Resource` state machine:** Always emit `Resource.loading()` before an async operation and `Resource.idle()` when resetting. Never skip the loading state.

---

## Key Components

### Activities

| Activity | Purpose |
|---|---|
| `SplashActivity` | Entry point after login; session check, routing |
| `LoginActivity` | Username + password login |
| `RegisterActivity` | New user registration |
| `MainActivity` | Main app shell; custom bottom nav bar (4 tabs + FAB + account icon) |
| `ProfileActivity` | View user profile |
| `UpdateProfileActivity` | Edit profile (name, email) |
| `ChangePasswordActivity` | Change password |

### Fragments (Main Tabs)

| Fragment | Tab | Purpose |
|---|---|---|
| `OverviewFragment` | Overview | Balance summary + period selector + tab to StatisticsFragment |
| `StatisticsFragment` | (sub-tab of Overview) | Pie/line charts via MPAndroidChart |
| `TransactionsFragment` | Transactions | Filterable transaction list with calendar view |
| `CategoriesFragment` | Categories | Income/expense category list |
| `BudgetsFragment` | Budgets | Budget list with progress |

### Bottom Sheets

All edit/create/pick actions are done via `BottomSheetDialogFragment`:
- `AddEditTransactionBottomSheet` — create or edit a transaction
- `AddEditCategoryBottomSheet` — create or edit a category (with icon/color picker)
- `AddEditBudgetBottomSheet` — create or edit a budget
- `FilterTransactionBottomSheet` / `FilterBottomSheet` — transaction filters
- `SelectCategoryBottomSheet` — category picker (used in transaction form)
- `DateRangePickerBottomSheet` — custom date range picker
- `MonthPickerBottomSheet` — month/year picker
- `TransactionDetailBottomSheet` — view and delete/edit a transaction

### ViewModels

All ViewModels are `@HiltViewModel`. They:
1. Inject Use Cases via constructor
2. Expose `LiveData<Resource<T>>` for each piece of state
3. Call Use Cases on `Schedulers.io()`, post results to LiveData on main thread
4. Use `CompositeDisposable` for cleanup

### Services

| Service | Purpose |
|---|---|
| `ExpenseNotificationListenerService` | Listens to device notifications; parses banking SMS/notifications; auto-creates transactions in the background |

---

## Network / Data Layer

### API Base URL
```
https://expensetracker-api-b1xz.onrender.com/
```

### API Interfaces (Retrofit)

| Interface | Endpoints |
|---|---|
| `AuthApi` | `POST /api/auth/login`, `POST /api/auth/register`, `POST /api/auth/logout` |
| `RefreshAuthApi` | `POST /api/auth/refresh` (uses a separate OkHttpClient without `AuthInterceptor`) |
| `UserApi` | `GET /api/users/profile`, `PUT /api/users/profile` |
| `TransactionApi` | `GET /api/transactions`, `GET /api/transactions/by-date`, `POST`, `PUT /{id}`, `DELETE /{id}` |
| `CategoryApi` | CRUD for categories |
| `BudgetApi` | CRUD for budgets |
| `ReportApi` | `GET /api/reports/statistics` |

### Response Format

All API responses use the envelope:
{
  "success": true,
  "data": { ... },
  "message": "..."
}
Mapped to `ApiResponseDto<T>` in `data.remote.dto.common`.

Paginated responses use `PagedResultDto<T>` with `items` list.

### Authentication Flow

1. Login → receive `accessToken` + `refreshToken` + expiry dates → stored encrypted in DataStore
2. `AuthInterceptor` attaches `Bearer <accessToken>` header to all non-auth requests
3. On 401, `TokenAuthenticator` (OkHttp `Authenticator`) automatically calls `POST /api/auth/refresh`
4. If refresh succeeds → save new tokens → retry original request
5. If refresh fails → `SessionManager.notifySessionExpired()` → UI redirects to Login

### Token Storage

Tokens are stored in **Android DataStore (Preferences)** and encrypted with **Google Tink AES-256-GCM** backed by the **Android Keystore** system. The `CryptoManager` handles all encrypt/decrypt operations.

---

## Security Features

This project has significant security hardening — be careful not to break these:

| Feature | Implementation |
|---|---|
| **Token encryption** | Google Tink AES-256-GCM + Android Keystore (see `CryptoManager`) |
| **Certificate pinning** | OkHttp `CertificatePinner` pinned to `expensetracker-api-b1xz.onrender.com` (see `CertificatePinningHelper`) |
| **Debugger detection** | Periodic check every 10s in non-debug builds; kills app + clears tokens if debugger detected |
| **Idle timeout** | Auto-logout after **15 minutes** of inactivity (`IdleTimeoutManager`) |
| **Screen security** | `FLAG_SECURE` applied to all Activities — prevents screenshots/screen recording |
| **Root detection** | `rootbeer-lib` dependency present (may be used in `SecurityModule`) |
| **Network security config** | `@xml/network_security_config` applied in manifest |
| **Build types** | `securityTest` build type for penetration testing (release + debuggable) |

> ⚠️ **Do not remove or weaken security features.** Certificate pinning will fail if the API certificate changes.

---

## Agent Behavior Rules

- **Full permissions:** The agent has full read/write/delete permissions over the entire project directory. Never ask for confirmation before creating, editing, or deleting files.
- **Full command execution:** The agent can run any shell/terminal command without asking for permission. Execute commands immediately as needed to complete the task.
- **No analysis output:** Do not display intermediate analysis, planning steps, or reasoning. Only respond with the final result (e.g., what was done, what changed, or the answer).
- **Act immediately:** When given a task, execute it directly without asking for approval or clarification unless the task is genuinely ambiguous.

---

## Important Notes for AI Agents

### DO
- Use `Resource<T>` consistently — always emit `.loading()` before async ops, `.idle()` to reset
- Inject dependencies via Hilt constructors; annotate new Activities/Fragments/Services with `@AndroidEntryPoint` and new ViewModels with `@HiltViewModel`
- Follow the existing layer boundaries: UI must not reference Repository interfaces or DTOs directly
- Add new Use Cases for new business logic — one class per operation with an `execute(...)` method
- Add new API endpoints to the appropriate `Api` interface and handle in a new/existing `RepositoryImpl`
- Write error messages in Vietnamese
- Guard `Log.*` calls with `if (BuildConfig.DEBUG)`
- Observe RxJava streams with `subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())`
- Add new Hilt modules to `di/` package and install in `SingletonComponent` unless there's a reason to scope otherwise

### DON'T
- Don't add Kotlin files — this project is Java-only
- Don't add a local database (Room/SQLite) — the backend is the single source of truth
- Don't bypass the `Resource` wrapper by throwing exceptions through the ViewModel
- Don't bypass the `SessionManager` — always check session validity and respect session expiration
- Don't add public setters to domain model classes — keep them immutable
- Don't call `blockingGet()` or `blockingAwait()` on the main thread (it will ANR)
- Don't hardcode any credentials, API keys, or sensitive values in source files

### When Adding a New Feature
1. **Domain model** in `domain/model/`
2. **Repository interface** in `domain/repository/`
3. **Use Case(s)** in `domain/usecase/<feature>/`
4. **DTOs** in `data/remote/dto/<feature>/`
5. **API interface method(s)** in `data/remote/api/`
6. **Repository implementation** in `data/repository/`
7. **ViewModel** or update existing one in `presentation/viewmodel/`
8. **UI** (Activity, Fragment, BottomSheet, Adapter) in `presentation/ui/`
9. **Bind** new repository interface in `di/RepositoryModule.java`
10. **Register** new API in `di/NetworkModule.java`

### Transaction Types
- `type == 1` → **Income** (thu nhập)
- `type == 2` → **Expense** (chi tiêu)

### Amount Validation
- Minimum valid amount: `> 999` VND
- Maximum valid amount: `<= 9,999,999,999,999.99` VND

### Date Format
- API accepts and returns dates as `yyyy-MM-dd` strings (ISO 8601 date only)
- Use `DateUtils.formatApiDate(Calendar)` to convert Calendar → API string
- Use `DateUtils.formatDisplayDate(Calendar)` for human-readable display

### Notification Auto-Transaction
The `ExpenseNotificationListenerService` automatically creates transactions from banking app notifications. Default categories:
- Income → category named `"Thu nhập khác"` (system category, `userId == null`)
- Expense → category named `"Chi phí khác"` (system category, `userId == null`)

---

## File Locations Quick Reference

| What | Where |
|---|---|
| App ID / SDK versions | `app/build.gradle.kts` |
| All library versions | `gradle/libs.versions.toml` |
| All permissions & components | `app/src/main/AndroidManifest.xml` |
| API base URL | `util/Constants.java` |
| Certificate pin | `security/CertificatePinningHelper.java` |
| Idle timeout duration | `security/IdleTimeoutManager.java` (15 min) |
| Debugger check interval | `ExpenseTrackerApplication.java` (10 sec) |
| ProGuard rules | `app/proguard-rules.pro` |
| Keystore | `expense-tracker-release.jks` (project root) |
| Keystore credentials | `keystore.properties` (git-ignored) |
