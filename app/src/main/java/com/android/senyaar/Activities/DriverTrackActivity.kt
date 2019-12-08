package com.android.senyaar.Activities

import android.Manifest
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Toast
import com.android.senyaar.Model.generalResponse
import com.android.senyaar.Presenters.PassengerInfoPresenter
import com.android.senyaar.R
import com.android.senyaar.Utils.PreferenceHelper
import com.android.senyaar.Views.PassengerInfoView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
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
import java.io.IOException
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit

class DriverTrackActivity : AppCompatActivity(), OnMapReadyCallback, PassengerInfoView,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener {


    private val overview = 0
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationRequest: LocationRequest? = null
    private var mLastLocation: Location? = null
    private var mCurrLocationMarker: Marker? = null
    val MY_PERMISSIONS_REQUEST_LOCATION = 99
    lateinit var prefs: PreferenceHelper
    var pDialog: ProgressDialog? = null
    private var presenter: PassengerInfoPresenter? = null
    var p1: LatLng? = null

    private lateinit var mMap: GoogleMap
    var address = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.driver_track_activity)
        //initView()
    }

    override fun getPreferences(): PreferenceHelper {
        return prefs
    }

    override fun startRide(response: String) {
        Log.d("testing", "Response" + response)
        val gsonBuilder = GsonBuilder()
        val gson = gsonBuilder.create()
        val responseModel = gson.fromJson(response, generalResponse::class.java)
        showDialog(responseModel.message!!)
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
           // presenter?.startRide(createJson(), prefs)
            //}
        }
        try {
            mMaterialDialog.show()
        } catch (e: Exception) {
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL;

        setupGoogleMapScreenSettings(googleMap)
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                buildGoogleApiClient()
            }
        } else {
            buildGoogleApiClient()
        }

    }

    @Synchronized
    protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        mGoogleApiClient?.connect()
    }

    override fun onConnected(p0: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest?.interval = 1000
        mLocationRequest?.fastestInterval = 1000
        mLocationRequest?.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
            )
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun getDirectionsDetails(
        origin: String,
        destination: String,
        mode: TravelMode
    ): DirectionsResult? {
        val now = DateTime()
        try {
            return DirectionsApi.newRequest(getGeoContext())
                .mode(mode)
                .origin(origin)
                .destination(destination)
                .departureTime(now)
                .await()
        } catch (e: ApiException) {
            e.printStackTrace()
            return null
        } catch (e: InterruptedException) {
            e.printStackTrace()
            return null
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

    }

    override fun initView() {
        //supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission()
        }
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        p1 = intent.getParcelableExtra("address")
        // getLocationFromAddress(intent.getStringExtra("address"))
    }

    private fun setupGoogleMapScreenSettings(mMap: GoogleMap) {
        mMap.isBuildingsEnabled = true
        mMap.isIndoorEnabled = true
        mMap.isTrafficEnabled = true
        val mUiSettings = mMap.uiSettings
        mUiSettings.isZoomControlsEnabled = true
        mUiSettings.isCompassEnabled = true
        mUiSettings.isMyLocationButtonEnabled = true
        mUiSettings.isScrollGesturesEnabled = true
        mUiSettings.isZoomGesturesEnabled = true
        mUiSettings.isTiltGesturesEnabled = true
        mUiSettings.isRotateGesturesEnabled = true
    }

    private fun addMarkersToMap(results: DirectionsResult, mMap: GoogleMap) {
        mMap.addMarker(
            MarkerOptions().position(
                LatLng(
                    results.routes[overview].legs[overview].startLocation.lat,
                    results.routes[overview].legs[overview].startLocation.lng
                )
            ).title(results.routes[overview].legs[overview].startAddress)
        )
        mMap.addMarker(
            MarkerOptions().position(
                LatLng(
                    results.routes[overview].legs[overview].endLocation.lat,
                    results.routes[overview].legs[overview].endLocation.lng
                )
            ).title(results.routes[overview].legs[overview].startAddress).snippet(
                getEndLocationTitle(results)
            )
        )
    }

    private fun positionCamera(route: DirectionsRoute, mMap: GoogleMap) {
        mMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    route.legs[overview].startLocation.lat,
                    route.legs[overview].startLocation.lng
                ), 12f
            )
        )
    }

    private fun addPolyline(results: DirectionsResult, mMap: GoogleMap) {
        val decodedPath = PolyUtil.decode(results.routes[overview].overviewPolyline.encodedPath)
        mMap.addPolyline(PolylineOptions().addAll(decodedPath))
    }

    private fun getEndLocationTitle(results: DirectionsResult): String {
        return "Time :" + results.routes[overview].legs[overview].duration.humanReadable + " Distance :" + results.routes[overview].legs[overview].distance.humanReadable
    }

    private fun getGeoContext(): GeoApiContext {
        val geoApiContext = GeoApiContext()
        return geoApiContext
            .setQueryRateLimit(3)
            .setApiKey(getString(R.string.google_maps_key))
            .setConnectTimeout(1, TimeUnit.SECONDS)
            .setReadTimeout(1, TimeUnit.SECONDS)
            .setWriteTimeout(1, TimeUnit.SECONDS)
    }

    override fun onLocationChanged(p0: Location?) {
        mLastLocation = p0!!
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker?.remove()
        }
        //Showing Current Location Marker on Map
        val latLng = LatLng(p0.getLatitude(), p0.getLongitude())
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11f))

        //this code stops location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi!!.removeLocationUpdates(mGoogleApiClient, this)
        }
        val geocoder = Geocoder(this, Locale.getDefault());
        try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            address = addresses.get(0).getAddressLine(0)
        } catch (ex: Exception) {
            ex.printStackTrace();
        }
        val results = getDirectionsDetails(
            address,
            intent.getStringExtra("location"),
            TravelMode.DRIVING
        )
        if (results != null) {
            addPolyline(results, mMap)
            positionCamera(results.routes[overview], mMap)
            addMarkersToMap(results, mMap)
        }
        var crntLocation = Location("crntlocation");
        crntLocation.latitude = p0.latitude
        crntLocation.longitude = p0.longitude

        var newLocation = Location("newlocation");
        newLocation.latitude = p1!!.latitude;
        newLocation.longitude = p1!!.longitude;
//float distance = crntLocation.distanceTo(newLocation);  in meters
        var distance = crntLocation.distanceTo(newLocation) / 1000; // in km
    }

    fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
            return false
        } else {
            return true
        }
    }

    fun getLocationFromAddress(value_address: String): LatLng? {
        val coder = Geocoder(this)
        val address: List<Address>?
        try {
            // May throw an IOException
            address = coder.getFromLocationName(value_address, 5)
            val location = address[0]
            p1 = LatLng(location.latitude, location.longitude)
        } catch (ex: IOException) {

            ex.printStackTrace()
        }
        return p1
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient()
                        }
                        mMap.isMyLocationEnabled = true
                    }
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
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

}