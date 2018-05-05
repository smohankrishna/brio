# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\java\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class email to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}




-dontwarn org.apache.harmony.awt.**
-dontwarn com.bbpos.**
-dontwarn com.sun.mail.imap.**
-dontwarn javax.activation.**
-dontwarn org.apache.harmony.awt.**
-dontwarn org.slf4j.**

-keep class org.apache.harmony.awt.**{
*;
}

-keep class com.bbpos.**{
*;
}

-keep class com.sun.mail.imap.*{
*;
}

-keep class javax.activation.**{
*;
}

-keep class java.beans.**{
*;
}

-keep class org.apache.harmony.awt.**{
*;
}

-keep class org.slf4j.**{
*;
}

-keep class com.google.gson** {
*;
}

-keepclassmembers class com.google.gson** {
*;
}

-keep class net.hova_it.barared.brio.apis.models.entities.**{
*;
}

-keep class com.starmicronics.starioextension.**{
*;
}

-keep class com.google.zxing.**{
*;
}

-dontwarn  com.starmicronics.starioextension.**

-dontwarn  com.google.zxing.**