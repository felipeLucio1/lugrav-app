package com.felipelucio.lugrav

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RecordingFileRulesTest {

    private val currentName = "lugrav_20260101_120000"

    @Test
    fun `when name is empty then should be invalid`() {
        assertFalse(RecordingFileRules.isValidName("", currentName))
    }

    @Test
    fun `when name contains only whitespace then should be invalid`() {
        assertFalse(RecordingFileRules.isValidName("   ", currentName))
    }

    @Test
    fun `when name equals current name then should be invalid`() {
        assertFalse(RecordingFileRules.isValidName(currentName, currentName))
    }

    @Test
    fun `when name equals current name with extension then should be invalid`() {
        assertFalse(RecordingFileRules.isValidName("$currentName.aac", currentName))
    }

    @Test
    fun `when name contains path separator then should be invalid`() {
        assertFalse(RecordingFileRules.isValidName("nome/invalido", currentName))
        assertFalse(RecordingFileRules.isValidName("nome\\invalido", currentName))
    }

    @Test
    fun `when name contains reserved character then should be invalid`() {
        val invalidNames = listOf(
            "nome:invalido",
            "nome*invalido",
            "nome?invalido",
            "nome\"invalido",
            "nome<invalido",
            "nome>invalido",
            "nome|invalido"
        )

        invalidNames.forEach { name ->
            assertFalse(name, RecordingFileRules.isValidName(name, currentName))
        }
    }

    @Test
    fun `when name is dot or dot dot then should be invalid`() {
        assertFalse(RecordingFileRules.isValidName(".", currentName))
        assertFalse(RecordingFileRules.isValidName("..", currentName))
    }

    @Test
    fun `when name starts or ends with dot then should be invalid`() {
        assertFalse(RecordingFileRules.isValidName(".oculta", currentName))
        assertFalse(RecordingFileRules.isValidName("nome.", currentName))
    }

    @Test
    fun `when name exceeds maximum length then should be invalid`() {
        val longName = "a".repeat(RecordingFileRules.MAX_NAME_LENGTH + 1)
        assertFalse(RecordingFileRules.isValidName(longName, currentName))
    }

    @Test
    fun `when name is valid then should be accepted`() {
        assertTrue(RecordingFileRules.isValidName("Minha gravação", currentName))
        assertTrue(RecordingFileRules.isValidName("a".repeat(RecordingFileRules.MAX_NAME_LENGTH), currentName))
    }

    @Test
    fun `when normalizing name then should trim and remove extension`() {
        assertEquals("Minha gravação", RecordingFileRules.normalizeName("  Minha gravação.aac  "))
        assertEquals("Minha gravação", RecordingFileRules.normalizeName("Minha gravação.AAC"))
        assertEquals("Minha gravação", RecordingFileRules.normalizeName("Minha gravação"))
    }
}
