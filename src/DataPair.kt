package ds

data class CmpPair<A: Comparable<A>, B: Comparable<B>>(var y: A, var x: B): Comparable<CmpPair<A, B>> {
    override fun compareTo(other: CmpPair<A, B>): Int {
        val da = y.compareTo(other.y)
        return if (da != 0) da else x.compareTo(other.x)
    }
}
