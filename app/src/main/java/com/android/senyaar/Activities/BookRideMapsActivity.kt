package com.android.senyaar.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import com.android.senyaar.Model.BookRideMapsModel
import com.android.senyaar.Model.InfoWindowData
import com.android.senyaar.Model.generalResponse
import com.android.senyaar.Model.generalResponseArray
import com.android.senyaar.Presenters.BookRideMapsPresenter
import com.android.senyaar.R
import com.android.senyaar.Utils.*
import com.android.senyaar.Views.BookRideMapsView
import com.android.senyaar.Views.DashboardPassengerView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_book_ride_maps.*
import kotlinx.android.synthetic.main.general_dialog.*
import me.drakeet.materialdialog.MaterialDialog
import org.json.JSONObject

class BookRideMapsActivity : AppCompatActivity(), OnMapReadyCallback,
    LocationListener, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, BookRideMapsView {
    internal var mLastLocation: Location? = null
    internal var mCurrLocationMarker: Marker? = null
    internal var mGoogleApiClient: GoogleApiClient? = null
    internal var mLocationRequest: LocationRequest? = null
    var infoWindow: ViewGroup? = null
    var infoButtonListener: OnInfoWindowElemTouchListener? = null
    var mapWrapperLayout: MapWrapperLayout? = null
    val MY_PERMISSIONS_REQUEST_LOCATION = 99
    lateinit var prefs: PreferenceHelper
    var pDialog: ProgressDialog? = null
    lateinit var jsonObj: JSONObject
    var flag = 0
    private var presenter: BookRideMapsPresenter? = null

    override fun getPreferences(): PreferenceHelper {
        return prefs
    }

    override fun showSnackbar(message: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDriver(response: String) {
        Log.d("testing", "Response" + response)
        val gsonBuilder = GsonBuilder()
        val gson = gsonBuilder.create()
        val responseModel = gson.fromJson(response, generalResponseArray::class.java)
        if (responseModel.data?.size == 0) {
            flag = 1
            showDialog("There are no drivers available at the moment. Please try again")
        } else {
            setMarkers(responseModel)
        }
    }

    override fun bookRide(response: String) {
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
            if (flag == 1) {
                dialog.dismiss()
                finish()
            } else {
                dialog.dismiss()
                startActivity(Intent(this, ScheduledTripsActivity::class.java))
            }
        }
        dialog.setCancelable(false)
    }

    override fun initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission()
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mapWrapperLayout = findViewById(R.id.map_relative_layout)
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

    override fun showServerError(errorMessage: String, tag: String) {
        server_alertdialog(errorMessage, tag)
    }

    private fun setMarkers(response: generalResponseArray) {
        mapWrapperLayout!!.init(mMap, getPixelsFromDp(this, (39 + 20).toFloat()));
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker?.remove()
        }
        var info: InfoWindowData
        var latLng: LatLng
        if (response.data != null && response.data!!.isNotEmpty()) {
            val dataArray = response.data!!
            for (item in dataArray.indices) {
                info = InfoWindowData(
                    dataArray[item].first_name + dataArray[item].last_name,
                    "Info",
                    dataArray[item].rating,
                    dataArray[item].id,
                    dataArray[item].shcedule_id,
                    dataArray[item].start_lat,
                    dataArray[item].start_long,
                    dataArray[item].end_long,
                    dataArray[item].profile_picture
                );
                latLng = LatLng(dataArray[item].start_lat!!.toDouble(), dataArray[item].start_long!!.toDouble())
                mMap?.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                if (dataArray[item].profile_picture != null) {
                    mCurrLocationMarker = mMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(dataArray[item].profile_picture!!)))
                    )
                } else {
                    mCurrLocationMarker = mMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView("")))
                    )
                }
                mCurrLocationMarker!!.setTag(info)
            }
        }
        //Place current location marker
        // adding a marker on map with image from  drawable
        this.infoWindow = getLayoutInflater().inflate(R.layout.custom_marker_dialog, null) as ViewGroup
        val name_tv = infoWindow!!.findViewById(R.id.rider_name) as TextView
        val details_tv = infoWindow!!.findViewById(R.id.rider_info) as TextView

        val rating_tv = infoWindow!!.findViewById(R.id.tv_rating) as TextView
        val rating_bar = infoWindow!!.findViewById(R.id.ratingBar) as RatingBar

        val book_tv = infoWindow!!.findViewById(R.id.tv_book) as TextView
        val contact_tv = infoWindow!!.findViewById(R.id.tv_contact) as TextView

        this.infoButtonListener = object : OnInfoWindowElemTouchListener(book_tv) {
            override fun onClickConfirmed(v: View, marker: Marker) {
                // Here we can perform some action triggered after clicking the button
                if (CommonMethods.passengerRedirect(prefs, localClassName, this@BookRideMapsActivity)) {
                    val book_ride_json = JSONObject()
                    book_ride_json.put("schedule_id", (marker.tag as InfoWindowData).schedule_id.toInt())
                    book_ride_json.put("schedule_date", intent.getStringExtra("date"))
                    book_ride_json.put("schedule_time", intent.getStringExtra("time"))
                    book_ride_json.put("pick_location_long", (marker.tag as InfoWindowData).start_long)
                    book_ride_json.put("pick_location_lat", (marker.tag as InfoWindowData).start_lat)
                    book_ride_json.put("pick_location_name", intent.getStringExtra("leaving"))
                    book_ride_json.put("drop_location_long", intent.getStringExtra("user_end_long"))
                    book_ride_json.put("drop_location_lat", intent.getStringExtra("user_end_lat"))
                    book_ride_json.put("drop_location_name", intent.getStringExtra("going"))
                    presenter?.bookRide(book_ride_json, prefs)
                }
            }
        }

        book_tv.setOnTouchListener(infoButtonListener)

        mMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoWindow(marker: Marker): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View {
                // Setting up the infoWindow with current's marker info
                val infoWindowData = marker.tag as InfoWindowData?
                name_tv.text = infoWindowData?.rider_name
                details_tv.text = infoWindowData?.rider_info
                rating_tv.text = infoWindowData?.getRating()
                rating_bar.numStars = Integer.parseInt(infoWindowData?.getRating())
                infoButtonListener!!.setMarker(marker)
                // We must call this to set the current marker and infoWindow references
                // to the MapWrapperLayout
                mapWrapperLayout!!.setMarkerWithInfoWindow(marker, infoWindow)
                return infoWindow!!
            }
        })

