package model

import java.awt.Color
import java.awt.Font
import java.awt.font.TextAttribute

class ColorTheme {
    companion object {
        private fun grey(a: Int) = Color(a, a, a)
    }

    val scrollBarButton = Color(100, 100, 100) // if i use fun grey, IDE does not show the getColor
    val scrollBarFocused = Color(150, 100, 100)
    val scrollBar = Color(200, 200, 200)

    val cursor: Color = Color.BLACK

    val selectModeBackground = Color(220, 220, 255)

    private val rowBackgroundOdd = grey(245)
    private val rowBackgroundEven: Color = Color.WHITE
    fun row(i: Int) = if (i % 2 == 1) rowBackgroundOdd else rowBackgroundEven

    fun getColor(c: CodeType): Color = when (c) {
        CodeType.BASE -> Color.BLACK
        CodeType.KEYWORD -> Color(0, 40, 150)
        CodeType.NUMBER -> Color(0, 0, 255)
        CodeType.OPERATOR -> Color(100, 100, 100)
        CodeType.COMMENT -> Color(0xAA, 0x55, 0)
        CodeType.STRING -> Color(0, 0x80, 0)
        else -> Color.BLACK
    }

    fun isBold(c: CodeType) = (c == CodeType.KEYWORD)

    // only mono-fonts are acceptable
    val baseFont: Font = Font("Consolas", Font.PLAIN, 16)
    private val boldFont = Font.getFont(baseFont.attributes.plus(TextAttribute.WEIGHT to TextAttribute.WEIGHT_BOLD))
    fun font(c: CodeType): Font = if (isBold(c)) boldFont else baseFont
}
