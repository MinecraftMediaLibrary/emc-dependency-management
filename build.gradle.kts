plugins {
    java
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("com.github.hierynomus.license-base") version "0.16.1"
}

group = "io.github.pulsebeat02"
version = "v1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("me.lucko:jar-relocator:1.5")
}

sourceSets {
    main {
        java {
            srcDir("src/main/java")
        }
        resources {
            srcDir("src/main/resources")
        }
    }
}

tasks {
    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    publish {
        dependsOn(clean)
        dependsOn(build)
    }
    shadowJar {
        archiveBaseName.set("emc-dependency-management-all")
        archiveClassifier.set("")
        relocate("me.lucko", "io.github.pulsebeat02.emcdependencymanagement.lib.lucko")
        relocate("org.objectweb", "io.github.pulsebeat02.emcdependencymanagement.lib.objectweb")
    }
    build {
        dependsOn(shadowJar)
    }
}


publishing {
    repositories {
        maven {
            setUrl("https://pulsebeat02.jfrog.io/artifactory/pulse-gradle-release-local")
            credentials {
                username = ""
                password = ""
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks["shadowJar"])
        }
    }
}

subprojects {

    apply(plugin = "com.github.hierynomus.license-base")

    license {
        header = rootProject.file("LICENSE")
        encoding = "UTF-8"
        mapping("java", "SLASHSTAR_STYLE")
        includes(listOf("**/*.java", "**/*.kts"))
    }
}