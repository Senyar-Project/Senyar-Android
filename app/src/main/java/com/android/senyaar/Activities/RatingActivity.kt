package com.android.senyaar.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.android.senyaar.R
import kotlinx.android.synthetic.main.activity_rating.*
import kotlinx.android.synthetic.main.appbar.*

class RatingActivity : AppCompatActivity() {

    lateinit var ratingValue: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)
        customToolbar()
        initialize()
    }

    private fun initialize() {
        iv_star_1.setOnClickListener {
            iv_star_1.setBackground(resources.getDrawable(R.drawable.yellow_star))
            iv_star_2.setBackground(resources.getDrawable(R.drawable.white_star))
            iv_star_3.setBackground(resources.getDrawable(R.drawable.white_star))
            iv_star_4.setBackground(resources.getDrawable(R.drawable.white_star))
            iv_star_5.setBackground(resources.getDrawable(R.drawable.white_star))
            ratingValue = "1"
        }
        iv_star_2.setOnClickListener {
            iv_star_1.setBackground(resources.getDrawable(R.drawable.yellow_star))
            iv_star_2.setBackground(resources.getDrawable(R.drawable.yellow_star))
            iv_star_3.setBackground(resources.getDrawable(R.drawable.white_star))
            iv_star_4.setBackground(resources.getDrawable(R.drawable.white_star))
            iv_star_5.setBackground(resources.getDrawable(R.drawable.white_star))
            ratingValue = "2"
        }
        iv_star_3.setOnClickListener {
            iv_star_1.setBackground(resources.getDrawable(R.drawable.yellow_star))
            iv_star_2.setBackground(resources.getDrawable(R.drawable.yellow_star))
            iv_star_3.setBackground(resources.getDrawable(R.drawable.yellow_star))
            iv_star_4.setBackground(resources.getDrawable(R.drawable.white_star))
            iv_star_5.setBackground(resources.getDrawable(R.drawable.white_star))
            ratingValue = "3"
        }
        iv_star_4.setOnClickListener {
            iv_star_1.background = resources.getDrawable(R.drawable.yellow_star)
            iv_star_2.background = resources.getDrawable(R.drawable.yellow_star)
            iv_star_3.setBackground(resources.getDrawable(R.drawable.yellow_star))
            iv_star_4.setBackground(resources.getDrawable(R.drawable.yellow_star))
            iv_star_5.setBackground(resources.getDrawable(R.drawable.white_star))
            ratingValue = "4"
        }
        iv_star_5.setOnClickListener {
            iv_star_1.setBackground(resources.getDrawable(R.drawable.yellow_star))
            iv_star_2.setBackground(resources.getDrawable(R.drawable.yellow_star))
            iv_star_3.setBackground(resources.getDrawable(R.drawable.yellow_star))
            iv_star_4.setBackground(resources.getDrawable(R.drawable.yellow_star))
            iv_star_5.setBackground(resources.getDrawable(R.drawable.yellow_star))
            ratingValue = "5"
        }
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
        text_toolbar.text = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}