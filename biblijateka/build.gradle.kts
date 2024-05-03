plugins {
    id ("com.android.dynamic-feature")
    id ("org.jetbrains.kotlin.android")
}

android {
    namespace = "by.carkva_gazeta.biblijateka"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    }
}

dependencies {
    //implementation fileTree(dir: "libs", include: ["*.jar"])
    implementation(project(":malitounik-bgkc"))
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("com.google.android.material:material:1.12.0")
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation ("com.github.barteksc:android-pdf-viewer:3.2.0-beta.1")
    implementation ("androidx.core:core-ktx:1.13.1")
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
}
