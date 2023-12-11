plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.13.2"
}

group = "com.zengdw.mybatis"
version = "2.2"

repositories {
    mavenLocal()
    maven("https://maven.aliyun.com/repository/public/")
    mavenCentral()
}

dependencies {
    implementation("org.mybatis.generator:mybatis-generator-core:1.4.2")
    implementation("com.github.javaparser:javaparser-symbol-solver-core:3.24.4")

}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2023.3")
    type.set("IU") // Target IDE Platform

    plugins.set(listOf("com.intellij.database"))
}

tasks {
    patchPluginXml {
        // 指定插件兼容的idea的最小和最大版本
        sinceBuild.set("233")
//        untilBuild.set("232.*")
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
