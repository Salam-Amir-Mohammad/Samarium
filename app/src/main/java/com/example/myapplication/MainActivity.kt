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
import android.telephony.*


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvNetworkType = findViewById(R.id.tv_NetworkType)
        tvNetworkQuality = findViewById(R.id.tv_NetworkQuality)
        tvNetworkQuantity = findViewById(R.id.tv_NetworkQuantity)
        tvLatitude = findViewById(R.id.tv_Latitude)
        tvLongitude = findViewById(R.id.tv_Longitude)
        tvTac = findViewById(R.id.tv_Tac)
        tvLac = findViewById(R.id.tv_Lac)
        tvRac = findViewById(R.id.tv_Rac)
        tvPLMNID = findViewById(R.id.tv_PLMNID)
        tvCellId = findViewById(R.id.tv_CellId)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    private fun hasRequiredPermissions(): Boolean {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
        )
        return permissions.all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun startLocationUpdates() {
        if (hasRequiredPermissions()) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_INTERVAL, 0f, this)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_INTERVAL, 0f, this)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun displayNetworkInfo() {
        val cellInfoList = telephonyManager.allCellInfo

        for (cellInfo in cellInfoList) {
            when (cellInfo) {
                is CellInfoLte -> {
                    updateLteCellInfo(cellInfo)
                    break
                }
                is CellInfoWcdma -> {
                    updateWcdmaCellInfo(cellInfo)
                    break
                }
                is CellInfoGsm -> {
                    updateGsmCellInfo(cellInfo)
                    break
                }
            }
        }
    }

    private fun updateLteCellInfo(cellInfo: CellInfoLte) {
        val cellIdentityLte = cellInfo.cellIdentity
        val cellSignalStrengthLte = cellInfo.cellSignalStrength

        tvPLMNID.text = "PLMN-ID: ${cellIdentityLte.mccString}${cellIdentityLte.mncString}"
        tvTac.text = "TAC: ${cellIdentityLte.tac}"
        tvCellId.text = "Cell ID: ${cellIdentityLte.ci}"
        tvNetworkType.text = "4G (LTE)"

        tvNetworkQuantity.text = "RSRQ: ${cellSignalStrengthLte.rsrq}, RSRP: ${cellSignalStrengthLte.rsrp}"
        tvNetworkQuality.text = "Signal Strength: ${cellSignalStrengthLte.dbm} dBm"
    }

    private fun updateWcdmaCellInfo(cellInfo: CellInfoWcdma) {
        val cellIdentityWcdma = cellInfo.cellIdentity
        val cellSignalStrengthWcdma = cellInfo.cellSignalStrength

        tvPLMNID.text = "PLMN-ID: ${cellIdentityWcdma.mccString}${cellIdentityWcdma.mncString}"
        tvLac.text = "LAC: ${cellIdentityWcdma.lac}"
        tvCellId.text = "Cell ID: ${cellIdentityWcdma.cid}"
        tvNetworkType.text = "3G (WCDMA)"

        tvNetworkQuantity.text = "RSCP: ${cellSignalStrengthWcdma.dbm}"
        tvNetworkQuality.text = "Signal Strength: ${cellSignalStrengthWcdma.dbm} dBm"
    }

    private fun updateGsmCellInfo(cellInfo: CellInfoGsm) {
        val cellIdentityGsm = cellInfo.cellIdentity
        val cellSignalStrengthGsm = cellInfo.cellSignalStrength

        tvPLMNID.text = "PLMN-ID: ${cellIdentityGsm.mccString}${cellIdentityGsm.mncString}"
        tvLac.text = "LAC: ${cellIdentityGsm.lac}"
        tvCellId.text = "Cell ID: ${cellIdentityGsm.cid}"
        tvNetworkType.text = "2G (GSM)"

        tvNetworkQuantity.text = "RSSI: ${cellSignalStrengthGsm.dbm}"
        tvNetworkQuality.text = "Signal Strength: ${cellSignalStrengthGsm.dbm} dBm"
    }

    override fun onLocationChanged(location: Location) {
        tvLatitude.text = "Latitude: ${location.latitude}"
        tvLongitude.text = "Longitude: ${location.longitude}"
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            startLocationUpdates()
            displayNetworkInfo()
        }
    }
}
