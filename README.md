<div align="center">

# TechWizards

**A native Android multiplayer dice game built with Jetpack Compose and Kotlin, backed by a Firebase serverless platform.**

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-minSdk%2024%20%7C%20targetSdk%2036-3DDC84?logo=android&logoColor=white)](https://developer.android.com/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024.10.01-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Material%203-Dynamic%20Color-757575?logo=materialdesign&logoColor=white)](https://m3.material.io/)
[![Room](https://img.shields.io/badge/Room-2.6.1-3DDC84?logo=sqlite&logoColor=white)](https://developer.android.com/training/data-storage/room)
[![Firebase](https://img.shields.io/badge/Firebase-BOM%2033.5.1-FFCA28?logo=firebase&logoColor=black)](https://firebase.google.com/)
[![Cloud Functions](https://img.shields.io/badge/Cloud%20Functions-v2%20%C2%B7%20Node%2020-4285F4?logo=googlecloud&logoColor=white)](https://firebase.google.com/docs/functions)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.7-3178C6?logo=typescript&logoColor=white)](https://www.typescriptlang.org/)
[![Retrofit](https://img.shields.io/badge/Retrofit-2.11.0-48B983?logo=square&logoColor=white)](https://square.github.io/retrofit/)
[![Gradle](https://img.shields.io/badge/Gradle-8.13%20%C2%B7%20AGP%208.13.2-02303A?logo=gradle&logoColor=white)](https://gradle.org/)
[![JDK](https://img.shields.io/badge/JDK-17-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/)

[Architecture (EN)](ARCHITECTURE.md) · [Arquitectura (ES)](docs/es/ARCHITECTURE.md) · [Working agreements](AGENTS.md)

</div>

---

## What this is

TechWizards is a dice game you can play alone or against others. Players roll, win or lose coins from their wallet, climb a shared leaderboard, and contribute to a **common prize pot** that any player can claim. Matches run in lobbies with join codes, carry an in-match chat, and stream state in realtime through Firestore.

The Android client is offline-capable: gameplay, wallet balance and match state persist locally in Room and mirror to the backend when connectivity allows.

### Why the architecture matters here

**Clean Architecture with a real domain boundary.** `domain/` holds 23 models and 12 repository contracts written in pure Kotlin — no Android imports, no framework types. Use cases depend on those contracts; concrete adapters are bound at runtime in `ServiceLocator`. Swapping Firestore for another backend is an adapter change, not a rewrite.

**Offline-first by design, not by retrofit.** The schema carries a full synchronisation bookkeeping trio: an `Outbox` durable operation queue with attempt counters, an `IdMap` reconciling local IDs with server-assigned ones, and `Tombstone` deletion markers so an offline delete is not resurrected by a later pull.

**Money operations are transactional and idempotent.** Coin deltas and prize claims run inside `db.runTransaction` on the server. The claim endpoint takes a `claimId` and compares it against `lastClaimId` inside the transaction, so a retried request returns the original result without paying twice.

**Secrets never reach the logs.** The OkHttp logging interceptor calls `redactHeader("Authorization")` even at `Level.BODY`; the backend logs only a token's length and a six-character preview; the app registers a PII masking regex before attaching any persistent log sink.

### Feature overview

| Area | Capabilities |
| --- | --- |
| **Gameplay** | Dice rolls resolved through a dedicated use case, wallet balance adjustment, victory detection |
| **Multiplayer** | Lobbies with unique join codes, matches with participants and per-match scores, event log with a monotonic sequence, in-match chat |
| **Progression** | Local match history, remote history mirrored to Firestore, top-10 leaderboard |
| **Common prize pot** | Shared pot that accumulates via increments and is claimed atomically with idempotency protection |
| **Geolocation** | Victory coordinates captured and persisted locally |
| **Media** | Foreground service for background music playback |
| **Personalisation** | Three UI languages (ES / EN / DE), light/dark theme, Material 3 dynamic colour |
| **Observability** | In-app decentralised logger with pluggable sinks and PII masking |

---

## Tech stack

| Layer | Technology |
| --- | --- |
| Language | Kotlin 2.0.21 · JDK 17 |
| UI | Jetpack Compose (BOM 2024.10.01) · Material 3 · Navigation Compose 2.9.5 · Core SplashScreen |
| Architecture | Clean Architecture · manual DI via Service Locator · MVVM with `StateFlow` |
| Local persistence | Room 2.6.1 over SQLite (`techwizards.db` v3) · DataStore Preferences 1.1.1 |
| Async | Kotlin Coroutines 1.10.2 · RxJava3 3.1.8 (see architecture §9.10) |
| Networking | Retrofit 2.11.0 · OkHttp 4.12.0 · Moshi 1.15.1 · Gson 2.11.0 |
| Background | WorkManager 2.9.1 |
| Auth | Firebase Auth · Google Sign-In (Play Services Auth 21.2.0) |
| Cloud | Cloud Firestore · Cloud Functions v2 (Express 4 on Node 20, TypeScript 5.7) |
| Build | Gradle 8.13 · AGP 8.13.2 · KSP 2.0.21-1.0.25 · version catalog |
| Testing | JUnit 4 · MockK 1.13.12 · coroutines-test · OkHttp MockWebServer · Espresso · Compose UI Test |

---

## Prerequisites

| Requirement | Version | Notes |
| --- | --- | --- |
| Android Studio | Ladybug or newer | Needs AGP 8.13.2 support |
| JDK | 17 | Bundled JBR is fine |
| Android SDK | API 36 | `compileSdk` and `targetSdk` |
| Node.js | 20 | Required for the Cloud Functions backend |
| Firebase CLI | latest | `npm install -g firebase-tools` |
| A Firebase project | — | Or access to `techwizards-dado` |

---

## Quickstart

### 1. Clone

```bash
git clone https://github.com/dadd86/TechWizards.git
cd TechWizards
```

### 2. Fix the hardcoded JDK path

`gradle.properties` currently pins a Windows JDK location, which breaks the build on macOS, Linux and CI. Remove or comment the line before your first sync:

```properties
# org.gradle.java.home=C:\\Program Files\\Android\\Android Studio\\jbr
```

### 3. Supply `google-services.json`

This file is gitignored and is **not** in the repository, so the project will not build without it. Download it from the Firebase console for your Android app (`com.diegodiaz.techwizards`) and place it at:

```text
app/google-services.json
```

### 4. Create `firebase.json`

The repository ships `.firebaserc` (pointing at project `techwizards-dado`) but not `firebase.json`. Both the emulator and deploy scripts need it. Create it at the repository root:

```bash
cat > firebase.json << 'FBEOF'
{
  "functions": {
    "source": "functions",
    "predeploy": ["npm --prefix \"$RESOURCE_DIR\" run build"]
  },
  "firestore": {
    "rules": "firestore.rules"
  },
  "emulators": {
    "functions": { "port": 5002 },
    "firestore": { "port": 8080 },
    "ui": { "enabled": true }
  }
}
FBEOF
```

> The functions port **must** be `5002`. Both product flavours hardcode it in `API_BASE_URL`.

### 5. Set the build environment variables

Two `buildConfigField` values are read from the environment at configuration time. Without `GOOGLE_WEB_CLIENT_ID`, the build succeeds but Google Sign-In fails at runtime with the `CHANGE_ME` placeholder.

```bash
# Linux / macOS
export GOOGLE_WEB_CLIENT_ID="<your-web-client-id>.apps.googleusercontent.com"
export API_SERIALIZER="moshi"

# Windows PowerShell
$env:GOOGLE_WEB_CLIENT_ID="<your-web-client-id>.apps.googleusercontent.com"
$env:API_SERIALIZER="moshi"
```

The web client ID comes from the Firebase console under Authentication → Sign-in method → Google → Web SDK configuration.

### 6. Start the backend emulator

```bash
cd functions
npm install
npm run serve
cd ..
```

This compiles the TypeScript and starts the Functions and Firestore emulators. Verify the API is reachable:

```bash
curl http://localhost:5002/techwizards-dado/us-central1/api/prize/common
```

Expected response when the document does not yet exist:

```json
{ "descripcion": "Premio común", "valor": 0, "updatedAt": 1754400000000 }
```

### 7. Pick a build variant and run

The `target` flavour dimension decides which host the app talks to:

| Variant | Use when | Points at |
| --- | --- | --- |
| `emulatorDebug` | Running on the Android emulator | `http://10.0.2.2:5002/...` |
| `deviceDebug` | Running on a physical device | `http://192.168.178.23:5002/...` |

```bash
# Android emulator
./gradlew installEmulatorDebug

# Physical device
./gradlew installDeviceDebug
```

> `deviceDebug` hardcodes the LAN address `192.168.178.23` in both `app/build.gradle.kts` and `res/xml/network_security_config.xml`. Change **both** to your own machine's IP, or the device will not reach the emulator.

### 8. Build a release APK

```bash
./gradlew assembleDeviceRelease
```

Release builds enable R8 shrinking. Moshi's `KotlinJsonAdapterFactory` is reflection-based, so verify JSON parsing on a shrunk build before shipping.

---

## Running the tests

```bash
# JVM unit tests
./gradlew testEmulatorDebugUnitTest

# Instrumented tests (requires a connected device or emulator)
./gradlew connectedEmulatorDebugAndroidTest

# Lint
./gradlew lintEmulatorDebug
```

The suite currently holds 12 `@Test` methods, of which 2 are unmodified Android Studio template tests and one class declares none. See [`ARCHITECTURE.md` §8](ARCHITECTURE.md#8-testing).

---

## Backend API

All routes are served by a single Express app exported as one Cloud Functions v2 entry point.

```bash
BASE="http://localhost:5002/techwizards-dado/us-central1/api"

# Public
curl "$BASE/leaderboard/top10"
curl "$BASE/scores/top10"
curl "$BASE/prize/common"

# Authenticated — requires a Firebase ID token
curl -X POST "$BASE/login" \
  -H "Authorization: Bearer $ID_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"alias":"Merlin"}'

curl -X POST "$BASE/scores" \
  -H "Authorization: Bearer $ID_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"alias":"Merlin","deltaMonedas":250}'

# Idempotent — repeating the same claimId does not pay twice
curl -X POST "$BASE/prize/common/claim" \
  -H "Authorization: Bearer $ID_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"claimId":"a3f9c1e2-7b44-4d90-8c15-2e6f0b9d1a77"}'
```

The full endpoint reference, including validation rules and error codes, is in [`ARCHITECTURE.md` §5](ARCHITECTURE.md#5-api-and-interface-specification).

---

## Repository structure

```text
TechWizards/
├── app/                                # Android application module
│   ├── src/main/java/com/diegodiaz/techwizards/
│   │   ├── app/                        # Application class, MainActivity, locale startup gate
│   │   ├── core/                       # ServiceLocator, SessionManager, Result, 16 use cases
│   │   ├── credenciales/               # Credential store abstraction
│   │   ├── data/
│   │   │   ├── local/                  # 15 Room entities, 14 DAOs, 14 mappers, migrations
│   │   │   ├── remote/                 # 5 Retrofit APIs, DTOs, Firestore data sources
│   │   │   ├── repository/impl/        # 13 repository implementations
│   │   │   └── transaction/            # TransactionRunner abstraction
│   │   ├── domain/                     # 23 models, 12 repository contracts — pure Kotlin
│   │   ├── integration/                # Music playback service, victory celebration worker
│   │   ├── ui/                         # 12 screens, 13 controllers, navigation, theme
│   │   └── util/                       # Logging with sinks, location, ids, time
│   ├── src/main/res/                    # values · values-de · values-en · values-night · raw · xml
│   ├── src/test/                        # JVM unit tests
│   ├── src/androidTest/                 # Instrumented tests
│   ├── schemas/                         # Exported Room schemas: 1.json, 2.json, 3.json
│   └── build.gradle.kts                 # Flavours, build types, env-driven BuildConfig
├── functions/                           # Cloud Functions backend
│   ├── src/index.ts                     # Express API — 9 routes
│   ├── package.json                     # Node 20, build/serve/deploy scripts
│   └── tsconfig.json
├── docs/es/ARCHITECTURE.md              # Architecture specification (Spanish)
├── SQL/PrimerSQL.sql                    # Reference DDL and pragmas
├── gradle/libs.versions.toml            # Version catalog
├── ARCHITECTURE.md                      # Architecture specification (English)
├── AGENTS.md                            # Project working agreements
├── firestore.rules
└── .firebaserc
```

---

## Troubleshooting

**Gradle sync fails with an invalid `java.home`.**
Remove the `org.gradle.java.home` line from `gradle.properties`. See step 2.

**Build fails: `File google-services.json is missing`.**
The file is gitignored by design. Download it from the Firebase console and place it at `app/google-services.json`.

**`firebase emulators:start` reports no configuration found.**
`firebase.json` is not in the repository. Create it as shown in step 4.

**Google Sign-In fails immediately.**
`GOOGLE_WEB_CLIENT_ID` was not exported before the build, so `BuildConfig` holds `CHANGE_ME`. Export it and rebuild — a Gradle sync alone is not enough, the value is baked in at configuration time.

**The app cannot reach the API from a physical device.**
`deviceDebug` targets `192.168.178.23`. Update the IP in `app/build.gradle.kts` and in `res/xml/network_security_config.xml`, then rebuild. Both must match.

**Realtime lobby and match listeners fail with permission-denied.**
The catch-all rule in `firestore.rules` is Firebase's default time-boxed rule and its expiry date has passed, so all direct client reads outside `players/{userId}/history` are denied. Server routes are unaffected because `firebase-admin` bypasses rules — which is why the leaderboard still works while listeners do not. See [`ARCHITECTURE.md` §9.1](ARCHITECTURE.md#91-firestore-catch-all-rule-has-expired--active-functional-defect) for the fix.

---

## Documentation

| Document | Language | Contents |
| --- | --- | --- |
| [`ARCHITECTURE.md`](ARCHITECTURE.md) | English | Full specification: stack, topology, patterns, persistence, APIs, UI, deployment, known limitations |
| [`docs/es/ARCHITECTURE.md`](docs/es/ARCHITECTURE.md) | Spanish | 1:1 equivalent of the above |
| [`AGENTS.md`](AGENTS.md) | Spanish | Project working agreements and conventions |
| [`SQL/README.md`](SQL/README.md) | Spanish | Purpose of the reference DDL |

---

## Before shipping

These are documented in detail in [`ARCHITECTURE.md` §9](ARCHITECTURE.md#9-known-limitations-and-architectural-risks). The first three are blockers.

- **Firestore rules have expired.** Replace the default catch-all with explicit per-collection rules.
- **`usesCleartextTraffic="true"`** at application level overrides the three-host allowlist and permits plaintext HTTP everywhere. Remove the attribute; the `network_security_config` allowlist then applies as intended.
- **Two endpoints return stack traces to the client.** `POST /prize/common/claim` returns `e.stack`; `POST /prize/common/increment` returns `e.message`. Align them with the opaque `internal` used elsewhere.
- **`EncryptedCredentialsStore` performs no encryption** — it is an in-memory store. Rename it, or back it with `EncryptedSharedPreferences`.
- **Unused calendar permissions** (`READ_CALENDAR`, `WRITE_CALENDAR`) are declared with no supporting code.
- **`versionCode` is still 1** and R8 shrinking has never been verified against reflection-based Moshi.

---

## Credits

Developed by Diego Armando Diaz Devia. Firebase project `techwizards-dado`.