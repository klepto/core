package dev.klepto.app.inject

/**
 * Marks a class as a child of a singleton.
 *
 * @param S the parent singleton type
 * @author Augustinas R. <http://github.com/klepto>
 */
interface ScopedSingleton<S : Singleton> : Singleton {
    /**
     * The parent singleton of this child.
     */
    val parent: S
}
