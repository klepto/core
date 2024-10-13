package dev.klepto.app.inject

import io.github.classgraph.ClassGraph
import kotlin.reflect.KClass

/**
 * Main entry point for dependency injection.
 *
 * @author Augustinas R. <http://github.com/klepto>
 */
object Inject {
    // Dependency registry.
    private val registry = mutableMapOf<Any, Any>()

    fun createSingletons(): List<Singleton> {
    }

    /**
     * Finds all dependencies of given type.
     */
    private fun find(type: KClass<*>): Set<KClass<*>> {
        ClassGraph()
            .verbose()
            .enableAllInfo()
            .scan().use { result ->
                return result.getClassesImplementing(type::class.java)
                    .mapNotNull { it.loadClass().kotlin }
                    .toSet()
            }
    }
}
