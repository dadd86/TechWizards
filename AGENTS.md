# AGENTS.md — Android Studio (Kotlin/Gradle)

## Objetivo
Estandarizar cómo desarrollamos, documentamos y validamos el agente en Android (Kotlin), garantizando:
- **Nomenclatura lowerCamelCase** en **todas** las variables, funciones, propiedades y **nombres de archivo** (ver sección “Reglas de estilo”).  
- **Documentación en español** (KDoc) detallando propósito, parámetros, retornos, errores y consideraciones de seguridad.
- **Compatibilidad 100%** con el esquema SQL definido en `SQL/PrimerSQL.sql`.
- **Código seguro y robusto** (validaciones, manejo de errores, límites de recursos, logging responsable).
- Inclusión y uso consistente de `util/loggingDecentralizedLogger.kt` en TODOS los módulos que lo requieran.

- Antes de responder, te pido que dediques tiempo a pensar y razonar detenidamente y a analizar con profundidad la siguiente consulta. Considera todos los aspectos relevantes y utiliza tu juicio crítico para formular la mejor respuesta posible. Esta debe ser robusta y detallada y ejecutar su trabajo de manera excepcional y meticulosa