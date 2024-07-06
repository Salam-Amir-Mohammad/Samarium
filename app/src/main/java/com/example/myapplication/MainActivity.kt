package com.example.myapplication

import ItemDecoration
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.telephony.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.database.NetworkInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.content.Intent
import android.widget.Button
import android.graphics.Color
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.anastr.speedviewlib.SpeedView
import com.github.anastr.speedviewlib.components.Style
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {


    private var lastSavedLocation: Location? = null

    private lateinit var telephonyManager: TelephonyManager
    private lateinit var locationManager: LocationManager
    private lateinit var database: AppDatabase
    private lateinit var btnStartStop: Button
    private lateinit var btnClearData: Button
    private var collectingData: Boolean = false
    private var job: Job? = null
    private var currentBatchId: Int = 0
    private lateinit var speedView: SpeedView
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        speedView = findViewById(R.id.speedView)

        speedView.minSpeed = -125f
        speedView.maxSpeed = -50f
        val sectionCount = 50

        // ساخت بخش‌ها
        speedView.makeSections(sectionCount, Color.CYAN, Style.BUTT)

        // تنظیم رنگ‌ها برای ایجاد یک طیف نرم از بنفش کمرنگ تا بنفش پررنگ
        for (i in 0 until sectionCount) {
            val fraction = i.toFloat() / (sectionCount - 1)
            val color = interpolateColor(Color.parseColor("#E6E6FA"), Color.parseColor("#4B0082"), fraction)
            speedView.sections[i].color = color
        }
        speedView.unit = ""
        // تنظیم مقدار سرعت (مثال)
        speedView.speedTo(50f)




        telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        database = AppDatabase.getDatabase(this)
        val btnDisplayData: Button = findViewById(R.id.btnDisplayData)
        btnClearData = findViewById(R.id.btnClearData)
        btnStartStop = findViewById(R.id.btnStartStop)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        btnStartStop.setOnClickListener {
            if (collectingData) {
                stopDataCollection()
                loadDataFromDatabase(recyclerView)
            } else {
                loadMaxBatchId()
                startDataCollection()
            }
        }

        btnDisplayData.setOnClickListener {
            val intent = Intent(this, detailList::class.java)
            startActivity(intent)
        }

        btnClearData.setOnClickListener {
            clearData()
            loadDataFromDatabase(recyclerView)
        }

        // Request permissions if necessary
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            getLocationAndNetworkInfo()
        }



        // Create an instance of ItemDecoration with the desired margin (in pixels)
        val marginInPixels = resources.getDimensionPixelSize(R.dimen.item_margin)
        val itemDecoration = ItemDecoration(this, marginInPixels)
        recyclerView.addItemDecoration(itemDecoration)

        loadDataFromDatabase(recyclerView)


    }
    private fun startDataCollection() {
        collectingData = true
        btnStartStop.text = "Stop"
        getLocationAndNetworkInfo()
    }
    data class Group(
        val id: Int,
        val boldText: String,
        val whiteText: String,
        val boldBottomText: String,

    )




    fun updateSpeed(newSpeed: Float) {
        GlobalScope.launch(Dispatchers.Main) {
            speedView.speedTo(newSpeed)
        }
    }



    private fun interpolateColor(colorStart: Int, colorEnd: Int, fraction: Float): Int {
        val startA = Color.alpha(colorStart)
        val startR = Color.red(colorStart)
        val startG = Color.green(colorStart)
        val startB = Color.blue(colorStart)

        val endA = Color.alpha(colorEnd)
        val endR = Color.red(colorEnd)
        val endG = Color.green(colorEnd)
        val endB = Color.blue(colorEnd)

        val a = (startA + (fraction * (endA - startA)).toInt())
        val r = (startR + (fraction * (endR - startR)).toInt())
        val g = (startG + (fraction * (endG - startG)).toInt())
        val b = (startB + (fraction * (endB - startB)).toInt())

        return Color.argb(a, r, g, b)
    }
    private fun stopDataCollection() {
        collectingData = false
        btnStartStop.text = "Start"
        job?.cancel()
        coroutineScope.coroutineContext.cancelChildren() // Cancel the ongoing job if exists
        job = null
    }

    private fun loadMaxBatchId() {
        lifecycleScope.launch(Dispatchers.IO) {
            val maxBatchId = database.networkInfoDao().getMaxBatchId() ?: 0
            currentBatchId = maxBatchId + 1
        }
    }




    fun clearData() {
        lifecycleScope.launch {
            // Clear data from database
            database.networkInfoDao().clearAll()

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

        job = coroutineScope.launch {
            while (collectingData) {
                val location = withContext(Dispatchers.Main) { getLastKnownLocation() }

                if (location != null) {
                    saveNetworkInfo(location)

                }
                //delay
                delay(5000) // Delay between each data collection (5 seconds)

                if (!collectingData) {
                    break
                }

            }
        }
    }

    private suspend  fun getLastKnownLocation(): Location? {
        return withContext(Dispatchers.Main) {
        var location: Location? = null
        // Get location
        if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }
            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 1f, locationListener)
        }
         location
        }
    }




    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
