package tests

import model.ds.CmpPair
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class CmpPairTest {

    @Test
    fun compareTo() {
        val a = arrayOf(CmpPair(1, 2), CmpPair(2, 2), CmpPair(1, 1), CmpPair(2, 1), CmpPair(3, 4))
        assertEquals(a.sorted().toString(), "[CmpPair(y=1, x=1), CmpPair(y=1, x=2), CmpPair(y=2, x=1), CmpPair(y=2, x=2), CmpPair(y=3, x=4)]")
    }
}