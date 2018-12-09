import parser.*
import ds.DataArray
import java.io.*

class Editor {
    private val data = DataArray<DataArray<CodeChar>>()
    private val colorer = Parser(data)
    private val emptyRow = DataArray<CodeChar>()
    private val emptyChar = CodeChar(' ', CodeType.EMPTY)

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
        colorer.apply()
    }

    fun save(output: PrintWriter) { // i do not use BufferedWriter to output '\n' properly
        for (i in 0 until data.size) {
            output.println(data.get(i).toList().map {it.char}.toCharArray())
        }
    }
    fun get(row: Int, column: Int): CodeChar = data.get(row, emptyRow).get(column, emptyChar)
    val rowCount: Int
        get() = data.size
}
