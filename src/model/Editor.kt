package model

import model.ds.DataArray
import model.ds.TextPosition
import java.io.*
import kotlin.system.measureTimeMillis

class Editor {
    private var clipboard: EditorData? = null
    private val data = EditorData()
    private val colorer = Parser(data)
    val cursor = Cursor(0, 0, data)
    var saved = true

    init {
        data.push(DataArray())
    }

    fun get(row: Int, column: Int): CodeChar = data.get(row, emptyRow).get(column, emptyChar)
    val text: String
        get() = data.getText()

    val rowCount: Int
        get() = data.size

    /** load/save routine */

    fun load(input: BufferedReader, apply: Boolean = true) {
        data.clear()
        for (s in input.readLines()) {
            val line = DataArray<CodeChar>()
            s.forEach { line.push(CodeChar(it, CodeType.BASE))}
            data.push(line)
        }
        saved = true
        if (apply)
            colorer.apply()
    }

    fun save(output: PrintWriter) { // i do not use BufferedWriter to output '\n' properly
        for (i in 0 until data.size) {
            output.println(data[i].map {it.char}.toCharArray())
        }
        saved = true
    }

    /** listeners & updaters */

    private val editListeners = ArrayList<() -> Unit>()
    private val navigateListeners = ArrayList<() -> Unit>()
    fun registerEditListener(f: () -> Unit) {
        editListeners.add(f)
    }
    fun registerNavigateListener(f: () -> Unit) {
        navigateListeners.add(f)
    }

    private fun editUpdate() {
        saved = false
        println("colorer.apply: time = %d".format(measureTimeMillis {
            colorer.apply()
        }))
        for (f in editListeners)
            f()
    }
    private fun navigateUpdate() {
        for (f in navigateListeners)
            f()
    }

    /** base edit functionality */

    fun editTypeChar(char: Char) {
        cursor.correctX()
        data[cursor.y].insertAfter(cursor.x, CodeChar(char, CodeType.BASE))
        cursor.x++
        editUpdate()
    }
    private fun mergeRows() {
        val row = data[cursor.y]
        row.insertAfter(row.size, data[cursor.y + 1])
        data.removeAfter(cursor.y + 1)
    }
    fun editBackspace() {
        data[cursor.y].removeBefore(cursor.x)
        if (cursor.x == 0 && cursor.y > 0) {
            cursor.y--
            cursor.x = rowLen()
            mergeRows()
        } else {
            cursor.x--
        }
        editUpdate()
    }
    fun editDelete() {
        if (cursor.x >= rowLen() && cursor.y + 1 < data.size) {
            cursor.x = rowLen()
            mergeRows()
        } else {
            data[cursor.y].removeAfter(cursor.x)
        }
        editUpdate()
    }
    fun editNewline() {
        val oldRow = data[cursor.y]
        val moveLen = maxOf(0, oldRow.size - cursor.x)
        navigateHome(true)
        val row = DataArray<CodeChar>()
        for (i in 0 until cursor.x)
            row.push(CodeChar(' '))
        for (i in 0 until moveLen)
            row.insertAfter(cursor.x, oldRow.pop())
        data.insertAfter(cursor.y + 1, row)
        cursor.y++
        editUpdate()
    }
    fun editDeleteLine() {
        data.removeAfter(cursor.y)
        if (cursor.y == data.size) {
            if (cursor.y == 0)
                data.push(DataArray())
            else
                cursor.y--
        }
        editUpdate()
    }
    fun editDuplicateLine() {
        data.insertAfter(cursor.y, DataArray(data.copyOfRow(cursor.y)))
        cursor.y++
        editUpdate()
    }

    /** base navigate functionality */

