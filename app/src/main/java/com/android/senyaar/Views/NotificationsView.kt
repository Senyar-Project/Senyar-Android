package com.android.senyaar.Views

import com.android.senyaar.Adapters.NotificationAdapter
import com.android.senyaar.Model.notificationModel

interface NotificationsView :BaseView{
    fun showAllNoticationsInList(adapter: NotificationAdapter)
    fun appClickListener(model: notificationModel)
}