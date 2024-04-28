package com.example.ecommerce.ui.home

import android.animation.ObjectAnimator
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.ecommerce.R
import com.example.ecommerce.data.datasource.datastore.AppPreferencesDataSource
import com.example.ecommerce.data.repository.common.AppDataStoreRepositoryImpl
import com.example.ecommerce.ui.common.viewmodel.UserViewModel
import com.example.ecommerce.ui.common.viewmodel.UserViewModelFactory
import com.example.ecommerce.ui.auth.AuthActivity
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
     private val userViewModel: UserViewModel by viewModels {
         UserViewModelFactory(AppDataStoreRepositoryImpl(appPreferencesDataSource = AppPreferencesDataSource(this)))
     }
    override fun onCreate(savedInstanceState: Bundle?) {
        initSplashScreen()
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Main) {
           val isLoggedIn = userViewModel.isUserLoggedIn().first()
            Log.d(TAG,"onCreate : Logged In : $isLoggedIn")
            if(isLoggedIn){
                setContentView(R.layout.activity_main)
            }else{
                goToAuthActivity()
            }
        }


    }

    private fun goToAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val option = ActivityOptions.makeCustomAnimation(
            this,android.R.anim.fade_in,android.R.anim.fade_out
        )
        startActivity(intent,option.toBundle())
    }

    private fun initSplashScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            installSplashScreen()
             splashScreen.setOnExitAnimationListener {splashScreenView ->
                 // Create your custom animation.
                 val slideUp = ObjectAnimator.ofFloat(
                     splashScreenView,
                     View.TRANSLATION_Y,
                     0f,
                     -splashScreenView.height.toFloat()
                 )
                 slideUp.interpolator = AnticipateInterpolator()
                 slideUp.duration = 1000L

                 // Call SplashScreenView.remove at the end of your custom animation.
                 slideUp.doOnEnd { splashScreenView.remove() }

                 // Run your animation.
                 slideUp.start()
             }

        }else{
            setTheme(R.style.Theme_ECommerce)
        }


    }
    companion object{
        const val TAG= "MainActivity"
    }
}