plugins {
    id("java")
    id("application")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // --- Framework Web ---
    implementation("io.javalin:javalin-bundle:5.6.1")

    // --- Manejo de JSON ---
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")

    // --- Base de Datos ---
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("mysql:mysql-connector-java:8.0.33")

    // --- Seguridad ---
    implementation("at.favre.lib:bcrypt:0.10.2")
    
    // JWT (JSON Web Tokens)
    implementation("com.auth0:java-jwt:4.4.0")

    // --- Utilidades ---
    implementation("io.github.cdimascio:dotenv-java:3.0.0")
}

application {
    mainClass.set("org.example.Main")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}