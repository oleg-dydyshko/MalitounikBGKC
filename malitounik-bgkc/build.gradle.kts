plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "by.carkva_gazeta.malitounik"
    compileSdk = 34

    defaultConfig {
        applicationId =  "by.carkva_gazeta.malitounik"
        minSdk = 19
        targetSdk = 34
        versionCode = 442194
        versionName = "4.3.11.9"

        //multiDexEnabled true

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
    implementation ("com.google.android.material:material:1.11.0")
    api ("androidx.appcompat:appcompat:1.6.1")
    api ("androidx.gridlayout:gridlayout:1.0.0")
    api ("com.google.android.play:feature-delivery:2.1.0")
    implementation ("com.google.android.play:app-update-ktx:2.1.0")
    implementation ("com.google.android.play:review-ktx:2.0.1")
    implementation ("com.google.android.gms:play-services-instantapps:18.0.1")
    api ("com.google.code.gson:gson:2.10.1")
    api ("androidx.webkit:webkit:1.9.0")
    api ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    api ("com.github.woxthebox:draglistview:1.7.3")
    api (platform("com.google.firebase:firebase-bom:32.7.0"))
    //api ("com.google.firebase:firebase-core:21.1.1")
    api ("com.google.firebase:firebase-storage-ktx")
    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation ("androidx.media3:media3-exoplayer:1.2.0")
    implementation ("androidx.media3:media3-exoplayer-smoothstreaming:1.2.0")
    implementation ("androidx.media:media:1.7.0")
    implementation ("androidx.core:core-ktx:1.12.0")
    //implementation ("androidx.multidex:multidex:2.0.1")
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
}

apply(plugin = "com.google.gms.google-services")
