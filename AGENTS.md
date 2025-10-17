# AGENTS.md — Configuración de agente de desarrollo Android (Kotlin/Gradle)

## 🎯 Propósito

Este archivo define las **reglas y el modo de razonamiento** que debe seguir el agente (ChatGPT/GPT-5) al trabajar en proyectos Android escritos en Kotlin y Gradle.

El objetivo es mantener:

* **Razonamiento técnico profundo y contextual**, evaluando implicaciones de seguridad, rendimiento, mantenibilidad y trazabilidad.
* **Respuestas meticulosas y completas**, con ejemplos funcionales y **documentación en español**.
* **Consistencia estructural** con el resto del ecosistema del proyecto (SQL, YAML, backend, DAO, etc.).
* **Estilo de código uniforme y profesional**.

---

## 🧩 Estilo y sintaxis

* Todas las variables, funciones, propiedades, archivos y módulos deben usar **lowerCamelCase** (también nombres de archivo Kotlin).
* Seguir el estándar de formato de Kotlin (2 espacios, sin `;`, comas finales permitidas).
* Importaciones organizadas, sin dependencias redundantes.
* Usar *sealed classes* para errores y *data classes* para modelos.
* **KDoc en español en CADA SECCIÓN DEL CÓDIGO**, con bloque `@security` cuando aplique.

---

## 🧠 Razonamiento esperado del agente

Antes de generar una respuesta:

1. **Analiza el contexto**: tipo de proyecto (móvil, DAO, IoT, financiero, etc.).
2. **Evalúa dependencias cruzadas** (SQL, YAML, repositorios, APIs, configuración Gradle).
3. **Considera la robustez** (validaciones, manejo de excepciones, límites de recursos, concurrencia segura).
4. **Piensa como un arquitecto Android Senior**, no como un asistente de texto.
5. **Responde con código + documentación + explicación técnica.**

> “No solo escribas código: explícame por qué es correcto, cómo escala y cómo se prueba.”

---

## 🧱 Reglas de diseño del código

* Documentar TODO con **KDoc en español**, incluyendo: `@param`, `@return`, `@throws` y la sección **`@security`** (riesgos o decisiones de diseño).
* Requerir uso de `util/loggingDecentralizedLogger.kt` en todos los módulos que interactúen con red, persistencia o procesos críticos.
* Evitar dependencias innecesarias o duplicadas.
* Validar coherencia con el esquema SQL en `SQL/PrimerSQL.sql`.
* Todas las operaciones de I/O deben ser `suspend` y ejecutarse en `Dispatchers.IO`.
* Validar entradas con precondiciones (`require`, validadores dedicados) y devolver **errores tipados**.

---

## 🧩 Arquitectura esperada (desacoplada por capas)

```
agent-android/
├─ SQL/
│  └─ PrimerSQL.sql
├─ util/
│  └─ loggingDecentralizedLogger.kt
├─ core/                     # Dominio (no depende de frameworks)
│  ├─ domain/                # entidades y reglas puras
│  ├─ usecases/              # casos de uso (coordinan repos + validaciones)
│  └─ common/                # tipos Result, AgentError, validadores
├─ infra/                    # Implementaciones concretas
│  ├─ persistence/           # Room/SQL + migraciones
│  ├─ network/               # Retrofit/OkHttp/Kotlinx Serialization
│  └─ di/                    # Hilt/Koin módulos
├─ presentation/ (opcional)  # UI o API interna
│  ├─ cli/                   # comandos utilitarios
│  └─ api/                   # endpoints internos si aplica
├─ integration/
│  └─ work/                  # WorkManager (tareas en background)
└─ samples/                  # App de ejemplo de uso
```

**Dependencias:** `presentation → core/infra`, `integration → core/infra`; **core** no conoce **infra**.
**Convención de nombres de archivo:** también en **lowerCamelCase** (p. ej. `precioRepository.kt`, `obtenerPrecioUseCase.kt`, `agentDatabase.kt`).

