# Lumyrinth ProGuard/R8 Rules

# Room Database rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Kotlin Coroutines & Serialization
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}

# AndroidX Keep annotations
-keep @interface androidx.annotation.Keep
-keep @androidx.annotation.Keep class * { *; }
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}
