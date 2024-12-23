package com.example.ecommerce.utils

import android.annotation.SuppressLint
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics

object CrashlyticsUtils {

    const val CUSTOM_KEY_ENDPOINT = "Custom-key"
    const val CUSTOM_KEY = "Custom-key"

    fun sendLogToCrashlytics( msg:String ,vararg keys:String){
        keys.forEach { key ->
            FirebaseCrashlytics.getInstance().setCustomKey(key,msg)
        }
        FirebaseCrashlytics.getInstance().recordException(CustomCrashlyticsLogException(msg))
    }

    fun sendLogToCrashlytics( msg:String ,vararg keys:Pair<String,String>){
        keys.forEach { key ->
            FirebaseCrashlytics.getInstance().setCustomKey(key.first,key.second)
        }
        FirebaseCrashlytics.getInstance().recordException(CustomCrashlyticsLogException(msg))
    }


    @SuppressLint("SuspiciousIndentation")
    inline fun <reified T : Exception>sendCustomLogToCrashlytics(
        msg:String ,
        vararg keys:Pair<String,String>
    ){
        keys.forEach { key ->
            FirebaseCrashlytics.getInstance().setCustomKey(key.first,key.second)
        }
        val exception = T::class.java.getConstructor(String::class.java).newInstance(msg)
            FirebaseCrashlytics.getInstance().recordException(exception)
    }
}

class CustomCrashlyticsLogException(msg:String):Exception(msg)