package minerofmillions.utils

class BiMap<K, V>(private val base: Map<K, V>) : Map<K, V> by base {
    init {
        assert(values.all { v -> values.count { it == v } == 1 }) { "BiMaps can only have one copy of values." }
    }

    private val reverse = toList().associate { it.second to it.first }
    fun getKey(value: V) = reverse[value]
}

fun <K, V> biMapOf(vararg pairs: Pair<K, V>) = BiMap(mapOf(pairs = pairs))