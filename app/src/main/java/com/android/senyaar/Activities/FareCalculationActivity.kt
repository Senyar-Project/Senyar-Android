package com.android.senyaar.Activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem

import com.android.senyaar.R
import kotlinx.android.synthetic.main.activity_fare_calculation.*
import kotlinx.android.synthetic.main.appbar.*
import android.location.Location.distanceBetween
import android.location.Location
import android.util.Log
import com.android.senyaar.Model.PassengerInfoModel
import com.android.senyaar.Model.generalResponse
import com.android.senyaar.NetworkCalls.JsonRequest
import com.android.senyaar.NetworkCalls.VolleyClassCentralized
import com.android.senyaar.Presenters.PassengerInfoPresenter
import com.android.senyaar.Utils.Configuration
import com.android.senyaar.Utils.MyApplication
import com.android.senyaar.Utils.PreferenceHelper
import com.android.senyaar.Views.DashboardDriverView
import com.android.senyaar.Views.PassengerInfoView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import org.json.JSONObject
import java.util.HashMap


class FareCalculationActivity : AppCompatActivity(), PassengerInfoView {

    lateinit var prefs: PreferenceHelper
    private var presenter: PassengerInfoPresenter? = null
    private var requestGetDistance: JsonRequest? = null

    var pDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fare_calculation)
        requestGetDistance = JsonRequest(MyApplication.getInstance(), requestGetDistanceListener)
        presenter = PassengerInfoPresenter(this, PassengerInfoModel())
        getDistance()
        customToolbar()
    }

    override fun startRide(response: String) {
        Log.d("testing", "Response" + response)
        val gsonBuilder = GsonBuilder()
        val gson = gsonBuilder.create()
        val responseModel = gson.fromJson(response, generalResponse::class.java)
        approx_fare_data.text = responseModel.data!!.estimated_fare
    }

    override fun getPreferences(): PreferenceHelper {
        return prefs
    }

    private fun getDistance(
    ) {
        val latitude_origin = intent.getStringExtra("user_lat").toDouble()
        val longitude_origin = intent.getStringExtra("user_long").toDouble()
        val latitude_dest = intent.getStringExtra("user_end_lat").toDouble()
        val longitude_dest = intent.getStringExtra("user_end_long").toDouble()
        val googlePlacesUrl = StringBuilder(Configuration.ENDPOINT_MAPS + "json?")
        googlePlacesUrl.append("origin=$latitude_origin,$longitude_origin")
        googlePlacesUrl.append("&destination=$latitude_dest,$longitude_dest")
        googlePlacesUrl.append("&mode=driving")
        googlePlacesUrl.append("&sensor=true")
        googlePlacesUrl.append("&key=" + resources.getString(R.string.google_maps_key))
        Log.d("testing", googlePlacesUrl.toString())
        requestGetDistance?.requestString(
            "google-api",
            googlePlacesUrl.toString(),
            null,
            null,
            Request.Method.GET,
            false
        )
    }

    override fun initView() {
        prefs = PreferenceHelper(this)
        pDialog = MyApplication.getInstance().progressdialog(this)
        bt_next.setOnClickListener {
            val intent = Intent(this, BookRideMapsActivity::class.java)
            intent.putExtra("driver_json", getIntent().getStringExtra("driver_json"))
            intent.putExtra("leaving", getIntent().getStringExtra("leaving"))
            intent.putExtra("going", getIntent().getStringExtra("going"))
            intent.putExtra("date", getIntent().getStringExtra("date"))
            intent.putExtra("time", getIntent().getStringExtra("time"))
            intent.putExtra("user_end_lat", getIntent().getStringExtra("user_end_lat"))
            startActivity(intent)
        }
        leaving_from_data.text = intent.getStringExtra("leaving")
        going_to_data.text = intent.getStringExtra("going")
        date_data.text = intent.getStringExtra("date")
        time_data.text = intent.getStringExtra("time")
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

    override fun showServerError(errorMessage: String, tag: String) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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


    private val requestGetDistanceListener =
        JsonRequest.OnRequestCompletedListener { requestName, status, response, errorMessage ->
            if (status) {
                Log.d("testing", "Distance call" + response)
                val distance: String
                val duration: String
                var json = JSONObject(response)
                distance =
                    json.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0)
                        .getJSONObject("distance").getString("value")
                duration =
                    json.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0)
                        .getJSONObject("duration").getString("value")
                Log.d("testing", "Distance:" + (distance.toFloat())/1000 + " Duration:" + duration)
                var fare_estimate_json: JSONObject = JSONObject()
                fare_estimate_json.put("distance_in_km", (distance.toFloat())/1000)
                fare_estimate_json.put("duration_in_min", (duration.toInt())/60)
                presenter?.fareEstimate(
                    fare_estimate_json, prefs
                )

            } else {
                Log.d("testing", "Distance call error" + response + errorMessage)

            }
        }


}