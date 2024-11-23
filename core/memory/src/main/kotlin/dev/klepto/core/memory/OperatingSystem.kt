package dev.klepto.core.memory

/**
 * Helper class for determining the currently running operating system.
 *
 * @author Augustinas R. <http://github.com/klepto>
 */
enum class OperatingSystem {
    WINDOWS,
    LINUX,
    MACOS,
    UNKNOWN,
    ;

    companion object {
        internal fun getCurrent(): OperatingSystem {
            val osName = System.getProperty("os.name").lowercase()
            return when {
                osName.contains("win") -> WINDOWS
                osName.contains("nux") -> LINUX
                osName.contains("mac") -> MACOS
                else -> UNKNOWN
            }
        }
    }
}
