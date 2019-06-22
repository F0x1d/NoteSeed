# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

-dontobfuscate

-renamesourcefileattribute SourceFile
-repackageclasses 'noteseed'
-allowaccessmodification

-obfuscationdictionary proguard-dictionary.txt
-packageobfuscationdictionary proguard-dictionary.txt
-classobfuscationdictionary proguard-dictionary.txt

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep class com.f0x1d.notes.utils.sync.SyncUtils {
    *;
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keep class * extends com.google.api.client.json.GenericJson {
    *;
}
-keep class com.google.api.services.drive.** {
    *;
}

-keep class com.f0x1d.notes.adapter.ItemsAdapter { *; }

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}