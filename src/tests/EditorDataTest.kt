package tests

import model.*
import model.ds.DataArray

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class EditorDataTest {

    @Test
    fun editorData() {
        val data = EditorData()
        data.push(emptyRow)
        data.get(0).insertAfter(0, arrayOf('a', 'b', 'c').map { CodeChar(it) })
        data.push(DataArray(data.copyOfRow(0)))
        data.get(0).set(1, CodeChar('x'))
        data.push(DataArray(data.subArray(1, 1, 3)))
        assertEquals(data.getText(), "axc\nabc\nbc")
    }
}