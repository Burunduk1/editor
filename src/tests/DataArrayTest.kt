package tests

import model.ds.DataArray
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class DataArrayTest {
    @Test
    fun testInsertRemovePush() {
        val array = DataArray<Int>()
        array.insertAfter(0, 100)
        array.insertAfter(0, 1)
        array.insertAfter(1, 20)
        assertEquals(array.get(1), 20, "array.insertAfter/get fails")
        assert(array.get(17, -1) == -1) { println("array.size/getDefault fails") }
        array.set(1, 30)
        array.push(5)
        assert(array.toString() == "[1, 30, 100, 5]") { println("array.set/toString fails") }
        array.removeBefore(-1)
        array.removeBefore(2)
        assert(array.toString() == "[1, 100, 5]") { println("array.removeBefore fails") }
        array.removeAfter(3)
        array.removeAfter(2)
        assert(array.toString() == "[1, 100]") { println("array.removeAfter fails") }
    }

    @Test
    fun testIterable() {
        val array = arrayOf(1, 2, 3, 4, 5)
        val data = DataArray(array.asIterable())
        assertArrayEquals(array, data.toList().toTypedArray())
        data.removeRange(2, 4)
        assertArrayEquals(arrayOf(1, 2, 5), data.toList().toTypedArray())
        data.insertAfter(3, array.asIterable())
        assertArrayEquals(arrayOf(1, 2, 5, 1, 2, 3, 4, 5), data.toList().toTypedArray())
        assertArrayEquals(arrayOf(2, 5, 1), data.slice(1, 4).asSequence().toList().toTypedArray())
    }
}