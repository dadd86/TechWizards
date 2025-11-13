# Paquete `com.diegodiaz.techwizards`

Núcleo de la aplicación Android siguiendo arquitectura limpia con capas separadas:

- `app/` Entrada Android (`Application`, `MainActivity`, navegación Compose).
- `core/` Casos de uso y utilidades compartidas.
- `domain/` Modelos y contratos limpios.
- `data/` Persistencia (Room, DataStore) e implementaciones de repositorio.
- `integration/` Servicios Android (multimedia, notificaciones, calendario).
- `ui/` Presentación Jetpack Compose con controladores (ViewModels) y pantallas.
- `util/` Helpers transversales (logging, sync, tiempo, ids).

Cada submódulo está documentado en su propio `README.md` describiendo responsabilidades y límites.