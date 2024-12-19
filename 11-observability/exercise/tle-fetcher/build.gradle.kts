plugins {
    java
    id("io.quarkus")
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))

    implementation("io.quarkus:quarkus-rest-client-jsonb")
    implementation("io.quarkus:quarkus-rest-client")
    implementation("io.quarkus:quarkus-rest-jsonb")
    implementation("io.quarkus:quarkus-rest")
    implementation("io.quarkus:quarkus-smallrye-health")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-container-image-jib")
    implementation("io.quarkus:quarkus-smallrye-openapi")
    implementation("io.quarkus:quarkus-logging-json")

    implementation("io.quarkus:quarkus-micrometer-registry-prometheus")

    implementation("io.quarkus:quarkus-opentelemetry")

    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
}

group = "de.qaware.cloudcomputing"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}
