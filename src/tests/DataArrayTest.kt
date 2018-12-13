package tests

import model.ds.DataArray
import org.junit.jupiter.api.Test

internal class DataArrayTest {
    @Test
    fun testDataArray() {
        val array = DataArray<Int>()
        array.insertAfter(0, 100)
        array.insertAfter(0, 1)
        array.insertAfter(1, 20)
        assert(array.get(1) == 20) { println("array.insertAfter/get fails") }
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
}