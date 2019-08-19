package com.android.senyaar.Presenters

import com.android.senyaar.Adapters.NotificationAdapter
import com.android.senyaar.Model.NotificationsDataFeeder
import com.android.senyaar.Views.NotificationsView

class NotificationsPresenter(view: NotificationsView, model: NotificationsDataFeeder) {
    private var view: NotificationsView = view
    var adapter: NotificationAdapter? = null
    private var model: NotificationsDataFeeder = model
    init {
        view.initView()
    }

    fun displayAllNotifications() {
        adapter = NotificationAdapter(model.populateList(), clickListener = {
            view.appClickListener(it)
        })
        view.showAllNoticationsInList(adapter!!)
    }

}