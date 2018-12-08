import java.io.File

fun main(args: Array<String>) {
    testDataArray()

    val e = Editor()
    File("tests/Test.java").bufferedReader().use {e.load(it)}
    File("tests/tmp.java").printWriter().use {e.save(it)}
}
