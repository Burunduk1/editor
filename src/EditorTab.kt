import java.awt.*
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
//        this.addMouseListener(object : MouseAdapter() {
//            override fun mouseClicked(e: MouseEvent) = println("Mouse clicked")
//            override fun mouseEntered(e: MouseEvent) = println("Mouse entered")
//        })
    }
    fun log() {
        println("hi from canvas[$id] size=${this.size}$")
    }

    override fun paintComponent(g: Graphics) {
//        log()
        if (g is Graphics2D) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g.setFont(baseFont)
            val metrics = g.getFontMetrics(baseFont)
            val w = metrics.widths[0]
            val h = metrics.height
            val indexSpace = w * 5
            val wPadding = w / 2
            val hPadding = 2
            g.drawLine(indexSpace, 0, indexSpace, editor.rowCount * h)
            scrollBar.draw(g)
            for (i in 0 until editor.rowCount) {
                for (j in 0 until 80) {
                    val c = editor.get(i, j)
                    g.color = colors.color(c.type)
                    g.drawString("%c".format(c.char), indexSpace + j * w + wPadding, i * h + metrics.ascent + hPadding)
                }
                val index = (i + 1).toString()
                g.drawString(index, indexSpace - metrics.stringWidth(index) - wPadding, i * h + metrics.ascent + hPadding)
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
