/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Java application project to get you started.
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/7.3/userguide/building_java_projects.html
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    // Apply the groovy plugin to also add support for Groovy (needed for Spock)
    groovy

    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Apache Ignite
    implementation("org.apache.ignite:ignite-core:2.15.0")
    implementation("org.apache.ignite:ignite-indexing:2.15.0")

    // Apache Commons
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("commons-io:commons-io:2.15.1")
    implementation("org.apache.commons:commons-lang3:3.14.0")
}

application {
    // Define the main class for the application.
    mainClass.set("de.qaware.cloudcomputing.bigdata.HelloWorld")

    applicationDefaultJvmArgs = listOf("--add-opens", "java.base/java.nio=ALL-UNNAMED")
}
