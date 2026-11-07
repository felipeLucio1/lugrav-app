package com.felipelucio.lugrav

object RecordingFileRules {
    const val EXTENSION = ".aac"
    const val MAX_NAME_LENGTH = 100

    private val INVALID_CHARS = Regex("""[\\/:*?"<>|\p{Cntrl}]""")

    fun normalizeName(raw: String): String {
        val trimmed = raw.trim()
        return if (trimmed.endsWith(EXTENSION, ignoreCase = true)) {
            trimmed.dropLast(EXTENSION.length).trim()
        } else {
            trimmed
        }
    }

    fun isValidName(raw: String, currentName: String): Boolean {
        val name = normalizeName(raw)
        return name.isNotBlank() &&
            name != currentName &&
            name != "." &&
            name != ".." &&
            !name.startsWith(".") &&
            !name.endsWith(".") &&
            name.length <= MAX_NAME_LENGTH &&
            !INVALID_CHARS.containsMatchIn(name)
    }
}
