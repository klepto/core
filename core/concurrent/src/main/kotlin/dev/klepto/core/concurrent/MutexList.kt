package dev.klepto.core.concurrent

/**
 * A thread-safe suspendable [List] that allows multiple readers and a
 * single writer.
 *
 * @author Augustinas R. <http://github.com/klepto>
 */
class MutexList<T> {
    private val list = mutableListOf<T>()
    private val mutex = ReadWriteMutex()

    /**
     * Adds an element to the list.
     */
    suspend fun add(element: T) {
        mutex.write { list.add(element) }
    }

    /**
     * Adds all elements to the list.
     */
    suspend fun addAll(elements: List<T>) {
        mutex.write { list.addAll(elements) }
    }

    /**
     * Gets an element by index from the list.
     */
    suspend fun get(index: Int): T? {
        return mutex.read { list.getOrNull(index) }
    }

    /**
     * Checks if the list contains an element.
     */
    suspend fun contains(element: T): Boolean {
        return mutex.read { list.contains(element) }
    }

    /**
     * Removes an element from the list.
     */
    suspend fun remove(element: T): Boolean {
        return mutex.write { list.remove(element) }
    }

    /**
     * Removes an element by index from the list.
     */
    suspend fun removeAt(index: Int): T {
        return mutex.write { list.removeAt(index) }
    }

    /**
     * Checks if the map is empty.
     */
    suspend fun isEmpty(): Boolean {
        return mutex.read { list.isEmpty() }
    }

    /**
     * Gets the size of the list.
     */
    suspend fun size(): Int {
        return mutex.read { list.size }
    }

    /**
     * Removes all elements from the list.
     */
    suspend fun clear() {
        mutex.write { list.clear() }
    }

    /**
     * Converts the list to a read-only list.
     */
    suspend fun toList(): List<T> {
        return mutex.read { list.toList() }
    }

    /**
     * Converts the list to a mutable list.
     */
    suspend fun toMutableList(): MutableList<T> {
        return mutex.read { list.toMutableList() }
    }
}
