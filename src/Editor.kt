import ds.CmpPair
import parser.*
import ds.DataArray
import java.io.*

class Editor {
    private val data = DataArray<DataArray<CodeChar>>()
    private val colorer = Parser(data)
    private val emptyRow = DataArray<CodeChar>()
    private val emptyChar = CodeChar(' ', CodeType.EMPTY)
    var saved = true

    class Cursor(private var _x: Int, private var _y: Int, private val data: DataArray<DataArray<CodeChar>>) {
        var y: Int
            get() = _y
            set(value) {
                _y = value
                correctY()
            }
        var x: Int
            get() = _x
            set(value) {
                _x = value
                correctX()
            }
        val pair: CmpPair<Int, Int>
            get() = CmpPair(y, x)

        fun correctX() { _x = maxOf(0, minOf(_x, data.get(_y).size)) }
        private fun correctY() { _y = maxOf(0, minOf(_y, data.size - 1)) }
    }
    val cursor = Cursor(0, 0, data)

    init {
        data.push(DataArray())
    }

    fun load(input: BufferedReader) {
        data.clear()
        for (s in input.readLines()) {
            val line = DataArray<CodeChar>()
            s.forEach {line.push(CodeChar(it, CodeType.BASE))}
            data.push(line)
        }
        saved = true
        colorer.apply()
    }

    fun save(output: PrintWriter) { // i do not use BufferedWriter to output '\n' properly
        for (i in 0 until data.size) {
            output.println(data.get(i).map {it.char}.toCharArray())
        }
        saved = true
    }
    fun get(row: Int, column: Int): CodeChar = data.get(row, emptyRow).get(column, emptyChar)

    val rowCount: Int
        get() = data.size
    private fun rowLen() = data.get(cursor.y).size

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
        colorer.apply()
        for (f in editListeners)
            f()
    }
    private fun navigateUpdate() {
        for (f in navigateListeners)
            f()
    }

    fun editTypeChar(char: Char) {
        cursor.correctX()
        data.get(cursor.y).insertAfter(cursor.x, CodeChar(char, CodeType.BASE))
        cursor.x++
        editUpdate()
    }
    private fun mergeRows() {
        val row = data.get(cursor.y)
        row.insertAfter(row.size, data.get(cursor.y + 1))
        data.removeAfter(cursor.y + 1)
    }
    fun editBackspace() {
        data.get(cursor.y).removeBefore(cursor.x)
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
            data.get(cursor.y).removeAfter(cursor.x)
        }
        editUpdate()
    }
    fun editNewline() {
        val oldRow = data.get(cursor.y)
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

    private fun subArray(i: Int, start: Int, end: Int) = data.get(i).subArray(start, end).asSequence().map {it.char}
    fun editDuplicateLine() {
        data.insertAfter(cursor.y, DataArray<CodeChar>())
        cursor.y++
        data.get(cursor.y - 1).insertAfter(0, subArray(cursor.y, 0, data.get(cursor.y).size).map {CodeChar(it)}.asIterable())
        editUpdate()
    }

    fun navigateHome(strict: Boolean = false) {
        var i = 0
        val row = data.get(cursor.y)
        while (i < row.size && Parser.isWhitespace(row.get(i).char))
            i++
//        println("i = $i, x = ${cursor.x}, char = ${row.get(i).char}")
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
        val row = data.get(cursor.y)
        if (cursor.x == row.size)
            return
        fun char() = row.get(cursor.x).char
        when {
            Parser.isWhitespace(char()) -> {
                while (cursor.x < row.size && Parser.isWhitespace(char()))
                    cursor.x++
            }
            Parser.canBeInName(char()) -> {
                while (cursor.x < row.size && Parser.canBeInName(char()))
                    cursor.x++
                while (cursor.x < row.size && Parser.isWhitespace(char()))
                    cursor.x++
            }
            else -> cursor.x++
        }
        navigateUpdate()
    }
    fun navigateTermLeft() {
        val row = data.get(cursor.y)
        if (cursor.x == 0)
            return
        fun char() = row.get(cursor.x - 1).char
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
}
