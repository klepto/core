package dev.klepto.core.inject

import dev.klepto.core.concurrent.MutexProperty
import kotlin.reflect.KClass

/**
 * Represents a dependency that can be resolved in suspending context.
 *
 * @author Augustinas R. <http://github.com/klepto>
 */
interface Dependency<T> {
    /**
     * Resolves the dependency.
     */
    suspend fun get(scope: DependencyScope): T

    /**
     * A key that uniquely identifies a dependency.
     *
     * @author Augustinas R. <http://github.com/klepto>
     */
    data class Key(
        val qualifier: Any?,
        val type: KClass<*>,
    )

    /**
     * Represents a dependency that is resolved by invoking a suspending function on each resolution.
     *
     * @author Augustinas R. <http://github.com/klepto>
     */
    data class Provider<T>(
        private val provider: suspend DependencyScope.() -> T,
    ) : Dependency<T> {
        /**
         * Resolves the dependency.
         */
        override suspend fun get(scope: DependencyScope): T {
            return provider(scope)
        }
    }

    /**
     * Represents a dependency that is resolved by invoking a suspending
     * function once and caching the value for future resolutions.
     *
     * @author Augustinas R. <http://github.com/klepto>
     */
    data class Singleton<T>(
        private val provider: suspend DependencyScope.() -> T,
    ) : Dependency<T> {
        /**
         * The resolved value of the dependency.
         */
        private var value = MutexProperty<T>()

        /**
         * Resolves the dependency.
         */
        override suspend fun get(scope: DependencyScope): T {
            val currentValue = value.getOrNull()
            if (currentValue != null) {
                return currentValue
            }

            val newValue = provider(scope)
            value.set(newValue)
            return newValue
        }
    }
}
