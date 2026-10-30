# Gson serializa estos modelos por reflexión: no ofuscar sus campos.
-keep class com.edu.resources.data.** { *; }
-keepattributes Signature, *Annotation*
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
