import java.io.FileInputStream
import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

android {
    namespace = "com.lumyrinth.app"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.lumyrinth.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 2
        versionName = "1.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    val signingProperties = Properties()
    val signingPropertiesFile = rootProject.file("keystore.properties")
    if (signingPropertiesFile.exists()) {
        FileInputStream(signingPropertiesFile).use { signingProperties.load(it) }
    }
    val hasReleaseSigning = signingProperties.getProperty("storeFile") != null
    if (hasReleaseSigning) {
        signingConfigs {
            create("release") {
                storeFile = rootProject.file(signingProperties.getProperty("storeFile"))
                storePassword = signingProperties.getProperty("storePassword")
                keyAlias = signingProperties.getProperty("keyAlias")
                keyPassword = signingProperties.getProperty("keyPassword")
            }
        }
    }
    buildTypes {
        debug {
            // Uses Android standard per-user debug keystore automatically
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            if (hasReleaseSigning) signingConfig = signingConfigs.getByName("release")
        }
    }
}

kotlin {
    // Compile to Java 21 bytecode without requiring a separate JDK 21 install.
    // Android Studio's configured JDK (JBR) supplies the compiler.
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

dependencies {
    constraints {
        implementation(libs.jdom2) {
            because("Upgrade jdom2 to 2.0.6.1 for security and compatibility")
        }
        implementation(libs.bouncycastle.bcprov) {
            because("Keep the transitive cryptography provider on the patched release")
        }
        implementation(libs.bouncycastle.bcpkix) {
            because("Keep the transitive PKIX provider on the patched release")
        }
        implementation(libs.jose4j) {
            because("Upgrade jose4j to 0.9.6 or later")
        }
        implementation(libs.commons.lang3) {
            because("Upgrade commons-lang3 to 3.17.0 or later")
        }
        implementation(libs.netty.codec) {
            because("Upgrade netty-codec to 4.1.118.Final or later")
        }
        implementation(libs.netty.codec.http) {
            because("Upgrade netty-codec-http to 4.1.118.Final or later")
        }
        implementation(libs.netty.codec.http2) {
            because("Upgrade netty-codec-http2 to 4.1.118.Final or later")
        }
        implementation(libs.netty.handler.proxy) {
            because("Keep the transitive Netty stack on one patched release")
        }
        implementation(libs.netty.handler) {
            because("Align all Netty modules to the patched release")
        }
    }
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.media3.exoplayer)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.androidx.room.testing)
    testImplementation(libs.androidx.work.testing)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
