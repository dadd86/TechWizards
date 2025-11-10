# `ui.navigation`

Define rutas tipadas para el NavHost Compose.

## Componentes

| Elemento | Descripción |
| --- | --- |
| `Ruta` | `sealed class` con objetos (`Bienvenida`, `Menu`, `Jugar`, `Historial`, `Ajustes`). Cada ruta expone `path` para usar en `NavGraph`. |

Las pantallas en `ui/view/NavGraph.kt` usan estas rutas para registrar destinos y facilitar el uso de `when` exhaustivos.