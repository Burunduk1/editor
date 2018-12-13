package model

import model.ds.TextPosition

class Selection(private val cursor: Cursor) {
    var on = false
    var selectStart = cursor.pair

    fun isSelected(p: TextPosition): Boolean {
        if (!on)
            return false
        val end = cursor.pair
        return minOf(end, selectStart) <= p && p < maxOf(end, selectStart)
    }
    fun startSelection() {
        selectStart = cursor.pair
        on = true
    }
    fun areaHandle(f: (TextPosition, TextPosition) -> Unit) = f(selectStart, cursor.pair)
}