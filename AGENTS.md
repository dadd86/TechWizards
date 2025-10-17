# AGENTS.md — Android Studio (Kotlin/Gradle)

## Objetivo
Estandarizar cómo desarrollamos, documentamos y validamos el agente en Android (Kotlin), garantizando:
- **Nomenclatura lowerCamelCase** en **todas** las variables, funciones, propiedades y **nombres de archivo** (ver sección “Reglas de estilo”).  
- **Documentación en español** (KDoc) detallando propósito, parámetros, retornos, errores y consideraciones de seguridad.
- **Compatibilidad 100%** con el esquema SQL definido en `SQL/PrimerSQL.sql`.
- **Código seguro y robusto** (validaciones, manejo de errores, límites de recursos, logging responsable).
- Inclusión y uso consistente de `util/loggingDecentralizedLogger.kt` en TODOS los módulos que lo requieran.