---

## 📦 Contrato de respuesta del agente (formato de salida)

Cada respuesta técnica debe incluir, en este orden cuando aplique:

1. **Resumen técnico breve** (2–5 líneas).
2. **Código completo y ejecutable** (Kotlin/Gradle) con **rutas sugeridas**.
3. **KDoc completa** (incluye `@security`).
4. **Pruebas mínimas** (unitarias/integración) si procede.
5. **Validaciones**: notas sobre SQL, errores, concurrencia, recursos.
6. **Checklist de cumplimiento** (marcada).
7. **Suposiciones** hechas y cómo parametrizarlas.

---

## 🧠 Validaciones previas que debe hacer el agente (self‑check)

Antes de entregar código o documentación:

1. Comprobar consistencia con el modelo SQL.
2. Validar la seguridad (sin PII en logs, uso de logger descentralizado).
3. Confirmar uso de corrutinas seguras (Dispatchers correctos, cancelación, timeouts).
4. Incluir bloque de KDoc completo y en español.
5. Incluir test de unidad si aplica.
6. Garantizar nomenclatura `lowerCamelCase`.

---

## 🧪 Pruebas y calidad

* Usar **MockK**, **JUnit5** y **Turbine** (si hay `Flow`).
* Cobertura mínima sugerida: **80% líneas** / **70% branches** (Jacoco).
* Generar ejemplos de tests unitarios con nombres expresivos (`deberiaDevolverPrecioActivo_cuandoActivoExiste()`).
* **Tests de migración Room** obligatorios ante cambios de esquema.

**Estructura de pruebas sugerida**

```
core/
└─ usecases/
   └─ obtenerPrecioUseCaseTest.kt
infra/
└─ persistence/
   └─ migrationTest.kt
```

---

## 🔒 Seguridad y buenas prácticas

* **Nunca** loggear datos sensibles o PII. Usar `redact()` para IDs/tokens.
* TLS/HTTPS obligatorio en red. Timeouts: **10s** connect/read/write.
* Evitar `GlobalScope`; usar `CoroutineScope` inyectado.
* WorkManager con **restricciones** (red/batería) y **backoff exponencial**.
* Proguard/R8: **conservar** clases de serialización.
* Limitar reintentos (máx **3**) con **jitter** y cancelación cooperativa.

---

## 📈 Performance y recursos

* Evitar trabajo pesado en main; limitar paralelismo (p. ej. `Dispatchers.IO.limitedParallelism(4)`).
* Cierres limpios de I/O (`use {}`), paginación en red/DB, operaciones en batch cuando sea posible.
* Minimizar asignaciones en loops críticos; preferir estructuras inmutables donde convenga.

---

## 🧰 Logging estándar (obligatorio)

```kotlin
loggingDecentralizedLogger.info(
    event = "sincronizacionExitosa",
    meta = mapOf("modulo" to "agenteSql", "estado" to "OK")
)
```

**Regla:** sin PII. Identificadores sensibles deben pasar por `redact()`.

---

## 🧷 Modelo de errores y Result canónico (referencia)

```kotlin
sealed class AgentError {
    data object Network : AgentError()
    data object Timeout : AgentError()
    data class Validation(val reason: String) : AgentError()
    data class Database(val cause: Throwable) : AgentError()
    data class Unknown(val cause: Throwable?) : AgentError()
}

sealed class Result<out T, out E> {
    data class Ok<T>(val value: T) : Result<T, Nothing>()
    data class Err<E>(val error: E) : Result<Nothing, E>()
}
```

---

## 🏗️ CI/CD y cumplimiento (recomendado)

**Calidad:** ktlint + detekt + Jacoco.
**Pruebas:** unitarias + migración Room.
**Build:** `assembleRelease` con R8.

**Checklist de PR (pegar en la descripción):**

