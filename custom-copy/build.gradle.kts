plugins {
    kotlin("jvm")
}

dependencies {
    implementation(files("../lib/kotlin-stdlib.jar")) // To include kotlin.meta
    
    testImplementation(files("../macros-jars/kotlin-macros-examples.custom-copy.main.jar")) // To use macros
}