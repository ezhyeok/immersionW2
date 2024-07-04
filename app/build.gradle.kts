plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("kotlin-parcelize")
    id("kotlin-kapt")
}


android {
    namespace = "com.example.tab3"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.tab3"
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

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.gson)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.databinding.runtime)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.github.prolificinteractive:material-calendarview:2.0.1")
    implementation("com.jakewharton.threetenabp:threetenabp:1.2.1")

    implementation("androidx.room:room-runtime:2.2.6")
    kapt("androidx.room:room-compiler:2.2.6")
    implementation("androidx.room:room-ktx:2.2.6")
    implementation("androidx.room:room-rxjava2:2.2.6")
    implementation("androidx.room:room-guava:2.2.6")
    kapt("org.xerial:sqlite-jdbc:3.34.0")
    androidTestImplementation("androidx.room:room-testing:2.2.6")

    implementation ("com.google.android.material:material:1.9.0")

    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    implementation("jp.wasabeef:glide-transformations:4.3.0")
    implementation("com.github.chrisbanes:PhotoView:2.3.0")
    implementation("com.naver.maps:map-sdk:3.18.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    //implementation("com.google.gms:google-services:4.3.15")
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
    implementation("com.google.firebase:firebase-analytics")
    // RxJava
    implementation("com.squareup.retrofit2:adapter-rxjava3:2.9.0")
    implementation("io.reactivex.rxjava3:rxjava:3.0.6")
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.0")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")
    // RxBinding (안드로이드 뷰 컴포넌트와 연결 목적) 필요
    implementation("com.jakewharton.rxbinding4:rxbinding:4.0.0")
    implementation("com.jakewharton.rxbinding4:rxbinding-core:4.0.0")
    implementation("com.jakewharton.rxbinding4:rxbinding-appcompat:4.0.0")
    implementation("com.jakewharton.rxbinding4:rxbinding-drawerlayout:4.0.0")
    implementation("com.jakewharton.rxbinding4:rxbinding-leanback:4.0.0")
    implementation("com.jakewharton.rxbinding4:rxbinding-recyclerview:4.0.0")
    implementation("com.jakewharton.rxbinding4:rxbinding-slidingpanelayout:4.0.0")
    implementation("com.jakewharton.rxbinding4:rxbinding-swiperefreshlayout:4.0.0")
    implementation("com.jakewharton.rxbinding4:rxbinding-viewpager:4.0.0")
    implementation("com.jakewharton.rxbinding4:rxbinding-viewpager2:4.0.0")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")

    // Koin (DI)
    // Koin for Android
    //implementation("io.insert-koin:koin-android:3.6.0")

// Koin AndroidX Scope features
    //implementation("io.insert-koin:koin-androidx-scope:3.6.0")

// Koin AndroidX ViewModel features
    //implementation("io.insert-koin:koin-androidx-viewmodel:3.6.0")
    implementation("io.insert-koin:koin-android:3.4.3")

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.8.1")
    implementation("com.squareup.retrofit2:converter-gson:2.8.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.8.1")
    // indicator seekbar
    implementation("com.google.android.material:material:1.4.0-beta01")
    implementation("com.github.warkiz:IndicatorSeekBar:2.1.1")
    //implementation("gun0912.ted:tedclustering-naver:x.y.z")
    implementation("io.github.ParkSangGwon:tedclustering-naver:1.0.2")
    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.19")

    // animation
    implementation("com.daimajia.androidanimations:library:2.4@aar")
}