package com.android.senyaar.Activities

import android.app.Dialog
import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.view.Window
import android.widget.FrameLayout
import android.widget.TextView
import com.android.senyaar.Model.ResetPasswordModel
import com.android.senyaar.Model.generalResponse
import com.android.senyaar.Presenters.ResetPasswordPresenter
import com.android.senyaar.R
import com.android.senyaar.Utils.MyApplication
import com.android.senyaar.Utils.PreferenceHelper
import com.android.senyaar.Views.SignInView
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_reset_password.*
import kotlinx.android.synthetic.main.appbar.*
import kotlinx.android.synthetic.main.general_dialog.*
import me.drakeet.materialdialog.MaterialDialog
import org.json.JSONObject

class ResetPasswordActivity : AppCompatActivity(), SignInView {
    override fun getProfile(response: generalResponse) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    lateinit var prefs: PreferenceHelper
    var pDialog: ProgressDialog? = null
    override fun changePassword(response: String) {
        Log.d("testing", "Response" + response)
        val gsonBuilder = GsonBuilder()
        val gson = gsonBuilder.create()
        val responseModel = gson.fromJson(response, generalResponse::class.java)
        showDialog(responseModel.message!!)
    }

    override fun loginSuccessful(response: generalResponse) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

            presenter?.changePassword(previous_password.text.toString(), new_password.text.toString(), prefs)
        }
        try {
            mMaterialDialog.show()
        } catch (e: Exception) {
        }

    }


    private var presenter: ResetPasswordPresenter? = null
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        presenter = ResetPasswordPresenter(this, ResetPasswordModel())
        customToolbar()
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
        text_toolbar.text = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun getPreferences(): PreferenceHelper {
        return prefs
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

    override fun successfulLogin() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initView() {
        prefs = PreferenceHelper(this)
        pDialog = MyApplication.getInstance().progressdialog(this)
        bt_reset_password.setOnClickListener {
            presenter?.verifyFields(
                previous_password.text.toString(),
                new_password.text.toString(),
                confirm_password.text.toString(), prefs
            )
        }
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
}