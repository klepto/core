package dev.klepto.core.memory

import java.nio.ByteBuffer

/**
 * Represents currently running OS process.
 *
 * @author Augustinas R. <http://github.com/klepto>
 */
interface MemoryProcess {
    /**
     * The unique identifier of the process.
     */
    val id: Long

    /**
     * The name of the process.
     */
    val name: String

    /**
     * Reads memory from the process.
     */
    suspend fun readMemory(
        offset: Long,
        size: Long,
    ): ByteBuffer {
        TODO("Unsupported operation")
    }

    companion object {
        operator fun invoke(
            id: Long,
            name: String,
        ): MemoryProcess = Container(id, name)
    }

    private data class Container(
        override val id: Long,
        override val name: String,
    ) : MemoryProcess
}
