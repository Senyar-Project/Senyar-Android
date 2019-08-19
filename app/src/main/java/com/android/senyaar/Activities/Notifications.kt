package com.android.senyaar.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.widget.LinearLayout
import com.android.senyaar.Adapters.NotificationAdapter
import com.android.senyaar.Model.NotificationsDataFeeder
import com.android.senyaar.Model.notificationModel
import com.android.senyaar.Presenters.NotificationsPresenter
import com.android.senyaar.R
import com.android.senyaar.Utils.PreferenceHelper
import com.android.senyaar.Views.NotificationsView
import kotlinx.android.synthetic.main.activity_notifications.*
import kotlinx.android.synthetic.main.appbar.*

class Notifications:AppCompatActivity(),NotificationsView {
    private var presenter: NotificationsPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
        presenter = NotificationsPresenter(this, NotificationsDataFeeder())
        presenter!!.displayAllNotifications()
        customToolbar()
    }
    override fun getPreferences(): PreferenceHelper {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showAllNoticationsInList(adapter: NotificationAdapter) {
        notification_list.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        notification_list.adapter = adapter    }

    override fun appClickListener(model: notificationModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initView() {
       // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showProgress() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideProgress() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
        text_toolbar.text="Notifications"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

}