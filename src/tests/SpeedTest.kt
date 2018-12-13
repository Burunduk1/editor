package tests

import model.Editor
import model.ds.CmpPair
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import utility.openFileForReading
import utility.openFileForWriting
import java.io.File
import java.time.Duration

internal class SpeedTest {

    @Test
    fun editBigFile() {
        val e = Editor()
        openFileForReading(File("tests/Main.test")).use { e.load(it, false) }
        val len = e.text.length
        println("loaded $len")
        val tmp = File("tests/tmp")
        assertTimeout(Duration.ofMillis(150)) { // check save
            openFileForWriting(tmp).use {
                for (i in 0 until 1e6.toInt() / len)
                    e.save(it)
            }
        }
        assertTimeout(Duration.ofMillis(150)) { // check load with no colorer
            openFileForReading(tmp).use { e.load(it, false) }
        }
        val len1 = e.text.length
        println("loaded $len1")
        assertTimeout(Duration.ofMillis(1500)) { // check colorer
            e.cursor.pair = CmpPair(10000, 1)
            e.editTypeChar('x')
        }
        val len2 = e.text.length
        assertEquals(len2 - len1, 1)
    }
}