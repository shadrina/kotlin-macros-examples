plugins {
    kotlin("jvm")
}

dependencies {
    implementation(files("../lib/kotlin-stdlib.jar")) // To include kotlin.meta
    
    testImplementation(files("../macros-jars/kotlin-macros-examples.hello-world.main.jar")) // To use macros
    testImplementation("org.junit.jupiter:junit-jupiter:5.5.1")
}