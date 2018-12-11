import files.checkFileForWriting
import files.openFileForWriting
import files.selectFile
import java.awt.*
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.*

class EditorCanvas(val editor: Editor) : JComponent() {
    companion object {
        var count = 0
        // only mono-fonts are acceptable
        val baseFont: Font by lazy { Font("Consolas", Font.PLAIN, 16) }
        val boldFont: Font by lazy { Font("Consolas", Font.BOLD, 16) }
    }
    val id = count++
    private val scrollBar: ScrollBar by lazy { ScrollBar(this) }

    private val metrics: FontMetrics by lazy { graphics.getFontMetrics(baseFont) }
    private val w: Int by lazy { metrics.widths[0] }
    private val h: Int by lazy { metrics.height }
    private val indexSpace: Int by lazy { w * 5 }
    private val wPadding: Int by lazy { w / 2 }
    private val hPadding: Int by lazy { 2 }

    init {
        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                if (indexSpace <= e.x && e.x <= width - scrollBar.barWidth) {
                    println("pressed inside [$w $h] x <- (${e.x} - ${indexSpace} - ${wPadding}) / $w")
                    editor.cursor.y = (e.y + scrollBar.y) / h
                    editor.cursor.x = (e.x - indexSpace - wPadding) / w
                    needScroll = true
                    repaint()
                }
            }
        })
    }
    fun log() {
        println("hi from canvas[$id] size=${this.size}$")
    }

    var needScroll = false

    override fun paintComponent(g: Graphics) {
        //log()
        if (g is Graphics2D) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

            if (needScroll) {
                val y0 = editor.cursor.y * h - scrollBar.y
                val y1 = y0 + h
                println("scrolling: y0=$y0 y1=$y1 height=$height")
                if (y0 < 0)
                    scrollBar.move(y0.toDouble())
                else if (y1 > height)
                    scrollBar.move((y1 - height).toDouble())
                needScroll = false
            }

            // draw text
            val y = scrollBar.y
            for (i in 0 until editor.rowCount) {
                g.color = if (i % 2 == 1) colors.rowBackgroundOdd else colors.rowBackgroundEven
                g.fillRect(0, i * h - y, width, h)
                for (j in 0 until 80) {
                    val c = editor.get(i, j)
                    g.setFont(if (colors.isBold(c.type)) boldFont else baseFont)
                    g.color = colors.color(c.type)
                    g.drawString("%c".format(c.char), indexSpace + j * w + wPadding, i * h + metrics.ascent + hPadding - y)
                }
                val index = (i + 1).toString()
                g.drawString(index, indexSpace - metrics.stringWidth(index) - wPadding, i * h + metrics.ascent + hPadding - y)
            }
            g.drawLine(indexSpace, -y, indexSpace, editor.rowCount * h - y)
            g.setColor(colors.cursorColor)

            // draw cursor
            val cursorWidth = 2
            g.fillRect(editor.cursor.x * w + indexSpace + wPadding - cursorWidth / 2,editor.cursor.y * h - y, cursorWidth, h)

            // draw scrollbar
            val preferredHeight = (editor.rowCount + 0) * h + metrics.ascent + 2 * hPadding
            if (this.height < preferredHeight) {
                scrollBar.ratio = this.height.toDouble() / preferredHeight
                scrollBar.draw(g)
            } else {
                scrollBar.ratio = 1.0
            }
        }
    }
}

class EditorTab(var file: File?) : JPanel(BorderLayout()) {
    private val editor = Editor()
    val canvas = EditorCanvas(editor)
    init {
        this.add(canvas)
        file?.let {
            println("editor: load ${file!!.name}")
            editor.load(files.openFileForReading(file!!))
        }
        addKeyListener(object : KeyAdapter() {
            val skippedKeys = arrayOf(8, 10, 127)
            override fun keyTyped(e: KeyEvent) {
                val code = e.keyChar.toInt()
                if (skippedKeys.indexOf(code) != -1 || e.isAltDown || e.isControlDown || e.isActionKey || e.isMetaDown) {
                    println("typed: skip $code =(")
                    return
                }
                println("typed: code=$code")
                canvas.editor.editTypeChar(e.keyChar)
            }
            override fun keyPressed(e: KeyEvent) {
                with (canvas.editor) {
                    when (e.keyCode) {
                        KeyEvent.VK_UP -> {
                            navigateUp()
                        }
                        KeyEvent.VK_DOWN -> {
                            navigateDown()
                        }
                        KeyEvent.VK_LEFT -> {
                            navigateLeft()
                        }
                        KeyEvent.VK_RIGHT -> {
                            navigateRight()
                        }
                        KeyEvent.VK_DELETE -> {
                            editDelete()
                        }
                        KeyEvent.VK_BACK_SPACE -> {
                            editBackspace()
                        }
                        KeyEvent.VK_ENTER -> {
                            editNewline()
                        }
                        KeyEvent.VK_HOME -> {
                            navigateHome()
                        }
                        KeyEvent.VK_END -> {
                            navigateEnd()
                        }
                        else -> return // skip moving
                    }
                }
            }
        })
        canvas.editor.registerNavigateListener { handleCursorAction() }
        canvas.editor.registerEditListener { handleCursorAction() }
    }

    private fun handleCursorAction() {
        println("${canvas.id}: handleCursorAction")
        canvas.needScroll = true
        canvas.repaint()
    }

    @Synchronized fun save() {
        if (file == null)
            file = checkFileForWriting(selectFile(this, JFileChooser.SAVE_DIALOG))
        if (file != null)
            openFileForWriting(file!!).use { editor.save(it) } // unsafe concurrency
    }

    val title: String
        get() {
            if (file == null)
                return "<untitled>"
            return (if (editor.saved) "" else "*") + file!!.name // unsafe concurrency
        }
}
