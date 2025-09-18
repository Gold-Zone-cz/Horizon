import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.9"
}

group = "cz.goldzone"
description = "Horizon"
version = "1.0.3"

java.toolchain {
    languageVersion = JavaLanguageVersion.of(21)
    vendor = JvmVendorSpec.ADOPTIUM
}

repositories {
    mavenCentral()

    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    maven {
        url = uri("https://repo.codemc.io/repository/maven-snapshots/")
    }

    maven {
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }

    maven {
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }

    maven {
        url = uri("https://jitpack.io")
    }

    maven {
        url = uri("https://repo.gold-zone.cz/releases")
    }

    maven {
        url = uri("https://repo.gold-zone.cz/private")

        credentials {
            username = (project.findProperty("goldzoneRepo.username") ?: System.getenv("GOLDZONE_REPO_USERNAME")) as String?
            password = (project.findProperty("goldzoneRepo.password") ?: System.getenv("GOLDZONE_REPO_PASSWORD")) as String?
        }

        authentication {
            create<BasicAuthentication>("basic")
        }
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.5-R0.1-SNAPSHOT")
    compileOnly("cz.goldzone:neuron-spigot:1.0.77")

    implementation("net.md-5:bungeecord-chat:1.21-R0.3")
    implementation("com.github.cryptomorin:XSeries:13.4.0")
    implementation("dev.digitality:digitalgui:1.1.3")
    implementation("dev.digitality:digitalconfig:1.2.0")

    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")

    implementation("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    withType<ShadowJar> {
        minimize {
            exclude(dependency("dev.digitality:digitalgui"))
        }

        relocate("dev.digitality.digitalgui", "cz.goldzone.horizon.api.digitalgui")
        relocate("dev.digitality.digitalconfig", "cz.goldzone.horizon.api.digitalconfig")

        archiveFileName = "${project.name}.jar"
    }

    processResources {
        filteringCharset = "UTF-8"

        filesMatching("plugin.yml") {
            expand(hashMapOf("version" to version))
        }
    }
}