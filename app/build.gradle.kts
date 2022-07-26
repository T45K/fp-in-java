@file:Suppress("UnstableApiUsage")

/*
* This file was generated by the Gradle 'init' task.
*
* This generated file contains a sample Java application project to get you started.
* For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
* User Manual available at https://docs.gradle.org/7.4.2/userguide/building_java_projects.html
*/

plugins {
    // Apply the groovy plugin to also add support for Groovy (needed for Spock)
    groovy

    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

java {
    sourceCompatibility = JavaVersion.VERSION_20
    targetCompatibility = JavaVersion.VERSION_20
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Use the latest Groovy version for Spock testing
    testImplementation("org.codehaus.groovy:groovy:3.0.9")

    // Use the awesome Spock testing and specification framework even with Java
    testImplementation("org.spockframework:spock-core:2.0-groovy-3.0")
    testImplementation("junit:junit:4.13.2")

    // This dependency is used by the application.
    implementation("com.google.guava:guava:30.1.1-jre")
}

application {
    // Define the main class for the application.
    mainClass.set("io.github.t45k.fpinjava.App")
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
    jvmArgs("--enable-preview")
}
