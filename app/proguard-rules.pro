# ExpenseTracker R8 rules

-keepattributes RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations,RuntimeVisibleParameterAnnotations,RuntimeInvisibleParameterAnnotations,AnnotationDefault

-keep class com.example.expensetracker.data.remote.dto.** { *; }

-keep class com.example.expensetracker.domain.model.** { *; }

-keep class com.example.expensetracker.data.remote.api.** { *; }

-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

-dontwarn javax.annotation.**
-dontwarn org.codehaus.mojo.animal_sniffer.**