val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val kotest_version: String by project

plugins {
    application
    kotlin("jvm") version "1.4.31"
}

group = "ru.serobyan"
version = "0.0.1"

application {
    mainClassName = "ApplicationKt"
}

repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
    maven { url = uri("https://kotlin.bintray.com/kotlinx") }
}

dependencies {
    implementation("org.jetbrains.kotlin", "kotlin-stdlib-jdk8", kotlin_version)
    implementation("ch.qos.logback", "logback-classic", logback_version)
    implementation("io.ktor", "ktor-server-netty", ktor_version)
    implementation("io.ktor", "ktor-server-core", ktor_version)
    implementation("io.ktor", "ktor-server-sessions", ktor_version)
    implementation("io.ktor", "ktor-websockets", ktor_version)
    implementation("io.ktor", "ktor-client-core", ktor_version)
    implementation("io.ktor", "ktor-client-core-jvm", ktor_version)
    implementation("io.ktor", "ktor-client-json-jvm", ktor_version)
    implementation("io.ktor", "ktor-client-gson", ktor_version)
    implementation("io.ktor", "ktor-client-cio", ktor_version)
    implementation("io.ktor", "ktor-client-websockets", ktor_version)
    implementation("io.ktor", "ktor-client-logging-jvm", ktor_version)
    testImplementation("io.ktor", "ktor-server-tests", ktor_version)
    testImplementation("io.ktor", "ktor-client-mock", ktor_version)
    testImplementation("io.ktor", "ktor-client-mock-jvm", ktor_version)
    testImplementation("io.kotest", "kotest-runner-junit5-jvm", kotest_version)
    testImplementation("io.kotest", "kotest-assertions-core-jvm", kotest_version)
    testImplementation("io.kotest", "kotest-property-jvm", kotest_version)
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("test_resources")

tasks.withType<Test> {
    useJUnitPlatform()
}