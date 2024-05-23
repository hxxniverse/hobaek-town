plugins {
    alias(libs.plugins.shadow)
    `maven-publish`
    signing
}

publishing {
    repositories {
        mavenLocal()

        maven {
            name = "server"
            url = rootProject.uri(".server/libraries")
        }

        maven {
            name = "central"

            credentials.runCatching {
                val nexusUsername: String by project
                val nexusPassword: String by project
                username = "reona7140@naver.com"
                password = "jak36466!"
            }.onFailure {
                logger.warn("Failed to load nexus credentials, Check the gradle.properties")
            }

            url = uri(
                if ("SNAPSHOT" in version as String) {
                    "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                } else {
                    "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                }
            )
        }
    }

    publications {
        fun MavenPublication.setup(target: Project) {
            artifactId = target.name

            from(target.components["java"])
            artifact(target.tasks["sourcesJar"])
            artifact(target.tasks["dokkaJar"])

            pom {
                name.set(target.name)
                description.set("Chaos! Destruction! Oblivion!")
                url.set("https://github.com/donghune/${rootProject.name}")

                licenses {
                    license {
                        name.set("GNU General Public License version 3")
                        url.set("https://opensource.org/licenses/GPL-3.0")
                    }
                }

                developers {
                    developer {
                        id.set("donghune")
                        name.set("donghune")
                        email.set("reona7140@naver.com")
                        url.set("https://github.com/donghune")
                        roles.addAll("developer")
                        timezone.set("Asia/Seoul")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/donghune/${rootProject.name}.git")
                    developerConnection.set("scm:git:ssh://github.com:donghune/${rootProject.name}.git")
                    url.set("https://github.com/donghune/${rootProject.name}")
                }
            }
        }
    }
}
