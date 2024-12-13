import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}
android {
    namespace = "com.example.StudyCoding"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.StudyCoding"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // BuildConfig에 정의 (코드에서 사용)
        // BuildConfig에 judgeO_api_key 추가
        buildConfigField(
            "String",
            "JUDGE0_API_KEY",
            localProperties["judge0_api_key"]?.toString() ?: "\"\""
        )

        // BuildConfig에 BrowseAI API Key 추가
        buildConfigField(
            "String",
            "BROWSEAI_API_KEY",
            localProperties["browseai_api_key"]?.toString() ?: "\"\""
        )

        // 로봇 ID를 BuildConfig에 추가
        buildConfigField(
            "String",
            "BROWSEAI_ROBOT_ID",
            localProperties["browseai_robot_id"]?.toString() ?: "\"\""
        )

    }

    buildFeatures {
        buildConfig = true // BuildConfig 사용 활성화
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("com.github.kbiakov:CodeView-Android:1.3.2") // CodeView 라이브러리 추가
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.retrofit)
    implementation(libs.retrofit2.converter.gson)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("com.android.support:recyclerview-v7:28.0.0")
    implementation("com.github.markusressel:KodeEditor:v4.0.1")
    implementation("com.otaliastudios:zoomlayout:1.9.0")
    implementation("com.github.markusressel.KodeHighlighter:core:v4.0.3")


}


