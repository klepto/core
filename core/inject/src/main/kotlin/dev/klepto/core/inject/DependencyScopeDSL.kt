package dev.klepto.core.inject

import kotlinx.coroutines.runBlocking
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Creates a root scope of dependency definitions that are configured using
 * receiver [block].
 */
@OptIn(ExperimentalContracts::class)
inline fun dependencies(crossinline block: suspend DependencyScope.() -> Unit): DependencyScope {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    // DSL initializer is not meant for concurrent access, synchronizing here is fine.
    return runBlocking {
        DependencyScope().apply { block() }
    }
}

/**
 * Creates a child scope of dependency definitions within already existing
 * receiver scope.
 */
@OptIn(ExperimentalContracts::class)
inline fun DependencyScope.scope(block: DependencyScope.() -> Unit): DependencyScope {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return DependencyScope(this).apply { block() }
}

/**
 * Resolves a dependency by its [qualifier] and reified generic type [T].
 */
suspend inline fun <reified T : Any> DependencyScope.get(qualifier: Any? = null): T {
    return get(T::class, qualifier)
}

/**
 * Resolves a dependency by its [qualifier] and reified generic type [T].
 */
suspend inline fun <reified T : Any> DependencyScope.getOrNull(qualifier: Any? = null): T? {
    return getOrNull(T::class, qualifier)
}

/**
 * Binds a dependency to the receiver scope.
 */
suspend inline fun <reified T : Any> DependencyScope.bind(
    singleton: Boolean,
    qualifier: Any? = null,
    noinline provider: suspend DependencyScope.() -> T,
) {
    bind(T::class, singleton, qualifier, provider)
}

/**
 * Binds a singleton dependency.
 */
suspend inline fun <reified T : Any> DependencyScope.singleton(
    qualifier: Any? = null,
    noinline provider: suspend DependencyScope.() -> T,
) {
    bind(true, qualifier, provider)
}

/**
 * Binds a provider dependency.
 */
suspend inline fun <reified T : Any> DependencyScope.provider(
    qualifier: Any? = null,
    noinline provider: suspend DependencyScope.() -> T,
) {
    bind(false, qualifier, provider)
}

/**
 * Reifies and resolves a provider function of zero parameters.
 */
suspend inline fun <reified T : Any> DependencyScope.of(crossinline provider: suspend () -> T): T {
    return provider()
}

/**
 * Reifies and resolves a provider function of one parameter.
 */
suspend inline fun <reified T : Any, reified P0 : Any> DependencyScope.of(crossinline provider: suspend (P0) -> T): T {
    return provider(get())
}

/**
 * Reifies and resolves a provider function of two parameters.
 */
suspend inline fun <reified T : Any, reified P0 : Any, reified P1 : Any> DependencyScope.of(
    crossinline provider: suspend(P0, P1) -> T,
): T {
    return provider(get(), get())
}

/**
 * Reifies and resolves a provider function of three parameters.
 */
suspend inline fun <reified T : Any, reified P0 : Any, reified P1 : Any, reified P2 : Any> DependencyScope.of(
    crossinline provider: suspend(P0, P1, P2) -> T,
): T {
    return provider(get(), get(), get())
}
