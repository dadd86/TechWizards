# API de puntuaciones y premio

Especificación REST para registrar puntuaciones (`score`) y administrar el premio vigente (`prize`). La definición OpenAPI completa vive en [`openapiScorePrize.yaml`](./openapiScorePrize.yaml).

## Modelos
- **score**: `{ userId, userName, points, timestamp }`
    - `userId` (string, requerido)
    - `userName` (string, requerido)
    - `points` (entero ≥ 0, requerido)
    - `timestamp` (string ISO-8601, requerido)
- **prize**: `{ description, value, updatedAt }`
    - `description` (string, requerido)
    - `value` (entero ≥ 0, requerido)
    - `updatedAt` (string ISO-8601, requerido)

## Autenticación y control de acceso
- **Firebase token (Bearer JWT)** para apps móviles y web. Se valida el `aud`/`iss` y la firma de Firebase.
- **API key (cabecera `X-API-Key`)** para integraciones de servicio a servicio.
- Endpoints protegidos:
    - `POST /scores`, `GET /scores/top10`, `GET /prize`: aceptan Firebase token **o** API key válida.
    - `PUT /prize`: solo API key con alcance `prize:write` o rol administrativo. Rechaza tokens sin ese permiso.

## Endpoints
### POST /scores
Crea un nuevo puntaje.

**Body**
```json
{
  "userId": "u-123",
  "userName": "MageMaster",
  "points": 1200,
  "timestamp": "2024-05-30T18:22:10Z"
}
```

**Respuestas**
- `201`: payload `Score` persistido.
- `400`: datos inválidos.
- `401`: falta token/API key.

### GET /scores/top10
Devuelve el top 10 ordenado por `points` desc (tie-breaker por `timestamp` asc). Permite filtrar desde una fecha con `?since=<ISO-8601>`.

**Respuesta 200**
```json
{
  "items": [
    {
      "userId": "u-123",
      "userName": "MageMaster",
      "points": 1200,
      "timestamp": "2024-05-30T18:22:10Z"
    }
  ]
}
```

### GET /prize
Retorna el premio vigente.

**Respuesta 200**
```json
{
  "description": "Cofre legendario +500 orbes",
  "value": 500,
  "updatedAt": "2024-05-30T18:30:00Z"
}
```

### PUT /prize
Actualiza el premio vigente (requiere `prize:write`).

**Body**
```json
{
  "description": "Cofre legendario +500 orbes",
  "value": 500
}
```

**Respuestas**
- `200`: premio actualizado.
- `400`: datos inválidos.
- `401`: sin API key.
- `403`: falta alcance `prize:write`.

## Ejemplos de cURL
Registrar puntaje con token Firebase:
```bash
curl -X POST http://localhost:8080/scores \
  -H "Authorization: Bearer <token_firebase>" \
  -H "Content-Type: application/json" \
  -d '{"userId":"u-123","userName":"MageMaster","points":1200,"timestamp":"2024-05-30T18:22:10Z"}'
```

Actualizar premio con API key:
```bash
curl -X PUT http://localhost:8080/prize \
  -H "X-API-Key: <api_key>" \
  -H "Content-Type: application/json" \
  -d '{"description":"Cofre legendario +500 orbes","value":500}'
```

## Entorno local/mock para pruebas rápidas
1. Instala Prism CLI (mock OpenAPI): `npm install -g @stoplight/prism-cli`.
2. Inicia el mock server con hot reload: `prism mock docs/openapiScorePrize.yaml -h 0.0.0.0 -p 8080`.
3. Usa los ejemplos de cURL sobre `http://localhost:8080`. Prism responderá con los contratos definidos.
4. Variables sugeridas para automatizar pruebas:
    - `BASE_URL=http://localhost:8080`
    - `API_KEY=<api_key_test>`
    - `FIREBASE_TOKEN=<token_firebase_test>`

Para un mock sin Node.js, usa Docker:
```bash
docker run --rm -it -p 8080:4010 \
  -v $(pwd)/docs/openapiScorePrize.yaml:/tmp/api.yaml stoplight/prism:4 \
  mock /tmp/api.yaml -h 0.0.0.0 -p 4010
```

## Notas adicionales
- Logs deben omitir PII; redacta `userId` y `userName` en entornos compartidos.
- Incluye `traceId` en headers de respuesta para trazabilidad.
- Mantén los contratos `Score`/`Prize` backwards compatible; agrega campos nuevos como opcionales.