package model.ds

class DataArray<T>() : Iterable<T> {
    private val data = ArrayList<T>()
    fun get(i: Int) = data[i]
    fun get(i: Int, default: T) = if (i < size) data[i] else default
    fun set(i: Int, x: T) {
        data[i] = x
    }
    fun insertAfter(i: Int, x: T) {
        data.add(i, x)
    }
    fun push(x: T) {
        insertAfter(data.size, x)
    }
    fun pop(): T = data.removeAt(data.size - 1)
    fun removeAfter(i: Int) {
        if (0 <= i && i < data.size)
            data.removeAt(i)
    }
    fun removeBefore(i: Int) {
        if (0 < i && i <= data.size)
            data.removeAt(i - 1)
    }
    override fun toString() = data.toString()
    fun toList(): List<T> = data.toList()
    val size: Int
        get() = data.size
    fun clear() = data.clear()

    fun insertAfter(i: Int, array: Iterable<T>) {
        var pos = i
        for (t in array)
            insertAfter(pos++, t)
    }
    constructor(array: Iterable<T>) : this() {
        insertAfter(0, array)
    }

    class DataArrayIterator<T>(private val data: DataArray<T>, private var index: Int,
                               private val border: Int = data.size) : Iterator<T> {
        override fun next(): T {
            return data.get(index++)
        }

        override fun hasNext(): Boolean {
            return index < border
        }
    }
    override fun iterator(): Iterator<T> {
        return DataArrayIterator(this, 0)
    }
    fun slice(start: Int, end: Int): Iterator<T> { // [start, end)
        return DataArrayIterator(this, start, end)
    }

    fun removeRange(start: Int, end: Int) {
        for (i in start until end)
            removeAfter(start)
    }
}
