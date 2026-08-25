plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
}

buildscript {
    configurations.all {
        resolutionStrategy {
            force("org.jdom:jdom2:2.0.6.1")
            force("org.bouncycastle:bcprov-jdk18on:1.80")
            force("org.bouncycastle:bcpkix-jdk18on:1.80")
            force("org.bitbucket.b_c:jose4j:0.9.6")
            force("org.apache.commons:commons-lang3:3.18.0")
            force("io.netty:netty-codec:4.1.118.Final")
            force("io.netty:netty-codec-http:4.1.118.Final")
            force("io.netty:netty-codec-http2:4.1.118.Final")
            force("io.netty:netty-handler-proxy:4.1.118.Final")
        }
    }
}

allprojects {
    configurations.all {
        resolutionStrategy {
            force("org.jdom:jdom2:2.0.6.1")
            force("org.bouncycastle:bcprov-jdk18on:1.80")
            force("org.bouncycastle:bcpkix-jdk18on:1.80")
            force("org.bitbucket.b_c:jose4j:0.9.6")
            force("org.apache.commons:commons-lang3:3.18.0")
            force("io.netty:netty-codec:4.1.118.Final")
            force("io.netty:netty-codec-http:4.1.118.Final")
            force("io.netty:netty-codec-http2:4.1.118.Final")
            force("io.netty:netty-handler-proxy:4.1.118.Final")
        }
    }
}
