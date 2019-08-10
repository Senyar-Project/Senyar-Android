package com.android.senyaar.Activities

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import com.android.senyaar.Presenters.ForgetPasswordPresenter
import com.android.senyaar.R
import com.android.senyaar.Utils.PreferenceHelper
import com.android.senyaar.Views.BaseView
import com.android.senyaar.Views.SignInView
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgetPasswordActivity : AppCompatActivity(), SignInView {
    override fun successfulLogin() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var presenter: ForgetPasswordPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        presenter = ForgetPasswordPresenter(this)
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
        bt_forget_password.setOnClickListener { presenter?.verifyFields(email.text.toString()) }
        text_sign_in.setOnClickListener { onBackPressed() }
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