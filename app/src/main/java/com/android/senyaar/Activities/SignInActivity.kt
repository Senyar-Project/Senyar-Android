package com.android.senyaar.Activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import com.android.senyaar.Presenters.SignInPresenter
import com.android.senyaar.R
import com.android.senyaar.Utils.PreferenceHelper
import com.android.senyaar.Views.SignInView
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity(), SignInView {
    override fun successfulLogin() {
        startActivity(Intent(this, DashboardPassengerActivity::class.java))
    }

    private var presenter: SignInPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        presenter = SignInPresenter(this)
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
        bt_sign_in.setOnClickListener { presenter?.verifyFields(email.text.toString(), password.text.toString()) }
        forgot_password.setOnClickListener { startActivity(Intent(this, ForgetPasswordActivity::class.java)) }
        text_sign_up.setOnClickListener { startActivity(Intent(this, SignUpActivity::class.java)) }
    }

    override fun showProgress() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideProgress() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPreferences(): PreferenceHelper {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}