# Paquete `com.diegodiaz.techwizards.app`

Contiene la infraestructura Android que arranca la aplicación y configura la capa de presentación.

## Componentes

| Clase | Descripción | Funciones clave |
| --- | --- | --- |
| `App` | Subclase de `Application` que inicializa dependencias globales. | `onCreate` registra sinks en `DecentralizedLogger` (`AndroidLogSink`, `FileLogSink`), aplica mascarado PII y delega en `ServiceLocator.init`. |
| `MainActivity` | `ComponentActivity` principal que monta la UI con Jetpack Compose. | `onCreate` habilita `EdgeToEdge`, crea estado `isDarkTheme` con `rememberSaveable` y delega en `TechWizardsTheme { AppRoot(...) }`. |

## Flujo

1. Android crea `App` → `onCreate` prepara logging y repositorios.
2. `MainActivity` crea `NavController` dentro de `AppRoot`.
3. `NavGraph` (desde `ui/view`) gestiona la navegación entre pantallas.

## Dependencias destacadas

* `ServiceLocator` para obtener repositorios (`JuegoRepositoryRoom`, `MatchRepositoryRoom`, etc.).
* `TechWizardsTheme` para estilo consistente entre tema claro/oscuro.
* `AppRoot` que define el `Scaffold` y nav graph principal.