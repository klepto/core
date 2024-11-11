package dev.klepto.app.inject

import dev.klepto.app.concurrent.MutexList
import dev.klepto.app.concurrent.MutexMap
import kotlin.reflect.KClass

/**
 * A scope that groups dependencies together. Dependencies within the same
 * scope can be resolved by their keys.
 *
 * @param parent The parent scope of this scope.
 * @author Augustinas R. <http://github.com/klepto>
 */
class DependencyScope(
    private val parent: DependencyScope? = null,
) {
    /**
     * Dependency definitions that belong to this scope.
     */
    private val dependencies = MutexMap<Dependency.Key, Dependency<*>>()

    /**
     * A list of dependency keys that are currently being resolved.
     */
    private val pending = MutexList<Dependency.Key>()

    /**
     * Binds a dependency to this scope.
     */
    suspend fun <T : Any> bind(
        type: KClass<T>,
        singleton: Boolean,
        qualifier: Any? = false,
        provider: suspend DependencyScope.() -> T,
    ) {
        val dependency =
            if (singleton) {
                Dependency.Singleton(provider)
            } else {
                Dependency.Provider(provider)
            }
        dependencies.put(Dependency.Key(qualifier, type), dependency)
    }

    /**
     * Binds all dependencies from given [scope] to this scope.
     */
    suspend fun bind(scope: DependencyScope) {
        dependencies.putAll(scope.dependencies.toMap())
    }

    /**
     * Attempts to recursively resolve a [Dependency] by its [key]
     * by first looking at dependencies that belong to this scope and then the [parent] scope.
     */
    private suspend fun resolve(key: Dependency.Key): Dependency<*>? {
        return dependencies.get(key) ?: parent?.resolve(key)
    }

    /**
     * Resolves a dependency by its [qualifier] and [type].
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun <T : Any> get(
        type: KClass<T>,
        qualifier: Any? = null,
    ): T {
        check(!pending.contains(Dependency.Key(qualifier, type))) {
            "Circular dependency detected: $qualifier, $type in scope: $this"
        }

        val dependencyKey = Dependency.Key(qualifier, type)
        val dependency = resolve(dependencyKey)

        // Allow resolving the scope itself.
        if (dependency == null && dependencyKey == DEPENDENCY_SCOPE_KEY) {
            return this as T
        }

        checkNotNull(dependency) { "Dependency not found: $qualifier, $type in scope: $this" }
        pending.add(dependencyKey)
        val result = dependency.get(this)
        pending.remove(dependencyKey)
        return result as T
    }

    companion object {
        private val DEPENDENCY_SCOPE_KEY = Dependency.Key(null, DependencyScope::class)
    }
}
