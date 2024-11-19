package com.example.apitest

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.weatherwear.R
import com.example.weatherwear.data.api.ApiService
import com.example.weatherwear.data.model.*
import com.example.weatherwear.util.RetrofitInstance
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class APITest2Activity : AppCompatActivity() {

    // UI 요소
    private lateinit var resultTextView: TextView
    private lateinit var getRegionButton: Button
    private lateinit var getWeatherButton: Button
    private lateinit var getClothingSetButton: Button

    // 위치 서비스
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // 위치 권한 요청 코드
    private val REQUEST_LOCATION_PERMISSION = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apitest2)

        // UI 초기화
        resultTextView = findViewById(R.id.textViewResult)
        getRegionButton = findViewById(R.id.buttonGetRegion)
        getWeatherButton = findViewById(R.id.buttonGetWeather)
        getClothingSetButton = findViewById(R.id.buttonGetClothingSet)

        // 위치 서비스 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // 위치 권한 요청
        requestLocationPermission()

        // 버튼 클릭 이벤트
        getRegionButton.setOnClickListener {
            getCurrentLocation { latitude, longitude ->
                val (nx, ny) = convertToGrid(latitude, longitude)
                sendRegionNameRequest(nx, ny)
            }
        }

        getWeatherButton.setOnClickListener {
            getCurrentLocation { latitude, longitude ->
                val (nx, ny) = convertToGrid(latitude, longitude)
                sendWeatherRequest(nx, ny)
            }
        }

        getClothingSetButton.setOnClickListener {
            getCurrentLocation { latitude, longitude ->
                val (nx, ny) = convertToGrid(latitude, longitude)
                sendClothingSetRequest(nx, ny)
            }
        }
    }

    // 위치 권한 요청 함수
    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    // 위치 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 현재 위치 가져오기
    private fun getCurrentLocation(callback: (latitude: Double, longitude: Double) -> Unit) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                callback(it.latitude, it.longitude)
            } ?: run {
                Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // NX, NY 좌표로 변환하는 함수
    private fun convertToGrid(lat: Double, lon: Double): Pair<Int, Int> {
        val RE = 6371.00877
        val GRID = 5.0
        val SLAT1 = 30.0
        val SLAT2 = 60.0
        val OLON = 126.0
        val OLAT = 38.0
        val XO = 43.0
        val YO = 136.0

        val DEGRAD = Math.PI / 180.0

        val re = RE / GRID
        val slat1 = SLAT1 * DEGRAD
        val slat2 = SLAT2 * DEGRAD
        val olon = OLON * DEGRAD
        val olat = OLAT * DEGRAD

        var sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5)
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn)
        var sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5)
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn
        var ro = Math.tan(Math.PI * 0.25 + olat * 0.5)
        ro = re * sf / Math.pow(ro, sn)
        var ra = Math.tan(Math.PI * 0.25 + lat * DEGRAD * 0.5)
        ra = re * sf / Math.pow(ra, sn)
        var theta = lon * DEGRAD - olon
        if (theta > Math.PI) theta -= 2.0 * Math.PI
        if (theta < -Math.PI) theta += 2.0 * Math.PI
        theta *= sn

        val x = (ra * Math.sin(theta) + XO).toInt()
        val y = (ro - ra * Math.cos(theta) + YO).toInt()
        return Pair(x, y)
    }

    // Region 이름 요청
    private fun sendRegionNameRequest(nx: Int, ny: Int) {
        val apiService = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)
        val gpsReport = GPSreport(nx, ny)

        CoroutineScope(Dispatchers.IO).launch {
            val response = apiService.getRegionName(gpsReport)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val region = response.body()
                    resultTextView.text = "지역 이름: ${region?: "Unknown"}"
                } else {
                    Toast.makeText(this@APITest2Activity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // 날씨 정보 요청
    private fun sendWeatherRequest(nx: Int, ny: Int) {
        val apiService = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)
        val gpsReport = GPSreport(nx, ny)

        CoroutineScope(Dispatchers.IO).launch {
            val response = apiService.getWeather(gpsReport)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val weather = response.body()
                    resultTextView.text = "날씨 정보:\n" +
                            "기온: ${weather?.temp ?: "Unknown"}\n" +
                            "강수 확률: ${weather?.probabilityOfRain ?: "Unknown"}%"
                } else {
                    Toast.makeText(this@APITest2Activity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // 의상 세트 요청
    private fun sendClothingSetRequest(nx: Int, ny: Int) {
        val apiService = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)
        val gpsReport = GPSreport(nx, ny)

        CoroutineScope(Dispatchers.IO).launch {
            val response = apiService.getClothingSet(gpsReport)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val clothingSet = response.body()
                    val sb = StringBuilder()
                    sb.append("의상 세트:\n")
                    sb.append("Set ID: ${clothingSet?.id ?: "Unknown"}\n")
                    clothingSet?.recommencdedClothings?.forEach { clothing ->
                        sb.append("${clothing.type}: ${clothing.name}\n")
                    }
                    resultTextView.text = sb.toString()
                } else {
                    Toast.makeText(this@APITest2Activity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
