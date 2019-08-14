plugins {
    kotlin("jvm")
}

dependencies {
    implementation(files("../lib/kotlin-stdlib.jar")) // To include kotlin.meta
    implementation("com.google.code.gson:gson:2.8.5")

    testImplementation(files("../macros-jars/kotlin-macros-examples.dataclass-from-json.main.jar")) // To use macros
}