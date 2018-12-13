package component

import model.ColorTheme
import model.Editor
import model.Selection
import model.ds.TextPosition
import java.awt.*
import java.awt.event.InputEvent.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JComponent

class EditorCanvas(val editor: Editor, private val colors: ColorTheme) : JComponent() {
    private val scrollBar: ScrollBar by lazy { ScrollBar(this, colors) }
    val selection = Selection(editor.cursor)

    private val metrics: FontMetrics by lazy { graphics.getFontMetrics(colors.baseFont) }
    private val w: Int by lazy { metrics.widths[0] }
    private val h: Int by lazy { metrics.height }
    private val indexSpace: Int by lazy { w * 5 }
    private val wPadding: Int by lazy { w / 2 }
    private val hPadding: Int by lazy { 2 }
    private val preferredHeight: Int
        get() = (editor.rowCount + 0) * h + metrics.ascent + 2 * hPadding

    val rowsOnScreen: Int
        get() = height / h

    private fun setCursorPosition(p: Point) {
        editor.cursor.pair = textPositionAt(p)
        scrollToCursor()
        repaint()
    }
    private fun textPositionAt(p: Point) = TextPosition(
        y = (p.y + scrollBar.y) / h,
        x = (p.x - indexSpace - wPadding) / w
    )

    fun scrollToCursor() {
        updateScrollBarRatio()
        val y0 = editor.cursor.y * h - scrollBar.y
        val y1 = y0 + h
        if (y0 < 0)
            scrollBar.moveParent(y0)
        else if (y1 > height)
            scrollBar.moveParent(y1 - height)
    }

    private fun updateScrollBarRatio() {
        scrollBar.ratio = minOf(1.0, height.toDouble() / preferredHeight)
    }

    /** draw part */

    override fun paintComponent(_g: Graphics) {
        val g = _g as Graphics2D
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // draw text
        val y = scrollBar.y
        for (i in 0 until editor.rowCount) {
            g.color = colors.row(i)
            g.fillRect(0, i * h - y, width, h)
            for (j in 0 until 80) {
                if (selection.isSelected(TextPosition(i, j))) {
                    g.color = colors.selectModeBackground
                    g.fillRect(indexSpace + j * w + wPadding, i * h - y, w, h)
                }
                val c = editor.get(i, j)
                g.font = colors.font(c.type)
                g.color = colors.getColor(c.type)
                g.drawString("%c".format(c.char), indexSpace + j * w + wPadding, i * h + metrics.ascent + hPadding - y)
            }
            val index = (i + 1).toString()
            g.drawString(index, indexSpace - metrics.stringWidth(index) - wPadding, i * h + metrics.ascent + hPadding - y)
        }
        g.drawLine(indexSpace, 0 - y, indexSpace, editor.rowCount * h - y)
        g.color = colors.cursor

        // draw cursor
        val cursorWidth = 2
        g.fillRect(editor.cursor.x * w + indexSpace + wPadding - cursorWidth / 2,editor.cursor.y * h - y, cursorWidth, h)

        // draw scrollbar
        updateScrollBarRatio() // optimize number of listeners => update exactly while draw
        if (height < preferredHeight)
            scrollBar.draw(g)
    }

    /** controller part */

    init {
        val mouseAdapter = object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                val shift = (e.modifiersEx and SHIFT_DOWN_MASK) != 0
                val left = (e.modifiersEx and BUTTON1_DOWN_MASK) != 0
                val right = (e.modifiersEx and BUTTON2_DOWN_MASK) != 0
                if (left || right) {
                    selection.on = shift
                }
                if (left || (right && shift)) {
                    if (indexSpace <= e.x && !scrollBar.isInComponent(e.point)) {
                        setCursorPosition(e.point)
                    }
                }
            }
            override fun mouseDragged(e: MouseEvent) {
                if (!scrollBar.isInComponent(e.point) && !scrollBar.barPressed) {
                    if (!selection.on)
                        selection.startSelection()
                    selection.on = true
                    setCursorPosition(e.point)
                }
            }
        }
        addMouseListener(mouseAdapter)
        addMouseMotionListener(mouseAdapter)
    }
}