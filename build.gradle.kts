import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED

plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.allopen") version "2.1.0"
    id("org.jetbrains.kotlinx.benchmark") version "0.4.13"
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

sourceSets {
    main.configure {
        kotlin.srcDir("$projectDir/solutions")
    }
    register("jvmBenchmark").configure {
        kotlin.srcDir("$projectDir/benchmarks")
    }
    test.configure {
        kotlin.srcDir("$projectDir/tests")
        resources.srcDir("$projectDir/inputs")
    }
}

kotlin {
    jvmToolchain(21)
    target {
        compilations.getByName("jvmBenchmark")
            .associateWith(compilations.getByName("main"))
    }
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    val aocktVersion = "0.2.1"
    val kotestVersion = "5.9.1"
    val immutableVersion = "0.3.8"
    val coroutinesVersion = "1.10.1"
    val aedileVersion = "2.0.2"
    val arrowVersion = "2.0.0"

    implementation("io.github.jadarma.aockt:aockt-core:$aocktVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:$immutableVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("com.sksamuel.aedile:aedile-core:$aedileVersion")
    implementation("io.github.j0s3f.kotlin:rangesets:1.0.2")
    implementation("io.arrow-kt:arrow-core:$arrowVersion")
    implementation("io.arrow-kt:arrow-fx-coroutines:$arrowVersion")
    testImplementation("io.github.jadarma.aockt:aockt-test:$aocktVersion")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
}

benchmark {
    targets {
        register("jvmBenchmark")
    }
    configurations {
        named("main") {
            iterations = 5
            warmups = 3
            iterationTime = 15
            iterationTimeUnit = "s"
            mode = "avgt"
            outputTimeUnit = "ms"
        }
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events = setOf(FAILED, SKIPPED)
        exceptionFormat = FULL
        showStandardStreams = true
        showCauses = true
        showExceptions = true
        showStackTraces = false

        // Prints a summary at the end.
        afterSuite(KotlinClosure2({ desc: TestDescriptor, result: TestResult ->
            if (desc.parent != null) return@KotlinClosure2
            with(result) {
                println(
                    "\nResults: $resultType (" +
                            "$testCount tests, " +
                            "$successfulTestCount passed, " +
                            "$failedTestCount failed, " +
                            "$skippedTestCount skipped" +
                            ")"
                )
            }
        }))
    }
}
