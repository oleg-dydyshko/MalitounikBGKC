plugins {
    id ("com.android.dynamic-feature")
    id ("org.jetbrains.kotlin.android")
}

android {
    namespace = "by.carkva_gazeta.admin"
    compileSdk = 35

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":malitounik-bgkc"))
    implementation (libs.androidx.constraintlayout)
    implementation (libs.material)
    implementation (libs.commons.text)
    implementation (libs.picasso)
    implementation (libs.androidx.core.ktx)
    testImplementation (libs.junit)
    androidTestImplementation (libs.androidx.junit)
    androidTestImplementation (libs.androidx.espresso.core)
}
