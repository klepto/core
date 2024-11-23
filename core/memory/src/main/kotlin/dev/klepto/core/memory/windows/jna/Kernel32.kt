package dev.klepto.core.memory.windows.jna

import com.sun.jna.Native

/**
 * Represents Windows Kernel32 API.
 *
 * @author Augustinas R. <http://github.com/klepto>
 */
@Suppress("ktlint:standard:function-naming")
interface Kernel32 : com.sun.jna.platform.win32.Kernel32 {
    companion object {
        val KERNEL32: Kernel32 = Native.load(Kernel32::class.java)
    }
}
