plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
     // --- Framework Web y Logging ---
    implementation("io.javalin:javalin:6.1.3")
    implementation("org.slf4j:slf4j-simple:2.0.13")

    // --- Base de Datos ---
    // Driver JDBC para la conexión con MySQL.
    implementation("com.mysql:mysql-connector-j:8.4.0")
    // Pool de conexiones de alto rendimiento (HikariCP).
    implementation("com.zaxxer:HikariCP:5.1.0")

    // --- Seguridad ---
    // Para hashear y verificar contraseñas con el algoritmo BCrypt.
    implementation("at.favre.lib:bcrypt:0.10.2")
    // Para crear y verificar JSON Web Tokens (JWT).
    implementation("io.jsonwebtoken:jjwt-api:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.5")

    // --- Manejo de JSON (Serialización/Deserialización) ---
    // Convierte objetos Java (DTOs) a JSON y viceversa.
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")

     // --- Variables de Entorno ---
    // Carga las credenciales y configuraciones desde un archivo .env.
    implementation("io.github.cdimascio:dotenv-java:3.0.0")

    // --- Dependencias de Pruebas ---
    // Framework para pruebas unitarias.
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}