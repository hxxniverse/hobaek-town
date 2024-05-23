import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import java.util.*

plugins {
    alias(libs.plugins.shadow)
}

dependencies {

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
        val group = dependency.group ?: error("group is null")
        val name = dependency.name
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
            archiveClassifier.set(name)
            archiveAppendix.set("bundle")

            from(sourceSets["main"].output)

            exclude("bundle-plugin.yml")
            rename("clip-plugin.yml", "plugin.yml")

            doLast {
                val plugins = rootProject.file(".server/plugins-$name")
                val update = plugins.resolve("update")

                println(archiveFile.get().asFile.path)

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

    registerJar("reobf")
}
