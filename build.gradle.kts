plugins {
    idea
    alias(libs.plugins.kotlin)
    alias(libs.plugins.dokka)
    kotlin("plugin.serialization") version "1.7.0-RC"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = rootProject.libs.plugins.kotlin.get().pluginId)

    repositories {
        mavenCentral()
        maven("https://papermc.io/repo/repository/maven-public/")
    }

    dependencies {
        compileOnly(rootProject.libs.paper)

        implementation(kotlin("stdlib"))
        implementation(kotlin("reflect"))

        implementation(rootProject.libs.kommand.api)
        implementation(rootProject.libs.kommand.core)
        implementation(rootProject.libs.monun.heartbeat.coroutines)
        implementation(rootProject.libs.kotlinx.serialization.json)
        implementation(rootProject.libs.kotlinx.serialization.protobuf)
        implementation(rootProject.libs.exposed.core)
        implementation(rootProject.libs.exposed.java.time)
        implementation(rootProject.libs.exposed.dao)
        implementation(rootProject.libs.exposed.jdbc)

        compileOnly(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    }
}

tasks {
    register<DefaultTask>("setupModules") {
        doLast {
            val defaultPrefix = "hobeaktown"
            val projectPrefix = rootProject.name

            // Rename: project name
            if (defaultPrefix != projectPrefix) {
                fun rename(suffix: String) {
                    val from = "$defaultPrefix-$suffix"
                    val to = "$projectPrefix-$suffix"
                    if (file(from).exists()) {
                        file(from).renameTo(file(to)).run {
                            if (this) {
                                println("Renamed: $from -> $to")
                            } else {
                                println("Failed to rename: $from -> $to")
                            }
                        }
                    } else {
                        println("Not found: $from")
                    }
                }

                rename("")

                fun renameWithLog(from: File, to: File) {
                    if (from.exists()) {
                        from.renameTo(to).run {
                            if (!this) {
                                println("Failed to rename: ${from.path} -> ${to.path}")
                            } else {
                                println("Renamed: ${from.path} -> ${to.path}")
                            }
                        }
                    } else {
                        println("Not found: ${from.path}")
                    }
                }

                // Rename: package io.github.monun -> package io.github.donghune.<projectPrefix>
                renameWithLog(
                    from = file("$projectPrefix-plugin/src/main/kotlin/io/github/monun"),
                    to = file("$projectPrefix-plugin/src/main/kotlin/io/github/donghune")
                )
                renameWithLog(
                    from = file("$projectPrefix-plugin/src/main/kotlin/io/github/donghune/$defaultPrefix"),
                    to = file("$projectPrefix-plugin/src/main/kotlin/io/github/donghune/$projectPrefix")
                )

                // Rename: nms
                renameWithLog(
                    file("$projectPrefix-core/v1.20.1/src/main/kotlin/io/github/monun"),
                    file("$projectPrefix-core/v1.20.1/src/main/kotlin/io/github/donghune")
                )
                renameWithLog(
                    file("$projectPrefix-core/v1.20.1/src/main/kotlin/io/github/donghune/hobeaktown"),
                    file("$projectPrefix-core/v1.20.1/src/main/kotlin/io/github/donghune/$projectPrefix")
                )

                // gradle.properties -> group=io.github.monun -> group=<packagePrefix>
                file("gradle.properties").takeIf { it.exists() }?.writeText(
                    """
                    kotlin.code.style=official
                    org.gradle.jvmargs=-Xmx4G
                    group=io.github.hxxniverse
                    version=0.0.1
                """.trimIndent()
                )
            }
        }
    }
}

idea {
    module {
        excludeDirs.add(file(".server"))
        excludeDirs.addAll(allprojects.map { it.buildDir })
        excludeDirs.addAll(allprojects.map { it.file(".gradle") })
    }
}
