plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.pokedex"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.pokedex"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    packagingOptions {
        resources {
            // evita conflito de arquivo duplicado entre compilers
            pickFirsts += "META-INF/gradle/incremental.annotation.processors"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.volley)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Retrofit
    /*  biblioteca que permite definir endpoints como interfaces Kotlin com suporte a suspend/coroutines,
        delega serialização JSON ao converter-gson, integra-se ao OkHttp (e logging-interceptor)
        para controle e depuração das requisições, centraliza tratamento de erros e respostas
        no repositório e facilita testes e manutenção ao desacoplar a camada de rede da UI
     */
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")

    implementation("androidx.palette:palette:1.0.0")

    // Gson (usado por Retrofit converter para para serialização e desserialização JSON em Java/Kotlin)
    implementation("com.google.code.gson:gson:2.10.1")

    // Timber (logging)
    /*
        biblioteca de logging leve (por Jake Wharton) que simplifica o uso do Log do Android: fornece
        chamadas estáticas fáceis (Timber.d, Timber.e, etc.)
     */
    implementation("com.jakewharton.timber:timber:4.7.1")

    // Coil
    /*
        biblioteca moderna para carregamento de imagens em Android escrita em Kotlin, otimizada para
        desempenho e integração com Jetpack Compose via coil-compose.
     */
    implementation("io.coil-kt:coil-compose:2.5.0")
}