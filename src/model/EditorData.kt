package model

import model.ds.DataArray

typealias EditorData = DataArray<DataArray<CodeChar>>

fun EditorData.getRow(i: Int) = get(i).asSequence().map { it.char }.joinToString("")
fun EditorData.getText() = (0 until size).joinToString("\n") { getRow(it) }

fun EditorData.subArray(i: Int, start: Int, end: Int) = get(i).slice(start, end).asSequence().map {CodeChar(it.char, it.type)}.asIterable()
fun EditorData.copyOfRow(i: Int) = subArray(i, 0, get(i).size)

val emptyRow = DataArray<CodeChar>()

fun EditorData(text: Iterable<Char>): EditorData {
    val result = EditorData()
    result.push(DataArray())
    for (c in text) {
        if (c == '\n') {
            result.push(DataArray())
        } else {
            result.last.push(CodeChar(c))
        }
    }
    return result
}
fun EditorData.flat() = asIterable().map {it.toList().plus(CodeChar('\n'))}.flatten()
