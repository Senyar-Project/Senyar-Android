package com.android.senyaar.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.android.senyaar.Presenters.LoginOptionPresenter
import com.android.senyaar.R
import com.android.senyaar.Views.LoginOptionView
import kotlinx.android.synthetic.main.activity_account_options.*

class LoginOptionActivity : AppCompatActivity(), LoginOptionView {
    private var presenter: LoginOptionPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_options)
        presenter = LoginOptionPresenter(this)
    }

    override fun initView() {
        bt_register.setOnClickListener {
            if(intent.getStringExtra("role").equals("ROLE_DRIVER")){
                val intent = Intent(this, SignUpDriverActivity::class.java)
                intent.putExtra("activityName", getIntent().getStringExtra("activityName"))
                startActivity(intent)
            }
            else {
                val intent = Intent(this, SignUpActivity::class.java)
                intent.putExtra("activityName", getIntent().getStringExtra("activityName"))
                startActivity(intent)
            }
            finish()
        }
        bt_sign_in.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            intent.putExtra("role", getIntent().getStringExtra("role"))
            intent.putExtra("activityName", getIntent().getStringExtra("activityName"))
            startActivity(intent)
            finish()
           }
    }
}