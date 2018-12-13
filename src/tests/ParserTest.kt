package tests

import model.EditorData
import model.Parser
import model.flat
import model.getText
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class ParserTest {
    // BASE=0, WHITESPACE=1, EMPTY=2, KEYWORD=3, OPERATOR=4, NUMBER=5, COMMENT=6, STRING=7
    private fun testPair(text: String, colors: String) {
        val data = EditorData(text.asIterable())
        val colorer = Parser(data)
        colorer.apply()

        println(data.getText())
        val dataFlatten = data.flat().toTypedArray()
        for (i in 0 until colors.length) {
            if (colors[i] != '\n') {
//                println("i = $i : ${colors[i]} and ${dataFlatten[i]}")
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
        testPair(
            text =   "/* // */  /*/ /* //x\n/*",
            colors = "66666666114441441666\n44"
        )
        testPair(
            text =   "/* // */  /*/ /* //x\n/*/",
            colors = "66666666116666666666\n666"
        )
        testPair(
            text =   "/* // */  /*/ /* //x\n/* // */x",
            colors = "66666666116666666666\n666666660"
        )
        testPair(
            text =   """ '"'x"xx"x'x"' " """ + "\n" + """ x"x"x """,
            colors = """17770777707777141""" + "\n" + """1077701"""
        )
        testPair(
            text =   "public class(int+inta + aint + int_int)",
            colors = "333333133333433340000141000014100000004"
        )
        testPair(
            text =   "2+3*500.500'500//500",
            colors = "54545554555455566666"
        )
    }
}