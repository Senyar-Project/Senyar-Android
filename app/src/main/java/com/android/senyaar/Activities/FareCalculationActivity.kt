package com.android.senyaar.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.android.senyaar.R
import kotlinx.android.synthetic.main.appbar.*

class FareCalculationActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rider_receipt)
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
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}