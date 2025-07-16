import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    // id("kotlin-android")
    // id("dev.flutter.flutter-gradle-plugin")
    id("com.google.gms.google-services")
    
}

// Keystore properties loading
val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("key.properties")
if (keystorePropertiesFile.exists()) {
    keystorePropertiesFile.inputStream().use {
        keystoreProperties.load(it)
    }
}

dependencies {
  // Import the Firebase BoM
  implementation(platform("com.google.firebase:firebase-bom:33.16.0"))


  // TODO: Add the dependencies for Firebase products you want to use
  // When using the BoM, don't specify versions in Firebase dependencies
  implementation("com.google.firebase:firebase-analytics")


  // Add the dependencies for any other desired Firebase products
  // https://firebase.google.com/docs/android/setup#available-libraries
}

android {
    namespace = "com.example.shopping_app"
    compileSdk = flutter.compileSdkVersion
    ndkVersion = "25.1.8937393" // Ensure this NDK version is installed via Android Studio SDK Manager

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    defaultConfig {
        applicationId = "com.granthgupta2003.shopping_app"
        minSdk = flutter.minSdkVersion
        targetSdk = flutter.targetSdkVersion
        versionCode = flutter.versionCode
        versionName = flutter.versionName
    }

    // Handle native library conflicts.
    // IMPORTANT: Removed 'doNotStrip("**/*.so")' from here.
    // This allows the 'debugSymbolLevel' setting in the buildTypes to correctly strip debug symbols.
    packagingOptions {
        pickFirst("**/libc++_shared.so")
        pickFirst("**/libjsc.so")
        pickFirst("**/libflutter.so")
        pickFirst("**/libapp.so")
    }

    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties.getProperty("storeFile"))
            storePassword = keystoreProperties.getProperty("storePassword")
            keyAlias = keystoreProperties.getProperty("keyAlias")
            keyPassword = keystoreProperties.getProperty("keyPassword")
        }
    }

    buildTypes {
        getByName("release") {
            // Apply the release signing configuration
            signingConfig = signingConfigs.getByName("release")

            // Enable minification (code shrinking via R8/ProGuard) and resource shrinking for release builds.
            // This addresses the "Removing unused resources requires unused code shrinking to be turned on" error.
            isMinifyEnabled = true
            isShrinkResources = true

            // Configure NDK debug symbol level to strip most debug symbols.
            // 'SYMBOL_TABLE' keeps enough information for crash reporting without making the binary too large.
            ndk {
                debugSymbolLevel = "SYMBOL_TABLE"
            }

            // For release builds, it's generally recommended to set these to false explicitly
            isDebuggable = false
            isJniDebuggable = false
        }
    }
}

flutter {
    source = "../.."
}
