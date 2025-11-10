# Paquete `com.diegodiaz.techwizards.ui`

Capa de presentación construida con Jetpack Compose. Contiene navegación, controladores (ViewModels), screens, tema y utilidades responsive.

## Subpaquetes

| Subpaquete | Rol |
| --- | --- |
| `view` | Composables de pantalla y NavGraph. |
| `controller` | ViewModels/controladores que exponen estado (`StateFlow`) y eventos (`SharedFlow`). |
| `navigation` | Declaración de rutas tipadas. |
| `theme` | Theming Material3 (colores, tipografías, `TechWizardsTheme`). |
| `responsive` | Helpers para adaptar layout según tamaño del dispositivo. |

Los controladores consumen casos de uso (`core.usecases`) y exponen datos a las pantallas que viven en `view`.