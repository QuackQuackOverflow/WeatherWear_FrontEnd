package com.example.weatherwear.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherwear.R

class ShowLocation : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_location)

        // Intent로 전달된 위도와 경도 가져오기
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        // TextView에 위도와 경도 표시
        findViewById<TextView>(R.id.latitudeView).text = "위도: $latitude"
        findViewById<TextView>(R.id.longitudeView).text = "경도: $longitude"
    }
}
