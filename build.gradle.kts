plugins {
    id("java")
}

group = "iuh.fit"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    // neo4j java driver
    implementation("org.neo4j.driver:neo4j-java-driver:5.28.1")
    // jarkata persistence api
    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")
    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
    // google gson
    implementation("com.google.code.gson:gson:2.12.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.test {
    useJUnitPlatform()
}