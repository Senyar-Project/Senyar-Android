package com.android.senyaar.Activities

import android.Manifest
import android.animation.ValueAnimator
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.animation.LinearInterpolator
import android.widget.Toast
import com.android.senyaar.Model.PassengerInfoModel
import com.android.senyaar.Model.generalResponse
import com.android.senyaar.Model.scheduledTripModel
import com.android.senyaar.Presenters.PassengerInfoPresenter
import com.android.senyaar.R
import com.android.senyaar.Utils.LatLngInterpolator
import com.android.senyaar.Utils.MarkerAnimation
import com.android.senyaar.Utils.MyApplication
import com.android.senyaar.Utils.PreferenceHelper
import com.android.senyaar.Views.PassengerInfoView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.GsonBuilder
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.android.PolyUtil
import com.google.maps.model.DirectionsResult
import com.google.maps.model.DirectionsRoute
import com.google.maps.model.TravelMode
import kotlinx.android.synthetic.main.general_dialog.*
import me.drakeet.materialdialog.MaterialDialog
import org.joda.time.DateTime
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DriverTrackActivity : AppCompatActivity(), OnMapReadyCallback, PassengerInfoView,
    GoogleMap.OnInfoWindowClickListener,
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    LocationListener {


    private var mLastLocation: Location? = null
    // Google client to interact with Google API
    private var mGoogleApiClient: GoogleApiClient? = null
    // boolean flag to toggle periodic location updates
    private val mRequestingLocationUpdates = false
    private var flag: Int = 0
    private var mLocationRequest: LocationRequest? = null
    private var mMap: GoogleMap? = null
    private var markerCount: Int = 0
    internal var mk: Marker? = null
    internal var mk_end: Marker? = null

    lateinit var prefs: PreferenceHelper
    var pDialog: ProgressDialog? = null
    private var presenter: PassengerInfoPresenter? = null
    var p1: LatLng? = null
    var p2: LatLng? = null
    var status: String? = null
    var distance: Float? = null
    var amount: String? = null
    var end_time: String? = null
    lateinit var model: scheduledTripModel
    val servicesAvailable: Boolean
        get() {
            val api = GoogleApiAvailability.getInstance()
            val isAvailable = api.isGooglePlayServicesAvailable(this)
            if (isAvailable == ConnectionResult.SUCCESS) {
                return true
            } else if (api.isUserResolvableError(isAvailable)) {

                val dialog = api.getErrorDialog(this, isAvailable, 0)
                dialog.show()
            } else {
                Toast.makeText(this, "Cannot Connect To Play Services", Toast.LENGTH_SHORT).show()
            }
            return false
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.driver_track_activity)
        presenter = PassengerInfoPresenter(this, PassengerInfoModel())

        markerCount = 0

        //showReceipt()
        //Check If Google Services Is Available
        if (servicesAvailable) {
            // Building the GoogleApi client
            buildGoogleApiClient()
            createLocationRequest()
            Toast.makeText(this, "Google Service Is Available!!", Toast.LENGTH_SHORT).show()
        }


    }

    /**
     * GOOGLE MAPS AND MAPS OBJECTS
     *
     */

    // After Creating the Map Set Initial Location
    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        //Uncomment To Show Google Location Blue Pointer
        // mMap.setMyLocationEnabled(true);
    }

    // Add A Map Pointer To The MAp
    fun addMarker(googleMap: GoogleMap?, lat: Double, lon: Double) {

        if (markerCount == 1) {
            animateMarker(mLastLocation, mk)
        } else if (markerCount == 0) {
            //Set Custom BitMap for Pointer
            val height = 80
            val width = 45
            val bitmapdraw = resources.getDrawable(R.drawable.icon_car) as BitmapDrawable
            val b = bitmapdraw.bitmap
            val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
            mMap = googleMap

            val latlong = LatLng(lat, lon)
            mk = mMap!!.addMarker(
                MarkerOptions().position(LatLng(lat, lon))
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin3))
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
            )
            mk_end = mMap!!.addMarker(
                MarkerOptions().position(p1!!)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin3))
                //.icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
            )
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 14f))

            //Set Marker Count to 1 after first marker is created
            markerCount = 1

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                return
            }
            //mMap.setMyLocationEnabled(true);
            startLocationUpdates()
        }
    }


    override fun onInfoWindowClick(marker: Marker) {
        //Toast.makeText(this, marker.title, Toast.LENGTH_LONG).show()
    }


    /**
     * LOCATION LISTENER EVENTS
     *
     */

    override fun onStart() {
        super.onStart()
        if (mGoogleApiClient != null) {
            mGoogleApiClient!!.connect()
        }
        //        startLocationUpdates();
    }

    override fun onResume() {
        super.onResume()

        servicesAvailable

        // Resuming the periodic location updates
        if (mGoogleApiClient!!.isConnected && mRequestingLocationUpdates) {
            startLocationUpdates()
        }
    }

    override fun onStop() {
        super.onStop()
        if (mGoogleApiClient!!.isConnected) {
            mGoogleApiClient!!.disconnect()
            mLastLocation = null
            p1 = null
        }
    }

    override fun onPause() {
        super.onPause()
    }

    //Method to display the location on UI
    private fun displayLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            // Check Permissions Now
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION
            )
        } else {


            mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient)

            if (mLastLocation != null) {
                val latitude = mLastLocation!!.latitude
                val longitude = mLastLocation!!.longitude
                val loc = "$latitude ,$longitude "
               // Toast.makeText(this, loc, Toast.LENGTH_SHORT).show()

                //Add pointer to the map at location
                addMarker(mMap, latitude, longitude)


            } else {

                Toast.makeText(
                        this, "Couldn't get the location. Make sure location is enabled on the device",
                Toast.LENGTH_SHORT
                ).show()
            }
            var crntLocation = Location("crntlocation")
            crntLocation.latitude = mLastLocation!!.latitude
            crntLocation.longitude = mLastLocation!!.longitude

            var newLocation = Location("newlocation")
            if (p1 != null) {
                newLocation.latitude = p1!!.latitude
                newLocation.longitude = p1!!.longitude
            }

            //float distance = crntLocation.distanceTo(newLocation);  in meters
            distance = crntLocation.distanceTo(newLocation) / 1000; // in km
            if (distance!! < 1 && flag == 0) {
                flag = 1
                if (status.equals("Booked")) {
                    showDialog("Start Ride", "start")
                } else {
                    showDialog("Complete Ride", "complete")
                }
            }
        }
    }


    // Creating google api client object
    @Synchronized
    protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API).build()
    }

    //Creating location request object
    protected fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 3000
        mLocationRequest!!.fastestInterval = 3000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest!!.smallestDisplacement = 10F
    }


    //Starting the location updates
    protected fun startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // Check Permissions Now
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION
            )
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this
            )
        }
    }

    //Stopping location updates
    protected fun stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
            mGoogleApiClient, this
        )
    }

    /**
     * Google api callback methods
     */
    override fun onConnectionFailed(result: ConnectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.errorCode)
    }

    override fun onConnected(arg0: Bundle?) {

        // Once connected with google api, get the location
        displayLocation()

        if (mRequestingLocationUpdates) {
            startLocationUpdates()
        }
    }

    override fun onConnectionSuspended(arg0: Int) {
        mGoogleApiClient!!.connect()
    }


    override fun onLocationChanged(location: Location) {
        // Assign the new location
        mLastLocation = location

//        Toast.makeText(
//            applicationContext, "Location changed!",
//            Toast.LENGTH_SHORT
//        ).show()

        // Displaying the new location on UI
        displayLocation()
    }

    private interface LatLngInterpolator {
        fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng

        class LinearFixed : LatLngInterpolator {
            override fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng {
                val lat = (b.latitude - a.latitude) * fraction + a.latitude
                var lngDelta = b.longitude - a.longitude
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360
                }
                val lng = lngDelta * fraction + a.longitude
                return LatLng(lat, lng)
            }
        }
    }

    companion object {

        private val PLAY_SERVICES_RESOLUTION_REQUEST = 1000
        private val REQUEST_LOCATION = 0
        private val TAG = ""


        fun animateMarker(destination: Location?, marker: Marker?) {
            if (marker != null) {
                val startPosition = marker.position
                val endPosition = LatLng(destination!!.latitude, destination.longitude)

                val startRotation = marker.rotation

                val latLngInterpolator = LatLngInterpolator.LinearFixed()
                val valueAnimator = ValueAnimator.ofFloat(0F, 1F)
                valueAnimator.duration = 1000 // duration 1 second
                valueAnimator.interpolator = LinearInterpolator()
                valueAnimator.addUpdateListener { animation ->
                    try {
                        val v = animation.animatedFraction
                        val newPosition =
                            latLngInterpolator.interpolate(v, startPosition, endPosition)
                        marker.setPosition(newPosition)
                        marker.rotation = computeRotation(v, startRotation, destination.bearing)
                    } catch (ex: Exception) {
                        // I don't care atm..
                    }
                }

                valueAnimator.start()
            }
        }

        private fun computeRotation(fraction: Float, start: Float, end: Float): Float {
            val normalizeEnd = end - start // rotate start to 0
            val normalizedEndAbs = (normalizeEnd + 360) % 360

            val direction =
                (if (normalizedEndAbs > 180) -1 else 1).toFloat() // -1 = anticlockwise, 1 = clockwise
            val rotation: Float
            if (direction > 0) {
                rotation = normalizedEndAbs
            } else {
                rotation = normalizedEndAbs - 360
            }

            val result = fraction * rotation + start
            return (result + 360) % 360
        }
    }

    private fun createJson(): JSONObject {
        var start_ride_json: JSONObject = JSONObject()
        start_ride_json.put("ride_id", model.ride_id!!.toInt())
        start_ride_json.put("start_date", model.date)
        start_ride_json.put("start_time", model.time)
        return start_ride_json
    }

    private fun completeRideJson(): JSONObject {
        var end_ride_json: JSONObject = JSONObject()
        end_ride_json.put("ride_id", model.ride_id!!.toInt())
        end_ride_json.put(
            "drop_date",
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        )
        end_ride_json.put(
            "drop_time",
            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        )
        end_time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(
            Date()
        )

        end_ride_json.put(
            "distance", distance
        )

        return end_ride_json
    }

    override fun getPreferences(): PreferenceHelper {
        return prefs
    }

    override fun startRide(response: String) {
        Log.d("testing", "Response" + response)
        val gsonBuilder = GsonBuilder()
        val gson = gsonBuilder.create()
        val responseModel = gson.fromJson(response, generalResponse::class.java)
        //if (responseModel.code.equals("200")) {
        showDialog(responseModel.message!!, "okay")
        //}
    }

    override fun completeRide(response: String) {
        Log.d("testing", "Response" + response)
        val gsonBuilder = GsonBuilder()
        val gson = gsonBuilder.create()
        val responseModel = gson.fromJson(response, generalResponse::class.java)
        if (responseModel.code.equals("200")) {
            amount = responseModel.data!!.total_amount
            showDialog(responseModel.message!!, "else")
        }
    }

    fun showReceipt() {
        val string1 = "08:00 PM"

        val time1 = SimpleDateFormat("HH:mm aa").parse(string1)
        val calendar1 = Calendar.getInstance()
        calendar1.setTime(time1)
        val time2 = SimpleDateFormat("hh:mm aa").parse(end_time)
        val calendar2 = Calendar.getInstance()
        calendar2.setTime(time2)
        calendar2.add(Calendar.DATE, 1)
        val x = calendar1.getTime()
        val xy = calendar2.getTime()
        val diff = x.getTime() - xy.getTime()
        var diffMinutes = diff / (60 * 1000)
        val diffHours = diffMinutes / 60
        println("diff hours" + diffHours)
    }

    fun showDialog(response: String, tag: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.general_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        dialog.dialog_title_data_navigation.text =
            response
        dialog.okay.setOnClickListener {
            dialog.dismiss()
            if (tag.equals("start")) {
                presenter?.startRide(createJson(), prefs)
            } else if (tag.equals("okay")) {
                if (mk_end != null) {
                    mk_end?.remove()
                }
                p1 = p2
                mk_end = mMap!!.addMarker(
                    MarkerOptions().position(p1!!)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin3))
                    //.icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                )
            } else if (tag.equals("complete")) {
                presenter?.completeRide(completeRideJson(), prefs)
            } else {
                val intent = Intent(this@DriverTrackActivity, RiderReceiptActivity::class.java)
                intent.putExtra("amount", amount)
                intent.putExtra("distance", distance)
                intent.putExtra("time", end_time)
                intent.putExtra("date", model.date)
                startActivity(intent)
            }
        }
        dialog.setCancelable(false)
    }

    override fun initView() {
        prefs = PreferenceHelper(this)
        pDialog = MyApplication.getInstance().progressdialog(this)
        p1 = intent.getParcelableExtra("address")
        p2 = intent.getParcelableExtra("address_dropoff")
        status = intent.getStringExtra("status")
        model = intent.getSerializableExtra("model") as scheduledTripModel

        if (status.equals("Booked")) {

        } else {
            p1 = p2
        }
        //Create The MapView Fragment
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
        server_alertdialog(errorMessage, tag)
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
}