//        val customInfoWindow = CustomInfoWindowGoogleMap(this);
//        mMap.setInfoWindowAdapter(customInfoWindow);


        //move map camera

        mMap?.animateCamera(CameraUpdateFactory.zoomTo(11f))

        mMap.setOnMarkerClickListener { marker ->
            // val position = (marker.getTag()) as Int
            mCurrLocationMarker!!.showInfoWindow()
            //Using position get Value from arraylist
            false
        }


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
            presenter?.getDrivers(jsonObj, prefs)

            //}
        }
        try {
            mMaterialDialog.show()
        } catch (e: Exception) {
        }

    }

    override fun onLocationChanged(location: Location?) {
        mLastLocation = location
        /* mapWrapperLayout!!.init(mMap, getPixelsFromDp(this, (39 + 20).toFloat()));
         if (mCurrLocationMarker != null) {
             mCurrLocationMarker?.remove()
         }
         //Place current location marker
         val latLng = LatLng(location!!.latitude, location.longitude)
 //        val markerOptions = MarkerOptions()
 //        markerOptions.position(latLng)
 //        markerOptions.title("Current Position")
 //        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
         // adding a marker on map with image from  drawable
         val info = InfoWindowData("Rider 1", "Info", "4");

         this.infoWindow = getLayoutInflater().inflate(R.layout.custom_marker_dialog, null) as ViewGroup
         val name_tv = infoWindow!!.findViewById(R.id.rider_name) as TextView
         val details_tv = infoWindow!!.findViewById(R.id.rider_info) as TextView

         val rating_tv = infoWindow!!.findViewById(R.id.tv_rating) as TextView
         val rating_bar = infoWindow!!.findViewById(R.id.ratingBar) as RatingBar

         val book_tv = infoWindow!!.findViewById(R.id.tv_book) as TextView
         val contact_tv = infoWindow!!.findViewById(R.id.tv_contact) as TextView

         this.infoButtonListener = object : OnInfoWindowElemTouchListener(book_tv) {
             override fun onClickConfirmed(v: View, marker: Marker) {
                 // Here we can perform some action triggered after clicking the button
                 CommonMethods.passengerRedirect(prefs, localClassName, this@BookRideMapsActivity)

             }
         }

         book_tv.setOnTouchListener(infoButtonListener)

         mMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
             override fun getInfoWindow(marker: Marker): View? {
                 return null
             }

             override fun getInfoContents(marker: Marker): View {
                 // Setting up the infoWindow with current's marker info
                 val infoWindowData = marker.tag as InfoWindowData?
                 name_tv.setText(infoWindowData?.getRider_name())
                 details_tv.setText(infoWindowData?.getRider_info())
                 rating_tv.setText(infoWindowData?.getRating())
                 rating_bar.numStars = Integer.parseInt(infoWindowData?.getRating())
                 infoButtonListener!!.setMarker(marker)
                 // We must call this to set the current marker and infoWindow references
                 // to the MapWrapperLayout
                 mapWrapperLayout!!.setMarkerWithInfoWindow(marker, infoWindow)
                 return infoWindow!!
             }
         })

 //        val customInfoWindow = CustomInfoWindowGoogleMap(this);
 //        mMap.setInfoWindowAdapter(customInfoWindow);
         mCurrLocationMarker = mMap.addMarker(
             MarkerOptions()
                 .position(latLng)
                 .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.thin_border_circle)))
         )

         //move map camera
         mMap?.moveCamera(CameraUpdateFactory.newLatLng(latLng))
         mMap?.animateCamera(CameraUpdateFactory.zoomTo(11f))

         mCurrLocationMarker!!.setTag(info)
         mMap.setOnMarkerClickListener { marker ->
             // val position = (marker.getTag()) as Int
             mCurrLocationMarker!!.showInfoWindow()
             //Using position get Value from arraylist
             false
         }*/

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onConnected(p0: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest?.interval = 1000
        mLocationRequest?.fastestInterval = 1000
        mLocationRequest?.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if ((ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        ) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_ride_maps)
        presenter = BookRideMapsPresenter(this, BookRideMapsModel())
        jsonObj = JSONObject(getIntent().getStringExtra("driver_json"))
        presenter?.getDrivers(jsonObj, prefs)

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.setMapType(GoogleMap.MAP_TYPE_NORMAL)
        //configureCameraIdle(mLastLocation);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                buildGoogleApiClient()
                mMap?.setMyLocationEnabled(true)
            }
        } else {
            buildGoogleApiClient()
            mMap?.setMyLocationEnabled(true)
        }
    }

    @Synchronized
    protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API).build()
        mGoogleApiClient?.connect()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton("OK") { dialogInterface, i ->
                        //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(
                            this@BookRideMapsActivity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            MY_PERMISSIONS_REQUEST_LOCATION
                        )
                    }
                    .create()
                    .show()


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient()
                        }
                        if (mMap != null) {
                            mMap?.setMyLocationEnabled(true)
                        }
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }

    private fun getMarkerBitmapFromView(resId: String): Bitmap {
        var customMarkerView = (getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
            R.layout.view_custom_marker,
            null
        )
        val markerImageView = customMarkerView.findViewById(R.id.profile_image) as CircleImageView
        if (!resId.equals("")) {
            Picasso.with(this).load(resId).placeholder(R.drawable.account_profile_img)
                .into(markerImageView)
        }
        //markerImageView.setImageResource(resId)
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight())
        customMarkerView.buildDrawingCache()
        val returnedBitmap = Bitmap.createBitmap(
            customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(returnedBitmap)
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
        val drawable = customMarkerView.getBackground()
        if (drawable != null)
            drawable!!.draw(canvas)
        customMarkerView.draw(canvas)
        return returnedBitmap
    }

    fun getPixelsFromDp(context: Context, dp: Float): Int {
        val scale = context.getResources().getDisplayMetrics().density
        return (dp * scale + 0.5f).toInt()
    }

}
