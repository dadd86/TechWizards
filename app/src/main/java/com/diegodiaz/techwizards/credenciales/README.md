# Paquete `com.diegodiaz.techwizards.credenciales`

Provee la abstracción de almacenamiento seguro para credenciales del jugador o tokens remotos.

## Componentes

| Elemento | Rol |
| --- | --- |
| `CredentialsStore` | Interfaz que define el contrato para leer y escribir credenciales protegidas. Se espera que exponga operaciones como `guardar(alias, secreto)` y `recuperar()`. |
| `EncryptedCredentialsStore` | Implementación placeholder preparada para usar APIs como `EncryptedSharedPreferences` o `androidx.security.crypto`. Encapsulará la lógica criptográfica real y delegará en `CredentialsStore`. |

## Integración prevista

* Los controladores o workers accederán al `CredentialsStore` a través del `ServiceLocator` cuando se implemente.
* Las operaciones deben ejecutarse en background para no bloquear la UI y siempre redactar los identificadores antes de enviarlos al logger.