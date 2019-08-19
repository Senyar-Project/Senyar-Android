package com.android.senyaar.Activities

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.TypedValue
import android.view.*
import android.view.Gravity.CENTER
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.android.senyaar.Model.genericSpinnerModel
import com.android.senyaar.Presenters.DatePickerPresenter
import com.android.senyaar.R
import kotlinx.android.synthetic.main.activity_dashboard_passenger.*
import kotlinx.android.synthetic.main.content_dashboard_passenger.*
import java.util.*


class DashboardPassengerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var presenter: DatePickerPresenter? = null

    private val TIME_PICKER_DIALOG = 0
    private var mHour: Int = 0
    private var mMinute: Int = 0
    lateinit var mCalendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_passenger)
        initialize()
        journeySpinner()
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
    }

    private fun initialize() {
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
        bt_leaving_from.setOnClickListener { startActivity(Intent(this, SearchLocation::class.java)) }
        menu_notifications.setOnClickListener { startActivity(Intent(this, Notifications::class.java)) }
        menu_settings.setOnClickListener { startActivity(Intent(this, Settings::class.java)) }
        menu_journey.setOnClickListener { startActivity(Intent(this, AccountDetailsActivity::class.java)) }

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
            override fun onItemSelected(arg0: AdapterView<*>, arg1: View, arg2: Int, arg3: Long) {
                // TODO Auto-generated method stub
                val journey_type = spinnerList[arg2]
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
            R.id.schedule -> startActivity(Intent(this, RiderReceiptActivity::class.java))
            else -> return false
        }
        return true
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_tools -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
