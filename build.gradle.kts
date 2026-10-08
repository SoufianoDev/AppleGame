plugins {
    // Revert to 2.1.10 as required by the plugin version 0.13.1-4.4.1
    kotlin("jvm") version "2.1.10"
    id("com.utopia-rise.godot-kotlin-jvm") version "0.13.1-4.4.1"
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}

val userHome = System.getProperty("user.home")
val sdkPath = "$userHome/Android/Sdk"

godot {
    registrationFileBaseDir.set(projectDir.resolve("gdj"))
    isRegistrationFileGenerationEnabled.set(true)
    isAndroidExportEnabled.set(true)
    
    // USE THE NEWER BUILD-TOOLS (36.1.0) TO HANDLE KOTLIN 2.1 METADATA
    // This fixes the "com.android.tools.r8.internal.E10: Should never be called" error
    d8ToolPath.set(file("$sdkPath/build-tools/36.1.0/d8"))
    
    // Keep your compile SDK at 34 as you requested
    androidCompileSdkDir.set(file("$sdkPath/platforms/android-34"))
}

tasks.register("createJre") {
    doLast {
        val jreDir = projectDir.resolve("jvm/jre-amd64-linux")
        if (jreDir.exists()) {
            jreDir.deleteRecursively()
        }
        exec {
            commandLine(
                "jlink",
                "--add-modules", "java.base,java.logging,java.desktop,jdk.unsupported",
                "--output", jreDir.absolutePath
            )
        }
        println("JRE created successfully at ${jreDir.absolutePath}")
    }
}
