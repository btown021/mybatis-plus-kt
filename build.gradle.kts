
plugins {
    kotlin("jvm") version "2.2.0"
    `maven-publish`
    signing
    id("com.vanniktech.maven.publish") version "0.36.0"

}

group = "io.github.btown021"
version = "1.0.0"


repositories {
    mavenCentral()
}

dependencies {
    compileOnly("com.baomidou:mybatis-plus-core:3.5.3")
    compileOnly("com.baomidou:mybatis-plus-extension:3.5.3")
    compileOnly("org.slf4j:slf4j-api:2.0.9")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure:3.0.0")
}



