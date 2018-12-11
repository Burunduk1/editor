package colors

import java.awt.Color
import parser.CodeType

private fun grey(a: Int) = Color(a, a, a)

val scrollBarButton = Color(100, 100, 100) // if i use fun grey, IDE does not show the color
val scrollBarFocused = Color(150, 100, 100)
val scrollBar = Color(200, 200, 200)

val cursorColor: Color = Color.BLACK

val rowBackgroundOdd = grey(245)
val rowBackgroundEven: Color = Color.WHITE

fun color(c: CodeType) = when (c) {
    CodeType.BASE -> Color.BLACK
    CodeType.KEYWORD -> Color(0, 40, 150)
    CodeType.NUMBER -> Color(0, 0, 255)
    CodeType.OPERATOR -> Color(100, 100, 100)
    else -> Color.BLACK
}
fun isBold(c: CodeType) = (c == CodeType.KEYWORD)
