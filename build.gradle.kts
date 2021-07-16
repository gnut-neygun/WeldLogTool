import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    id("org.jetbrains.compose") version "0.5.0-build235"
}

group = "me.tung.nguyen"
version = "1.0"

repositories {
    jcenter()
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.apache.pdfbox:pdfbox:2.0.23")
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:0.15.1")
    implementation("com.openhtmltopdf:openhtmltopdf-pdfbox:1.0.8")
    // https://mvnrepository.com/artifact/org.thymeleaf/thymeleaf
    implementation("org.thymeleaf:thymeleaf:3.0.12.RELEASE")
    testImplementation(platform("org.junit:junit-bom:5.7.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")

}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

compose.desktop {
    application {
        fromFiles()
        mainClass = "MainKt"
        nativeDistributions {
            val copyTask by tasks.register<Copy>("copyCSVAndReadMe") {
                from(project.fileTree("/") { include("test.csv", "WpsList.csv", "PleaseReadMeFirst.txt") })
                into(outputBaseDir.dir("main/app/MyTool"))
            }
            project.afterEvaluate {
                val distributableTask = tasks.named("createDistributable").get()
                distributableTask.finalizedBy(copyTask)
            }
            macOS {
                iconFile.set(File("build/resources/main/app-icon.png"))
                // macOS specific options
            }
            windows {
                iconFile.set(File("build/resources/main/app-icon.ico"))
                // Windows specific options
            }
            linux {
                iconFile.set(File("build/resources/main/app-icon.png"))
                // Linux specific options
            }

            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "MyTool"
            packageVersion = "1.0.0"
        }
    }
}

tasks.register("testGradle") {
    println(tasks.names)
}
