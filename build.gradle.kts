plugins {
    java
    id("org.jetbrains.intellij") version "0.4.14"
    id("com.github.node-gradle.node") version "2.2.0"
    kotlin("jvm") version "1.3.60"
    id("com.mgd.core.gradle.s3") version "1.0.4"
    id("com.github.b3er.local.properties") version "1.1"
}

group = "com.applause.a11y.fixer.plugin.intellij"
version = "0.0.4"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testCompile("junit", "junit", "4.12")
    implementation(files("${buildDir}/external"))
}

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
}

s3 {
    region = "us-east-1"
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}


task<Exec>("pullA11yFixer") {
    commandLine = listOf("git", "submodule", "update", "--remote", "--recursive")
}

task<com.moowork.gradle.node.npm.NpmTask>("a11yFixerInstall") {
    dependsOn("pullA11yFixer", "npmSetup")
    mustRunAfter("pullA11yFixer")
    setArgs(listOf("run", "install-a11y-fixer"))
}

task<com.moowork.gradle.node.npm.NpmTask>("a11yFixerPackage") {
    dependsOn("a11yFixerInstall")
    setArgs(listOf("run", "pkg"))
    finalizedBy("a11yFixerCopyPackage")
}

task<Copy>("a11yFixerCopyPackage") {
    from("${projectDir}/applause-a11y-fixer/pkg")
    into("${projectDir}/src/main/resources/bin")
}

tasks.getByName("buildPlugin").dependsOn("a11yFixerCopyPackage")
tasks.getByName("buildPlugin").mustRunAfter("a11yFixerCopyPackage")

var uploadBuild = task<com.mgd.core.gradle.S3Upload>("uploadBuild") {
    dependsOn("buildPlugin")
    mustRunAfter("buildPlugin")

    val propertiesSet = arrayOf(
        project.hasProperty("aws.bucket"),
        project.hasProperty("aws.accessKeyId"),
        project.hasProperty("aws.secretKey")
    ).all { it }

    if (!propertiesSet) {
        throw GradleException("AWS properties not set")
    }

    System.setProperty("aws.accessKeyId", project.property("aws.accessKeyId").toString())
    System.setProperty("aws.secretKey", project.property("aws.secretKey").toString())

    val buildFile = "${project.name}-${project.version}.zip"
    val filePath = "${project.buildDir}/distributions/$buildFile"

    bucket = project.property("aws.bucket").toString()
    key = "intellij/builds/$buildFile"
    file = filePath
    overwrite = true
}
