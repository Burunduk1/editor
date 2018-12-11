package ds

data class CmpPair<A: Comparable<A>, B: Comparable<B>>(val a: A, val b: B): Comparable<CmpPair<A, B>> {
    override fun compareTo(other: CmpPair<A, B>): Int {
        val da = a.compareTo(other.a)
        return if (da != 0) da else b.compareTo(other.b)
    }
}
