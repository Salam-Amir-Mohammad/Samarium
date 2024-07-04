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
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.database.NetworkInfo
import kotlinx.coroutines.launch
import android.telephony.CellInfoWcdma
import android.telephony.CellSignalStrengthWcdma

class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var tvNetworkType: TextView
    private lateinit var tvNetworkQuality: TextView
    private lateinit var tvNetworkQuantity: TextView
    private lateinit var tvLatitude: TextView
    private lateinit var tvLongitude: TextView
    private lateinit var eventTime: TextView
    private lateinit var tvTac: TextView
    private lateinit var tvLac: TextView
    private lateinit var tvRac: TextView
    private lateinit var tvPLMNID: TextView
    private lateinit var tvCellId: TextView

    private lateinit var locationManager: LocationManager
    private lateinit var telephonyManager: TelephonyManager
    private lateinit var database: AppDatabase

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
        eventTime = findViewById(R.id.tv_eventTime)
        tvTac = findViewById(R.id.tv_Tac)
        tvLac = findViewById(R.id.tv_Lac)
        tvRac = findViewById(R.id.tv_Rac)
        tvPLMNID = findViewById(R.id.tv_PLMNID)
        tvCellId = findViewById(R.id.tv_CellId)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        database = AppDatabase.getDatabase(this)

        if (!hasRequiredPermissions()) {
            requestPermissions()
        } else {
            startLocationUpdates()
            displayNetworkInfo()
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
        )
        return permissions.all {
            ContextCompat.checkSelfPermission(
                this,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
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
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                LOCATION_UPDATE_INTERVAL,
                0f,
                this
            )
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                LOCATION_UPDATE_INTERVAL,
                0f,
                this
            )
            eventTime.text = "Event Time: ${System.currentTimeMillis()}"
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

        val plmnId = "${cellIdentityLte.mccString}${cellIdentityLte.mncString}"
        val tac = cellIdentityLte.tac?.toString()
        val cellId = cellIdentityLte.ci?.toString()
        val networkType = "4G (LTE)"
        val networkQuantity = cellSignalStrengthLte.rsrq?.toString() ?: ""
        val networkQuality = cellSignalStrengthLte.rsrp?.toString() ?: ""

        tvPLMNID.text = "PLMN-ID: ${cellIdentityLte.mccString}${cellIdentityLte.mncString}"
        tvTac.text = "TAC: ${cellIdentityLte.tac}"
        tvCellId.text = "Cell ID: ${cellIdentityLte.ci}"
        tvNetworkType.text = "4G (LTE)"

        tvNetworkQuantity.text = "RSRP: ${cellSignalStrengthLte.rsrp} dbm"
        tvNetworkQuality.text = "RSRQ: ${cellSignalStrengthLte.rsrq}"

        storeNetworkInfo(NetworkInfo(
            eventTime = System.currentTimeMillis(),
            latitude = tvLatitude.text.toString().substringAfter(": ").toDouble(),
            longitude = tvLongitude.text.toString().substringAfter(": ").toDouble(),
            cellTechnology = networkType,
            cellId = cellId,
            plmnId = plmnId,
            rac = null,
            tac = tac,
            lac = null,
            rsrq = networkQuantity.toIntOrNull(),
            rsrp = networkQuality.toIntOrNull(),
            rscp = null,
            ecNo = null,
            qualityOfService = calculateQualityOfService(networkQuality) // Replace with actual situation
        ))
    }

    private fun updateWcdmaCellInfo(cellInfo: CellInfoWcdma) {
        val cellIdentityWcdma = cellInfo.cellIdentity
        val cellSignalStrengthWcdma = cellInfo.cellSignalStrength

        val plmnId = "${cellIdentityWcdma.mccString}${cellIdentityWcdma.mncString}"
        val lac = cellIdentityWcdma.lac?.toString()
        val cellId = cellIdentityWcdma.cid?.toString()
        val networkType = "3G (WCDMA)"
        val networkQuantity = cellSignalStrengthWcdma.dbm.toString()
        val networkQuality = cellSignalStrengthWcdma.dbm.toString()

        tvPLMNID.text = "PLMN-ID: $plmnId"
        tvLac.text = "LAC: $lac"
        tvCellId.text = "Cell ID: $cellId"
        tvNetworkType.text = networkType
        tvNetworkQuantity.text = "RSCP: $networkQuantity dbm"
        tvNetworkQuality.text = "Ec/No: $networkQuality"

        storeNetworkInfo(NetworkInfo(
            eventTime = System.currentTimeMillis(),
            latitude = tvLatitude.text.toString().substringAfter(": ").toDouble(),
            longitude = tvLongitude.text.toString().substringAfter(": ").toDouble(),
            cellTechnology = networkType,
            cellId = cellId,
            plmnId = plmnId,
            rac = null,
            tac = null,
            lac = lac,
            rsrq = null,
            rsrp = null,
            rscp = networkQuantity.toIntOrNull(),
            ecNo = networkQuality.toIntOrNull(),
            qualityOfService = calculateQualityOfService(networkQuality) // Replace with actual situation
        ))
    }

    private fun updateGsmCellInfo(cellInfo: CellInfoGsm) {
        val cellIdentityGsm = cellInfo.cellIdentity
        val cellSignalStrengthGsm = cellInfo.cellSignalStrength

        val plmnId = "${cellIdentityGsm.mccString}${cellIdentityGsm.mncString}"
        val lac = cellIdentityGsm.lac?.toString()
        val cellId = cellIdentityGsm.cid?.toString()
        val networkType = "2G (GSM)"
        val networkQuantity = cellSignalStrengthGsm.dbm.toString()

        tvPLMNID.text = "PLMN-ID: $plmnId"
        tvLac.text = "LAC: $lac"
        tvCellId.text = "Cell ID: $cellId"
        tvNetworkType.text = networkType
        tvNetworkQuantity.text = "RSSI: $networkQuantity"
        tvNetworkQuality.text = "Signal Strength: $networkQuantity dBm"

        storeNetworkInfo(NetworkInfo(
            eventTime = System.currentTimeMillis(),
            latitude = tvLatitude.text.toString().substringAfter(": ").toDouble(),
            longitude = tvLongitude.text.toString().substringAfter(": ").toDouble(),
            cellTechnology = networkType,
            cellId = cellId,
            plmnId = plmnId,
            rac = null,
            tac = null,
            lac = lac,
            rsrq = null,
            rsrp = null,
            rscp = null,
            ecNo = null,
            qualityOfService = calculateQualityOfService(networkQuantity) // Replace with actual situation
        ))
    }

    private fun calculateQualityOfService(signalStrength: String): String {
        return when {
            signalStrength.toIntOrNull() != null -> {
                val strength = signalStrength.toInt()
                when {
                    strength >= -80 -> "Excellent"
                    strength >= -95 -> "Good"
                    strength >= -110 -> "Fair"
                    strength >= -125 -> "Poor"
                    else -> "Very Poor"
                }
            }
            else -> "Unknown"
        }
    }

    private fun storeNetworkInfo(networkInfo: NetworkInfo) {
        lifecycleScope.launch {
            database.networkInfoDao().insert(networkInfo)
        }
    }

    override fun onLocationChanged(location: Location) {
        tvLatitude.text = "Latitude: ${location.latitude}"
        tvLongitude.text = "Longitude: ${location.longitude}"
        eventTime.text = "Event Time: ${System.currentTimeMillis()}"
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            startLocationUpdates()
            displayNetworkInfo()
        }
    }
}
