package com.android.senyaar.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.android.senyaar.R

class SplashScreen : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        //if (PermissionUtils.askAllPermissions(this))
        openStartup()
    }


    private fun openStartup() {
        val handler = Handler()
        handler.postDelayed({
            startActivity(Intent(this@SplashScreen, LoginOptionActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 2800)
    }
}