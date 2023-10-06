plugins {
  with(libs.plugins) {
    alias(kotlin)
    alias(shadow)
    alias(minotaur)
    alias(runPaper)
    alias(changelog)
    alias(spotless)
  }
}

repositories {
  mavenCentral()

  maven("https://repo.papermc.io/repository/maven-public/")
  maven("https://repo.md5lukas.de/public/")
}

dependencies {
  implementation(libs.paper)
  implementation(libs.stdlib)
  // implementation(libs.coroutines)

  implementation(libs.papertrail)
  implementation(libs.dependencyLoader)
}

tasks {
  compileKotlin {
    compilerOptions.freeCompilerArgs.addAll(
        "-Xjvm-default=all",
        "-Xlambdas=indy",
    )
  }

  shadowJar {
    archiveClassifier = ""

    minimize {
      // Only referenced by the paper-plugin.yml
      exclude(dependency(libs.dependencyLoader.get()))
      // Only referenced by the plugin.yml
      exclude(dependency(libs.papertrail.get()))
    }

    exclude("META-INF/")

    dependencies {
      include(dependency(libs.dependencyLoader.get()))
      include(dependency(libs.papertrail.get()))
    }

    relocate("de.md5lukas.paper.loader", "de.md5lukas.<package>")
    relocate("io.papermc.papertrail", "de.md5lukas.<package>.legacy")
  }

  processResources {
    val properties =
        mapOf(
            "name" to project.name,
            "version" to project.version,
            "kotlinVersion" to libs.versions.kotlin.get(),
            // "coroutinesVersion" to libs.versions.coroutines.get(),
        )

    inputs.properties(properties)

    filteringCharset = "UTF-8"

    filesMatching(listOf("paper-plugin.yml", "plugin.yml", "dependencies.yml")) {
      expand(properties)
    }
  }
}

spotless { kotlin { ktfmt() } }

modrinth {
  val modrinthToken: String? by project

  token = modrinthToken

  projectId = "<id>"
  versionType = "release"
  uploadFile.set(tasks.shadowJar)

  gameVersions.addAll("1.20.1")
  loaders.addAll("paper")

  changelog.set(
      provider {
        with(project.changelog) {
          renderItem(getLatest().withEmptySections(false).withHeader(false))
        }
      })

  debugMode = true
}
