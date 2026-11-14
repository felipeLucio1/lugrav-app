package com.felipelucio.lugrav

object TagRules {
    const val MAX_NAME_LENGTH = 30

    private val COLOR_PATTERN = Regex("^#[0-9A-Fa-f]{6}$")

    fun normalizeName(raw: String): String = raw.trim()

    fun isValidName(name: String): Boolean {
        val normalized = normalizeName(name)
        return normalized.isNotEmpty() &&
            normalized.length <= MAX_NAME_LENGTH &&
            normalized.none { it == '\n' || it == '\r' || it.isISOControl() }
    }

    fun isDuplicateName(name: String, existingNames: Collection<String>): Boolean {
        val normalized = normalizeName(name)
        return existingNames.any { it.equals(normalized, ignoreCase = true) }
    }

    fun isValidColor(colorHex: String): Boolean = COLOR_PATTERN.matches(colorHex)

    fun requireValidName(name: String): String {
        val normalized = normalizeName(name)
        require(normalized.isNotBlank()) { "Tag name must not be blank" }
        require(normalized.length <= MAX_NAME_LENGTH) {
            "Tag name must have at most $MAX_NAME_LENGTH characters"
        }
        require(normalized.none { it == '\n' || it == '\r' || it.isISOControl() }) {
            "Tag name must not contain control characters"
        }
        return normalized
    }

    fun requireValidColor(colorHex: String): String {
        require(isValidColor(colorHex)) { "Tag color must match #RRGGBB" }
        return colorHex.uppercase()
    }
}
