import com.android.build.api.dsl.ApplicationBuildType

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.galacticai.networkpulse"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.galacticai.networkpulse"
        minSdk = 26
        targetSdk = 33
        versionCode = 3
        versionName = "0.4-beta"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        fun commonBuildConfig(type: ApplicationBuildType) {
            type.isMinifyEnabled = true
            type.proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isDebuggable = false
            commonBuildConfig(this)
        }
        debug {
            isDebuggable = true
            commonBuildConfig(this)
        }
    }

    val javaVersion = JavaVersion.VERSION_17
    composeOptions.kotlinCompilerExtensionVersion = "1.5.7"
    kotlinOptions.jvmTarget = javaVersion.toString()
    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.2")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.foundation:foundation:1.6.2")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.databinding:databinding-runtime:8.3.0")
    implementation("androidx.compose.ui:ui-tooling-preview-android:1.6.2")
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")
    implementation("androidx.compose.ui:ui-text-google-fonts:1.6.2")
    implementation("com.github.Breens-Mbaka:Searchable-Dropdown-Menu-Jetpack-Compose:0.2.8")
    implementation("com.github.GIGAMOLE:ComposeFadingEdges:1.0.4")
    implementation("com.github.Gurupreet:FontAwesomeCompose:1.1.0")
    implementation("org.greenrobot:eventbus:3.3.1")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor")
}


