package com.example.myapplication


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.telephony.*
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.database.NetworkInfo
import com.example.myapplication.database.NetworkInfoDatabaseHelper

class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var tvNetworkType: TextView
    private lateinit var tvNetworkQuality: TextView
    private lateinit var tvNetworkQuantity: TextView
    private lateinit var tvLatitude: TextView
    private lateinit var tvLongitude: TextView
    private lateinit var tvTac: TextView
    private lateinit var tvLac: TextView
    private lateinit var tvRac: TextView
    private lateinit var tvPLMNID: TextView
    private lateinit var tvCellId: TextView

    private lateinit var locationManager: LocationManager
    private lateinit var telephonyManager: TelephonyManager

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
        private const val LOCATION_UPDATE_INTERVAL = 5000L // 5 seconds
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

}
