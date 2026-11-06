plugins {
    kotlin("jvm") version "2.4.21"
    application
}

group = "eden.historical"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jsoup:jsoup:1.23.2")
    implementation("com.google.code.gson:gson:2.10.1")
}

application {
    mainClass.set("eden.historical.AppKt")
}