package com.android.senyaar.Activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import com.android.senyaar.Model.SignUpModel
import com.android.senyaar.Model.generalResponse
import com.android.senyaar.Presenters.SignUpPresenter
import com.android.senyaar.R
import com.android.senyaar.Utils.PreferenceHelper
import com.android.senyaar.Views.SignUpView
import kotlinx.android.synthetic.main.activity_register.*
import android.app.ProgressDialog
import com.android.senyaar.Utils.MyApplication
import me.drakeet.materialdialog.MaterialDialog


class SignUpActivity : AppCompatActivity(), SignUpView {
    var pDialog: ProgressDialog? = null

    override fun signUpSuccessful(response: generalResponse) {
        Log.d("testing", "Response" + response.message)
        if (response.code.equals("200")) {
            Log.d("testing", "Response" + response.message + response.code)
            val intent = Intent(this, SignInActivity::class.java)
            intent.putExtra("role", "ROLE_CUSTOMER")
            startActivity(intent)
            finish()
        } else {
            showSnackbar(response.message!!)
        }
    }

    override fun showServerError(errorMessage: String, tag: String) {
        server_alertdialog(errorMessage, tag)
    }

    private var presenter: SignUpPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        presenter = SignUpPresenter(this, SignUpModel())
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
        pDialog = MyApplication.getInstance().progressdialog(this);
        text_terms_and_condition.setText(Html.fromHtml("Agree to <font color='#6D9FFF'><b><big>Terms & Conditions </b></big></font>"))
        bt_register.setOnClickListener {
            presenter?.verifyFields(
                first_name.text.toString(),
                last_name.text.toString(),
                email.text.toString(),
                password.text.toString(),
                confirm_password.text.toString(),
                checkbox_terms_and_condition
            )
        }
        text_sign_in.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            intent.putExtra("role", "ROLE_CUSTOMER")
            startActivity(intent)
            finish()
        }
    }

    override fun showProgress() {
        pDialog?.show()
    }

    override fun hideProgress() {
        if (pDialog?.isShowing!!) {
            pDialog?.hide()
        }
    }

    override fun getPreferences(): PreferenceHelper {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
            presenter?.signUp(
                first_name.text.toString(),
                last_name.text.toString(),
                email.text.toString(),
                password.text.toString()
            )
            //}
        }
        try {
            mMaterialDialog.show()
        } catch (e: Exception) {
        }

    }
}