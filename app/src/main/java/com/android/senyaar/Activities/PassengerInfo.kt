package com.android.senyaar.Activities

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import com.android.senyaar.Model.PassengerInfoModel
import com.android.senyaar.Model.generalResponse
import com.android.senyaar.Model.scheduledTripModel
import com.android.senyaar.Presenters.PassengerInfoPresenter
import com.android.senyaar.R
import com.android.senyaar.Utils.MyApplication
import com.android.senyaar.Utils.PreferenceHelper
import com.android.senyaar.Views.PassengerInfoView
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_passenger_info.*
import kotlinx.android.synthetic.main.appbar.*
import kotlinx.android.synthetic.main.general_dialog.*
import me.drakeet.materialdialog.MaterialDialog
import org.json.JSONObject
import android.support.v4.app.SupportActivity
import android.support.v4.app.SupportActivity.ExtraData
import android.support.v4.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.google.android.gms.maps.model.LatLng


class PassengerInfo : AppCompatActivity(), PassengerInfoView {
    lateinit var prefs: PreferenceHelper
    var pDialog: ProgressDialog? = null
    private var presenter: PassengerInfoPresenter? = null
    lateinit var model: scheduledTripModel
    override fun getPreferences(): PreferenceHelper {
        return prefs
    }

    override fun startRide(response: String) {
        Log.d("testing", "Response" + response)
        val gsonBuilder = GsonBuilder()
        val gson = gsonBuilder.create()
        val responseModel = gson.fromJson(response, generalResponse::class.java)

        //  showDialog(responseModel.message!!)
    }

    fun showDialog(response: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.general_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        dialog.dialog_title_data_navigation.text =
            response
        dialog.okay.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setCancelable(false)
    }

    override fun initView() {
        prefs = PreferenceHelper(this)
        pDialog = MyApplication.getInstance().progressdialog(this)
        if (prefs.isLogin == "passenger") {
            bt_next.visibility = View.GONE
        } else {
            bt_next.visibility = View.VISIBLE

        }
        model = intent.getSerializableExtra("model") as scheduledTripModel
        if (model.status.equals("Booked")) {
            bt_next.visibility = View.VISIBLE
        } else {
            bt_next.visibility = View.GONE
        }
        bt_next.setOnClickListener {
            if (model.ride_id != null) {
                val intent = Intent(this, DriverTrackActivity::class.java)
                val location = LatLng(
                    model.passenger_start_lat!!.toDouble(),
                    model.passenger_start_lon!!.toDouble()
                )
                intent.putExtra("address", location)
                intent.putExtra("location", model.passenger_pickup_location)
                startActivity(intent)
                // presenter?.startRide(createJson(), prefs)
            }
        }
    }

    override fun showProgress() {
        if (pDialog != null) {
            pDialog?.show()
        }
    }

    private fun createJson(): JSONObject {
        var start_ride_json: JSONObject = JSONObject()
        start_ride_json.put("ride_id", model.ride_id!!.toInt())
        start_ride_json.put("start_date", model.date)
        start_ride_json.put("start_time", model.time)
        return start_ride_json
    }

    override fun hideProgress() {
        if (pDialog?.isShowing!!) {
            pDialog?.hide()
        }
    }

    override fun showServerError(errorMessage: String, tag: String) {
        server_alertdialog(errorMessage, tag)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passenger_info)
        customToolbar()
        presenter = PassengerInfoPresenter(this, PassengerInfoModel())
        updateView(intent.getSerializableExtra("model") as scheduledTripModel)
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
            presenter?.startRide(createJson(), prefs)
            //}
        }
        try {
            mMaterialDialog.show()
        } catch (e: Exception) {
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
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun updateView(model: scheduledTripModel) {
        departure_location_data.text = model.departure
        arrival_location_data.text = model.arrival
        date_data.text = model.date
        time_data.text = model.time
        journey_type_data.text = model.time

    }

}