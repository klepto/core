package dev.klepto.app.concurrent

/**
 * A thread-safe property that allows multiple readers and a single writer.
 *
 * @author Augustinas R. <http://github.com/klepto>
 */
class MutexProperty<T>(private val initial: (suspend () -> T)? = null) {
    private val mutex = ReadWriteMutex()
    private var value: T? = null

    /**
     * Gets the value of the property.
     */
    suspend fun get(): T {
        val value = getOrNull()
        checkNotNull(value) { "Property has not been initialized" }
        return value
    }

    /**
     * Gets the value of the property or `null` if it has not been initialized.
     */
    suspend fun getOrNull(): T? {
        val currentValue = mutex.read { value }
        if (currentValue != null) {
            return currentValue
        }

        return initial?.invoke()?.also {
            mutex.write { value = it }
        }
    }

    /**
     * Sets the value of the property.
     */
    suspend fun set(value: T) {
        mutex.write { this.value = value }
    }
}
