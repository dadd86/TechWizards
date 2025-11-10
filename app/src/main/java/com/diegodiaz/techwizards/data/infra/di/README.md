# `data.infra.di`

Espacio reservado para módulos de inyección manual o frameworks DI ligeros. `moduloPartidas` será el punto de registro para factories de repositorios relacionados con partidas y matches.

Sugerencias de implementación:

* Exponer métodos `provideJuegoRepository(context)` que deleguen en `ServiceLocator` o construyan nuevas instancias.
* Encapsular la creación de `TransactionRunner` para reutilizarlo entre repositorios.