import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    application
    id("com.diffplug.spotless") version "6.12.0"
}

group = "de.thomas-popp"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jgrapht", "jgrapht-core", "1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    testImplementation(kotlin("test"))
}

spotless { // if you are using build.gradle.kts, instead of 'spotless {' use:
    // configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
        // by default the target is every '.kt' and '.kts` file in the java sourcesets
        ktfmt() // has its own section below
        // ktlint() // has its own section below
        // diktat()   // has its own section below
        // prettier() // has its own section below
    }
    kotlinGradle {
        ktlint() // or ktfmt() or prettier()
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}