* [ ] lowerCamelCase en símbolos/archivos.
* [ ] KDoc con `@security`.
* [ ] Sin PII en logs; logger descentralizado.
* [ ] Cambios SQL con migración + test.
* [ ] Cobertura ≥ 80% líneas / 70% branches.
* [ ] Explicación técnica y riesgos.
* [ ] Version bump + changelog (si aplica).

**Gradle raíz (sugerido)**

```kotlin
plugins {
  id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
  id("io.gitlab.arturbosch.detekt") version "1.23.6"
}

ktlint {
  android.set(true)
  filter { exclude("**/build/**") }
}

detekt {
  buildUponDefaultConfig = true
  config.setFrom(files("$rootDir/config/detekt.yml"))
}
```

**Room schema export (módulo infra/persistence)**

```kotlin
android {
  defaultConfig {
    javaCompileOptions {
      annotationProcessorOptions {
        arguments(
          mapOf(
            "room.schemaLocation" to "$projectDir/schemas",
            "room.incremental" to "true"
          )
        )
      }
    }
  }
}
```

---

## 🧠 Meta-prompt de razonamiento profundo (para el agente)

**Instrucciones internas (no mostrar cadenas de pensamiento):**

* Delibera de forma privada y exhaustiva sobre requisitos, riesgos, arquitectura, seguridad, rendimiento y pruebas.
* Verifica compatibilidad con `SQL/PrimerSQL.sql`, uso de `loggingDecentralizedLogger`, nomenclatura lowerCamelCase y corrutinas seguras.
* Si falta información, asume **valores conservadores**, indícalos en “Suposiciones” y ofrece cómo parametrizarlos.
* **No reveles tu razonamiento paso a paso** ni tu proceso interno; entrega solo conclusiones claras, código, KDoc, pruebas y justificación de alto nivel.
* Estructura la salida siguiendo el **Contrato de respuesta** y la **Checklist** de este documento.
* Sé específico y accionable: muestra rutas de archivo, nombres de clases y snippets de Gradle listos para pegar.

**Plantilla de salida esperada:**

1. Resumen técnico
2. Código (con rutas de archivo)
3. KDoc (incluye `@security`)
4. Pruebas (si aplica)
5. Validaciones (SQL, errores, concurrencia, recursos)
6. Checklist de cumplimiento (marcada)
7. Suposiciones y cómo cambiarlas

---

## 📎 Ejemplo mínimo (caso de uso + test)

**Archivo:** `core/usecases/obtenerPrecioUseCase.kt`

```kotlin
class obtenerPrecioUseCase(
  private val precioRepository: precioRepository
) {
  /**
   * Obtiene el precio normalizado de un activo.
   *
   * @param assetId Identificador interno del activo (no nulo ni vacío).
   * @return Precio en unidades base o error tipado.
   * @throws IllegalArgumentException si assetId es inválido.
   * @security
   * - No registra PII.
   * - Redacta identificadores en logs.
   */
  suspend operator fun invoke(assetId: String): Result<Double, AgentError> {
    require(assetId.isNotBlank()) { "assetId vacío" }
    return precioRepository.obtenerPrecio(assetId)
  }
}
```

**Archivo:** `core/usecases/obtenerPrecioUseCaseTest.kt`

```kotlin
class obtenerPrecioUseCaseTest {
  @Test
  fun deberiaDevolverPrecioActivo_cuandoActivoExiste() = runTest {
    // Arrange
    val repo = mockk<precioRepository>()
    coEvery { repo.obtenerPrecio("asset123") } returns Result.Ok(10.5)
    val useCase = obtenerPrecioUseCase(repo)

    // Act
    val result = useCase("asset123")

    // Assert
    assertTrue(result is Result.Ok && result.value == 10.5)
  }
}
```

---

## ✅ Nota final

Antes de entregar cualquier respuesta, el agente debe **razonar en frío** sobre el impacto en rendimiento, mantenibilidad y trazabilidad.
**Nunca improvisar**: la salida debe ser meditada, justificada y alineada con la arquitectura indicada en este documento.
