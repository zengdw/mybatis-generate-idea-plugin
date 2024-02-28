plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.13.2"
}

group = "com.zengdw.mybatis"
version = "3.0"

repositories {
    mavenLocal()
    maven("https://maven.aliyun.com/repository/public/")
    mavenCentral()
}

dependencies {
    implementation("org.mybatis.generator:mybatis-generator-core:1.4.2")
    implementation("com.github.javaparser:javaparser-symbol-solver-core:3.24.4") {
        exclude(module = "guava")
    }
    implementation("com.google.guava:guava:32.1.2-jre")
    compileOnly("org.projectlombok:lombok:1.18.30")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2020.3.1")
    type.set("IU") // Target IDE Platform

    plugins.set(listOf("com.intellij.database", "com.intellij.java"))
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    patchPluginXml {
        // 指定插件兼容的idea的最小和最大版本
        sinceBuild.set("203.6682.168")
        untilBuild.set("233.*")
    }
}
