plugins {
    base
    kotlin("jvm") version "1.3.41" apply false
}

allprojects {
    group = "ru.nsu.fit"
    version = "1.0-SNAPSHOT"

    repositories {
        jcenter()
    }
}

dependencies {
    subprojects.forEach {
        archives(it)
    }
}