import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import java.util.*

plugins {
    idea
    alias(libs.plugins.kotlin)
    alias(libs.plugins.paperweight)
    alias(libs.plugins.shadow)
    kotlin("plugin.serialization") version "1.7.0-RC"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    paperweight.paperDevBundle(libs.versions.paper)

    implementation(rootProject.libs.kommand.api)
    implementation(rootProject.libs.kommand.core)
    implementation(rootProject.libs.kotlinx.serialization.json)
    implementation(rootProject.libs.kotlinx.serialization.protobuf)
    implementation(rootProject.libs.exposed.core)
    implementation(rootProject.libs.exposed.java.time)
    implementation(rootProject.libs.exposed.dao)
    implementation(rootProject.libs.exposed.jdbc)

    compileOnly(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}

extra.apply {
    set("pluginName", rootProject.name.split('-').joinToString("") {
        it.replaceFirstChar { char ->
            if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString()
        }
    })
    set("packageName", rootProject.name.replace("-", ""))
    set("kotlinVersion", libs.versions.kotlin)
    set("paperVersion", libs.versions.paper.get().split('.').take(2).joinToString(separator = "."))

    val pluginLibraries = LinkedHashSet<String>()

    configurations.findByName("implementation")?.allDependencies?.forEach { dependency ->
        if (dependency.group == null) {
            return@forEach
        }

        val group = dependency.group ?: error("group is null")
        val name = dependency.name ?: error("name is null")
        var version = dependency.version

        if (group == "org.jetbrains.kotlin" && version == null) {
            version = getKotlinPluginVersion()
        }

        requireNotNull(version) { "version is null" }
        require(version != "latest.release") { "version is latest.release" }

        pluginLibraries += "$group:$name:$version"
        set("pluginLibraries", pluginLibraries.joinToString("\n  ") { "- $it" })
    }
    println("pluginLibraries: $pluginLibraries")
}

tasks {
    processResources {
        filesMatching("*.yml") {
            expand(project.properties)
            expand(extra.properties)
        }
    }

    fun registerJar(name: String) {
        val taskName = name + "Jar"

        register<ShadowJar>(taskName) {
            println("Executing $taskName")
            from(sourceSets["main"].output)
            archiveVersion.set("")

            exclude("bundle-plugin.yml")
            rename("clip-plugin.yml", "plugin.yml")

            doLast {
                val plugins = rootProject.file(".server/plugins-reobf")
                val update = plugins.resolve("update")

                copy {
                    println("Copying ${archiveFileName.get()} to $plugins")
                    if (file(archiveFileName.get()).exists()) {
                        file(archiveFileName.get()).delete()
                    }
                    from(archiveFile)
                    into(plugins)
                    println("Copied ${archiveFile.get().asFile.path} to $plugins")
                }.run {
                    update.resolve("RELOAD").delete()
                    println("Update ${archiveFileName.get()}")
                }
            }
        }
    }
    registerJar("paper")

//    register<DefaultTask>("setupModules") {
//        doLast {
//            val defaultPrefix = "hobeaktown"
//            val projectPrefix = rootProject.name
//
//            // Rename: project name
//            if (defaultPrefix != projectPrefix) {
//                fun rename(suffix: String) {
//                    val from = "$defaultPrefix-$suffix"
//                    val to = "$projectPrefix-$suffix"
//                    if (file(from).exists()) {
//                        file(from).renameTo(file(to)).run {
//                            if (this) {
//                                println("Renamed: $from -> $to")
//                            } else {
//                                println("Failed to rename: $from -> $to")
//                            }
//                        }
//                    } else {
//                        println("Not found: $from")
//                    }
//                }
//
//                rename("")
//
//                fun renameWithLog(from: File, to: File) {
//                    if (from.exists()) {
//                        from.renameTo(to).run {
//                            if (!this) {
//                                println("Failed to rename: ${from.path} -> ${to.path}")
//                            } else {
//                                println("Renamed: ${from.path} -> ${to.path}")
//                            }
//                        }
//                    } else {
//                        println("Not found: ${from.path}")
//                    }
//                }
//
//                // Rename: package io.github.monun -> package io.github.donghune.<projectPrefix>
//                renameWithLog(
//                    from = file("$projectPrefix-plugin/src/main/kotlin/io/github/monun"),
//                    to = file("$projectPrefix-plugin/src/main/kotlin/io/github/donghune")
//                )
//                renameWithLog(
//                    from = file("$projectPrefix-plugin/src/main/kotlin/io/github/donghune/$defaultPrefix"),
//                    to = file("$projectPrefix-plugin/src/main/kotlin/io/github/donghune/$projectPrefix")
//                )
//
//                // Rename: nms
//                renameWithLog(
//                    file("$projectPrefix-core/v1.20.1/src/main/kotlin/io/github/monun"),
//                    file("$projectPrefix-core/v1.20.1/src/main/kotlin/io/github/donghune")
//                )
//                renameWithLog(
//                    file("$projectPrefix-core/v1.20.1/src/main/kotlin/io/github/donghune/hobeaktown"),
//                    file("$projectPrefix-core/v1.20.1/src/main/kotlin/io/github/donghune/$projectPrefix")
//                )
//
//                // gradle.properties -> group=io.github.monun -> group=<packagePrefix>
//                file("gradle.properties").takeIf { it.exists() }?.writeText(
//                    """
//                    kotlin.code.style=official
//                    org.gradle.jvmargs=-Xmx4G
//                    group=io.github.hxxniverse
//                    version=0.0.1
//                """.trimIndent()
//                )
//            }
//        }
//    }
}

idea {
    module {
        excludeDirs.add(file(".server"))
    }
}
