    plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.whatif_tfg"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.whatif_tfg"
        minSdk = 24
        targetSdk = 33
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
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.6.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.6.0")
    implementation("androidx.activity:activity:1.8.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    // Importamos la Firebase BoM (se supone que contiene todas las librerías)
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    // TODO: Add the dependencies for Firebase products you want to use
    // Implementamos google analytics
    implementation("com.google.firebase:firebase-analytics")
    // Implementamos la plataforma de autentificación
    implementation("com.google.firebase:firebase-auth")
    // Implementación de Realtime Database
    implementation("com.google.firebase:firebase-database-ktx")
    //implementation("com.google.android.gms:play-services-auth:20.7.0")
    // Si queremos añadir más librerías podemos consultar las que hay en
    // https://firebase.google.com/docs/android/setup#available-libraries
    implementation ("androidx.recyclerview:recyclerview:1.3.2")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("androidx.drawerlayout:drawerlayout:1.2.0")
    implementation ("com.google.android.material:material:1.12.0")


}
