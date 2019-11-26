package com.android.senyaar.Presenters

import android.widget.CheckBox
import com.android.senyaar.Model.SignUpModel
import com.android.senyaar.Model.generalResponse
import com.android.senyaar.Utils.CommonMethods
import com.android.senyaar.Utils.CommonMethods.emailVerification
import com.android.senyaar.Utils.CommonMethods.isPasswordStrong
import com.android.senyaar.Views.SignUpView
import org.json.JSONObject
import java.util.regex.Matcher
import java.util.regex.Pattern

class SignUpPresenter(private var view: SignUpView, var model: SignUpModel) : SignUpModel.OnFinishedListener {
    init {
        view.initView()
    }

    override fun onResultSuccess(response: generalResponse) {
        view.hideProgress()
        view.signUpSuccessful(response)
    }

    override fun onResultFail(strError: String, tag: String) {
        view.hideProgress()
        view.showServerError(strError, tag)
    }

    fun verifyFields(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String,
        checkBox: CheckBox
    ) {
        if (firstName.isEmpty()) {
            view.showSnackbar("Enter first name")
            return
        }
        if (lastName.isEmpty()) {
            view.showSnackbar("Enter last name")
            return
        }
        if (email.isEmpty()) {
            view.showSnackbar("Enter email address")
            return
        }
        if (!CommonMethods.emailVerification(email.replace(" ", ""))) {
            view.showSnackbar("Email address is not valid")
            return
        }
        if (password.isEmpty()) {
            view.showSnackbar("Enter password")
            return
        }
        if (password.length < 8) {
            view.showSnackbar("Password must be at least 8 characters long")
            return
        }
        if (confirmPassword.isEmpty()) {
            view.showSnackbar("Confirm your password")
            return
        }
        if (confirmPassword.length < 8) {
            view.showSnackbar("Password must be at least 8 characters long")
            return
        }
        if (confirmPassword != password) {
            view.showSnackbar("Passwords do not match")
            return

        }
        if (!isPasswordStrong(password)) {
            view.showSnackbar("Password length must be 8 characters minimum and should contain at least one capital letter,one small letter, one digit and one special character")
            return

        }
        if (!checkBox.isChecked()) {
            view.showSnackbar("Accept terms and conditions")
            return
        } else {
            signUp(
                firstName,
                lastName,
                email,
                password
            )
        }
    }




    fun verifyDriverFields(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String, mobileNumber: String, vehicleNumber: String,
        licenseNumber: String, creditNumber:String,checkBox: CheckBox
    ) {
        if (firstName.isEmpty()) {
            view.showSnackbar("Enter first name")
            return
        }
        if (lastName.isEmpty()) {
            view.showSnackbar("Enter last name")
            return
        }
        if (email.isEmpty()) {
            view.showSnackbar("Enter email address")
            return
        }
        if (!emailVerification(email.replace(" ", ""))) {
            view.showSnackbar("Email address is not valid")
            return
        }
        if (password.isEmpty()) {
            view.showSnackbar("Enter password")
            return
        }
        if (password.length < 8) {
            view.showSnackbar("Password must be at least 8 characters long")
            return
        }
        if (confirmPassword.isEmpty()) {
            view.showSnackbar("Confirm your password")
            return
        }
        if (confirmPassword.length < 8) {
            view.showSnackbar("Password must be at least 8 characters long")
            return
        }
        if (confirmPassword != password) {
            view.showSnackbar("Passwords do not match")
            return

        }
        if (!isPasswordStrong(password)) {
            view.showSnackbar("Password length must be 8 characters minimum and should contain at least one capital letter,one small letter, one digit and one special character")
            return
        }

        if (mobileNumber.isEmpty()) {
            view.showSnackbar("Enter Mobile Number")
            return
        }
        if (vehicleNumber.isEmpty()) {
            view.showSnackbar("Enter Vehicle Number")
            return
        }
        if (licenseNumber.isEmpty()) {
            view.showSnackbar("Enter Driving License Number")
            return
        }
        if (creditNumber.isEmpty()) {
            view.showSnackbar("Enter Credit Card Number")
            return
        }
        if (!checkBox.isChecked()) {
            view.showSnackbar("Accept terms and conditions")
            return
        } else {
            signUpDriver(
                firstName,
                lastName,
                email,
                password, mobileNumber, vehicleNumber, licenseNumber,creditNumber
            )
        }
    }

    fun signUp(
        firstName: String, lastName: String, email: String, password: String
    ) {
        view.showProgress()
        var signup_json = JSONObject()
        signup_json.put("first_name", firstName)
        signup_json.put("last_name", lastName)
        signup_json.put("email", email)
        signup_json.put("password", password)
        signup_json.put("profile_type", "ROLE_CUSTOMER")
        model.signUp(this, signup_json)
    }

    fun signUpDriver(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        mobileNumber: String, vehicleNumber: String,
        licenseNumber: String,creditNumber:String
    ) {
        view.showProgress()
        var signup_json = JSONObject()
        signup_json.put("first_name", firstName)
        signup_json.put("last_name", lastName)
        signup_json.put("email", email)
        signup_json.put("password", password)
        signup_json.put("vechicle_number", vehicleNumber)
        signup_json.put("driving_license_number", licenseNumber)
        signup_json.put("mobile_number", mobileNumber)
        signup_json.put("credit_card_number", creditNumber)
        signup_json.put("profile_type", "ROLE_DRIVER")
        model.signUp(this, signup_json)
    }
}