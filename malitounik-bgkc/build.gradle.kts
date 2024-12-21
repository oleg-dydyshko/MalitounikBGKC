plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
}

android {
    namespace = "by.carkva_gazeta.malitounik"
    compileSdk = 35

    defaultConfig {
        applicationId =  "by.carkva_gazeta.malitounik"
        minSdk = 21
        targetSdk = 35
        versionCode = 442400
        versionName = "5.1.1.12"

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
    dynamicFeatures += setOf(":resources", ":admin")
}

dependencies {
    //implementation fileTree(dir: "libs", include: ["*.jar"])
    implementation (libs.androidx.constraintlayout)
    implementation (libs.material)
    api (libs.androidx.appcompat)
    api (libs.androidx.gridlayout)
    api (libs.feature.delivery)
    implementation (libs.app.update.ktx)
    implementation (libs.review.ktx)
    implementation (libs.play.services.instantapps)
    api (libs.gson)
    //api (libs.pdf.viewer)
    //api ("androidx.webkit:webkit:1.10.0")
    api (libs.androidx.swiperefreshlayout)
    api (libs.draglistview)
    api (platform(libs.firebase.bom))
    //api ("com.google.firebase:firebase-core:21.1.1")
    api (libs.firebase.storage.ktx)
    api (libs.firebase.appcheck.playintegrity)
    implementation (libs.picasso)
    implementation (libs.androidx.media3.exoplayer)
    implementation (libs.androidx.media3.exoplayer.smoothstreaming)
    implementation (libs.androidx.media)
    implementation (libs.androidx.core.ktx)
    //implementation(libs.androidx.pdf.viewer)
    implementation (libs.itextg)
    implementation (libs.core)
    //implementation(libs.itext7.core)
    //implementation ("androidx.multidex:multidex:2.0.1")
    testImplementation (libs.junit)
    androidTestImplementation (libs.androidx.junit)
    androidTestImplementation (libs.androidx.espresso.core)
}

apply(plugin = "com.google.gms.google-services")
