import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.gradleup.shadow") version "9.6.1"
    kotlin("jvm") version "2.4.10"
    kotlin("plugin.serialization") version "2.4.10"
}

group = project.properties["group"] as String
version = project.properties["version"] as String

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

    implementation("org.litote.kmongo:kmongo-coroutine:4.8.0")
    implementation("io.github.reactivecircus.cache4k:cache4k:0.9.0")
    implementation("ch.qos.logback:logback-classic:1.4.5")
    implementation("net.dv8tion:JDA:6.5.0")
    implementation("club.minnced:jda-ktx:0.15.0")
    implementation("dev.arbjerg:lavaplayer:2.2.7")

    implementation("moe.kyokobot.libdave:adapter-jda:0.1.2")
    implementation("moe.kyokobot.libdave:impl-jni:0.1.2")
    implementation("moe.kyokobot.libdave:natives-win-x86-64:0.1.3")
    implementation("moe.kyokobot.libdave:natives-linux-x86-64:0.1.3")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to project.properties["mainClassName"] as String
        )
    }
}

tasks.shadowJar {
    archiveBaseName.set("Telephone")
    archiveClassifier.set("")
    archiveVersion.set(project.version.toString())
}
