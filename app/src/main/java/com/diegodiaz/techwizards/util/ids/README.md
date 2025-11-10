# `util.ids`

Utilidades para generar identificadores únicos.

## Componente

| Clase | Propósito |
| --- | --- |
| `UuidProvider` | Será el wrapper centralizado sobre `UUID.randomUUID()` o proveedores deterministas (ej. para pruebas). La idea es permitir inyección de un generador configurable para repetir escenarios. |

Se recomienda exponer métodos como `fun nuevoId(prefix: String? = null): String` y evitar su uso directo desde la UI; en su lugar, consumirlo vía casos de uso o repositorios.