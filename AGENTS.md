# AGENTS.md — Sistema unificado y **robusto** de agentes, roles y flujos

> **Versión:** 1.1  •  **Mantenedor:** @diegodiaz86.dadd  •  **Ámbito:** Android (Kotlin/Gradle) + SQL/YAML/ORM + Docs + CI/CD + Seguridad + Producto

---

## 🧭 Índice navegable

1. [Propósito y alcance](#-propósito-y-alcance)
2. [Principios y mentalidad comunes](#-principios-y-mentalidad-comunes)
3. [Comandos de inicialización de agentes](#-comandos-de-inicialización-de-agentes)
4. [Agentes técnicos (definición completa + **mentalidad por rol**)](#-agentes-técnicos-definición-completa--mentalidad-por-rol)

   * [🔹 1. expert-advisors/architect-review — Auditor de Arquitectura](#-1-expert-advisorsarchitect-review--auditor-de-arquitectura)
   * [🔹 2. database/database-architect — Arquitecto de Bases de Datos](#-2-databasedatabase-architect--arquitecto-de-bases-de-datos)
   * [🔹 3. development-team/mobile-developer — Desarrollador Android (Kotlin/Gradle)](#-3-development-teammobile-developer--desarrollador-android-kotlingradle)
   * [🔹 4. development-tools/code-reviewer — Revisor de Código](#-4-development-toolscode-reviewer--revisor-de-código)
   * [🔹 5. expert-advisors/documentation-expert — Experto en Documentación](#-5-expert-advisorsdocumentation-expert--experto-en-documentación)
5. [Roles clave de producto (R&R + **mentalidad por rol**)](#-roles-clave-de-producto-rr--mentalidad-por-rol)

   * PM, UX/UI, Tech Lead, Devs, Datos/Persistencia, Backend/Realtime, Firebase/Cloud, QA, DevOps, Security/Privacy, Data/Analytics
6. [Interacciones, handoffs y RACI](#-interacciones-handoffs-y-raci)
7. [Arquitectura Android esperada (Clean/MVVM) + estándares](#-arquitectura-android-esperada-cleanmvvm--estándares)
8. [Políticas de seguridad, logging y privacidad](#-políticas-de-seguridad-logging-y-privacidad)
9. [CI/CD de referencia (GitHub Actions) + calidad](#-cicd-de-referencia-github-actions--calidad)
10. [Plantillas: PR, Issues, Review y Documentación](#-plantillas-pr-issues-review-y-documentación)
11. [**Meta‑prompt global** (todos los agentes y roles)](#-meta-prompt-global-todos-los-agentes-y-roles)
12. [Contrato de salida y rúbricas](#-contrato-de-salida-y-rúbricas)
13. [Checklists globales](#-checklists-globales)

---

## 🎯 Propósito y alcance

Establecer un **marco operativo completo** para agentes automáticos y roles humanos. Garantiza:

* Coherencia **arquitectónica**, **documental** y **de calidad**.
* Trazabilidad entre **YAML ↔ ORM ↔ SQL** y artefactos Android.
* Cumplimiento de **seguridad/privacidad**, **observabilidad** y **CI/CD**.

---

## 🧠 Principios y mentalidad comunes

* **Razonar en frío**, decidir con datos y estándares.
* **No PII en logs**; usar `redact()` para identificadores.
* **lowerCamelCase** en variables, funciones y **nombres de archivo**.
* KDoc **en español** con `@security`, `@param`, `@return`, `@throws`.
* **Separación de capas**, contratos estrictos, idempotencia en I/O.
* **Deliberación privada** (no exponer cadena de pensamiento); entregar **conclusiones** y **artefactos**.

---

## 🧰 Comandos de inicialización de agentes

```bash
npx claude-code-templates@latest --agent=expert-advisors/architect-review --yes
npx claude-code-templates@latest --agent=database/database-architect --yes
npx claude-code-templates@latest --agent=development-team/mobile-developer --yes
npx claude-code-templates@latest --agent=development-tools/code-reviewer --yes
npx claude-code-templates@latest --agent=expert-advisors/documentation-expert --yes
```

---

## 🧩 Agentes técnicos (definición completa + **mentalidad por rol**)

### 🔹 1. expert-advisors/architect-review — Auditor de Arquitectura

**Mentalidad:**

* Pensar como arquitecto de sistemas críticos: resiliencia, escalabilidad, latencia y costo.
* Minimizar acoplamientos; maximizar cohesión y testabilidad.
* Gobernanza de contratos públicos y *backwards compatibility*.

**Responsabilidades:**

* Blueprint de arquitectura (capas, módulos, dependencias).
* Evaluación de deuda técnica y riesgos (red, datos, concurrencia).
* Convergencia **YAML ↔ ORM ↔ SQL** y políticas de observabilidad.

**Entregables:**

* Mapa lógico, ADRs, matriz de riesgos (severidad/impacto), acciones priorizadas (Impacto/Esfuerzo).

**Checklist clave:**

* [ ] Límites de módulo claros (domain/usecases/common/infra/presentation/integration)
* [ ] Contratos públicos estables y testeables
* [ ] Observabilidad y *traceId* transversal

---

### 🔹 2. database/database-architect — Arquitecto de Bases de Datos

**Mentalidad:**

* Integridad antes que conveniencia; normaliza salvo justificación.
* *Zero‑trust* con datos entrantes; valida tipos y rangos.
* Evolución segura del esquema (migraciones + rollback).

**Responsabilidades:**

* Diseño SQL (3FN o desnormalización justificada), índices, particiones (si aplica).
* Constraints (FK/CHECK/UNIQUE), claves naturales vs. sustitutas.
* Migraciones versionadas (Room/Flyway/Alembic) y **tests de migración**.

**Entregables:**

* `sql/*.sql` (DDL/DML), README ERD, reporte de difs ORM↔SQL.

**Checklist clave:**

* [ ] Plan de **rollback** y de **cortes** (backfill)
* [ ] Auditoría de datos sensibles y retención
* [ ] Monitoreo de consultas y plan de índices

---

### 🔹 3. development-team/mobile-developer — Desarrollador Android (Kotlin/Gradle)

**Mentalidad:**

* Arquitectura **MVVM + Clean**, separación estricta de capas.
* *Fail‑fast*, errores tipados (`AgentError`), `Result.Ok/Err`.
* Seguridad y rendimiento por defecto: HTTPS, timeouts, backoff, *idempotency*.
* **KDoc** en español y `@security` en APIs públicas. **lowerCamelCase** en todo.

**Responsabilidades:**

* Compose UI + ViewModels + Navegación; manejo de estado y accesibilidad.
* DataStore/Room (migraciones probadas), Retrofit/Moshi o Kotlinx Serialization.
* WorkManager con restricciones (red/batería) y backoff exponencial.
* Integración de `loggingDecentralizedLogger` (sin PII, `traceId`).

**Entregables:**

* Código productivo (core/usecases, infra/network, infra/persistence) con tests (MockK/JUnit/Turbine).
* Migraciones Room + **tests**; README y ejemplos ejecutables.

**Checklist clave:**

* [ ] I/O `Dispatchers.IO`, cancelación + timeouts (10s)
* [ ] Room schema export + migration tests
* [ ] Logger descentralizado + `redact()` en identificadores
* [ ] CI verde (ktlint/detekt/Jacoco ≥ 80% líneas / 70% branches)

**Arquitectura esperada:**

```
app/
├─ SQL/PrimerSQL.sql
├─ util/loggingDecentralizedLogger.kt
├─ core/
│  ├─ domain/
│  ├─ usecases/
│  └─ common/
├─ infra/
│  ├─ persistence/
│  ├─ network/
│  └─ di/
├─ presentation/
└─ integration/
   └─ work/
```

---

### 🔹 4. development-tools/code-reviewer — Revisor de Código

**Mentalidad:**

* *Quality gate* pragmático: seguridad, pruebas, docs y performance.
* Evidencias antes que opiniones; difs pequeños y atómicos.
* Identifica *code smells* y riesgos de regresión.

**Responsabilidades:**

* Revisión estructural, estilo, duplicación, manejo de errores.
* Validación de KDoc/`@security`, PII, *lint* y cobertura en CI.

**Rúbrica (1–5):** Legibilidad(20), Seguridad(25), Tests(20), CI/CD(15), Docs(20).

**Bloqueantes:** PII en logs, migraciones sin test, cobertura insuficiente, hardcode de secretos.

---

### 🔹 5. expert-advisors/documentation-expert — Experto en Documentación

**Mentalidad:**

* Docs como producto: navegación, reutilización, ejemplos reproducibles.
* Trazabilidad entre decisiones (ADRs), código y contratos de datos.

**Responsabilidades:**

* AGENTS/READMEs/KDoc/YAML coherentes y versionados.
* Diagramas (mermaid/plantuml) + tablas de trazabilidad.

**Checklist:**

* [ ] Índice navegable y anchors
* [ ] `@security` y contexto de decisión
* [ ] Changelog y convenciones de nombres

---

## 👥 Roles clave de producto (R&R + **mentalidad por rol**)

**Product Manager (PM)**

* *Mentalidad:* impacto/KPI‑driven (retención, FTUE, ARPDAU). Claridad de alcance y trade‑offs.
* *Responsabilidades:* roadmap, KPIs, backlog y criterios de salida.

**UX/UI Designer**

* *Mentalidad:* accesibilidad y consistencia (design tokens, estados). Rapidez para validar.
* *Responsabilidades:* research, wireframes, prototipos Compose‑friendly, specs completas.

**Android Tech Lead / Arquitecto**

* *Mentalidad:* *guardrail* técnico; estabilidad y escalabilidad.
* *Responsabilidades:* decisiones de módulos, DI, estrategia de tests, PRs críticos.

**Android Developer(s) – App**

* *Mentalidad:* foco en DX/UX y confiabilidad.
* *Responsabilidades:* features Compose, navegación, manejo de estado/errores.

**Android Dev – Datos/Persistencia**

* *Mentalidad:* integridad de datos, *offline‑first*.
* *Responsabilidades:* Room, migraciones, paginado, transacciones atómicas, colas de sync.

**Backend/Realtime Engineer**

* *Mentalidad:* baja latencia, seguridad y costo.
* *Responsabilidades:* signaling (WS/serverless), rate limiting, auth, endpoints wallet/matches.

**Firebase/Cloud Engineer**

* *Mentalidad:* seguridad por defecto y eficiencia de costos.
* *Responsabilidades:* Firestore/RTDB, reglas, FCM/Remote Config, observabilidad.

**QA Engineer (Automation)**

* *Mentalidad:* *break it early*; cobertura inteligente.
* *Responsabilidades:* unit/instrumented/UI (Macrobenchmark/Espresso), resiliencia offline/online.

**DevOps / CI‑CD**

* *Mentalidad:* pipelines confiables, reproducibles y rápidos.
* *Responsabilidades:* cache, linters, Jacoco, firmas, Play Console tracks.

**Security/Privacy**

* *Mentalidad:* *least privilege* y defensa en profundidad.
* *Responsabilidades:* HTTPS, secretos, PII, OWASP Mobile, threat modeling.

**Data/Analytics**

* *Mentalidad:* *measurement first*; decisiones basadas en datos.
* *Responsabilidades:* eventos (GameStart, MatchResult, CoinsDelta, ResetWallet), dashboards KPIs.

---

## 🔗 Interacciones, handoffs y RACI

| Actividad            | PM | UX | TechLead | Dev | DB Arch | QA | DevOps | Sec | Docs |
| -------------------- | -- | -- | -------- | --- | ------- | -- | ------ | --- | ---- |
| Definir alcance/KPIs | A  | C  | C        | I   | I       | I  | I      | I   | C    |
| Diseño arquitectura  | C  | I  | A        | C   | C       | I  | C      | C   | C    |
| Esquema SQL/ORM      | I  | I  | C        | C   | A       | I  | I      | C   | C    |
| Implementación App   | I  | C  | C        | A/R | C       | C  | I      | I   | I    |
| Pruebas & Calidad    | I  | I  | C        | R   | C       | A  | I      | C   | C    |
| CI/CD & Release      | I  | I  | C        | C   | I       | C  | A      | C   | I    |
| Seguridad & Logs     | I  | I  | C        | R   | C       | C  | C      | A   | I    |
| Documentación        | C  | C  | C        | C   | C       | C  | I      | I   | A    |

**Handoffs clave:** UX → Dev (specs); PM → Dev (criterios/KPIs); DB Arch → Dev (DDL/ORM/migraciones); Dev → QA (build de pruebas); DevOps → Dev (artefactos, signing); Docs cierra release.

---

## 🏗️ Arquitectura Android esperada (Clean/MVVM) + estándares

```
app/
├─ SQL/PrimerSQL.sql
├─ util/loggingDecentralizedLogger.kt
├─ core/
│  ├─ domain/
│  ├─ usecases/
│  └─ common/
├─ infra/
│  ├─ persistence/
│  ├─ network/
│  └─ di/
├─ presentation/
└─ integration/
   └─ work/
```

**Estándares:** Kotlin 2 espacios, sin `;`, trailing commas; I/O `Dispatchers.IO`; HTTPS (10s); WorkManager con restricciones/backoff; Room export + migration tests; `Result` y `AgentError`.

---

## 🔒 Políticas de seguridad, logging y privacidad

* **Nunca** PII en logs; usar `redact()`; `traceId` transversal.
* TLS/HTTPS, timeouts 10s, reintentos ≤3 con jitter; idempotencia donde aplique.
* Rotación de logs y métricas (latencia, error rate).
* Threat modeling básico; OWASP Mobile checklist en cada release.

---

## 🚀 CI/CD de referencia (GitHub Actions) + calidad

**Calidad:** ktlint, detekt, Jacoco (≥80% líneas / ≥70% branches).
**Flujo CI:** lint → build → unit tests → instrumentation (si aplica) → cobertura → artefactos.
**Release:** firmas, notes, Play Console (internal/alpha), *version bump*.

**Quality gate (bloqueantes):** PII en logs • migraciones sin test • cobertura bajo objetivo • fallos linters.

---

## 🧾 Plantillas: PR, Issues, Review y Documentación

**PR:** Resumen • Cambios • Riesgos • Checklist (lowerCamelCase/KDoc/Logger/Migraciones/Tests/Cobertura/CI) • Evidencias • Changelog

**Issue:** Contexto • Aceptación • Métricas • Impacto • Dueño/ETA

**Review:** Hallazgos críticos/mayores/menores • Recomendaciones • Puntuación (1–5)

**Docs:** Índice • Objetivo • Arquitectura • Contratos • Ejemplos • Trazabilidad • ADRs

---

## 🧠 Meta‑prompt global (todos los agentes y roles)

> **Instrucciones internas:**
>
> * Delibera en privado sobre requisitos, riesgos, arquitectura, seguridad, rendimiento y pruebas.
> * Verifica compatibilidad **YAML ↔ ORM ↔ SQL ↔ Gradle** y uso de `loggingDecentralizedLogger`.
> * Si falta información, **asume valores conservadores**, decláralos en *Suposiciones* y ofrece cómo parametrizarlos.
> * No expongas tu cadena de pensamiento; entrega **conclusiones**, **código/documentos**, **tests** y **plan de acción**.
> * Estructura la salida con el **Contrato de salida** y las **Rúbricas**; cita rutas de archivo, nombres de clases y snippets listos para pegar.

---

## 📦 Contrato de salida y rúbricas

**Salida estándar (toda tarea):**

1. Resumen técnico (2–5 líneas)
2. Código/documento completo (con rutas)
3. KDoc/Markdown con `@security`
4. Pruebas y/o validaciones (incluye migraciones si aplica)
5. Observaciones (SQL, errores, concurrencia, recursos)
6. Checklist marcada
7. Suposiciones y próximos pasos

**Rúbrica general (0–5):** Técnico, Seguridad/Privacidad, Observabilidad, Testing, Documentación, Arquitectura/Acoplamiento. **Nota global** y justificación breve.

---

## ✅ Checklists globales

**Global:** lowerCamelCase • KDoc en español con `@security` • Logger descentralizado + `traceId` • YAML↔ORM↔SQL consistente • Corrutinas seguras • Result/AgentError • Tests (unit/integration/migration) con cobertura objetivo • CI verde • Docs actualizadas (AGENTS/README/ADR/ERD)

**Android:** WorkManager con restricciones/backoff • Room export + migration tests • Networking HTTPS + timeouts (10s) + errores tipados

**Datos/DB:** Índices adecuados • constraints FK/CHECK/UNIQUE • plan de rollback/backfill

**Seguridad:** Threat modeling • OWASP Mobile • secretos gestionados

**Analytics:** Eventos (GameStart, MatchResult, CoinsDelta, ResetWallet) • dashboards KPIs

---

> **Cierre:** Este documento es el **contrato operativo** entre agentes y roles. Mantenerlo al día forma parte del *Definition of Done* de cada release.

---

# 🧱 Blueprint MVVM — Proyecto de ejemplo *JuegosAzar*

> **Objetivo:** incorporar una **arquitectura MVVM + Clean Architecture** completa, compatible con **Compose, Room, Firebase (REST), Multiplayer (WebSocket/Serverless), Moshi/Gson, Retrofit**, y prácticas de desacoplamiento. Cada componente lista su **función** de forma breve.

## 🗂️ Estructura de paquetes (módulo `app/`)

```
app/
├─ core/
│  ├─ domain/
│  │  ├─ entities/              # Entidades de dominio puras (p.ej. Moneda, Lanzamiento, Partida)
│  │  ├─ valueobjects/          # Tipos seguros (p.ej. PlayerId, Coins)
│  │  └─ repositories/          # Interfaces de repos (GameRepository, WalletRepository)
│  ├─ usecases/
│  │  ├─ lanzarDadoUseCase.kt   # Ejecuta lógica de juego; devuelve Result
│  │  ├─ obtenerMonedasUseCase.kt
│  │  ├─ registrarResultadoUseCase.kt
│  │  └─ syncPartidasUseCase.kt # Sincroniza offline↔online
│  └─ common/
│     ├─ result/Result.kt       # Result.Ok/Err
│     ├─ errors/AgentError.kt   # Errores tipados (Network/Timeout/Validation/Database/Unknown)
│     ├─ logger/LoggerFacade.kt # Wrapper hacia loggingDecentralizedLogger
│     └─ mappers/               # Mappers entidad↔DTO↔DB
├─ infra/
│  ├─ network/
│  │  ├─ retrofit/
│  │  │  ├─ GameApi.kt          # REST (Firebase callable/RTDB/Firestore REST)
│  │  │  ├─ RetrofitModule.kt   # Cliente, timeouts, interceptores (traceId)
│  │  │  └─ dto/                # DTOs de red (GameDto, WalletDto)
│  │  ├─ websocket/
│  │  │  ├─ RealtimeClient.kt   # Señalización multiplayer y eventos
│  │  │  └─ MessageModels.kt    # Mensajes WS (joinRoom, roll, result)
│  │  └─ serializers/
│  │     └─ MoshiOrGson.kt      # Config común Moshi/Gson (una sola, seleccionar por build)
│  ├─ persistence/
│  │  ├─ db/AppDatabase.kt      # Room DB
│  │  ├─ dao/
│  │  │  ├─ PartidaDao.kt       # Historial local
│  │  │  └─ WalletDao.kt        # Saldo local
│  │  └─ model/
│  │     ├─ PartidaEntity.kt
│  │     └─ WalletEntity.kt
│  ├─ realtime/
│  │  └─ FirebaseRestClient.kt  # Llamadas REST a Firebase (Firestore/RTDB) + Remote Config
│  └─ di/
│     └─ AppModules.kt          # Hilt/Koin: repos, usecases, Retrofit, WS, Room
├─ data/
│  ├─ repositories/
│  │  ├─ GameRepositoryImpl.kt  # Orquesta Room + REST + WS
│  │  └─ WalletRepositoryImpl.kt
│  └─ sources/
│     ├─ LocalDataSource.kt     # Room
│     ├─ RemoteDataSource.kt    # Retrofit (Firebase REST)
│     └─ RealtimeDataSource.kt  # WebSocket / FCM
├─ presentation/
│  ├─ navigation/
│  │  └─ AppNavigator.kt        # Gráfico de navegación Compose
│  ├─ welcome/WelcomeViewModel.kt & WelcomeScreen.kt
│  ├─ menu/MenuViewModel.kt & MenuScreen.kt
│  ├─ play/PlayViewModel.kt & PlayScreen.kt
│  ├─ history/HistoryViewModel.kt & HistoryScreen.kt
│  └─ settings/SettingsViewModel.kt & SettingsScreen.kt
└─ integration/
   ├─ work/SyncWorker.kt        # Reintentos con backoff, restricciones
   ├─ analytics/AnalyticsTracker.kt # Eventos (GameStart, MatchResult, CoinsDelta)
   └─ notifications/Notifier.kt # Notificaciones de resultados/logros
```

## 🧩 Función de cada componente (breve)

* **entities/**: modelos puros de negocio, sin dependencias de frameworks.
* **valueobjects/**: tipos con validación interna (evita strings crudos para IDs/monedas).
* **repositories/** (interfaces): contrato estable entre dominio y datos.
* **usecases/**: orquestan reglas; 1 caso de uso = 1 responsabilidad.
* **Result/AgentError**: error handling explícito y testeable.
* **mappers/**: conversión DTO↔Entidad↔Entity Room.
* **RetrofitModule**: cliente HTTPS con timeouts 10s, interceptor `traceId` y `redact()`.
* **GameApi**: define endpoints REST (Firebase callable/Firestore REST/RTDB REST).
* **RealtimeClient**: conexión WS para partidas en tiempo real (join, roll, result).
* **AppDatabase/Dao/Entity**: persistencia local, *paginación* para historial.
* **FirebaseRestClient**: capa REST para Fire* (token, reglas, retries con jitter).
* **RepositoriesImpl**: *merge* de fuentes (local/remote/realtime); políticas *offline‑first*.
* **Navigation**: rutas tipadas y *deep links* si aplica.
* **ViewModels**: exponen `UiState` inmutable y `UiEvent`; usan *usecases*.
* **Screens**: Compose; accesibles; sin lógica de negocio.
* **SyncWorker**: reintentos/backoff; idempotencia; cola de sync.
* **AnalyticsTracker**: KPIs y fun events; adaptable a Firebase Analytics/Ampli.
* **Notifier**: integra FCM/WorkManager para notificaciones locales/remotas.

## 🔌 Integraciones clave

* **Firebase vía REST**: Firestore (REST) o RTDB REST. Usar reglas de seguridad, claves en keystore y `Authorization` por sesión.
* **Multiplayer**: WS para signaling (salas, turnos, lanzamientos). Persistir snapshots en Room para *rejoin* y *recovery*.
* **Serialización**: **Moshi** por defecto; **Gson** opcional (solo uno activo por *buildType*).

## 🧪 Testing mínimo por capa

* **Usecases**: Mock repos; tests deterministas de reglas (ganar/perder monedas).
* **RepositoriesImpl**: tests de fusión fuentes + manejo de errores (Network/Timeout/Database).
* **DAO/Migraciones**: `MigrationTest` con *Room in‑memory*; verificaciones de índices.
* **ViewModels**: `Turbine`/`runTest` para flujos de estado.
* **E2E ligero**: *happy path* de lanzar→guardar→listar historial.

## 🔒 Seguridad/Observabilidad específicas

* `traceId` por sesión/partida para correlación de logs.
* `redact()` en IDs/tokens; **nunca** PII.
* Reglas de Firebase alineadas a modelo de auth (scoped access por jugador/room).
* Reintentos ≤3 (jitter) y *circuit breaker* para WS si hay fallo repetido.

## 🧠 Prompt operativo (AGENT – Arquitecto MVVM Android)

> Usa este prompt para que el agente genere o refactorice la arquitectura completa, con código y descripciones por componente.

```
Eres un experto arquitecto en MVVM para Android. Analiza de manera minuciosa la arquitectura actual y **diseña/actualiza** una **arquitectura MVVM + Clean** para el proyecto *JuegosAzar* con **Compose, Room, Firebase (REST), Multiplayer (WebSocket/Serverless), Moshi/Gson y Retrofit**.

Requisitos:
1) Genera la estructura de paquetes y archivos como en el Blueprint.
2) Para cada componente (paquete/clase), añade **explicación breve** de su función.
3) Implementa scaffolds de: `Result`, `AgentError`, `RetrofitModule`, `GameApi`, `AppDatabase`, `Daos`, `RepositoriesImpl`, `Usecases`, `ViewModels`, `Screens` (Welcome, Menu, Play, History, Settings), `SyncWorker`, `AnalyticsTracker`, `Notifier`.
4) Configura DI (Hilt/Koin), timeouts HTTPS 10s, backoff exponencial, `traceId`, `redact()`.
5) Incluye **tests mínimos** por capa y un `MigrationTest` de Room.
6) Entrega salida con el **Contrato estándar** (Resumen → Código con rutas → KDoc → Pruebas → Validaciones → Checklist → Suposiciones).
```

## 📋 Checklist adicional (MVVM JuegosAzar)

* [ ] Todas las pantallas Compose creadas con estados inmutables.
* [ ] Navegación tipada; backstack controlado.
* [ ] Repos *offline‑first* con fusión Room/REST/WS.
* [ ] WS resiliente (reconexión, *heartbeats*).
* [ ] Reglas Firebase auditadas y probadas.
* [ ] Analytics de eventos clave y panel de KPIs.
* [ ] Tests: usecases, repos, VM, migraciones.

---

## 📊 KPIs & esquema de analytics

**Eventos mínimos (Data/Analytics):**

* `GameStart { playerId, sessionId, coinsBefore }`
* `RollDice { playerId, sessionId, bet, result, coinsDelta }`
* `MatchResult { playerId, sessionId, outcome(win|lose|draw), coinsAfter }`
* `ResetWallet { playerId, sessionId, reason }`
* `SettingsChanged { playerId, key, value }`

**Métricas clave:** Retención D1/D7/D30, FTUE completion, ARPDAU (si aplica), %Crash free, p95 latencia WS/REST.

---

## ⚙️ GitHub Actions (pipeline YAML listo)

```yaml
name: android-ci
on: [push, pull_request]
jobs:
  build-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { distribution: temurin, java-version: '17' }
      - name: Ktlint
        run: ./gradlew ktlintCheck --no-daemon
      - name: Detekt
        run: ./gradlew detekt --no-daemon
      - name: Unit tests
        run: ./gradlew testDebugUnitTest --no-daemon
      - name: Jacoco report
        run: ./gradlew jacocoTestReport --no-daemon
      - name: Build debug
        run: ./gradlew assembleDebug --no-daemon
      - name: Upload reports
        uses: actions/upload-artifact@v4
        with:
          name: reports
          path: |
            **/build/reports/**
            **/build/jacoco/**
```

---

## ☁️ Firebase (reglas ejemplo — Firestore)

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /players/{playerId} {
      allow read, write: if request.auth != null && request.auth.uid == playerId;
    }
    match /rooms/{roomId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null &&
                   request.resource.data.members.hasOnly([request.auth.uid]) == false;
    }
    match /history/{doc} {
      allow read, write: if request.auth != null &&
        request.resource.data.playerId == request.auth.uid;
    }
  }
}
```

---

## 🔌 Retrofit & DI (Hilt) — snippets

**RetrofitModule.kt**

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
  @Provides @Singleton
  fun provideOkHttp(): OkHttpClient = OkHttpClient.Builder()
    .connectTimeout(10, TimeUnit.SECONDS)
    .readTimeout(10, TimeUnit.SECONDS)
    .writeTimeout(10, TimeUnit.SECONDS)
    .addInterceptor { chain ->
      val traceId = UUID.randomUUID().toString()
      chain.proceed(
        chain.request().newBuilder()
          .addHeader("X-Trace-Id", traceId)
          .build()
      )
    }
    .build()

  @Provides @Singleton
  fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
    .baseUrl(BuildConfig.API_BASE_URL)
    .client(client)
    .addConverterFactory(MoshiConverterFactory.create())
    .build()

  @Provides @Singleton
  fun provideGameApi(retrofit: Retrofit): GameApi = retrofit.create(GameApi::class.java)
}
```

**AppModules.kt**

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModules {
  @Provides @Singleton fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
    Room.databaseBuilder(ctx, AppDatabase::class.java, "app.db")
      .fallbackToDestructiveMigrationOnDowngrade()
      .build()

  @Provides fun providePartidaDao(db: AppDatabase) = db.partidaDao()
  @Provides fun provideWalletDao(db: AppDatabase) = db.walletDao()

  @Provides @Singleton
  fun provideRepositories(
    daoP: PartidaDao,
    daoW: WalletDao,
    api: GameApi,
    rt: RealtimeClient
  ): GameRepository = GameRepositoryImpl(
    local = LocalDataSource(daoP, daoW),
    remote = RemoteDataSource(api),
    realtime = RealtimeDataSource(rt)
  )
}
```

---

## 🗃️ Room — migración y test

**MigrationTest.kt**

```kotlin
@RunWith(AndroidJUnit4::class)
class MigrationTest {
  @get:Rule val helper = MigrationTestHelper(
    InstrumentationRegistry.getInstrumentation(),
    AppDatabase::class.java.canonicalName,
    FrameworkSQLiteOpenHelperFactory()
  )

  @Test fun migrate1to2() {
    helper.createDatabase("app.db", 1).apply { close() }
    val db = Room.databaseBuilder(
      ApplicationProvider.getApplicationContext(),
      AppDatabase::class.java, "app.db"
    ).addMigrations(MIGRATION_1_2).build().openHelper.writableDatabase
    db.query("SELECT * FROM Partida LIMIT 1")
    db.close()
  }
}
```

---

## 🔄 WebSocket — política de reconexión

```kotlin
class RealtimeClient(/* ... */) {
  private var retry = 0
  suspend fun connect() {
    while (retry < 3) {
      try { /* abrir socket, enviar heartbeat */ return } 
      catch (e: IOException) {
        delay((1.shl(retry)) * 1000L + Random.nextLong(250))
        retry++
      }
    }
    throw AgentError.Network
  }
}
```

---

## 🧭 Navegación Compose (tipada)

```kotlin
@Serializable data class HistoryArgs(val fromDate: String? = null)

NavHost(navController, startDestination = "welcome") {
  composable("welcome") { WelcomeScreen(/*...*/) }
  composable("menu") { MenuScreen(/*...*/) }
  composable("play") { PlayScreen(/*...*/) }
  composable<HistoryArgs>("history") { HistoryScreen(/*...*/) }
  composable("settings") { SettingsScreen(/*...*/) }
}
```

---

## 🗄️ DataStore (preferencias de usuario)

```kotlin
object SettingsKeys {
  val music = booleanPreferencesKey("music")
  val sfx = booleanPreferencesKey("sfx")
  val dark = booleanPreferencesKey("dark")
}
class SettingsRepository(private val ds: DataStore<Preferences>) {
  val settings: Flow<Settings> = ds.data.map { p ->
    Settings(p[music] ?: true, p[sfx] ?: true, p[dark] ?: false)
  }
  suspend fun setMusic(enabled: Boolean) { ds.edit { it[music] = enabled } }
}
```

---

## 🚢 Release: Play Console & notas

**Flujo:** internal → closed/alpha → production • *versionCode* + *changelog* • *mapping.txt* guardado.

**Plantilla de notas:**

* Novedades
* Correcciones
* Riesgos conocidos
* Métricas esperadas

---

## 📝 ADR — plantilla breve

```
ADR-0001: Serializador por defecto (Moshi)
Contexto: Se requieren DTOs multiplataforma y rendimiento.
Decisión: Usar Moshi; Gson queda para compatibilidad legacy en buildType `legacy`.
Consecuencias: Homogeneidad en adapters; menor reflexión; tests adaptados.
```

---

## 🔒 Riesgos y mitigaciones

* **WS inestable:** reconexión exponencial + *heartbeats* + *circuit breaker*.
* **Drift de esquema:** tests de migración + validación YAML↔ORM↔SQL en CI.
* **PII en logs:** `redact()` + linters de seguridad.
* **Timeouts/latencia:** métricas p95, *bulk* y paginación en REST.

---

## 🔗 Mapeo pantallas ↔ casos de uso ↔ repos

| Pantalla | UseCases                                     | Repos              |
| -------- | -------------------------------------------- | ------------------ |
| Welcome  | —                                            | —                  |
| Menú     | obtenerMonedasUseCase                        | WalletRepository   |
| Play     | lanzarDadoUseCase, registrarResultadoUseCase | GameRepository     |
| History  | syncPartidasUseCase                          | GameRepository     |
| Settings | —                                            | SettingsRepository |

---

> Fin del anexo MVVM JuegosAzar. Mantener estas secciones como **Definition of Done** para features relacionadas.

---

# 🧩 Addendum de completitud (100%)

> Este anexo refuerza áreas faltantes para garantizar cobertura total en arquitectura, seguridad, UX y operación.

## ♿ Accesibilidad (a11y) & Localización (i18n/l10n)

* **Texto escalable** y contraste AA/AAA (Compose `MaterialTheme` + `LocalContentColor`).
* **Semántica** en componentes (`Modifier.semantics`, `contentDescription`).
* **Focusable** y navegación por teclado/rotación.
* **Idiomas**: estructura `strings.xml` por `values-xx` y `plural`. Política de *fallback*.
* **Formateo regional** (`NumberFormat`, `DateTimeFormatter` con `Locale`).
* **Tests**: snapshot accesible y TalkBack (manual/automatizado donde aplique).

## 🧭 Gestión de estado (UDF sobre MVVM)

* Unidirectional Data Flow: `UiState` inmutable + `UiEvent` + `Effect` de un solo uso.
* **Side‑effects** aislados (`LaunchedEffect`, `DisposableEffect`).
* **Regla**: ninguna *Screen* conoce fuentes de datos; todo pasa por ViewModel→UseCases.

## 🌐 Config multi‑entorno y Feature Flags

* *Build variants*: `dev`, `staging`, `prod` con `BuildConfig.API_BASE_URL` y `SERIALIZER` (moshi|gson).
* **Remote Config** (Firebase): *kill‑switch*, *feature flags*, *experimentos*. Documentar valores y *owners*.
* **Estrategia de *rollout***: `% de usuarios`, **guardrails** y plan de rollback.

## 🔐 Gestión de secretos y llaves

* **Keystore** Android para tokens locales.
* `EncryptedSharedPreferences` / *Tink* para datos sensibles.
* **Nunca** llaves en repos; usar *Gradle secrets* y variables de entorno.
* **Cert pinning** opcional con OkHttp (`CertificatePinner`).

## 📴 Offline‑first avanzado

* **Cola transaccional**: operaciones de juego/monedas en `SyncQueue` con *retries* y *idempotency key*.
* **Política de merge**: *last‑write‑wins* para ajustes; *server‑authoritative* para wallet.
* **Conflictos**: registrar *conflict events* y ofrecer *undo* local si procede.

## 🤝 Multiplayer: consistencia & sincronización

* **Modelo**: server‑authoritative; clientes envían *intents* (roll/bet/join).
* **Ordenación**: timestamps de servidor + *room sequencer*.
* **Reintentos** WS con *heartbeat*; *circuit breaker* y *backoff*.
* **Rejoin**: *state snapshot* en Room para *resume*.

## 🎨 Design System Compose

* **Tokens** temáticos (espaciado, tipografía, radios, elevaciones) en `designsystem/`.
* **Componentes** atómicos con estados (enabled/disabled/error/empty/loading).
* **Previews** nombradas (`PreviewSmall/Medium/Large`, `UiMode`).

## 🧪 Contratos de API y Mock Server

* **Pacto** de contrato: tests de cliente con *MockWebServer* y *JSON fixtures* (`src/test/resources/fixtures`).
* **Validación** de esquemas (JSON‑Schema) para DTOs críticos.

## 📈 Presupuestos de performance & *profiling*

* **Budgets**: inicio < 800ms, *frame* > 60fps, p95 red < 400ms, memoria estable.
* **Herramientas**: *Baseline Profiles*, *Macrobenchmark*, *Trace*. Reglas en CI.

## 🔥 Crash reporting & SLO/SLA

* **Crashlytics** o equivalente: % *crash‑free* por versión (> 99.5%).
* **Alertas**: latencia p95 REST/WS y error‑rate > 2%.
* **SLOs**: disponibilidad 99.9% para backend; *RTO/RPO* documentados.

## 🧹 Linter anti‑PII para logs (regla de ejemplo)

* **Regla estática** (detekt custom): bloquear `.info|.debug` con claves tipo `token|email|address|dni`.
* **Whitelist** de campos permitidos y `redact()` obligatorio.

## 🔢 Versionado y compatibilidad

* **SemVer** para módulos Kotlin; *API surface* controlado.
* *Mobile app*: `versionCode` incremental + `versionName` SemVer.
* **Compatibilidad** de modelos persistentes + *migration plan*.

## 📦 Semillas y migraciones de datos

* Semillas para entornos dev/staging (monedas demo, rooms de prueba).
* Scripts de limpieza y *fixtures* reproducibles en CI.

## 🧭 Governance de Telemetría

* **Catálogo de eventos** versionado (tabla con owner, payload, GDPR tag).
* **Políticas de retención** y DSAR (borrado bajo solicitud).

## 🧑‍💻 Guía de contribución (extracto)

* *Pequeño y atómico*: un objetivo por PR, < 400 líneas si es posible.
* *Explica riesgos* y *plan de test* dentro del PR.
* *Changelog* y *labels* correctos.

## 📚 Matriz de trazabilidad (módulo ↔ doc ↔ test)

| Módulo              | Doc                   | Test                            |
| ------------------- | --------------------- | ------------------------------- |
| `infra/network`     | ADR‑0001 Serializador | `GameApiContractTest`           |
| `infra/persistence` | ERD v1.2              | `MigrationTest`                 |
| `core/usecases`     | Casos de Uso          | `UseCasesTestSuite`             |
| `presentation/*`    | UX Specs              | `ViewModelTest`, `SnapshotA11y` |

## 🧪 Matriz de pruebas extendida

* **Unitarias**: reglas de negocio, mappers, validadores.
* **Contratos**: REST/WS con *MockWebServer*.
* **Migración DB**: versiones mayores.
* **UI**: Espresso + Macrobenchmark.
* **Resiliencia**: *network chaos* (timeouts, 5xx, pérdida WS).

## 🧱 Plantilla de módulo *designsystem*

```
app/designsystem/
├─ theme/
│  ├─ Color.kt, Shape.kt, Type.kt
│  └─ Theme.kt
├─ components/
│  ├─ AzButton.kt (states + a11y)
│  └─ AzCard.kt
└─ tokens/
   └─ Spacing.kt, Elevation.kt
```

## 🧩 Anti‑corrupción (ACL) entre dominio y red/DB

* Mappers unidireccionales para aislar lógicas de formato (DTO ↔ Entidad ↔ Entity Room).
* **Regla**: dominio nunca importa paquetes de red/DB.

## 🔁 Estrategia de *feature rollout* y *experimentos*

* Definir población, *guardrails* y *success metrics*.
* Rollback automático si KPIs caen > umbral.

---

> Con este addendum, el AGENTS.md cubre 100% de las áreas solicitadas: arquitectura MVVM/Compose, Room/Firebase/WS, seguridad, CI/CD, a11y/i18n, performance, contratos, *rollouts*, gobernanza de datos y operación.
