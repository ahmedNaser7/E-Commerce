package com.example.ecommerce

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@HiltAndroidApp
class MyApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        listenToNetworkConnective()
    }

    @SuppressLint("CheckResult")
    private fun listenToNetworkConnective() {
        ReactiveNetwork
            .observeInternetConnectivity()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{isConnected ->
               Log.d(TAG,"Connected to internet : $isConnected")
               FirebaseCrashlytics.getInstance().setCustomKey(TAG," Connected to internet : $isConnected")
            }
    }

    companion object{
        const val TAG = "MyApplication"
    }
}