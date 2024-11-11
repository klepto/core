import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

group = "dev.klepto"
version = "0.0.1-SNAPSHOT"

plugins {
    id("org.jetbrains.kotlin.jvm") version "2.0.21" apply false
    id("org.jetbrains.kotlin.plugin.power-assert") version "2.0.0" apply false
}

configure(subprojects) {
    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.kotlin.plugin.power-assert")
    }

    configure<KotlinJvmProjectExtension> {
        jvmToolchain(21)
    }

    dependencies {
        val testImplementation by configurations
        testImplementation("io.kotest:kotest-runner-junit5:6.0.0.M1")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
