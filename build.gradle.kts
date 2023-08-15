import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    java
    id("org.springframework.boot") version "3.1.2"
    id("io.spring.dependency-management") version "1.1.2"
    id("org.graalvm.buildtools.native") version "0.9.23"
}

group = "com.staticnoiselog"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")

    // automatic generation of REST API documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
    implementation("com.github.therapi:therapi-runtime-javadoc:0.15.0")
    annotationProcessor("com.github.therapi:therapi-runtime-javadoc-scribe:0.15.0")

    testImplementation("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")

    runtimeOnly("org.postgresql:postgresql")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<BootBuildImage>("bootBuildImage") {
    buildpacks.set(
        listOf(
            "gcr.io/paketo-buildpacks/ca-certificates:3.6.3",
            "gcr.io/paketo-buildpacks/bellsoft-liberica:10.2.5",
            "gcr.io/paketo-buildpacks/syft:1.32.1",
            "gcr.io/paketo-buildpacks/executable-jar:6.7.4",
            "gcr.io/paketo-buildpacks/spring-boot:5.26.1",
            "gcr.io/paketo-buildpacks/native-image:5.12.1"
        )
    )
}