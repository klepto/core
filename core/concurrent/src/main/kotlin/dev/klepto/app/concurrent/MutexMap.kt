package dev.klepto.app.concurrent

/**
 * A thread-safe suspendable [Map] that allows multiple readers and a single writer.
 *
 * @author Augustinas R. <http://github.com/klepto>
 */
class MutexMap<K, V> {
    private val map = mutableMapOf<K, V>()
    private val mutex = ReadWriteMutex()

    /**
     * Puts a key-value pair into the map.
     */
    suspend fun put(
        key: K,
        value: V,
    ) {
        mutex.write { map[key] = value }
    }

    /**
     * Puts all key-value pairs into the map.
     */
    suspend fun putAll(entries: Map<K, V>) {
        mutex.write { map.putAll(entries) }
    }

    /**
     * Gets a value by key from the map.
     */
    suspend fun get(key: K): V? {
        return mutex.read { map[key] }
    }

    /**
     * Removes a key-value pair from the map.
     */
    suspend fun remove(key: K): V? {
        return mutex.write { map.remove(key) }
    }

    /**
     * Gets all keys from the map.
     */
    suspend fun getKeys(): Set<K> {
        return mutex.read { map.keys }
    }

    /**
     * Gets all values from the map.
     */
    suspend fun getValues(): Collection<V> {
        return mutex.read { map.values }
    }

    /**
     * Checks if the map contains a key.
     */
    suspend fun containsKey(key: K): Boolean {
        return mutex.read { map.containsKey(key) }
    }

    /**
     * Checks if the map contains a value.
     */
    suspend fun containsValue(value: V): Boolean {
        return mutex.read { map.containsValue(value) }
    }

    /**
     * Checks if the map is empty.
     */
    suspend fun isEmpty(): Boolean {
        return mutex.read { map.isEmpty() }
    }

    /**
     * Gets the size of the map.
     */
    suspend fun size(): Int {
        return mutex.read { map.size }
    }

    /**
     * Clears the map.
     */
    suspend fun clear() {
        mutex.write { map.clear() }
    }

    /**
     * Converts the map to a read-only map.
     */
    suspend fun toMap(): Map<K, V> {
        return mutex.read { map.toMap() }
    }

    /**
     * Converts the map to a mutable map.
     */
    suspend fun toMutableMap(): MutableMap<K, V> {
        return mutex.read { map.toMutableMap() }
    }
}
