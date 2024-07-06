package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.database.NetworkInfo
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var database: AppDatabase
    private var batchId: Int = 0
    private var technology: String = "Both"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        database = AppDatabase.getDatabase(this)
        batchId = intent.getIntExtra("BATCH_ID", 0)
        technology = intent.getStringExtra("TECHNOLOGY") ?: "Both"

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }



    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
        }

        val tehran = LatLng(35.6892, 51.3890)
        map.addMarker(MarkerOptions().position(tehran).title("Tehran"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(tehran, 12f))


        GlobalScope.launch(Dispatchers.IO) {
            val networkInfos = database.networkInfoDao().getNetworkInfosByBatchId(batchId)
            withContext(Dispatchers.Main) {
                displayNetworkInfoOnMap(networkInfos)
            }
        }


//        GlobalScope.launch(Dispatchers.IO) {
//            val networkInfos = database.networkInfoDao().getNetworkInfosByBatchId(batchId)
//            withContext(Dispatchers.Main) {
//                // Display circles on the map for each networkInfo
//                var x = 0
//                for (networkInfo in networkInfos) {
//                    println(x)
//                    x+=1
//                    val location = LatLng(networkInfo.latitude, networkInfo.longitude)
//                    map.addCircle(
//                        CircleOptions()
//                            .center(location)
//                            .radius(2.0) // شعاع دایره به متر
//                            .strokeColor(0xFFFF0000.toInt()) // رنگ خط دایره (قرمز)
//                            .fillColor(0x30FF0000) // رنگ داخلی دایره (قرمز شفاف)
//                            .strokeWidth(2f) // عرض خط دایره
//                    )
//                }
//
//                // Move camera to the first location (if available)
//                if (networkInfos.isNotEmpty()) {
//                    val firstLocation = LatLng(networkInfos[0].latitude, networkInfos[0].longitude)
//                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 19f))
//                }
//            }
//        }
    }

    private fun displayNetworkInfoOnMap(networkInfos: List<NetworkInfo>) {
        var color = when (technology) {
            "LTE" -> 0xFF800080.toInt() // بنفش
            "WCDMA" -> 0xFF0000FF.toInt() // آبی
            else -> 0xFF008000.toInt() // سبز
        }

        val filteredNetworkInfos = when (technology) {
            "LTE" -> networkInfos.filter { it.cellTechnology == "LTE" }
            "WCDMA" -> networkInfos.filter { it.cellTechnology == "WCDMA" }
            else -> networkInfos
        }

        for (networkInfo in filteredNetworkInfos) {
            val location = LatLng(networkInfo.latitude, networkInfo.longitude)
            val alpha = getAlphaForQuality(networkInfo.qualityOfService)
            map.addCircle(
                CircleOptions()
                    .center(location)
                    .radius(2.0)
                    .strokeColor(color)
                    .fillColor(color and 0x00FFFFFF or (alpha shl 24))
                    .strokeWidth(2f)
            )
        }

        if (filteredNetworkInfos.isNotEmpty()) {
            val firstLocation =
                LatLng(filteredNetworkInfos[0].latitude, filteredNetworkInfos[0].longitude)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 19f))
        }

    }

    private fun getAlphaForQuality(quality: String): Int {
        return when (quality) {
            "Excellent" -> 255
            "Good" -> 200
            "Fair" -> 150
            "Poor" -> 100
            "Very Poor" -> 50
            else -> 100
        }
    }

}