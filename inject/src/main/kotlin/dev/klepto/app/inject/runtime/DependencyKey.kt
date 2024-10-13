package dev.klepto.app.inject.runtime

/**
 * Represents a key for a dependency.
 *
 * @author Augustinas R. <http://github.com/klepto>
 */
data class DependencyKey(
    val type: Class<*>,
    val qualifier: Any? = null,
    val scope: DependencyKey? = null,
)
