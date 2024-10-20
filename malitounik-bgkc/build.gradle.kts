plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "by.carkva_gazeta.malitounik"
    compileSdk = 35

    defaultConfig {
        applicationId =  "by.carkva_gazeta.malitounik"
        minSdk = 21
        targetSdk = 35
        versionCode = 442359
        versionName = "5.0.3.8"

        //multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndk {
            abiFilters.addAll(arrayOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    bundle {
        density.enableSplit = true
        language.enableSplit = false
        abi.enableSplit = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    dynamicFeatures += setOf(":resources", ":biblijateka", ":admin")
}

dependencies {
    //implementation fileTree(dir: "libs", include: ["*.jar"])
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("com.google.android.material:material:1.12.0")
    api ("androidx.appcompat:appcompat:1.7.0")
    api ("androidx.gridlayout:gridlayout:1.0.0")
    api ("com.google.android.play:feature-delivery:2.1.0")
    implementation ("com.google.android.play:app-update-ktx:2.1.0")
    implementation ("com.google.android.play:review-ktx:2.0.2")
    implementation ("com.google.android.gms:play-services-instantapps:18.1.0")
    api ("com.google.code.gson:gson:2.11.0")
    //api ("androidx.webkit:webkit:1.10.0")
    api ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    api ("com.github.woxthebox:draglistview:1.7.3")
    api (platform("com.google.firebase:firebase-bom:33.4.0"))
    //api ("com.google.firebase:firebase-core:21.1.1")
    api ("com.google.firebase:firebase-storage-ktx")
    api ("com.google.firebase:firebase-appcheck-playintegrity")
    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation ("androidx.media3:media3-exoplayer:1.4.1")
    implementation ("androidx.media3:media3-exoplayer-smoothstreaming:1.4.1")
    implementation ("androidx.media:media:1.7.0")
    implementation ("androidx.core:core-ktx:1.13.1")
    //implementation ("androidx.multidex:multidex:2.0.1")
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.2.1")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.6.1")
}

apply(plugin = "com.google.gms.google-services")
