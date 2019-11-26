package com.android.senyaar.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.android.senyaar.R
import com.android.senyaar.Utils.CommonMethods
import com.android.senyaar.Utils.PreferenceHelper
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.appbar.*

class Settings : AppCompatActivity() {
    lateinit var prefs: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        initialize()
        customToolbar()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            //when home button is clicked, go back
            android.R.id.home -> {
                onBackPressed()
                overridePendingTransition(0, 0)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun customToolbar() {
        setSupportActionBar(toolbar_edit)
        text_toolbar.text = "Settings"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun initialize() {
        prefs = PreferenceHelper(this)


        rl_change_password.setOnClickListener {
            if (intent.getStringExtra("role").equals("ROLE_CUSTOMER")) {
                if (CommonMethods.passengerRedirect(prefs, localClassName, this@Settings)) {
                    startActivity(Intent(this, ResetPasswordActivity::class.java))
                }
            } else {
                if (CommonMethods.driverRedirects(prefs, localClassName, this@Settings)) {
                    startActivity(Intent(this, ResetPasswordActivity::class.java))
                }
            }
        }
        tv_rate_app.setOnClickListener { startActivity(Intent(this, RatingActivity::class.java)) }
    }
}