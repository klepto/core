package dev.klepto.core.memory

import dev.klepto.core.memory.windows.WinMemoryApi

/**
 * Provides API for reading memory from currently running OS processes.
 *
 * @author Augustinas R. <http://github.com/klepto>
 */
interface MemoryApi {
    /**
     * Obtains a list of currently running OS processes.
     */
    suspend fun getProcesses(): List<MemoryProcess>

    companion object {
        operator fun invoke(): MemoryApi {
            return when (OperatingSystem.getCurrent()) {
                OperatingSystem.WINDOWS -> WinMemoryApi()
                else -> throw UnsupportedOperationException("Unsupported operating system")
            }
        }
    }
}
