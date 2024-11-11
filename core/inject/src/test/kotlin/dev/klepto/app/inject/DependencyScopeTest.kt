package dev.klepto.app.inject

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

/**
 * Tests for [DependencyScope].
 *
 * @author Augustinas R. <http://github.com/klepto>
 */
class DependencyScopeTest : FunSpec({
    coroutineTestScope = true

    test("should bind a string constant") {
        val scope =
            dependencies {
                singleton { "Hello, World!" }
            }

        scope.get<String>() shouldBe "Hello, World!"
    }

    test("should return new instances when not singleton") {
        val scope =
            dependencies {
                provider { listOf(0) }
            }

        val result1 = scope.get(List::class)
        val result2 = scope.get(List::class)
        (result1 === result2) shouldBe false
    }

    test("should return same instance when singleton") {
        val scope =
            dependencies {
                singleton { listOf(0) }
            }

        val result1 = scope.get(List::class)
        val result2 = scope.get(List::class)
        (result1 === result2) shouldBe true
    }

    test("should throw when circular dependency is detected") {
        val scope =
            dependencies {
                singleton { of(::CircularA) }
                singleton { of(::CircularB) }
            }

        shouldThrow<IllegalStateException> {
            scope.get<CircularA>()
        }.message shouldContain "detected"

        shouldThrow<IllegalStateException> {
            scope.get<CircularB>()
        }.message shouldContain "detected"
    }

    test("should resolve with qualifier") {
        val scope =
            dependencies {
                singleton("a") { "A" }
                singleton("b") { "B" }
            }

        scope.get<String>("a") shouldBe "A"
        scope.get<String>("b") shouldBe "B"
        shouldThrow<IllegalStateException> {
            scope.get<String>()
        }
    }

    test("should resolve parent dependencies") {
        val parent =
            dependencies {
                singleton { "Hello, World!" }
                singleton { 42 }
            }

        val child =
            parent.scope {
                singleton { "Goodbye, World!" }
            }

        parent.get<String>() shouldBe "Hello, World!"
        child.get<String>() shouldBe "Goodbye, World!"
        child.get<Int>() shouldBe 42
    }

    test("should override and resolve when combining scopes") {
        val scopeA =
            dependencies {
                singleton { 0.1 }
                singleton { 42 }
            }
        val scopeB =
            dependencies {
                singleton { "Hello, World!" }
                singleton { 40 }
            }
        val scope =
            dependencies {
                bind(scopeA)
                bind(scopeB)
            }

        scope.get<Double>() shouldBe 0.1
        scope.get<Int>() shouldBe 40
        scope.get<String>() shouldBe "Hello, World!"
    }

    test("should resolve itself") {
        val scope = dependencies {}
        scope.get<DependencyScope>() shouldBe scope
    }
}) {
    class CircularA(private val b: CircularB)

    class CircularB(private val a: CircularA)
}
