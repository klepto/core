package dev.klepto.core.concurrent

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * A Read-Write mutex implementation that allows multiple readers to access
 * a resource concurrently, but only one writer at a time.
 *
 * Code adopted from:
 * https://gist.github.com/bobvawter/4ff642d5996dfccb228425909f303306
 *
 * See also:
 *
 * https://github.com/Kotlin/kotlinx.coroutines/issues/94
 * https://github.com/Kotlin/kotlinx.coroutines/pull/2045
 *
 * @author bobvawter <http://github.com/bobvawter>
 * @author Augustinas R. <http://github.com/klepto>
 */
class ReadWriteMutex {
    /**
     * The number of active readers.
     */
    private var readers = 0

    /**
     * A mutex to guard the creation of new readers.
     */
    private val readMutex = Mutex()

    /**
     * A mutex to guard the creation of new writers.
     */
    private val writeMutex = Mutex()

    /**
     * Controls access to state of this [ReadWriteMutex].
     */
    private val stateMutex = Mutex()

    /**
     * Acquires the read lock.
     */
    suspend fun lockRead() {
        readMutex.withLock {
            stateMutex.withLock {
                if (readers++ == 0) {
                    writeMutex.lock(this)
                }
            }
        }
    }

    /**
     * Releases the read lock.
     */
    suspend fun unlockRead() {
        stateMutex.withLock {
            if (--readers == 0) {
                writeMutex.unlock(this)
            }
        }
    }

    /**
     * Acquires the read lock and executes the specified [block].
     */
    @OptIn(ExperimentalContracts::class)
    suspend inline fun <T> read(block: () -> T): T {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }

        return try {
            lockRead()
            block()
        } finally {
            unlockRead()
        }
    }

    /**
     * Acquires the write lock.
     */
    suspend fun lockWrite() {
        readMutex.lock()
        writeMutex.lock()
    }

    /**
     * Releases the write lock.
     */
    fun unlockWrite() {
        writeMutex.unlock()
        readMutex.unlock()
    }

    /**
     * Acquires the write lock and executes the specified [block].
     */
    @OptIn(ExperimentalContracts::class)
    suspend inline fun <T> write(block: () -> T): T {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }

        return try {
            lockWrite()
            block()
        } finally {
            unlockWrite()
        }
    }
}
