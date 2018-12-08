import java.io.*

class Editor {
    private var data : DataArray<DataArray<Char>> = DataArray()
    private val emptyRow = DataArray<Char>()
    init {
        data.push(DataArray())
    }

    fun load(input: BufferedReader) {
        data = DataArray()
        for (s in input.readLines()) {
            val line = DataArray<Char>()
            s.forEach {line.push(it)}
            data.push(line)
        }
    }
    fun save(output: PrintWriter) { // i do not use BufferedWriter to output '\n' properly
        for (i in 0 until data.size) {
            output.println(data.get(i).toList().toCharArray())
        }
    }
    fun get(row: Int, column: Int): Char = data.get(row, emptyRow).get(column, ' ')
    val rowCount: Int
        get() = data.size
}
