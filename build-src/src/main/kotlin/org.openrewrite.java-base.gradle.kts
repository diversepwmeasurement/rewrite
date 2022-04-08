import gradle.kotlin.dsl.accessors._378df9f54425f6cc20297cfac9699af1.java
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.exclude
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType

plugins {
    `java-base`
    kotlin("jvm")
    id("org.openrewrite.base")
    id("org.gradle.test-retry")
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

configurations.all {
    exclude("com.google.errorprone", "*")
    resolutionStrategy.cacheDynamicVersionsFor(0, "seconds")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
    options.isFork = true
    options.release.set(8)
}

tasks.named<Test>("test").configure {
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
    useJUnitPlatform {
        excludeTags("debug")
    }
    jvmArgs = listOf(
        "-XX:+UnlockDiagnosticVMOptions",
        "-XX:+ShowHiddenFrames"
    )
    testLogging {
        showExceptions = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showCauses = true
        showStackTraces = true
    }
}