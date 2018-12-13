package tests

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

internal class FilesTest {
    @Test
    fun checkFileForReading() {
        assertNull(utility.checkFileForReading(null))
        assertNotNull(utility.checkFileForReading(File("tests/read-only.txt")))
        assertNull(utility.checkFileForReading(File("tests/b.txt")))
    }

    @Test
    fun checkFileForWriting() {
        assertNull(utility.checkFileForWriting(null))
        assertNull(utility.checkFileForWriting(File("tests/read-only.txt")))
        assertNotNull(utility.checkFileForWriting(File("tests/Main.test")))
        assertNotNull(utility.checkFileForWriting(File("tests/b.txt")))
        assertFalse(File("tests/b.txt").exists())
    }
}