package com.android.senyaar.Adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.senyaar.Model.notificationModel
import com.android.senyaar.R
import kotlinx.android.synthetic.main.activity_notification_listrow.view.*

class NotificationAdapter(
    val notificationList: List<notificationModel>,
    val clickListener: (notificationModel) -> Unit
) :
    RecyclerView.Adapter<NotificationAdapter.ViewItemHolder>() {
    override fun onBindViewHolder(itemHolder: ViewItemHolder, position: Int) {
        val model = notificationList[position]
        itemHolder.heading?.text = model.heading
        itemHolder.sub_heading?.text = model.sub_heading

        itemHolder.clickableView.setOnClickListener {
            clickListener(model)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewItemHolder {
        var view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.activity_notification_listrow, parent, false)
        return ViewItemHolder(view)
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    class ViewItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        val heading = view.tv_heading_notification
        val sub_heading = view.tv_sub_heading_notification
        val clickableView = view

        init {
            //  view.setOnClickListener {clickListener(part)}
        }
    }

    fun getItem(position: Int): notificationModel {
        return notificationList[position]
    }
}