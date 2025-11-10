# `ui.theme`

Define el tema Material3 de Tech Wizards.

## Archivos

| Archivo | Contenido |
| --- | --- |
| `Theme.kt` | Composable `TechWizardsTheme` que alterna entre tema claro/oscuro y expone `MaterialTheme`. Integra el toggle `isDarkTheme` que se gestiona desde `MainActivity`. |
| `Color.kt` | Paleta personalizada: colores primarios, secundarios, fondos y roles extendidos. |
| `Type.kt` | Configuración tipográfica (`Typography`) que armoniza con `UiDims`. |

La UI debe envolver todas las pantallas en `TechWizardsTheme` para garantizar consistencia visual.