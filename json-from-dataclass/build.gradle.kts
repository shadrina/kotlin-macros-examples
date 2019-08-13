plugins {
    kotlin("jvm")
}

dependencies {
    implementation(files("../lib/kotlin-stdlib.jar")) // To include kotlin.meta
    implementation("com.google.code.gson:gson:2.8.5")
}