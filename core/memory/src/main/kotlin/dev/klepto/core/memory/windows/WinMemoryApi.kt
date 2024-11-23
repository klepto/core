package dev.klepto.core.memory.windows

import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.Tlhelp32
import com.sun.jna.platform.win32.WinDef.DWORD
import dev.klepto.core.memory.MemoryApi
import dev.klepto.core.memory.MemoryProcess
import dev.klepto.core.memory.windows.jna.Buffers
import dev.klepto.core.memory.windows.jna.Kernel32.Companion.KERNEL32

/**
 * Provides API for interacting with currently running Windows processes.
 *
 * @author Augustinas R. <http://github.com/klepto>
 */
class WinMemoryApi : MemoryApi {
    /**
     * Obtains a list of currently running Windows processes.
     */
    override suspend fun getProcesses(): List<MemoryProcess> {
        val result = mutableListOf<MemoryProcess>()
        val entry = Tlhelp32.PROCESSENTRY32.ByReference()
        val snapshot =
            KERNEL32.CreateToolhelp32Snapshot(
                Tlhelp32.TH32CS_SNAPPROCESS,
                DWORD(0),
            )
        check(snapshot != null) { "Failed to create snapshot" }

        try {
            while (KERNEL32.Process32Next(snapshot, entry)) {
                val id = entry.th32ProcessID.toLong()
                val name = Native.toString(Buffers.toByteArrayPacked(entry.szExeFile))
                val moduleOffset = findModuleOffset(id, name) ?: continue
                result += WinMemoryProcess(id, name, moduleOffset)
            }
        } finally {
            KERNEL32.CloseHandle(snapshot)
        }

        return result
    }

    /**
     * Finds the module offset of the process by its name.
     */
    private fun findModuleOffset(
        processId: Long,
        processName: String,
    ): Long? {
        val entry = Tlhelp32.MODULEENTRY32W.ByReference()
        val snapshot =
            KERNEL32.CreateToolhelp32Snapshot(
                Tlhelp32.TH32CS_SNAPMODULE,
                DWORD(processId),
            )

        try {
            while (KERNEL32.Module32NextW(snapshot, entry)) {
                val moduleName = Buffers.toString(entry.szModule)
                if (processName == moduleName) {
                    return Pointer.nativeValue(entry.modBaseAddr)
                }
            }
        } finally {
            KERNEL32.CloseHandle(snapshot)
        }
        return null
    }
}
