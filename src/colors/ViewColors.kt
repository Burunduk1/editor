package colors

import java.awt.Color
import parser.CodeType

private fun grey(a: Int) = Color(a, a, a)
val scrollBarButton = Color(100, 100, 100) // if i use fun grey, IDE does not show the color
val scrollBar = Color(200, 200, 200)

fun color(c: CodeType) = when (c) {
    CodeType.BASE -> Color(0, 0, 0)
    CodeType.KEYWORD -> Color(0, 30, 80)
    CodeType.OPERATOR -> Color(100, 100, 100)
    else -> Color(0, 0, 0)
}
fun isBold(c: CodeType) = (c == CodeType.KEYWORD)
