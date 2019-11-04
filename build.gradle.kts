import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  kotlin("jvm") version "1.3.50"
  id("com.github.johnrengelman.shadow").version("5.0.0")
}

group = "com.tetragramato"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
  jcenter()
}

val kotlinVersion = "1.3.50"
val vertxVersion = "3.8.3"
val junitJupiterEngineVersion = "5.4.0"
val coroutines_version = "1.3.2"

dependencies {
  implementation(kotlin("stdlib-jdk8"))
  implementation("io.vertx:vertx-junit5:$vertxVersion")
  implementation("io.vertx:vertx-health-check:$vertxVersion")
  implementation("io.vertx:vertx-web:$vertxVersion")
  implementation("io.vertx:vertx-lang-kotlin-coroutines:$vertxVersion")
  implementation("io.vertx:vertx-lang-kotlin:$vertxVersion")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$coroutines_version")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug:$coroutines_version")

  testImplementation("io.vertx:vertx-junit5:$vertxVersion")
  testImplementation("org.assertj:assertj-core:3.13.2")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterEngineVersion")
  testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterEngineVersion")
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

configurations {
  testImplementation {
    exclude(group = "junit")
  }
  implementation {
    exclude(module = "kotlin-stdlib-jdk7")
  }
}

val build: DefaultTask by tasks
val shadowJar = tasks["shadowJar"] as ShadowJar
build.dependsOn(shadowJar)

tasks {
  compileKotlin {
    kotlinOptions {
      freeCompilerArgs = listOf("-Xjsr305=strict")
      jvmTarget = "1.8"
    }
  }

  compileTestKotlin {
    kotlinOptions {
      freeCompilerArgs = listOf("-Xjsr305=strict")
      jvmTarget = "1.8"
    }
  }

  shadowJar {
    archiveClassifier.set("fat")
    manifest {
      attributes(mapOf("Main-Class" to "com.tetragramato.vertx.MainKt"))
    }
    mergeServiceFiles()
  }

  test {
    useJUnitPlatform()
  }
}
