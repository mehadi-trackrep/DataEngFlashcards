buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.3")
    }
}

plugins {
    id("com.android.application") version "8.11.1" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    id("com.google.gms.google-services") version "4.4.3" apply false
}