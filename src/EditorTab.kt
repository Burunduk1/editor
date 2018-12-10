import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseWheelEvent
import java.io.File
import javax.swing.*

data class EditorCanvas(val editor: Editor) : JComponent() {
    companion object {
        var count = 0
        // only mono-fonts are acceptable
        var baseFont = Font("Consolas", Font.PLAIN, 16)
        var boldFont = Font("Consolas", Font.BOLD, 16)
    }
    private val id = count++

    private val scrollBar = ScrollBar(this)

    init {
        this.addMouseWheelListener(object : MouseAdapter() {
//            override fun mouseClicked(e: MouseEvent) = println("Mouse clicked")
//            override fun mouseEntered(e: MouseEvent) = println("Mouse entered")
            override fun mouseWheelMoved(e: MouseWheelEvent) {
                scrollBar.scroll(e.wheelRotation)
                this@EditorCanvas.repaint()
            }
        })
    }
    fun log() {
        println("hi from canvas[$id] size=${this.size}$")
    }

    override fun paintComponent(g: Graphics) {
        //log()
        if (g is Graphics2D) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            val metrics = g.getFontMetrics(baseFont)
            val w = metrics.widths[0]
            val h = metrics.height
            val indexSpace = w * 5
            val wPadding = w / 2
            val hPadding = 2
            val preferredHeight = (editor.rowCount + 1) * h + metrics.ascent + 2 * hPadding
            if (this.height < preferredHeight) {
                scrollBar.ratio = this.height.toDouble() / preferredHeight
                scrollBar.draw(g)
            } else {
                scrollBar.ratio = 1.0
            }
            val y = scrollBar.y
            g.drawLine(indexSpace, -y, indexSpace, editor.rowCount * h - y)
            for (i in 0 until editor.rowCount) {
                for (j in 0 until 80) {
                    val c = editor.get(i, j)
                    g.setFont(if (colors.isBold(c.type)) boldFont else baseFont)
                    g.color = colors.color(c.type)
                    g.drawString("%c".format(c.char), indexSpace + j * w + wPadding, i * h + metrics.ascent + hPadding - y)
                }
                val index = (i + 1).toString()
                g.drawString(index, indexSpace - metrics.stringWidth(index) - wPadding, i * h + metrics.ascent + hPadding - y)
            }
        }
    }
}

class EditorTab(val file: File?) : JPanel(BorderLayout()) {
    private val editor = Editor()
    val canvas = EditorCanvas(editor)
    private val saved = true
    init {
        this.add(canvas)
        file?.let {
            println("editor: load ${file.name}")
            editor.load(files.openFile(file))
        }
    }
    val title: String
        get() {
            if (file == null)
                return "<untitled>"
            return (if (saved) "" else "*") + file.name
        }
}
