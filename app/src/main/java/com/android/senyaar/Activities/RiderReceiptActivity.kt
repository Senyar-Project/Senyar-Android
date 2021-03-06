package com.android.senyaar.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.android.senyaar.R
import kotlinx.android.synthetic.main.appbar.*
import kotlinx.android.synthetic.main.rider_receipt.*

class RiderReceiptActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rider_receipt)
        initView()
        customToolbar()
    }

    private fun initView() {
        journey_fare.text=intent.getStringExtra("amount")
        distance_data.text=intent.getStringExtra("distance")
        date_data.text=intent.getStringExtra("date")
        time_data.text=intent.getStringExtra("time")
        distance_fare_data.text=intent.getStringExtra("distance")
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
        text_toolbar.text="Journey Details"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

}