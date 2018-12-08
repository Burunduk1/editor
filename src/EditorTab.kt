import java.awt.*
import java.awt.event.*
import javax.swing.*

data class EditorCanvas(val editor: Editor) : JComponent() {
    companion object {
        var count = 0
        var editorFont = Font("Consolas", Font.PLAIN, 16) // only mono-font is acceptable
    }
    private val id = count++
    init {
        this.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) = println("Mouse clicked")
            override fun mouseEntered(e: MouseEvent) = println("Mouse entered")
        })
    }
    override fun paintComponent(g: Graphics) {
        println("canvas[$id] size=${this.size}, call repaint")
        if (g is Graphics2D) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g.setFont(editorFont)
            val metrics = g.getFontMetrics(editorFont)
            val w = metrics.widths[0]
            val h = metrics.height
            val indexSpace = w * 5
            val wpadding = w / 2
            val hpadding = 2
            g.drawLine(indexSpace, 0, indexSpace, editor.rowCount * h)
            for (i in 0 until editor.rowCount) {
                for (j in 0 until 80) {
                    val char = editor.get(i, j)
                    g.drawString("%c".format(char), indexSpace + j * w + wpadding, i * h + metrics.ascent + hpadding)
                }
                val index = (i + 1).toString()
                g.drawString(index, indexSpace - metrics.stringWidth(index) - wpadding, i * h + metrics.ascent + hpadding)
            }
        }
    }
}

data class EditorTab(var filename: String?) : JPanel(BorderLayout()) {
    private val editor = Editor()
    private val canvas = EditorCanvas(editor)
    init {
        this.add(canvas)
        files.openOrCreate(filename)?.let { editor.load(it) }
    }
}
