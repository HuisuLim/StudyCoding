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
            localProperties["judgeO_api_key"]?.toString() ?: "\"\""
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

}