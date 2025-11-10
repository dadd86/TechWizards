# `data.repository`

Agrupa las implementaciones que satisfacen los contratos de `domain.repository`. Las clases de este paquete se encargan de traducir entre DAOs, mapeadores y modelos del dominio.

## Organización

* Subpaquete `impl`: implementaciones Room/DataStore concretas.
* Cada repositorio recibe los DAOs necesarios (inyectados vía `ServiceLocator` o módulos de DI) y, cuando aplica, un `TransactionRunner` para garantizar atomicidad.

## Ciclo típico

1. La UI invoca un caso de uso (`core.usecases`).
2. El caso de uso delega en una interfaz de `domain.repository`.
3. La instancia concreta (`data.repository.impl.*`) ejecuta la operación contra Room/DataStore y devuelve un `Result` con modelos de `domain.model`.

Los repositorios manejan mascarado de logs mediante `DecentralizedLogger` y transforman excepciones en `AgentError` antes de propagarlas.