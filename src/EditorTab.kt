import ds.CmpPair
import files.checkFileForWriting
import files.openFileForWriting
import files.selectFile
import java.awt.*
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.KeyEvent.*
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

    var selectMode = false
    var selectStart = editor.cursor.pair

    private fun isSelected(row: Int, column: Int): Boolean {
        if (!selectMode)
            return false
        val p = CmpPair(row, column)
        val end = editor.cursor.pair
        return if (selectStart < end) selectStart <= p && p < end else selectStart > p && p >= end
    }

    val rowsOnScreen: Int
        get() = height / h

    init {
        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                if (indexSpace <= e.x && e.x <= width - scrollBar.barWidth) {
                    println("pressed inside [$w $h] x <- (${e.x} - ${indexSpace} - ${wPadding}) / $w")
                    editor.cursor.y = (e.y + scrollBar.y) / h
                    editor.cursor.x = (e.x - indexSpace - wPadding) / w
                    scrollToCursor()
                    repaint()
                }
            }
        })
    }
    fun log() {
        println("hi from canvas[$id] size=${this.size}$")
    }

    fun scrollToCursor() {
        val y0 = editor.cursor.y * h - scrollBar.y
        val y1 = y0 + h
        if (y0 < 0)
            scrollBar.move(y0.toDouble())
        else if (y1 > height)
            scrollBar.move((y1 - height).toDouble())
    }

    override fun paintComponent(g: Graphics) {
        //log()
        println("selected: mode=${selectMode} start=${selectStart} end=${editor.cursor.pair}")
        if (g is Graphics2D) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

            // draw text
            val y = scrollBar.y
            for (i in 0 until editor.rowCount) {
                g.color = if (i % 2 == 1) colors.rowBackgroundOdd else colors.rowBackgroundEven
                g.fillRect(0, i * h - y, width, h)
                for (j in 0 until 80) {
                    if (isSelected(i, j)) {
                        g.color = colors.selectModeBackground
                        g.fillRect(indexSpace + j * w + wPadding, i * h - y, w, h)
                    }
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
    companion object {
        val tabSize: Int by lazy { 4 }
    }
    val canvas = EditorCanvas(editor)

    init {
        this.add(canvas)
        file?.let {
            println("editor: load ${file!!.name}")
            editor.load(files.openFileForReading(file!!))
        }
        addKeyListener(object : KeyAdapter() {
            val skippedKeys = arrayOf(8, 9, 10, 13, 27, 127)
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
                val isNavigation = arrayOf(
                    VK_UP,
                    VK_DOWN,
                    VK_LEFT,
                    VK_RIGHT,
                    VK_PAGE_UP,
                    VK_PAGE_DOWN,
                    VK_HOME,
                    VK_END
                ).indexOf(e.keyCode) != -1
                if (!isNavigation || !e.isShiftDown) {
                    canvas.selectMode = false
                } else {
                    with(canvas) {
                        if (e.isShiftDown) {
                            if (!selectMode) {
                                selectStart = editor.cursor.pair
                                selectMode = true
                            }
                        }
                    }
                }
                with(canvas.editor) {
                    when (e.keyCode) {
                        VK_UP -> {
                            navigateUp(1)
                        }
                        VK_DOWN -> {
                            navigateDown(1)
                        }
                        VK_PAGE_UP -> {
                            navigateUp(maxOf(1, canvas.rowsOnScreen - 1))
                        }
                        VK_PAGE_DOWN -> {
                            navigateDown(maxOf(1, canvas.rowsOnScreen - 1))
                        }
                        VK_LEFT -> {
                            if (e.isControlDown)
                                navigateTermLeft()
                            else
                                navigateLeft()
                        }
                        VK_RIGHT -> {
                            if (e.isControlDown)
                                navigateTermRight()
                            else
                                navigateRight()
                        }
                        VK_DELETE -> {
                            editDelete()
                        }
                        VK_BACK_SPACE -> {
                            editBackspace()
                        }
                        VK_ENTER -> {
                            editNewline()
                        }
                        VK_HOME -> {
                            if (e.isControlDown)
                                navigateToBegin()
                            else
                                navigateHome()
                        }
                        VK_END -> {
                            if (e.isControlDown)
                                navigateToEnd()
                            else
                                navigateEnd()
                        }
                        VK_TAB -> {
                            for (i in 0 until tabSize)
                                editTypeChar(' ')
                        }
                        VK_Y -> {
                            if (e.isControlDown)
                                editDeleteLine()
                        }
                        VK_D -> {
                            if (e.isControlDown)
                                editDuplicateLine()
                        }
                    }
                }
            }
        })
        canvas.editor.registerNavigateListener { handleCursorAction() }
        canvas.editor.registerEditListener { handleCursorAction() }
    }

    private fun handleCursorAction() {
        println("${canvas.id}: handleCursorAction")
        canvas.scrollToCursor()
        canvas.repaint()
    }

    fun save(): Boolean {
        if (file == null)
            file = checkFileForWriting(selectFile(this, JFileChooser.SAVE_DIALOG))
        if (file != null) {
            openFileForWriting(file!!).use {
                editor.save(it)
                return true
            } // unsafe concurrency
        }
        return false
    }

    fun close(): Boolean {
        println("try to close ${title} saved=${editor.saved}")
        return editor.saved || save()
    }

    val needSave: Boolean
        get() = !editor.saved

    val title: String
        get() {
            if (file == null)
                return "<untitled>"
            return (if (editor.saved) "" else "*") + file!!.name // unsafe concurrency
        }
}
