package dev.klepto.core.memory.windows

import com.sun.jna.Memory
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.WinNT
import com.sun.jna.platform.win32.WinNT.PROCESS_VM_READ
import dev.klepto.core.concurrent.MutexProperty
import dev.klepto.core.memory.MemoryProcess
import dev.klepto.core.memory.windows.jna.Kernel32.Companion.KERNEL32
import java.nio.ByteBuffer

/**
 * Represents a Windows process.
 *
 * @author Augustinas R. <http://github.com/klepto>
 */
data class WinMemoryProcess(
    override val id: Long,
    override val name: String,
    private val moduleOffset: Long,
) : MemoryProcess {
    /**
     * The handle to the process.
     */
    private val handle =
        MutexProperty<WinNT.HANDLE> {
            KERNEL32.OpenProcess(PROCESS_VM_READ, true, id.toInt())
        }

    /**
     * Reads memory from the process.
     */
    override suspend fun readMemory(
        offset: Long,
        size: Long,
    ): ByteBuffer {
        Memory(size).use { memory ->
            val address = Pointer.createConstant(offset)
            KERNEL32.ReadProcessMemory(handle.get(), address, memory, size.toInt(), null)
            return memory.getByteBuffer(0, size)
        }
    }
}
