package com.android.senyaar.Activities

import android.app.Dialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.TextView
import com.android.senyaar.Model.ProfileModel
import com.android.senyaar.Model.generalResponse
import com.android.senyaar.Presenters.DashboardPassengerPresenter
import com.android.senyaar.Presenters.DatePickerPresenter
import com.android.senyaar.R
import com.android.senyaar.Utils.CommonMethods
import com.android.senyaar.Utils.MyApplication
import com.android.senyaar.Utils.PreferenceHelper
import com.android.senyaar.Views.DashboardPassengerView
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_dashboard_passenger.*
import kotlinx.android.synthetic.main.content_dashboard_passenger.*
import kotlinx.android.synthetic.main.nav_header_dashboard_passenger.*
import kotlinx.android.synthetic.main.nav_header_dashboard_passenger.view.*
import org.json.JSONObject
import java.util.*


class DashboardPassengerActivity : AppCompatActivity(), DashboardPassengerView {
    private var presenter: DatePickerPresenter? = null

    private val TIME_PICKER_DIALOG = 0
    private var mHour: Int = 0
    private var mMinute: Int = 0
    lateinit var mCalendar: Calendar
    var journeyType: String? = ""
    private var presenter_dashboard: DashboardPassengerPresenter? = null
    var pDialog: ProgressDialog? = null
    lateinit var leavingLatLng: Location
    lateinit var goingLatLng: Location
    lateinit var prefs: PreferenceHelper
    lateinit var header_view: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_passenger)
        presenter_dashboard = DashboardPassengerPresenter(this, ProfileModel())
        initialize()
        journeySpinner()
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun getPreferences(): PreferenceHelper {
        return prefs
    }

    override fun getProfile(response: String) {
        Log.d("testing", "Response" + response)
        val gsonBuilder = GsonBuilder()
        val gson = gsonBuilder.create()
        val responseModel = gson.fromJson(response, generalResponse::class.java)
        header_view.tv_user_name.setText(responseModel.data?.first_name + " " + responseModel.data?.last_name)
    }

    fun initialize() {
        header_view = nav_view.getHeaderView(0)

        header_view.tv_user_name.setOnClickListener {
            if (CommonMethods.passengerRedirect(prefs, localClassName, this@DashboardPassengerActivity)) {
                startActivity(Intent(this, MyProfileActivity::class.java))
            }

        }
    }

    override fun initView() {
        pDialog = MyApplication.getInstance().progressdialog(this)
        prefs = PreferenceHelper(this)
        mCalendar = Calendar.getInstance()
        mHour = mCalendar.get(Calendar.HOUR_OF_DAY)
        mMinute = mCalendar.get(Calendar.MINUTE)
        presenter = DatePickerPresenter()

        bt_departure_time.setOnClickListener {
            showDialog(TIME_PICKER_DIALOG)
        }
        bt_departure_date.setOnClickListener {
            presenter!!.getDate(bt_departure_date, this)
        }
        bt_leaving_from.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("source", "leaving")
            startActivityForResult(intent, 1)
        }
        bt_going_to.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("source", "going")
            startActivityForResult(intent, 2)
        }

        menu_notifications.setOnClickListener { startActivity(Intent(this, Notifications::class.java)) }
        menu_settings.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            intent.putExtra("role", "ROLE_CUSTOMER")
            startActivity(intent)

        }
        //menu_journey.setOnClickListener { startActivity(Intent(this, RiderReceiptActivity::class.java)) }
        driver_link.setOnClickListener { startActivity(Intent(this, DashboardDriverActivity::class.java)) }

        bt_next.setOnClickListener {
            if (presenter_dashboard!!.verifyFields(
                    bt_leaving_from.text.toString(),
                    bt_going_to.text.toString(),
                    bt_departure_time.text.toString(),
                    bt_departure_date.text.toString(), journeyType!!
                )
            ) {
                val intent = Intent(this, FareCalculationActivity::class.java)
                intent.putExtra("leaving", bt_leaving_from.text)
                intent.putExtra("going", bt_going_to.text)
                intent.putExtra("date", bt_departure_date.text)
                intent.putExtra("time", bt_departure_time.text)
                intent.putExtra("user_end_lat", goingLatLng.latitude.toString())
                intent.putExtra("user_end_long", goingLatLng.longitude.toString())
                intent.putExtra("user_lat", leavingLatLng.latitude.toString())
                intent.putExtra("user_long", leavingLatLng.longitude.toString())
                intent.putExtra("driver_json", createJson().toString())
                startActivity(intent)
            }
        }
    }

    fun createJson(): JSONObject {
        val get_driver_json = JSONObject()
        get_driver_json.put("schedule_date", bt_departure_date.text)
        get_driver_json.put("schedule_time", bt_departure_time.text)
        get_driver_json.put("user_lat", leavingLatLng.latitude.toString())
        get_driver_json.put("user_long", leavingLatLng.longitude.toString())
        get_driver_json.put("user_end_lat", goingLatLng.latitude.toString())
        get_driver_json.put("user_end_long", goingLatLng.longitude.toString())
        return get_driver_json
    }

    override fun showProgress() {
        pDialog?.show()
    }

    override fun hideProgress() {
        if (pDialog?.isShowing!!) {
            pDialog?.hide()
        }
    }

    override fun showServerError(errorMessage: String, tag: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> {
                if (data != null) {
                    bt_leaving_from.text = data!!.getStringExtra("location")
                    leavingLatLng = data.getParcelableExtra("latLng")
                }
            }

            2 -> {
                if (data != null) {
                    bt_going_to.text = data!!.getStringExtra("location")
                    goingLatLng = data.getParcelableExtra("latLng")
                }
            }
        }

    }

    override fun onCreateDialog(id: Int): Dialog {
        when (id) {
            TIME_PICKER_DIALOG -> return TimePickerDialog(
                this@DashboardPassengerActivity,
                timePickerListener,
                mHour,
                mMinute,
                false
            )
        }
        return super.onCreateDialog(id)
    }

    private val timePickerListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minutes ->
        // TODO Auto-generated method stub
        mHour = hourOfDay
        mMinute = minutes
        updateTime(mHour, mMinute)
    }


    private fun updateTime(hours: Int, mins: Int) {
        var hours = hours
        var timeSet = ""
        val dateTime = StringBuilder().append(hours).append(':').append(mins).append(" ").toString()
        if (hours > 12) {
            hours -= 12
            timeSet = "PM"
        } else if (hours == 0) {
            hours += 12
            timeSet = "AM"
        } else if (hours == 12)
            timeSet = "PM"
        else
            timeSet = "AM"
        var minutes = ""
        if (mins < 10)
            minutes = "0$mins"
        else
            minutes = mins.toString()
        // cb.setChecked(true);

        bt_departure_time.text =
            StringBuilder().append(hours).append(':').append(minutes).append(" ").append(timeSet).toString()
    }

    private fun journeySpinner() {
        val spinnerList = ArrayList<String>()
        spinnerList.add(0, "Journey type")
        spinnerList.add(1, "One Way")
        spinnerList.add(2, "Return")

        val adp = object :
            ArrayAdapter<String>(this@DashboardPassengerActivity, android.R.layout.simple_spinner_item, spinnerList) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                // Get the current item from ListView
                val view = super.getView(position, convertView, parent)
                val tv = view.findViewById<View>(android.R.id.text1) as TextView
                tv.setTextColor(resources.getColor(R.color.grey_text))
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.font))
                tv.text = spinnerList[position]
                return view
            }

            override fun isEnabled(position: Int): Boolean {
                // Disable the first item from Spinner
                // First item will be use for hint
                return position != 0
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val spinnerview = super.getDropDownView(position, convertView, parent)
                val spinnertextview = spinnerview as TextView
                spinnertextview.text = spinnerList[position]
                return spinnerview
            }
        }
        adp.setDropDownViewResource(R.layout.spinner_dropdown_item)
        //adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_type.adapter = adp
        spinner_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>, arg1: View?, arg2: Int, arg3: Long) {
                journeyType = spinnerList[arg2]
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {}
        }
    }


    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(com.android.senyaar.R.menu.dashboard_passenger, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.schedule -> if (CommonMethods.passengerRedirect(
                    prefs,
                    localClassName,
                    this@DashboardPassengerActivity
                )
            ) {
                startActivity(Intent(this, ScheduledTripsActivity::class.java))
            }
            else -> return false
        }
        return true
    }


    override fun onResume() {
        super.onResume()
        overridePendingTransition(0, 0)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        if (prefs.isLogin.equals("passenger")) {
            if (prefs.sign_in_token != null && prefs.role != null) {
                header_view.tv_user_name.setText(prefs.userName_passenger)
                if (prefs.userImage_passenger != null && !prefs.userImage_passenger.equals("")) {
                    Picasso.with(this).load(prefs.userImage_passenger).placeholder(R.drawable.account_profile_img)
                        .into(header_view.imageView)
                }
                //  presenter_dashboard!!.getProfile(prefs)
            }
        } else {
            header_view.tv_user_name.setText("Guest User")
        }
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
}
