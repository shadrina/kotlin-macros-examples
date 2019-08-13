plugins {
    kotlin("jvm")
}

dependencies {
    // To include kotlin.meta
    compile(files("../lib/kotlin-stdlib.jar"))
    // To use macros
    testCompile(files("../classes/artifacts/kotlin_macros_examples_hello_world_main_jar/kotlin-macros-examples.hello-world.main.jar"))
}