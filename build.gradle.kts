import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

plugins {
    kotlin("jvm") version "1.9.25" apply false
}

configure(allprojects) {
    group = "dev.klepto"
    version = "0.0.1-SNAPSHOT"
    apply<KotlinPluginWrapper>()
}
