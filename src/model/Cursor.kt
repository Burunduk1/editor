package model

import model.ds.TextPosition

class Cursor(private var _x: Int, private var _y: Int, private val data: EditorData) {
    var y: Int // always correct
        get() = _y
        set(value) {
            _y = value
            correctY()
        }
    var x: Int // maybe incorrect
        get() = _x
        set(value) {
            _x = value
            correctX()
        }
    var pair: TextPosition
        get() = TextPosition(y, x)
        set(p) {
            x = p.x
            y = p.y
        }

    fun correctX() { _x = maxOf(0, minOf(_x, data.get(_y).size)) }
    private fun correctY() { _y = maxOf(0, minOf(_y, data.size - 1)) }

    override fun toString() = "cursor[row=$y column=$x]"
}
