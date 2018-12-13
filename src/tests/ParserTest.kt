package tests

import model.EditorData
import model.Parser
import model.flat
import model.getText
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class ParserTest {
    // BASE, WHITESPACE, EMPTY, KEYWORD, OPERATOR, NUMBER, COMMENT, STRING
    private fun testPair(text: String, colors: String) {
        val data = EditorData(text.asIterable())
        val colorer = Parser(data)
        colorer.apply()

        println(data.getText())
        val dataFlatten = data.flat().toTypedArray()
        for (i in 0 until colors.length) {
            if (colors[i] != '\n') {
                assertEquals(colors[i].toInt() - '0'.toInt(), dataFlatten[i].type.ordinal)
            }
        }
    }
    @Test
    fun applyTest() {
        testPair(
            text =   "// comment\n'string' class  (10) word",
            colors = "6666666666\n7777777713333311455410000"
        )
    }
}