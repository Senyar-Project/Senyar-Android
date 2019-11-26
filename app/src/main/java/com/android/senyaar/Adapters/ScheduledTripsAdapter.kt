package com.android.senyaar.Adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.senyaar.Model.scheduledTripModel
import com.android.senyaar.R
import kotlinx.android.synthetic.main.activity_scheduled_trip_listrow.view.*

class ScheduledTripsAdapter(
    val notificationList: List<scheduledTripModel>,
    val clickListener: (scheduledTripModel) -> Unit
) :
    RecyclerView.Adapter<ScheduledTripsAdapter.ViewItemHolder>() {
    override fun onBindViewHolder(itemHolder: ViewItemHolder, position: Int) {
        val model = notificationList[position]
        itemHolder.journey?.text = "Journey " + model.journey
        itemHolder.departure?.text = model.departure
        itemHolder.arrival?.text = model.arrival
        itemHolder.date?.text = model.date
        itemHolder.time?.text = model.time
        itemHolder.status?.text = model.status

        itemHolder.clickableView.setOnClickListener {
            clickListener(model)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewItemHolder {
        var view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.activity_scheduled_trip_listrow, parent, false)
        return ViewItemHolder(view)
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    class ViewItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        val journey = view.tv_heading
        val departure = view.tv_departure_data
        val arrival = view.tv_arrival_data
        val time = view.tv_time_data
        val date = view.tv_date_data
        val status = view.tv_status_data
        val clickableView = view

        init {
            //  view.setOnClickListener {clickListener(part)}
        }
    }

    fun getItem(position: Int): scheduledTripModel {
        return notificationList[position]
    }

}