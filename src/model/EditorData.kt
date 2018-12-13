package model

import model.ds.DataArray

typealias EditorData = DataArray<DataArray<CodeChar>>

fun EditorData.getRow(i: Int) = get(i).asSequence().map { it.char }.joinToString("")
fun EditorData.getText() = (0 until size).joinToString("\n") { getRow(it) }

fun EditorData.subArray(i: Int, start: Int, end: Int) = get(i).slice(start, end).asSequence().map {CodeChar(it.char, it.type)}.asIterable()
fun EditorData.copyOfRow(i: Int) = subArray(i, 0, get(i).size)

val emptyRow = DataArray<CodeChar>()
