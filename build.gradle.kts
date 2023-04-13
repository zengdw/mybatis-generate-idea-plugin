plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.13.2"
}

group = "com.zengdw.mybatis"
version = "1.0"

repositories {
    mavenLocal()
    maven("https://maven.aliyun.com/repository/public/")
    mavenCentral()
}

dependencies {
    implementation("org.mybatis.generator:mybatis-generator-core:1.4.2")
    implementation("org.freemarker:freemarker:2.3.32")
    implementation("com.github.javaparser:javaparser-symbol-solver-core:3.24.4")

}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2020.1.1")
    type.set("IU") // Target IDE Platform

    plugins.set(listOf("com.intellij.database"))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

    patchPluginXml {
        // 指定插件兼容的idea的最小和最大版本
        sinceBuild.set("201")
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
