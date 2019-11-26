package com.android.senyaar.Activities

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.Window
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.android.senyaar.Model.SignInModel
import com.android.senyaar.Model.generalResponse
import com.android.senyaar.Presenters.SignInPresenter
import com.android.senyaar.R
import com.android.senyaar.Utils.MyApplication
import com.android.senyaar.Utils.PreferenceHelper
import com.android.senyaar.Views.SignInView
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.general_dialog.*
import me.drakeet.materialdialog.MaterialDialog


class SignInActivity : AppCompatActivity(), SignInView {
    override fun changePassword(response: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    lateinit var pDialog: ProgressDialog
    lateinit var prefs: PreferenceHelper
    lateinit var message: String

    override fun loginSuccessful(response: generalResponse) {
        if (response.code.equals("200")) {
            Log.d("testing", "Response" + response.message + response.code)
            prefs.sign_in_token = response.data?.token
            val set = HashSet<String>()
            set.addAll(response.data?.roles!!)
            prefs.role = set
            message = response.message!!
            presenter?.getProfile(prefs)
        } else {
            showSnackbar(response.message!!)
        }
    }

    override fun getProfile(response: generalResponse) {
        Log.d("testing", "Response" + response)
        if (prefs.role.contains(intent.getStringExtra("role"))) {
            if (intent.getStringExtra("role").equals("ROLE_CUSTOMER")) {
                prefs.isLogin = "passenger"
                prefs.userName_passenger = response.data?.first_name + " " + response.data?.last_name
                prefs.userImage_passenger = response.data?.profile_picture
                //startActivity(Intent(this, BookRideMapsActivity::class.java))
            } else {
                prefs.isLogin = "driver"
                prefs.userName_driver = response.data?.first_name + " " + response.data?.last_name
                prefs.userImage_driver = response.data?.profile_picture
                //startActivity(Intent(this, DashboardDriverActivity::class.java))
            }
            onBackPressed()
            finish()
        } else {
            showDialog(response.message!!)
        }
    }

    override fun showServerError(errorMessage: String, tag: String) {
        server_alertdialog(errorMessage, tag)
    }

    override fun successfulLogin() {
        startActivity(Intent(this, DashboardPassengerActivity::class.java))
    }

    private var presenter: SignInPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        presenter = SignInPresenter(this, SignInModel())
    }

    override fun showSnackbar(message: String) {
        val snack = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val view = snack.view
        view.setBackgroundColor(resources.getColor(R.color.snackbar_red))
        val textView = view.findViewById(android.support.design.R.id.snackbar_text) as TextView
        textView.setTextColor(resources.getColor(R.color.white))
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.font))
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        params.gravity = Gravity.CENTER_HORIZONTAL
        view.layoutParams = params
        snack.show()
    }

    override fun initView() {
        pDialog = MyApplication.getInstance().progressdialog(this)
        prefs = PreferenceHelper(this)
        bt_sign_in.setOnClickListener { presenter?.verifyFields(email.text.toString(), password.text.toString()) }
        forgot_password.setOnClickListener { startActivity(Intent(this, ForgetPasswordActivity::class.java)) }
        text_sign_up.setOnClickListener {
            if (intent.getStringExtra("role").equals("ROLE_DRIVER")) {
                val intent = Intent(this, SignUpDriverActivity::class.java)
                intent.putExtra("activityName", getIntent().getStringExtra("activityName"))
                startActivity(intent)
            } else {
                val intent = Intent(this, SignUpActivity::class.java)
                intent.putExtra("activityName", getIntent().getStringExtra("activityName"))
                startActivity(intent)
            }
            finish()
        }
    }

    override fun showProgress() {
        if (null != pDialog) {
            pDialog.show()
        }
    }

    override fun hideProgress() {
        if (pDialog.isShowing) {
            pDialog.hide()
        }
    }

    override fun getPreferences(): PreferenceHelper {
        return prefs
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
            presenter?.login(email.text.toString(), password.text.toString())
            //}
        }
        try {
            mMaterialDialog.show()
        } catch (e: Exception) {
        }

    }


    fun showDialog(response: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.general_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        dialog.dialog_title_data_navigation.text =
            "Your account is not associated with the driver account.Do you want to sign up as driver?"
        dialog.okay.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setCancelable(false)
    }
}