package com.diegodiaz.techwizards.core.token

import java.util.Base64
import org.json.JSONObject

/**
 * Utilidad para leer claims del JWT sin validar firma.
 *
 * @security
 * Esta lectura se usa solo para habilitar/deshabilitar UI. La autorización real
 * siempre depende del backend.
 */
object TokenClaimsParser {
    /**
     * Determina si el token contiene el claim de administrador.
     *
     * @param token JWT en formato compact.
     * @return `true` si el claim `admin` o `role=admin` está presente.
     * @security No se debe usar para decisiones de backend ni control crítico.
     */
    fun isAdmin(token: String?): Boolean {
        if (token.isNullOrBlank()) return false
        val parts = token.split(".")
        if (parts.size < 2) return false
        val payloadJson = decodePayload(parts[1]) ?: return false
        return payloadJson.optBoolean("admin", false) ||
                payloadJson.optString("role") == "admin" ||
                payloadJson.optJSONObject("claims")?.optBoolean("admin", false) == true
    }

    private fun decodePayload(payload: String): JSONObject? {
        return runCatching {
            val normalized = normalizeBase64(payload)
            val decoded = Base64.getUrlDecoder().decode(normalized)
            JSONObject(String(decoded, Charsets.UTF_8))
        }.getOrNull()
    }

    private fun normalizeBase64(value: String): String {
        val padding = (4 - value.length % 4) % 4
        return value + "=".repeat(padding)
    }
}