plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.weatherwear"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.weatherwear"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    /*Google Play Services Location 추가, FusedLocationProviderClient와 같은
    위치 관련 클래스와 기능을 사용할 수 있게 함.
    이를 통해 GPS 기반의 사용자 위치 정보를 효율적으로 가져올 수 있음*/
    implementation(libs.play.services.location)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.play.services.contextmanager)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // 앱의 build.gradle 파일
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp 로깅 인터셉터 추가 (네트워크 요청/응답 로그 확인용)
    implementation ("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")


    // ConstraintLayout 의존성 추가
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")

}
