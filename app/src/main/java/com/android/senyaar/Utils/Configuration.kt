package com.android.senyaar.Utils

class Configuration {

    companion object {
        var BASE_URL = "http://18.188.185.198:8080/v1/"
        var ENDPOINT_LOGIN = BASE_URL + "users/login"
        var ENDPOINT_SIGNUP = BASE_URL + "users/signup"
        var ENDPOINT_SCHEDULE_DRIVER = BASE_URL + "captain/schedule/add"
        var ENDPOINT_DRIVERS_LIST = BASE_URL + "captain/getList"
        var ENDPOINT_SCHEDULED_RIDES = BASE_URL + "captain/history"
        var ENDPOINT_SCHEDULED_RIDES_PASSENGER = BASE_URL + "ride/history"
        var ENDPOINT_BOOK_RIDE = BASE_URL + "ride/book"
        var ENDPOINT_START_RIDE = BASE_URL + "ride/start"
        var ENDPOINT_GET_PROFILE = BASE_URL + "users/profile"
        var ENDPOINT_UPDATE_PROFILE = BASE_URL + "users/profile/update"
        var ENDPOINT_MAPS = "https://maps.googleapis.com/maps/api/directions/"
        var ENDPOINT_FARE_ESTIMATE = BASE_URL + "ride/estimate"
        var ENDPOINT_UPLOAD_IMAGE = BASE_URL + "files/upload/user-profile"
        var ENDPOINT_RESET_PASSWORD = BASE_URL +"users/reset-password"

    }
}