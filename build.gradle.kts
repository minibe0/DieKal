import com.android.build.gradle.tasks.ProcessAndroidResources


// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("com.google.gms.google-services") version "4.3.15" apply false

}
tasks {
    register("processDebugResources", ProcessAndroidResources::class) {
        // processDebugResources 작업에 대한 구성
    }
}



apply(plugin = "com.google.gms.google-services")
