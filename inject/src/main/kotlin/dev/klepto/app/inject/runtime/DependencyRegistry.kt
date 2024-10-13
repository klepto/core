package dev.klepto.app.inject.runtime

/**
 * Dependency registry.
 *
 * @author Augustinas R. <http://github.com/klepto>
 */
class DependencyRegistry {
    private val dependencies = mutableMapOf<DependencyKey, Any>()

    /**
     * Retrieves dependency by key.
     */
    operator fun get(key: DependencyKey): Any? {
        return dependencies[key]
    }

    /**
     * Sets dependency by key.
     */
    operator fun set(
        key: DependencyKey,
        value: Any,
    ) {
        dependencies[key] = value
    }
}
