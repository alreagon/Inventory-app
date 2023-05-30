package com.zexceed.skripsiehapp.features.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.zexceed.skripsiehapp.MainActivity
import com.zexceed.skripsiehapp.R
import com.zexceed.skripsiehapp.features.auth.login.LoginActivity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        splashScreen.setKeepOnScreenCondition { true }

        if(Firebase.auth.currentUser?.uid != null) {
            startActivity(Intent(this, MainActivity::class.java)).also {
                finishAffinity()
            }
        } else {
            startActivity(Intent(this, LoginActivity::class.java)).also {
                finishAffinity()
            }
        }
    }

    companion object {
        const val TAG = "SplashActivity"
    }
}