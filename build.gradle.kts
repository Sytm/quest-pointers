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

  implementation(libs.konfig)
  implementation(libs.schedulers)
  implementation(libs.pointers)

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
      include(dependency(libs.pointers.get()))
      include(dependency(libs.schedulers.get()))
      include(dependency(libs.konfig.get()))
    }

    relocate("de.md5lukas.paper.loader", "de.md5lukas.questpointers")
    relocate("io.papermc.papertrail", "de.md5lukas.questpointers.legacy")
  }

  processResources {
    val properties =
        mapOf(
            "version" to project.version,
            "kotlinVersion" to libs.versions.kotlin.get(),
        )

    inputs.properties(properties)

    filteringCharset = "UTF-8"

    filesMatching(listOf("paper-plugin.yml", "plugin.yml", "dependencies.yml")) {
      expand(properties)
    }
  }

  runServer {
    minecraftVersion(libs.versions.paper.get().substringBefore('-'))
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
