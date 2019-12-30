package com.android.senyaar.Activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import android.widget.LinearLayout
import com.android.senyaar.Adapters.ScheduledTripsAdapter
import com.android.senyaar.Model.ScheduledTripDataFeeder
import com.android.senyaar.Model.generalResponseArray
import com.android.senyaar.Model.scheduledTripModel
import com.android.senyaar.Presenters.ScheduledTripPresenter
import com.android.senyaar.R
import com.android.senyaar.Utils.Configuration
import com.android.senyaar.Utils.MyApplication
import com.android.senyaar.Utils.PreferenceHelper
import com.android.senyaar.Utils.RecyclerItemClickListener
import com.android.senyaar.Views.ScheduledTripView
import kotlinx.android.synthetic.main.activity_scheduled_trip.*
import kotlinx.android.synthetic.main.appbar.*
import me.drakeet.materialdialog.MaterialDialog

class ScheduledTripsActivity : AppCompatActivity(), ScheduledTripView {

    lateinit var prefs: PreferenceHelper
    var pDialog: ProgressDialog? = null
    private var presenter: ScheduledTripPresenter? = null

    override fun showServerError(errorMessage: String, tag: String) {
        server_alertdialog(errorMessage, tag)
    }

    override fun getDriverList(response: generalResponseArray) {
        Log.d("testing", "Response" + response.message)
        presenter!!.displayAllData(populateList(response))
    }

    fun populateList(response: generalResponseArray): List<scheduledTripModel> {
        var count = 1
        var List = ArrayList<scheduledTripModel>()
        if (response.data != null && response.data!!.isNotEmpty()) {
            val dataArray = response.data!!
            for (item in dataArray.indices) {
                List.add(
                    scheduledTripModel(
                        count++,
                        dataArray[item].captain_start_location_name,
                        dataArray[item].captain_en_location_name,
                        dataArray[item].schedule_date,
                        dataArray[item].schedule_time,
                        dataArray[item].rider_id,
                        dataArray[item].ride_id,
                        dataArray[item].captain_id,
                        dataArray[item].schedule_id,
                        dataArray[item].schedule_status,
                        dataArray[item].captain_start_lat,
                        dataArray[item].captain_start_long,
                        dataArray[item].rider_pickup_lat,
                        dataArray[item].rider_pickup_long,
                        dataArray[item].rider_dropoff_lat,
                        dataArray[item].rider_dropoff_long,
                        dataArray[item].rider_pickup_location_name

                    )
                )
            }
        }


        return List
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduled_trip)
        presenter = ScheduledTripPresenter(this, ScheduledTripDataFeeder())
        if (prefs.isLogin.equals("driver")) {
            presenter!!.getScheduledRides(prefs, Configuration.ENDPOINT_SCHEDULED_RIDES)

        } else {
            presenter!!.getScheduledRides(prefs, Configuration.ENDPOINT_SCHEDULED_RIDES_PASSENGER)

        }
        customToolbar()
    }

    override fun getPreferences(): PreferenceHelper {
        return prefs
    }

    override fun showAlltripsInList(adapter: ScheduledTripsAdapter) {
        scheduled_trip_list.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        scheduled_trip_list.adapter = adapter
        scheduled_trip_list.addOnItemTouchListener(
            RecyclerItemClickListener(
                this,
                RecyclerItemClickListener.OnItemClickListener { view, position ->
                    val intent = Intent(this, PassengerInfo::class.java)
                    intent.putExtra("model", adapter.getItem(position))
                    startActivity(intent)
                })
        )
    }

    override fun appClickListener(model: scheduledTripModel) {
        //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initView() {
        prefs = PreferenceHelper(this)
        pDialog = MyApplication.getInstance().progressdialog(this)

    }

    override fun showProgress() {
        if (pDialog != null) {
            pDialog?.show()
        }
    }

    override fun hideProgress() {
        if (pDialog?.isShowing!!) {
            pDialog?.hide()
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
        text_toolbar.text = "Your Scheduled Journey"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun server_alertdialog(msg: String, tag: String) {
        val mMaterialDialog = MaterialDialog(this)
        mMaterialDialog.setMessage(msg)
        mMaterialDialog.setCanceledOnTouchOutside(false)
        mMaterialDialog.setNegativeButton(resources.getString(R.string.cancel)) {
            mMaterialDialog.dismiss()
        }
        mMaterialDialog.setPositiveButton(resources.getString(R.string.retry)) {
            mMaterialDialog.dismiss()
            // if (tag.equals("get")) {
            if (prefs.isLogin.equals("driver")) {
                presenter!!.getScheduledRides(prefs, Configuration.ENDPOINT_SCHEDULED_RIDES)

            } else {
                presenter!!.getScheduledRides(
                    prefs,
                    Configuration.ENDPOINT_SCHEDULED_RIDES_PASSENGER
                )

            }
            //}
        }
        try {
            mMaterialDialog.show()
        } catch (e: Exception) {
        }

    }

}