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

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#-----------------混淆配置设定------------------------------------------------------------------------
-optimizationpasses 5                                                               #指定代码压缩级别
-dontusemixedcaseclassnames                                                        #不使用大小写混合
-dontskipnonpubliclibraryclasses                                                  #指定不忽略非公共类库
-dontpreverify                                                                      #不预校验，如果需要预校验，是-dontoptimize
-ignorewarnings                                                                     #屏蔽警告
-verbose                                                                             #混淆时记录日志
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*            #混淆时的算法

-keepattributes EnclosingMethod
-keepattributes InnerClasses

#-----------------生成日志数据，gradle build时在本项目根目录输出---------------------------------------
-dump class_files.txt            #apk包内所有class的内部结构
-printseeds seeds.txt            #未混淆的类和成员
-printusage unused.txt           #打印未被使用的代码
-printmapping mapping.txt        #混淆前后的映射

#-----------------混淆第三方jar包，如果出现Warning:Exception while processing task java.io.IOException: The same input jar [XXX] is specified twice应当去掉-----------------------------------------------------------------------
#-libraryjars libs/nineoldandroids-2.4.0.jar

#-----------------不需要混淆第三方类库------------------------------------------------------------------
#-----------------友盟社会化分享-------------------
-dontshrink
-dontoptimize
-dontwarn com.google.android.maps.**
-dontwarn android.webkit.WebView
-dontwarn com.umeng.**
-dontwarn com.tencent.weibo.sdk.**
-dontwarn com.facebook.**
-keep public class javax.**
-keep public class android.webkit.**
-dontwarn android.support.v4.**
-keep enum com.facebook.**
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

-keep public interface com.facebook.**
-keep public interface com.tencent.**
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**

-keep public class com.umeng.socialize.* {*;}


-keep class com.facebook.**
-keep class com.facebook.** { *; }
-keep class com.umeng.scrshot.**
-keep public class com.tencent.** {*;}
-keep class com.umeng.socialize.sensor.**
-keep class com.umeng.socialize.handler.**
-keep class com.umeng.socialize.handler.*
-keep class com.umeng.weixin.handler.**
-keep class com.umeng.weixin.handler.*
-keep class com.umeng.qq.handler.**
-keep class com.umeng.qq.handler.*
-keep class com.tencent.mm.sdk.** {
   *;
}
-keep class com.tencent.mm.opensdk.** {
   *;
}
-keep class com.tencent.wxop.** {
   *;
}
-keep class com.tencent.mm.sdk.** {
   *;
}
-dontwarn twitter4j.**
-keep class twitter4j.** { *; }

-keep class com.tencent.** {*;}
-dontwarn com.tencent.**
-keep class com.kakao.** {*;}
-dontwarn com.kakao.**
-keep public class com.umeng.com.umeng.soexample.R$*{
    public static final int *;
}
-keep public class com.linkedin.android.mobilesdk.R$*{
    public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class com.tencent.open.TDialog$*
-keep class com.tencent.open.TDialog$* {*;}
-keep class com.tencent.open.PKDialog$*
-keep class com.tencent.open.PKDialog$* {*;}
-keep class com.sina.** {*;}
-dontwarn com.sina.**
-keep class  com.alipay.share.sdk.** {
   *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keep class com.linkedin.** { *; }
-keep class com.android.dingtalk.share.ddsharemodule.** { *; }
-keepattributes Signature

#==================gson && protobuf========================
-dontwarn com.google.**
-keep class com.google.gson.** {*;}
-keep class com.google.protobuf.** {*;}


#---------------（可选）避免Log打印输出---------------
-assumenosideeffects class android.util.Log {
   public static *** v(...);
   public static *** d(...);
   public static *** i(...);
   public static *** w(...);
 }

#-----------------qiujuer-------------------
-keep class net.qiujuer.genius.** {*;}
-dontwarn net.qiujuer.genius.**


#-----------------glide-------------------
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#-----------------okhttp-------------------
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform


#-----------------retrofit-------------------
-dontwarn okio.**
-dontwarn javax.annotation.**
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**


#-----------------rxjava-------------------
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}

#-----------------banner-------------------
-keep class com.youth.banner.** {
    *;
 }

#-----------------easing-------------------
-keep class com.daimajia.easing.** { *; }
-keep class com.daimajia.androidanimations.** { *; }
-keep interface com.daimajia.easing.** { *; }


#-----------------eventbus-------------------
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keepclassmembers class ** {
    public void onEvent*(**);
}

#-----------------https://github.com/AriaLyy/Aria-------------------
-dontwarn com.arialyy.aria.**
-keep class com.arialyy.aria.**{*;}
-keep class **$$DownloadListenerProxy{ *; }
-keep class **$$UploadListenerProxy{ *; }
-keep class **$$DownloadGroupListenerProxy{ *; }
-keepclasseswithmembernames class * {
    @Download.* <methods>;
    @Upload.* <methods>;
    @DownloadGroup.* <methods>;
}

#Gson混淆配置
-keepattributes Annotation
-keep class com.idea.fifaalarmclock.entity.*
-keep class com.google.gson.stream.* { *; }

#引用mars的xlog，混淆配置
-keep class com.tencent.mars.** {
  public protected private *;
}

#-----------------不需要混淆系统组件等-------------------------------------------------------------------
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference

-keep class cn.njmeter.njmeter.bean.**{*;}                                      #过滤掉实体类
-keep class cn.njmeter.njmeter.widget.**{*;}                                    #过滤掉自定义控件

#----------------保护指定的类和类的成员，但条件是所有指定的类和类成员是要存在------------------------------------
-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}


#----------------保持自定义控件类不被混淆------------------------------------------------------------------------
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}


#----------------Mob SMS短信验证码------------------------------------------------------------------------
-keep class com.mob.**{*;}
-keep class cn.smssdk.**{*;}
-dontwarn com.mob.**

#----------------dkplayer视频播放器------------------------------------------------------------------------
-keep class tv.danmaku.ijk.** { *; }
-dontwarn tv.danmaku.ijk.**
-keep class com.dueeeke.videoplayer.** { *; }
-dontwarn com.dueeeke.videoplayer.**


#----------------不知道什么鬼东西------------------------------------------------------------------------
-keep class org.xmlpull.v1.** { *;}
-dontwarn org.xmlpull.v1.**


#----------------图像方向纠正与裁切------------------------------------------------------------------------
-keep class me.pqpo.smartcropperlib.**{*;}