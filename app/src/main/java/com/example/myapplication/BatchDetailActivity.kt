package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.database.AppDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BatchDetailActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NetworkInfoAdapter
    private lateinit var database: AppDatabase
    private var batchId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_batch_detail)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = NetworkInfoAdapter()
        recyclerView.adapter = adapter

        database = AppDatabase.getDatabase(this)
        batchId = intent.getIntExtra("BATCH_ID", 0)

        loadBatchDetails(batchId)


    }
    private fun loadBatchDetails(batchId: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            val networkInfos = database.networkInfoDao().getNetworkInfosByBatchId(batchId)
            withContext(Dispatchers.Main) {
                adapter.setData(networkInfos)
            }
        }
    }

}
