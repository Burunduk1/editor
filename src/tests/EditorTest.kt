package tests

import model.Editor
import model.ds.TextPosition
import org.apache.commons.io.FileUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import utility.keyEventHandler
import utility.openFileForReading
import utility.openFileForWriting
import java.io.File
import kotlin.reflect.KFunction

internal class EditorTest {
    @Test
    fun saveAndLoadTest() {
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

    private var e = Editor()

    private fun editorWithText(text: String): Editor {
        val tmp = File("tests/tmp.test")
        val e = Editor()
        openFileForWriting(tmp).use { it.print(text) }
        openFileForReading(tmp).use { e.load(it) }
        return e
    }
    private fun assertAction(f: KFunction<*>, y: Int, x: Int) {
        f.call(e)
        assertEquals(e.cursor.pair, TextPosition(y, x))
    }

    @Test
    fun navigateTest() {
        e = editorWithText("  word word, word_(10)  \n\n30 40\n")
        assertAction(Editor::navigateTermLeft, 0, 0)
        assertAction(Editor::navigateTermRight, 0, 2)
        assertAction(Editor::navigateTermRight, 0, 7)
        assertAction(Editor::navigateTermRight, 0, 11)
        assertAction(Editor::navigateTermRight, 0, 13)
    }
}