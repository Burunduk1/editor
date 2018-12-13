package tests

import model.Editor
import org.apache.commons.io.FileUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import utility.openFileForReading
import utility.openFileForWriting
import java.io.File

internal class EditorTest {
    @Test
    fun saveAndLoad() {
        val e = Editor()
        val f = File("tests/Main.test")
        val tmp = File("tests/tmp.test")
        openFileForReading(f).use { e.load(it, false) }
        assertEquals(e.text.length, 2654)
        tmp.delete()
        assertFalse(tmp.exists())
        openFileForWriting(tmp).use { e.save(it) }
        assertTrue(FileUtils.contentEquals(f, tmp), "model.Editor.load/save failed")
        tmp.delete()
    }
}