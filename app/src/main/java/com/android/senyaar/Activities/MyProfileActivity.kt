package com.android.senyaar.Activities

import android.Manifest
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Toast
import com.android.senyaar.Model.ProfileModel
import com.android.senyaar.Model.generalResponse
import com.android.senyaar.NetworkCalls.VolleyMultipartRequest
import com.android.senyaar.NetworkCalls.VolleyMultipartRequest.DataPart
import com.android.senyaar.NetworkCalls.VolleySingleton
import com.android.senyaar.Presenters.ProfilePresenter
import com.android.senyaar.R
import com.android.senyaar.Utils.AppHelper
import com.android.senyaar.Utils.Configuration
import com.android.senyaar.Utils.MyApplication
import com.android.senyaar.Utils.PreferenceHelper
import com.android.senyaar.Views.ProfileView
import com.android.volley.*
import com.google.common.base.Utf8
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.appbar.*
import kotlinx.android.synthetic.main.general_dialog.*
import me.drakeet.materialdialog.MaterialDialog
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets


class MyProfileActivity : AppCompatActivity(), ProfileView {


    lateinit var prefs: PreferenceHelper
    var pDialog: ProgressDialog? = null
    private var presenter: ProfilePresenter? = null
    private val GALLERY = 1
    val CAMERA = 2
    var flag = 0
    lateinit var message: String
    lateinit var myProfileImage: Bitmap
    override fun getPreferences(): PreferenceHelper {
        return prefs
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        customToolbar()
        presenter = ProfilePresenter(this, ProfileModel())
        presenter?.getProfile(prefs)
        initialize()
    }

    override fun showSnackbar(message: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getProfile(response: String) {
        Log.d("testing", "Response" + response)
        val gsonBuilder = GsonBuilder()
        val gson = gsonBuilder.create()
        val responseModel = gson.fromJson(response, generalResponse::class.java)
        if (flag == 1) {
            updateView(responseModel)
            showDialog(message)
        } else {
            updateView(responseModel)
        }
    }

    override fun updateProfile(response: String) {
        Log.d("testing", "Response" + response)
        val gsonBuilder = GsonBuilder()
        val gson = gsonBuilder.create()
        val responseModel = gson.fromJson(response, generalResponse::class.java)
        message = responseModel.message!!
        flag = 1
        presenter?.getProfile(prefs)
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
            finish()
        }
        dialog.setCancelable(false)
    }

    override fun initView() {
        prefs = PreferenceHelper(this)
        pDialog = MyApplication.getInstance().progressdialog(this)
        if (prefs.isLogin.equals("passenger")) {
            rl_driver_fields.visibility = View.GONE
        } else {
            rl_driver_fields.visibility = View.VISIBLE
        }

        tv_toolbar_right.setOnClickListener {
            presenter?.updateProfile(createJson(), prefs)
            uploadImage()
        }
    }

