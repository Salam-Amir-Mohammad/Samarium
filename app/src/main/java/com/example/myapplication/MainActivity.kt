package com.example.myapplication

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.telephony.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var tvNetworkType: TextView
    private lateinit var tvNetworkQuality: TextView
    private lateinit var tvNetworkQuantity: TextView
    private lateinit var tvLatitude: TextView
    private lateinit var tvLongitude: TextView
    private lateinit var tvEventTime: TextView
    private lateinit var tvTac: TextView
    private lateinit var tvLac: TextView
    private lateinit var tvRac: TextView
    private lateinit var tvPLMNID: TextView
    private lateinit var tvCellId: TextView

    private lateinit var telephonyManager: TelephonyManager
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        tvNetworkType = findViewById(R.id.tv_NetworkType)
        tvNetworkQuality = findViewById(R.id.tv_NetworkQuality)
        tvNetworkQuantity = findViewById(R.id.tv_NetworkQuantity)
        tvLatitude = findViewById(R.id.tv_Latitude)
        tvLongitude = findViewById(R.id.tv_Longitude)
        tvEventTime = findViewById(R.id.tv_eventTime)
        tvTac = findViewById(R.id.tv_Tac)
        tvLac = findViewById(R.id.tv_Lac)
        tvRac = findViewById(R.id.tv_Rac)
        tvPLMNID = findViewById(R.id.tv_PLMNID)
        tvCellId = findViewById(R.id.tv_CellId)

        telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Request permissions if necessary
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            getLocationAndNetworkInfo()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocationAndNetworkInfo()
        }
    }

    private fun getLocationAndNetworkInfo() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        // Get location
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)

        // Get network info
        getNetworkInfo()

        // Update event time
        updateEventTime()
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            tvLatitude.text = "Latitude: ${location.latitude}"
            tvLongitude.text = "Longitude: ${location.longitude}"
            updateEventTime()
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

        override fun onProviderEnabled(provider: String) {}

        override fun onProviderDisabled(provider: String) {}
    }

    private fun getNetworkInfo() {
        val cellInfoList = telephonyManager.allCellInfo

        for (cellInfo in cellInfoList) {
            when (cellInfo) {
                is CellInfoLte -> {
                    val cellIdentityLte = cellInfo.cellIdentity
                    val cellSignalStrengthLte = cellInfo.cellSignalStrength

                    tvNetworkType.text = "LTE"
                    tvTac.text = "TAC: ${cellIdentityLte.tac}"
                    tvCellId.text = "Cell ID: ${cellIdentityLte.ci}"
                    tvPLMNID.text = "PLMN ID: ${cellIdentityLte.mccString}${cellIdentityLte.mncString}"
                    tvNetworkQuality.text = "RSRQ: ${cellSignalStrengthLte.rsrq} dB"
                    tvNetworkQuantity.text = "RSRP: ${cellSignalStrengthLte.rsrp} dBm"
                }
                is CellInfoWcdma -> {
                    val cellIdentityWcdma = cellInfo.cellIdentity
                    val cellSignalStrengthWcdma = cellInfo.cellSignalStrength

                    tvNetworkType.text = "WCDMA"
                    tvLac.text = "LAC: ${cellIdentityWcdma.lac}"
                    tvCellId.text = "Cell ID: ${cellIdentityWcdma.cid}"
                    tvPLMNID.text = "PLMN ID: ${cellIdentityWcdma.mccString}${cellIdentityWcdma.mncString}"
                    tvNetworkQuality.text = "Ec/N0: ${cellSignalStrengthWcdma.ecNo} dB"
                    tvNetworkQuantity.text = "RSCP: ${cellSignalStrengthWcdma.dbm} dBm"
                }
            }
        }

        // Update event time on network info change
        updateEventTime()
    }

    private fun updateEventTime() {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentTime = sdf.format(Date())
        tvEventTime.text = "Event Time: $currentTime"
    }
}
