plugins {
    id("org.jetbrains.intellij.platform") version "2.2.1"
}

group = "com.zengdw.mybatis"
version = "3.2.0"

repositories {
    mavenLocal()
    maven("https://maven.aliyun.com/repository/public/")
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    implementation("org.mybatis.generator:mybatis-generator-core:1.4.2")
    implementation("com.github.javaparser:javaparser-symbol-solver-core:3.24.4") {
        exclude(module = "guava")
    }
    implementation("com.google.guava:guava:32.1.2-jre")
    compileOnly("org.projectlombok:lombok:1.18.30")

    intellijPlatform {
        val type = providers.gradleProperty("platformType").get()
        val version = providers.gradleProperty("platformVersion").get()
        create(type, version)
//        local("C:\\JetBrains\\IntelliJ IDEA Ultimate")
        bundledPlugins(providers.gradleProperty("platformBundledPlugins").map { it.split(',') })
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "232.*"
            untilBuild = provider { null }
        }
    }
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
}
