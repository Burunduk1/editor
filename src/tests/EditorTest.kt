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
    private fun<T1> assertAction1(f: KFunction<*>, arg1: T1, y: Int, x: Int) {
        f.call(e, arg1)
        assertEquals(e.cursor.pair, TextPosition(y, x))
    }

    @Test
    fun navigateTest() {
        e = editorWithText("  word word, word_(10)  \n\n30 40\n")
        assertAction(Editor::navigateTermLeft, 0, 0)
        for (x in arrayOf(2, 7, 11, 13, 18, 19, 21, 24, 24))
            assertAction(Editor::navigateTermRight, 0, x)
        for (x in arrayOf(21, 19, 18, 13, 11, 7, 2, 0))
            assertAction(Editor::navigateTermLeft, 0, x)
        assertAction(Editor::navigateRight, 0, 1)
        assertAction(Editor::navigateEnd, 0, 24)
        assertAction(Editor::navigateRight, 0, 24)
        assertAction1(Editor::navigateDown, 1, 1, 24)
        assertAction1(Editor::navigateDown, 1, 2, 24)
        assertAction(Editor::navigateTermRight, 2, 5)
        assertAction(Editor::navigateLeft, 2, 4)
        assertAction(Editor::navigateToBegin, 0, 4)
        assertAction1(Editor::navigateHome, false, 0, 2)
        assertAction(Editor::navigateToEnd, 2, 2)
        assertAction1(Editor::navigateUp, 100, 0, 2)
        assertAction1(Editor::navigateHome, true, 0, 2)
        assertAction1(Editor::navigateHome, false, 0, 0)
    }

    @Test
    fun editTest() {
        e = editorWithText("  word word, word_(10)  \n\n30 40\n")
        assertAction(Editor::navigateEnd, 0, 24)
        assertAction(Editor::editNewline, 1, 2)
        assertEquals(e.rowCount, 4)
        assertAction1(Editor::editTypeChar, 'Z', 1, 3)
        assertAction1(Editor::navigateUp, 1, 0, 3)
        assertAction(Editor::editNewline, 1, 2)
        assertAction1(Editor::navigateHome, false, 1, 0)
        assertAction(Editor::editNewline, 2, 0)
        assertEquals(e.text, "  w\n\n  ord word, word_(10)  \n  Z\n\n30 40")
        assertAction(Editor::editBackspace, 1, 0)
        assertAction(Editor::editBackspace, 0, 3)
        assertAction(Editor::editDelete, 0, 3)
        assertAction(Editor::editDelete, 0, 3)
        assertAction(Editor::navigateEnd, 0, 24)
        assertAction(Editor::editDelete, 0, 24)
        assertAction(Editor::editBackspace, 0, 23)
        assertAction(Editor::editBackspace, 0, 22)
        assertAction(Editor::navigateLeft, 0, 21)
        assertAction(Editor::navigateLeft, 0, 20)
        assertAction(Editor::navigateLeft, 0, 19)
        assertAction(Editor::editDelete, 0, 19)
        assertAction(Editor::editBackspace, 0, 18)
        assertAction(Editor::editDelete, 0, 18)
        assertAction(Editor::editBackspace, 0, 17)
        assertEquals(e.text, "  word word, word)  Z\n\n30 40")
        assertAction(Editor::navigateToEnd, 2, 17)
        assertAction(Editor::navigateEnd, 2, 5)
        assertAction(Editor::editDelete, 2, 5)
        assertAction(Editor::navigateToBegin, 0, 5)
        assertAction1(Editor::navigateHome, false, 0, 2)
        assertAction1(Editor::navigateHome, false, 0, 0)
        assertAction(Editor::editBackspace, 0, 0)
        assertEquals(e.text, "  word word, word)  Z\n\n30 40")
        assertAction1(Editor::navigateDown, 1, 1, 0)
        assertAction(Editor::editDuplicateLine, 2, 0)
        assertAction(Editor::editDuplicateLine, 3, 0)
        assertAction1(Editor::editTypeChar, '*', 3, 1)
        assertAction(Editor::editDuplicateLine, 4, 1)
        assertAction1(Editor::editTypeChar, '*', 4, 2)
        assertAction(Editor::editDeleteLine, 4, 2)
        assertEquals(e.text, "  word word, word)  Z\n\n\n*\n30 40")
    }
}