    private fun createJson(): JSONObject {
        var profile_json = JSONObject()
        profile_json.put("first_name", first_name.text)
        profile_json.put("last_name", last_name.text)
        profile_json.put("vechicle_number", vehicle_number.text)
        profile_json.put("driving_license_number", license_number.text)
        profile_json.put("mobile_number", mobile_number.text)
        if (prefs.isLogin == "passenger") {
            profile_json.put("user_type", "passenger")
        } else {
            profile_json.put("user_type", "driver")
        }
        return profile_json
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

    private fun updateView(response: generalResponse) {
        first_name.setText(response.data?.first_name)
        last_name.setText(response.data?.last_name)
        email.setText(response.data?.username)
        mobile_number.setText(response.data?.mobile_number)
        userName.text = response.data?.first_name + " " + response.data?.last_name
        vehicle_number.setText(response.data?.vechicle_number)
        license_number.setText(response.data?.licence_number)
        if (response.data?.profile_picture != null && !response.data?.profile_picture.equals("")) {
            Picasso.with(this).load(response.data?.profile_picture).placeholder(R.drawable.account_profile_img)
                .into(imageView)
        }
        if (prefs.isLogin == "passenger") {
            prefs.userName_passenger = response.data?.first_name + " " + response.data?.last_name
            prefs.userImage_passenger = response.data?.profile_picture
        } else {
            prefs.userName_driver = response.data?.first_name + " " + response.data?.last_name
            prefs.userImage_driver = response.data?.profile_picture
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
            if (tag.equals("get")) {
                presenter?.getProfile(prefs)
            }
            if (tag.equals("update_image")) {
                uploadImage()
            } else {
                presenter?.updateProfile(createJson(), prefs)
            }
        }
        try {
            mMaterialDialog.show()
        } catch (e: Exception) {
        }

    }


    private fun initialize() {

        imageView.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) !== PackageManager.PERMISSION_GRANTED
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(arrayOf<String>(android.Manifest.permission.CAMERA), 5)
                }
                //If permission is granted then show dialog to user to select an option
            } else {
                showPictureDialog()
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

    private fun customToolbar() {
        setSupportActionBar(toolbar_edit)
        text_toolbar.text = "Account Details"
        tv_toolbar_right.text = "Save"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    //Set up permissions for camera
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 5) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showPictureDialog()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //When reset password activity returns result
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //if gallery has been requested, get the selected image in a bitmap and set it on the screen
        if (requestCode == GALLERY) {
            if (data != null) {
                val myPath = data.data
                try {
                    myProfileImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), myPath)
                    // val realPath = getRealPathFromURI(myPath)
                    //val bitmap = modifyOrientation(myProfileImage, realPath)
                    imageView.setImageBitmap(myProfileImage)
                    var iStream: InputStream? = null
                    if (myPath != null) {
                        iStream = this.getContentResolver().openInputStream(myPath)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        } else if (requestCode == CAMERA) {
            myProfileImage = data!!.extras!!.get("data") as Bitmap
            imageView.setImageBitmap(myProfileImage)
        }//if the camera is requested, get the picture in a bitmap and set it to the screen
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        //actions for user to select
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(
            pictureDialogItems
        ) { dialog, which ->
            when (which) {
                //if first option is selected, call select photos from gallery function
                0 -> choosePhotoFromGallary()
                //if second item is selected, call take photo from camera function
                1 -> takePhotoFromCamera()
            }
        }

        pictureDialog.show()
    }

    fun choosePhotoFromGallary() {
        val galleryIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY)
    }

    //open the camera from the user's phone
    private fun takePhotoFromCamera() {
        val intent = Intent(ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    private fun uploadImage() {
        val multipartRequest = object : VolleyMultipartRequest(
            Request.Method.POST,
            Configuration.ENDPOINT_UPLOAD_IMAGE,
            object : Response.Listener<NetworkResponse> {
                override fun onResponse(response: NetworkResponse) {
                    val resultResponse = String(response.data)
                    Log.d("testing", "CODE: $resultResponse")

                }
            },
            object : Response.ErrorListener {

                override fun onErrorResponse(error: VolleyError) {
                    val networkResponse = error.networkResponse
                    var errorMessage = "Unknown error"
                    if (networkResponse == null) {
                        if (error.javaClass == TimeoutError::class.java) {
                            errorMessage = "Request timeout"
                            server_alertdialog(errorMessage, "update_image")
                        } else if (error.javaClass == NoConnectionError::class.java) {
                            errorMessage = "Failed to connect server"
                            server_alertdialog(errorMessage, "update_image")
                        }
                    } else {
                        val result = String(networkResponse!!.data)
                        val obj: JSONObject
                        try {
                            val body = String(networkResponse!!.data, StandardCharsets.UTF_8)
                            obj = JSONObject(body)
                            Log.d("response", obj.get("code").toString())
                        } catch (e: UnsupportedEncodingException) {
                            e.printStackTrace()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    }
                    error.printStackTrace()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): HashMap<String, String> {
                val params = HashMap<String, String>()
                //set the access token in the header
                params["Authorization"] = String.format("Bearer %s", prefs.sign_in_token)
                return params
            }


            protected override fun getByteData(): Map<String, DataPart> {
                val params = HashMap<String, DataPart>()
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put(
                    "profile",
                    DataPart(
                        "profileImage.png",
                        AppHelper.getFileDataFromDrawable(getApplication().getBaseContext(), imageView.getDrawable()),
                        "image/png"
                    )
                )
                return params
            }

        }
        // Adding String request to request queue
        multipartRequest.retryPolicy = DefaultRetryPolicy(
            30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        VolleySingleton.getInstance(application.baseContext).addToRequestQueue(multipartRequest)
    }
}