package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class detailList : AppCompatActivity() {
    private lateinit var database: AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_list)
        database = AppDatabase.getDatabase(this)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadDataFromDatabase(recyclerView)
    }



    private fun loadDataFromDatabase(recyclerView: RecyclerView) {
        lifecycleScope.launch(Dispatchers.IO) {
            val networkInfoList = database.networkInfoDao().getTopBatchIdsWithDetails_50()

            // Process the data to create Group objects
            val groups = networkInfoList.map { info ->
                val quality = calculateMostFrequentQuality(info.qualities)
                val cellTech = calculateMostFrequentTechnology(info.technologies)
                MainActivity.Group(
                    info.batchId,
                    "Batch ID: ${info.batchId}",
                    "Quality: $quality",
                    "Technology: $cellTech"
                )
            }

            withContext(Dispatchers.Main) {
                val adapter = GroupAdapter(groups)
                recyclerView.adapter = adapter
            }
        }
    }


    private fun calculateMostFrequentQuality(concatenatedQualities: String): String {
        val qualities = concatenatedQualities.split(",")
        return qualities.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: "Unknown"
    }

    private fun calculateMostFrequentTechnology(concatenatedTechnologies: String): String {
        val technologies = concatenatedTechnologies.split(",")
        return technologies.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: "Unknown"
    }
}