package tests

import model.Editor
import utility.openFileForReading
import utility.openFileForWriting
import org.apache.commons.io.FileUtils
import java.io.File

fun main(args: Array<String>) {
    testDataArray()

    // test model.Editor.save/load and Files
    val e = Editor()
    val f = File("tests/Main.test")
    val tmp = File("tests/tmp.test")
    openFileForReading(f).use {e.load(it)}
    openFileForWriting(f).use {e.save(it)}
    assert(FileUtils.contentEquals(f, tmp)) {println("model.Editor.load/save failed")}
    tmp.delete()
}
