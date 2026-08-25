import java.io.FileInputStream
import java.util.Properties

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
    compileSdk = 35

    defaultConfig {
        applicationId = "com.lumyrinth.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    buildFeatures { compose = true }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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

dependencies {
    constraints {
        implementation(libs.jdom2) {
            because("Upgrade jdom2 to 2.0.6.1 for security and compatibility")
        }
        implementation(libs.bouncycastle.bcprov) {
            because("Upgrade bcprov-jdk18on to 1.80 or later")
        }
        implementation(libs.bouncycastle.bcpkix) {
            because("Upgrade bcpkix-jdk18on to 1.80 or later")
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
            because("Upgrade netty-handler-proxy to 4.1.118.Final or later")
        }
    }
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime)
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
    implementation(libs.androidx.media3.exoplayer)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
