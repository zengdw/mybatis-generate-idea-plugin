plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.12.0"
}

group = "com.zengdw.mybatis"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    maven("https://maven.aliyun.com/repository/public/")
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.1.4")
    type.set("IU") // Target IDE Platform

    plugins.set(listOf("com.intellij.database"))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    patchPluginXml {
        // 指定插件兼容的idea的最小和最大版本
        sinceBuild.set("221")
        untilBuild.set("231.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
