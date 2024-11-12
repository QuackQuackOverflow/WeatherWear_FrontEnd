package com.example.weatherwear.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.weatherwear.R
import com.google.android.gms.location.*

class MainActivity : AppCompatActivity() {

    private lateinit var customDialog: ClothingPopup
    private lateinit var reviewPopup: ReviewPopup
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val REQUEST_LOCATION_PERMISSION = 100

    override fun onCreate(savedInstanceState: Bundle?) {

        //

        //

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // FusedLocationProviderClient 초기화 (위치 서비스 접근)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // 위치 권한 요청
        requestLocationPermission()

        // 현재 위치 버튼 클릭 시 위치 정보 요청
        findViewById<Button>(R.id.checkCurrentLocation).setOnClickListener {
            getCurrentLocation()
        }

        // 시간대별 날씨 레이아웃 생성
        generateTimeWeatherLayout()

        // currentTempView 버튼 클릭 시 DetailedWeatherActivity로 이동
        findViewById<Button>(R.id.currentTempView_main).setOnClickListener {
            val intent = Intent(this, DetailedWeatherActivity::class.java)
            startActivity(intent)
        }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        } else {
            getCurrentLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            return
        }

        // 위치 정보를 가져오기
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val latitude = it.latitude
                val longitude = it.longitude

                Toast.makeText(
                    this,
                    "Location: Latitude = $latitude, Longitude = $longitude",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(this, ShowLocation::class.java).apply {
                    putExtra("latitude", latitude)
                    putExtra("longitude", longitude)
                }
                startActivity(intent)
            } ?: run {
                Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun showClothingPopup() {
        customDialog = ClothingPopup(this)
        customDialog.show()
    }

    private fun showReviewPopup() {
        reviewPopup = ReviewPopup(this)
        reviewPopup.show()
    }

    // 시간대별 날씨 레이아웃 생성 함수
    private fun generateTimeWeatherLayout() {
        val timeWeatherContainer = findViewById<LinearLayout>(R.id.hourlyWeatherScrollView_main)
        val defaultIcon = R.drawable.baseline_sunny_24_40dp_with_outline
        val defaultTemperature = "0°C"

        for (hour in 0..23) {
            val hourLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(10, 0, 10, 0)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val hourText = TextView(this).apply {
                text = "${hour}시"
                textSize = 20f
                gravity = Gravity.CENTER
            }

            val weatherIcon = ImageView(this).apply {
                setImageResource(defaultIcon)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val tempText = TextView(this).apply {
                text = defaultTemperature
                textSize = 20f
                gravity = Gravity.CENTER
            }

            hourLayout.addView(hourText)
            hourLayout.addView(weatherIcon)
            hourLayout.addView(tempText)
            timeWeatherContainer.addView(hourLayout)
        }
    }
}
