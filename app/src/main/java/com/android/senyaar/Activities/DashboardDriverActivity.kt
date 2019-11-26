package com.android.senyaar.Activities

import android.app.Dialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.util.TypedValue
import android.view.*

import android.widget.FrameLayout
import android.widget.TextView
import com.android.senyaar.Model.DashboardDriverModel
import com.android.senyaar.Model.generalResponse
import com.android.senyaar.Presenters.DashboardDriverPresenter
import com.android.senyaar.Presenters.DatePickerPresenter
import com.android.senyaar.R
import com.android.senyaar.Utils.CommonMethods
import com.android.senyaar.Utils.MyApplication
import com.android.senyaar.Utils.PreferenceHelper
import com.android.senyaar.Views.DashboardDriverView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_dashboard_driver.*
import kotlinx.android.synthetic.main.content_dashboard_driver.*
import kotlinx.android.synthetic.main.general_dialog.*
import kotlinx.android.synthetic.main.nav_header_dashboard_driver.*
import kotlinx.android.synthetic.main.nav_header_dashboard_driver.view.*
import java.util.*

class DashboardDriverActivity : AppCompatActivity(), DashboardDriverView {
    var pDialog: ProgressDialog? = null
    private var presenter: DatePickerPresenter? = null
    private var presenter_dashboard: DashboardDriverPresenter? = null
    lateinit var prefs: PreferenceHelper
    private val TIME_PICKER_DIALOG = 0
    private var mHour: Int = 0
    private var mMinute: Int = 0
    lateinit var mCalendar: Calendar
    lateinit var leavingLatLng: Location
    lateinit var goingLatLng: Location
    lateinit var header_view: View

    override fun getPreferences(): PreferenceHelper {
        return prefs
    }

    override fun scheduleRide(response: generalResponse) {
        Log.d("testing", "Response" + response.message)
        showDialog(response.message)

    } override fun getProfile(response: generalResponse) {
        Log.d("testing", "Response" + response)
        header_view.tv_userName.setText(response.data?.first_name + " " + response.data?.last_name)
    }

    fun showDialog(response: String?) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.general_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        dialog.dialog_title_data_navigation.text =
            response
        dialog.okay.setOnClickListener {
            dialog.dismiss()
            bt_going_to.text="Going to"
            bt_departure_date.text="Departure Date"
            bt_departure_time.text="Departure Time"
            bt_leaving_from.text="Leave from"
        }
        dialog.setCancelable(false)
    }

    override fun initView() {
        pDialog = MyApplication.getInstance().progressdialog(this)
        mCalendar = Calendar.getInstance()
        mHour = mCalendar.get(Calendar.HOUR_OF_DAY)
        mMinute = mCalendar.get(Calendar.MINUTE)
        presenter = DatePickerPresenter()
        prefs = PreferenceHelper(this)
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
            intent.putExtra("role", "ROLE_DRIVER")
            startActivity(intent)
        }
        menu_journey.setOnClickListener { startActivity(Intent(this, RiderReceiptActivity::class.java)) }
        driver_link.setOnClickListener { startActivity(Intent(this, DashboardPassengerActivity::class.java)) }


        bt_next.setOnClickListener {
            if (presenter_dashboard!!.verifyFields(
                    bt_leaving_from.text.toString(),
                    bt_going_to.text.toString(),
                    bt_departure_time.text.toString(),
                    bt_departure_date.text.toString()
                )
            ) {
                if (CommonMethods.driverRedirects(prefs, localClassName, this@DashboardDriverActivity)) {
                    presenter_dashboard!!.scheduleRide(
                        leavingLatLng.latitude.toString(),
                        leavingLatLng.longitude.toString(),
                        bt_leaving_from.text.toString(),
                        goingLatLng.latitude.toString(),
                        goingLatLng.longitude.toString(),
                        bt_going_to.text.toString(),
                        bt_departure_date.text.toString(),
                        bt_departure_time.text.toString(),
                        prefs
                    )
                }

            }

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


    override fun showServerError(errorMessage: String, tag: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_driver)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        presenter_dashboard = DashboardDriverPresenter(this, DashboardDriverModel())
        initialize()
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    fun initialize() {
        header_view = nav_view.getHeaderView(0)

        header_view.rl_name.setOnClickListener {
            if (CommonMethods.driverRedirects(prefs, localClassName, this@DashboardDriverActivity)) {
                startActivity(Intent(this, MyProfileActivity::class.java))
            }
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
                this@DashboardDriverActivity,
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
        if (hours < 10)
            bt_departure_time.text =
                StringBuilder().append("0" + hours).append(':').append(minutes).append(" ").append(timeSet).toString()
        else
            bt_departure_time.text =
                StringBuilder().append(hours).append(':').append(minutes).append(" ").append(timeSet).toString()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.dashboard_rider, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.schedule -> if (CommonMethods.driverRedirects(prefs, localClassName, this@DashboardDriverActivity)) {
                startActivity(Intent(this, ScheduledTripsActivity::class.java))
            }
            else -> return false
        }
        return true
    }


    override fun onResume() {
        super.onResume()
        overridePendingTransition(0,0)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        if (prefs.isLogin.equals("driver")) {
            if (prefs.sign_in_token != null && prefs.role != null) {
                header_view.tv_userName.setText(prefs.userName_driver)
                if (prefs.userImage_passenger != null && !prefs.userImage_passenger.equals("")) {
                    Picasso.with(this).load(prefs.userImage_driver).placeholder(R.drawable.account_profile_img)
                        .into(header_view.imageView)
                }
                // presenter_dashboard!!.getProfile(prefs)
            }
        } else {
            header_view.tv_userName.setText("Guest User")
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
