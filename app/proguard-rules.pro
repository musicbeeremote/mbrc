-printmapping proguard/mapping.txt

-keepattributes SourceFile,LineNumberTable

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference

-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}


-keep class javax.inject.** { *; }
-keep class javax.annotation.** { *; }

-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}

# Proguard configuration for Jackson 2.x (fasterxml package instead of codehaus package)

-keep class com.fasterxml.jackson.databind.ObjectMapper {
    public <methods>;
    protected <methods>;
}
-keep class com.fasterxml.jackson.databind.ObjectWriter {
    public ** writeValueAsString(**);
}

## Retrolambda specific rules ##

# as per official recommendation: https://github.com/evant/gradle-retrolambda#proguard
-dontwarn java.lang.invoke.*

#Toothpick
# Do not obfuscate annotation scoped classes
-keepnames @javax.inject.Singleton class *
# Add any custom defined @Scope (e.g. @Singleton) annotations here
# because proguard does not allow annotation inheritance rules

# Do not obfuscate classes with Injected Constructors
-keepclasseswithmembernames class * {
    @javax.inject.Inject <init>(...);
}

# Do not obfuscate classes with Injected Fields
-keepclasseswithmembernames class * {
    @javax.inject.Inject <fields>;
}

# Do not obfuscate classes with Injected Methods
-keepclasseswithmembernames class * {
    @javax.inject.Inject <methods>;
}

## Square Picasso specific rules ##
## https://square.github.io/picasso/ ##

-dontwarn com.squareup.okhttp.**


# AppCompat v7
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

# Support Design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }


-dontwarn com.fasterxml.jackson.databind.ext.**

### Jackson SERIALIZER SETTINGS
-keepclassmembers class * {
    @com.fasterxml.jackson.annotation.* <fields>;
    @com.fasterxml.jackson.annotation.* <init>(...);
}

# Jackson
-dontskipnonpubliclibraryclassmembers
-keepattributes *Annotation*,EnclosingMethod,Signature
-keepnames class com.fasterxml.jackson.** { *; }
-dontwarn com.fasterxml.jackson.databind.**

# Keep anything annotated with @JsonCreator
-keepclassmembers public class * {
    @com.fasterxml.jackson.annotation.JsonCreator *;
    @com.fasterxml.jackson.annotation.JsonProperty *;
}

-keep class kotlin.** { *; }

-keep class com.kelsos.mbrc.** {
    void set*(***);
    void set*(int, ***);

    boolean is*();
    boolean is*(int);

    *** get*();
    *** get*(int);
}

-keep class androidx.datastore.*.** {*;}
-keep public class * extends com.google.protobuf.GeneratedMessageLite { *; }
