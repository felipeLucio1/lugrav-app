package com.felipelucio.lugrav

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TagRulesTest {

    @Test
    fun `when normalize name then should trim whitespace`() {
        assertEquals("Trabalho", TagRules.normalizeName("  Trabalho  "))
    }

    @Test
    fun `when name is blank then should be invalid`() {
        assertFalse(TagRules.isValidName("   "))
    }

    @Test
    fun `when name has content then should be valid`() {
        assertTrue(TagRules.isValidName("Trabalho"))
    }

    @Test
    fun `when color is valid then should be accepted`() {
        assertTrue(TagRules.isValidColor("#29B6F6"))
        assertTrue(TagRules.isValidColor("#29b6f6"))
    }

    @Test
    fun `when color is invalid then should be rejected`() {
        assertFalse(TagRules.isValidColor("29B6F6"))
        assertFalse(TagRules.isValidColor("#29B6F"))
        assertFalse(TagRules.isValidColor("#GGGGGG"))
    }

    @Test
    fun `when require valid name then should return trimmed name`() {
        assertEquals("Trabalho", TagRules.requireValidName(" Trabalho "))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `when require blank name then should throw`() {
        TagRules.requireValidName(" ")
    }

    @Test
    fun `when require valid color then should return uppercase color`() {
        assertEquals("#29B6F6", TagRules.requireValidColor("#29b6f6"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `when require invalid color then should throw`() {
        TagRules.requireValidColor("blue")
    }

    @Test
    fun `when name exceeds max length then should be invalid`() {
        val name = "a".repeat(TagRules.MAX_NAME_LENGTH)
        assertTrue(TagRules.isValidName(name))
        assertFalse(TagRules.isValidName(name + "a"))
    }

    @Test
    fun `when name contains control characters then should be invalid`() {
        assertFalse(TagRules.isValidName("Traba\nlho"))
        assertFalse(TagRules.isValidName("Traba\tlho"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `when require too long name then should throw`() {
        TagRules.requireValidName("a".repeat(TagRules.MAX_NAME_LENGTH + 1))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `when require name with control characters then should throw`() {
        TagRules.requireValidName("Traba\nlho")
    }

    @Test
    fun `when checking duplicate name then should ignore case`() {
        assertTrue(TagRules.isDuplicateName("trabalho", listOf("Trabalho")))
        assertTrue(TagRules.isDuplicateName("  TRABALHO  ", listOf("Trabalho")))
        assertFalse(TagRules.isDuplicateName("Pessoal", listOf("Trabalho")))
    }
}
