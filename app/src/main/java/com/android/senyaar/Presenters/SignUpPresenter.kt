package com.android.senyaar.Presenters

import android.widget.CheckBox
import com.android.senyaar.Utils.CommonMethods
import com.android.senyaar.Views.SignUpView
import java.util.regex.Matcher
import java.util.regex.Pattern

class SignUpPresenter(private var view: SignUpView) {
    init {
        view.initView()
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
        if (!checkBox.isChecked()) {
            view.showSnackbar("Accept terms and conditions")
            return
        }
    }

    fun emailVerification(email: String?): Boolean {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isPasswordStrong(pass: String): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[-@#!£$%^&*()_+~`{}\\[\\]:\"'?,.\\\\/])(?=.*[A-Z])(?=.*[a-z])(?=\\S+$).{8,}$"
        //String PASSWORD_PATTERN="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(pass)
        return matcher.matches()
    }
}