package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.myapplication.ui.theme.MyApplicationTheme

class Detail : ComponentActivity() {
    private lateinit var btnLTE: Button
    private lateinit var btnWCDMA: Button
    private  lateinit var btnBOTH:Button
    private  lateinit var btnLOG:Button

    private var batchId: Int = 0
    private var quality:String = ""
    private var technology:String = ""

    private lateinit var text1: TextView
    private lateinit var text2: TextView
    private lateinit var text3: TextView
    private lateinit var bottomText1: TextView
    private lateinit var bottomText2: TextView
    private lateinit var bottomText3: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        btnLTE = findViewById(R.id.fab_lte)
        btnWCDMA = findViewById(R.id.fab_wcdma)
        btnBOTH = findViewById(R.id.fab_both)
        btnLOG = findViewById(R.id.btLogs)
        batchId = intent.getIntExtra("groupId", 0)
        quality = intent.getStringExtra("quality") ?: ""
        technology = intent.getStringExtra("technology") ?: ""
        val qualityValue = quality.split(":").lastOrNull() ?: ""
        val technologyValue = technology.split(":").lastOrNull() ?: ""

        text1 = findViewById(R.id.text1)
        text2 = findViewById(R.id.text2)
        text3 = findViewById(R.id.text3)
        bottomText1 = findViewById(R.id.bottomText1)
        bottomText2 = findViewById(R.id.bottomText2)
        bottomText3 = findViewById(R.id.bottomText3)
        bottomText1.text = batchId.toString()
        bottomText2.text = qualityValue;
        bottomText3.text = technologyValue;


        val imageView = findViewById<ImageView>(R.id.statusPic)
        val qualityValueTrimmed = qualityValue.trim()

        if(qualityValueTrimmed == "Good" || qualityValueTrimmed == "Excellent")
        {
            imageView.setImageResource(R.drawable.good_signal)
            bottomText2.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
        }else{
            imageView.setImageResource(R.drawable.poor_signal)
            bottomText2.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))

        }

        btnLOG.setOnClickListener {
            val intent = Intent(this, BatchDetailActivity::class.java)
            intent.putExtra("BATCH_ID", batchId)
            startActivity(intent)
        }

        btnLTE.setOnClickListener {
            navigateToNextPage("LTE")
        }

        btnWCDMA.setOnClickListener {
            navigateToNextPage("WCDMA")
        }

        btnBOTH.setOnClickListener {
            navigateToNextPage("Both")
        }
    }

    private fun navigateToNextPage(technology: String) {
        val intent = Intent(this, MapsActivity::class.java)
        intent.putExtra("BATCH_ID", batchId)
        intent.putExtra("TECHNOLOGY", technology)
        startActivity(intent)
    }
}



