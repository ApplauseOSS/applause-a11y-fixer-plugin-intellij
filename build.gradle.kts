plugins {
    id("org.jetbrains.intellij") version "0.4.14"
    id("com.github.node-gradle.node") version "2.2.0"
    java
    kotlin("jvm") version "1.3.60"
}

group = "com.applause.a11y.fixer.plugin.intellij"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testCompile("junit", "junit", "4.12")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2019.3"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

node {
    version = "12.13.0"
    npmVersion = "6.7.0"
    download = true
    npmInstallCommand = "run cli-install"
//    nodeModulesDir = file("${project.projectDir}/applause-a11y-fixer/node_modules")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes(
        """Initial release."""
    )
}

task<com.moowork.gradle.node.npm.NpmTask>("runCli") {
//    dependsOn("npmInstall")
    setArgs(listOf("run", "cli"))
}
