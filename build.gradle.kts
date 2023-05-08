import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    kotlin("plugin.jpa") version "1.6.21"
    id("org.springframework.boot") version "2.4.13"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("maven-publish")
}

group = "net.villenium"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("io.springfox:springfox-boot-starter:3.0.0")

    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-gson:0.11.5")

    implementation("com.google.code.gson:gson")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.5")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

configurations {
    listOf(apiElements, runtimeElements).forEach {
        it.get().outgoing.artifact(tasks.bootJar)
    }
}

publishing {
    repositories {
        maven {
            url = uri("https://repo.yuraender.ru/private")
            credentials {
                username = System.getenv("YE_REPO_USER")
                password = System.getenv("YE_REPO_PASSWORD")
            }
        }
    }
    publications.create<MavenPublication>("maven") {
        artifact(tasks.bootJar.get())
    }
}