//            tvLatitude.text = "Latitude: ${location.latitude}"
//            tvLongitude.text = "Longitude: ${location.longitude}"
            updateEventTime()
            saveNetworkInfo(location)
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

        override fun onProviderEnabled(provider: String) {}

        override fun onProviderDisabled(provider: String) {}
    }




    private fun saveNetworkInfo(location: Location) {

        if (lastSavedLocation != null && location.latitude == lastSavedLocation!!.latitude && location.longitude == lastSavedLocation!!.longitude) {
            return  // Skip saving duplicate location
        }

        // Update last saved location
        lastSavedLocation = location
            coroutineScope.launch(Dispatchers.IO) {
            val networkInfoList = mutableListOf<NetworkInfo>()

            val cellInfoList = telephonyManager.allCellInfo

            for (cellInfo in cellInfoList) {
                val networkInfo = when (cellInfo) {
                    is CellInfoLte -> {
                        val cellIdentityLte = cellInfo.cellIdentity
                        val cellSignalStrengthLte = cellInfo.cellSignalStrength
                        GlobalScope.launch(Dispatchers.Main) {
                            updateSpeed(cellSignalStrengthLte.rsrp.toFloat())
                        }
                        NetworkInfo(
                            eventTime = System.currentTimeMillis(),
                            latitude = location.latitude,
                            longitude = location.longitude,
                            cellTechnology = "LTE",
                            cellId = cellIdentityLte.ci.toString(),
                            plmnId = "${cellIdentityLte.mccString}${cellIdentityLte.mncString}",
                            rac = null,
                            tac = cellIdentityLte.tac.toString(),
                            lac = null,
                            rsrp = cellSignalStrengthLte.rsrp,
                            rsrq = cellSignalStrengthLte.rsrq,
                            rscp = null,
                            ecNo = null,
                            qualityOfService = calculateQualityOfService(cellSignalStrengthLte.rsrp.toString(), "LTE"),
                            batchId = currentBatchId
                        )
                    }
                    is CellInfoWcdma -> {
                        val cellIdentityWcdma = cellInfo.cellIdentity
                        val cellSignalStrengthWcdma = cellInfo.cellSignalStrength
                        GlobalScope.launch(Dispatchers.Main) {
                            updateSpeed(cellSignalStrengthWcdma.dbm.toFloat())
                        }
                        NetworkInfo(
                            eventTime = System.currentTimeMillis(),
                            latitude = location.latitude,
                            longitude = location.longitude,
                            cellTechnology = "WCDMA",
                            cellId = cellIdentityWcdma.cid.toString(),
                            plmnId = "${cellIdentityWcdma.mccString}${cellIdentityWcdma.mncString}",
                            rac = null,
                            tac = null,
                            lac = cellIdentityWcdma.lac.toString(),
                            rsrp = null,
                            rsrq = null,
                            rscp = cellSignalStrengthWcdma.dbm,
                            ecNo = cellSignalStrengthWcdma.ecNo,
                            qualityOfService = calculateQualityOfService(cellSignalStrengthWcdma.dbm.toString(), "WCDMA"),
                            batchId = currentBatchId
                        )
                    }
                    else -> null
                }
                networkInfo?.let { networkInfoList.add(it) }
            }

            for (networkInfo in networkInfoList) {
                if(collectingData)
                {
                    val count = database.networkInfoDao().countByBatchAndLocation(
                        networkInfo.batchId,
                        networkInfo.latitude,
                        networkInfo.longitude
                    )
                    if(count == 0)
                    {
                        database.networkInfoDao().insert(networkInfo)
                    }
                }
            }
        }
    }




    private fun calculateQualityOfService(signalStrength: String, cellTechnology: String): String {
        return when (cellTechnology) {
            "LTE" -> calculateLTEQuality(signalStrength)
            "WCDMA" -> calculateWCDMAQuality(signalStrength)
            "GSM" -> calculateGSMQuality(signalStrength)
            else -> "Unknown"
        }
    }

    private fun calculateLTEQuality(signalStrength: String): String {
        return when {
            signalStrength.toIntOrNull() != null -> {
                val rsrp = signalStrength.toInt()
                when {
                    rsrp >= -80 -> "Excellent"
                    rsrp >= -95 -> "Good"
                    rsrp >= -110 -> "Fair"
                    rsrp >= -125 -> "Poor"
                    else -> "Very Poor"
                }
            }
            else -> "Unknown"
        }
    }

    private fun calculateWCDMAQuality(signalStrength: String): String {
        return when {
            signalStrength.toIntOrNull() != null -> {
                val rscp = signalStrength.toInt()
                when {
                    rscp >= -50 -> "Excellent"
                    rscp >= -75 -> "Good"
                    rscp >= -95 -> "Fair"
                    rscp >= -115 -> "Poor"
                    else -> "Very Poor"
                }
            }
            else -> "Unknown"
        }
    }

    private fun calculateGSMQuality(signalStrength: String): String {
        return when {
            signalStrength.toIntOrNull() != null -> {
                val rssi = signalStrength.toInt()
                when {
                    rssi >= -50 -> "Excellent"
                    rssi >= -75 -> "Good"
                    rssi >= -95 -> "Fair"
                    rssi >= -115 -> "Poor"
                    else -> "Very Poor"
                }
            }
            else -> "Unknown"
        }
    }




    private fun updateEventTime() {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentTime = sdf.format(Date())
        //tvEventTime.text = "Event Time: $currentTime"
    }

    private fun loadDataFromDatabase(recyclerView: RecyclerView) {
        lifecycleScope.launch(Dispatchers.IO) {
            val networkInfoList = database.networkInfoDao().getTopBatchIdsWithDetails()

            // Process the data to create Group objects
            val groups = networkInfoList.map { info ->
                val quality = calculateMostFrequentQuality(info.qualities)
                val cellTech = calculateMostFrequentTechnology(info.technologies)
                Group(info.batchId, "Batch ID: ${info.batchId}", "Quality: $quality", "Technology: $cellTech")
            }

            withContext(Dispatchers.Main) {
                val adapter = GroupAdapter(groups)
                recyclerView.adapter = adapter
            }
        }
    }
    data class BatchDetails(
        val batchId: Int,
        val qualities: String,
        val technologies: String
    )

    private fun calculateMostFrequentQuality(concatenatedQualities: String): String {
        val qualities = concatenatedQualities.split(",")
        return qualities.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: "Unknown"
    }

    private fun calculateMostFrequentTechnology(concatenatedTechnologies: String): String {
        val technologies = concatenatedTechnologies.split(",")
        return technologies.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: "Unknown"
    }

}