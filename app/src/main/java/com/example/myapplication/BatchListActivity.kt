package com.example.myapplication


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BatchListActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var llBatchList: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_batch_list)

        llBatchList = findViewById(R.id.llBatchList)
        database = AppDatabase.getDatabase(this)

        loadBatchIds()
    }

    private fun loadBatchIds() {
        lifecycleScope.launch(Dispatchers.IO) {
            val batchIds = database.networkInfoDao().getAllBatchIds()
            withContext(Dispatchers.Main) {
                displayBatchIds(batchIds)
            }
        }
    }

    private fun displayBatchIds(batchIds: List<Int>) {
        for (batchId in batchIds) {
            val button = Button(this).apply {
                text = "Batch $batchId"
                setOnClickListener {
                    val intent = Intent(this@BatchListActivity, BatchDetailActivity::class.java)
                    intent.putExtra("BATCH_ID", batchId)
                    startActivity(intent)
                }
            }
            llBatchList.addView(button)
        }
    }
}
