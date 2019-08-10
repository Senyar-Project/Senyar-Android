package com.android.senyaar.Utils

class CommonMethods {
    public fun emailVerification(email: String?): Boolean {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}