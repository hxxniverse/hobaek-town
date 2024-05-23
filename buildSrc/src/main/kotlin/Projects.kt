import org.gradle.api.Project

private fun Project.getSubprojectByName(name: String) = project(":${rootProject.name}-$name")

val Project.projectPlugin
    get() = getSubprojectByName("plugin")
