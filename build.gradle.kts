plugins {
    kotlin("jvm") version "2.2.0"
    `maven-publish`
    signing
    id("com.vanniktech.maven.publish") version "0.34.0"

}

group = "io.github.btown021"
version = "1.0.3"


repositories {
    mavenCentral()
}

dependencies {
    compileOnly("com.baomidou:mybatis-plus-core:3.5.3")
    compileOnly("com.baomidou:mybatis-plus-extension:3.5.3")
    compileOnly("org.slf4j:slf4j-api:2.0.9")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure:3.0.0")
}

kotlin {
    jvmToolchain(17)
}



val artifactId = rootProject.name
val groupId = group
val versionId = version
val descriptions = "Kotlin DSL for MyBatis-Plus QueryWrapper "

val authorName = "btown021"
val developerId= authorName

val gitRepoName = "mybatis-plus-kt"
val gitUri = "github.com/${authorName}"
val emails = "btown021@163.com"

val license = "The Apache License, Version 2.0"
val licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0.txt"

description = descriptions
group = groupId
version = versionId

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    if (!project.hasProperty("mavenCentralUsername")) {
        throw IllegalArgumentException("mavenCentralUsername is not set")
    } else if (!project.hasProperty("mavenCentralPassword")) {
        throw IllegalArgumentException("mavenCentralPassword is not set")
    } else if (!project.hasProperty("signing.keyId")) {
        throw IllegalArgumentException("signing.keyId is not set")
    } else if (!project.hasProperty("signing.password")) {
        throw IllegalArgumentException("signing.password is not set")
    }

    coordinates(groupId.toString(), artifactId, versionId.toString())

    pom {
        name.set(artifactId)
        description.set(descriptions)
        inceptionYear.set("2026")
        url.set("https://$gitUri/$gitRepoName/")
        licenses {
            license {
                name.set(license)
                url.set(licenseUrl)
                distribution.set(licenseUrl)
            }
        }
        developers {
            developer {
                id.set(developerId)
                name.set(authorName)
                email.set(emails)
                url.set("https://$gitUri")
            }
        }
        scm {
            url.set(gitRepoName)
            connection.set("scm:git:git://$gitUri/$gitRepoName.git")
            developerConnection.set("scm:git:ssh://git@$gitUri/$gitRepoName.git")
        }
    }
}


//publishing {
//    repositories {
//        maven {
//            name = "localNexus"
//            url = if (version.toString().endsWith("SNAPSHOT")) {
//                uri("http://192.168.0.108:88/repository/maven-snapshots/")
//            } else {
//                uri("http://192.168.0.108:88/repository/maven-releases/")
//            }
//
//            isAllowInsecureProtocol = true
//
//            credentials {
//                // 从 gradle.properties 中读取 nexus.username 和 nexus.password
//                username = project.findProperty("nexus.username") as? String
//                password = project.findProperty("nexus.password") as? String
//            }
//        }
//    }
//}



