plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.yuanbao.timetable"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.yuanbao.timetable"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    // 资源不压缩：wasm / js / 模型 gzip 由 WebView 直接读取
    androidResources {
        noCompress += listOf("wasm", "js", "gz", "traineddata")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    // WebViewAssetLoader：把 assets 映射成虚拟 https 域
    // 这一步是关键 —— Web Worker 无法在 file:// 下创建，必须给它一个同源 https 域
    implementation("androidx.webkit:webkit:1.8.0")
}