    fun navigateHome(strict: Boolean = false) {
        var i = 0
        val row = data[cursor.y]
        while (i < row.size && Parser.isWhitespace(row[i].char))
            i++
        if (!strict)
            cursor.x = if (i >= cursor.x) 0 else i
        else
            cursor.x = if (i > cursor.x) 0 else i
        navigateUpdate()
    }
    fun navigateEnd() {
        cursor.x = rowLen()
        navigateUpdate()
    }
    fun navigateDown(dy: Int) {
        cursor.y += dy
        navigateUpdate()
    }
    fun navigateUp(dy: Int) {
        cursor.y -= dy
        navigateUpdate()
    }
    fun navigateRight() {
        cursor.x++
        navigateUpdate()
    }
    fun navigateLeft() {
        cursor.x--
        navigateUpdate()
    }
    fun navigateTermRight() {
        val row = data[cursor.y]
        cursor.correctX()
        if (cursor.x == row.size)
            return
        fun char() = row[cursor.x].char
        when {
            Parser.canBeInName(char()) -> {
                while (cursor.x < row.size && Parser.canBeInName(char()))
                    cursor.x++
            }
            !Parser.isWhitespace(char()) -> {
                cursor.x++
            }
        }
        while (cursor.x < row.size && Parser.isWhitespace(char()))
            cursor.x++
        navigateUpdate()
    }
    fun navigateTermLeft() {
        val row = data[cursor.y]
        cursor.correctX()
        if (cursor.x == 0)
            return
        fun char() = row[cursor.x - 1].char
        when {
            Parser.isWhitespace(char()) -> {
                while (cursor.x > 0 && Parser.isWhitespace(char()))
                    cursor.x--
                if (cursor.x > 0 && Parser.canBeInName(char()))
                    while (cursor.x > 0 && Parser.canBeInName(char()))
                        cursor.x--
                else
                    cursor.x--
            }
            Parser.canBeInName(char()) -> {
                while (cursor.x > 0 && Parser.canBeInName(char()))
                    cursor.x--
            }
            else -> cursor.x--
        }
        navigateUpdate()
    }

    fun navigateToBegin() {
        cursor.y = 0
        navigateUpdate()
    }
    fun navigateToEnd() {
        cursor.y = data.size - 1
        navigateUpdate()
    }

    /** clipboard routine */

    private fun rowLen() = data[cursor.y].size

    fun copyToClipboard(start: TextPosition, end: TextPosition) {
        val l = minOf(start, end).copy()
        val r = maxOf(start, end).copy()
        println("copy range: $l $r")
        val buffer = EditorData()
        if (l.y < r.y) {
            buffer.push(DataArray(data.subArray(l.y, l.x, data[l.y].size)))
            l.y++
            l.x = 0
        }
        while (l.y < r.y)
            buffer.push(DataArray(data.copyOfRow(l.y++)))
        buffer.push(DataArray(data.subArray(l.y, l.x, r.x)))
        clipboard = buffer
    }

    fun pasteFromClipboard() {
        clipboard?.let {
            if (it.size >= 1) {
                cursor.correctX()
                if (it.size == 1) {
                    data[cursor.y].insertAfter(cursor.x, it.copyOfRow(0))
                } else {
                    editNewline()
                    data[cursor.y - 1].insertAfter(data[cursor.y - 1].size, it.copyOfRow(0))
                    println("${cursor.y} < ${data.size}$")
                    for (i in 1 until it.size - 1) {
                        data.insertAfter(cursor.y, DataArray(it.copyOfRow(i)))
                        cursor.y++
                    }
                    data[cursor.y].insertAfter(cursor.x, it.copyOfRow(it.size - 1))
                }
                cursor.x += it.last.size
            }
        }
        editUpdate()
    }

    fun deleteBlock(start: TextPosition, end: TextPosition) {
        val l = minOf(start, end).copy()
        val r = maxOf(start, end).copy()
        println("delete range: $l $r")
        val multiRow = l.y < r.y
        var aliveFirstRow = false
        if (l.x > 0 && l.y < r.y) {
            data[l.y].removeRange(l.x, data[l.y].size)
            l.y++
            l.x = 0
            aliveFirstRow = true
        }
        while (l.y < r.y) {
            data.removeAfter(l.y)
            r.y--
        }
        if (l.x < r.x)
            data[l.y].removeRange(l.x, r.x)
        cursor.y = l.y
        cursor.x = l.x
        if (multiRow && aliveFirstRow) {
            cursor.y--
            cursor.x = data[cursor.y].size
            mergeRows()
        }
        editUpdate()
    }
}
