package com.android.senyaar.Model

class NotificationsDataFeeder {
    fun populateList(): List<notificationModel> {
        var notificationList = ArrayList<notificationModel>()
        notificationList.add(
            notificationModel(
                "Urgent",
                "K915,Yousuf Shaheed Street,School Road,Adyala Road,Rawalpindi"
            )
        )
        notificationList.add(
            notificationModel(
                "Normal",
                "House Number 255,Sector B,Street 6,Askari 14"
            )
        )
        return notificationList
    }
}