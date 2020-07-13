import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "6.0.0"
    id("maven-publish")
}

val lwjgl by configurations.creating

repositories {
    mavenCentral()
}

dependencies {
    lwjgl("org.lwjgl.lwjgl:lwjgl_util:2.9.3") {
        isTransitive = false // only want col, dim, point, vec, mat, quat, etc.
    }
}

val relocatedJar = file("$buildDir/relocated.jar")
val relocate by tasks.creating(ShadowJar::class) {
    destinationDirectory.set(buildDir)
    archiveBaseName.set(name)
    configurations = listOf(lwjgl)
    exclude("META-INF/**/*")

    relocate("org.lwjgl.util", "de.johni0702.minecraft.gui.utils.lwjgl")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.ReplayMod"
            artifactId = "lwjgl-utils"
            version = "1.0"

            artifact(relocate.archiveFile) {
                builtBy(relocate)
            }
        }
    }
}

// Cause JitPack is stubborn
tasks.create("clean")
