class DataArray<T> {
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
    override fun toString() = data.toString()
    fun toList(): List<T> = data.toList()
    val size: Int
        get() = data.size
}
