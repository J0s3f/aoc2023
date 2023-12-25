package util

object ExtensionFunctions {
    fun <T> List<T>.combinations(): Sequence<Pair<T, T>> {
        val collection = this
        return sequence {
            for (i in 0 until collection.size - 1)
                for (j in i + 1 until collection.size)
                    yield(collection[i] to collection[j])
        }
    }
}