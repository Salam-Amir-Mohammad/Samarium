package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DisplayActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NetworkInfoAdapter
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = NetworkInfoAdapter()
        recyclerView.adapter = adapter

        database = AppDatabase.getDatabase(this)

        loadData()
    }

    private fun loadData() {
        GlobalScope.launch(Dispatchers.IO) {
            val networkInfoList = database.networkInfoDao().getAll()
            withContext(Dispatchers.Main) {
                adapter.setData(networkInfoList)
            }
        }
    }
}
