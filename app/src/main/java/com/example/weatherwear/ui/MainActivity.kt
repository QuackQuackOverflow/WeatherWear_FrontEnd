// MainActivity.kt

package com.example.weatherwear.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weatherwear.R
import com.google.android.gms.location.*

class MainActivity : AppCompatActivity() {

    //SwipeRefreshLayout(스와이프하여 새로고침을 위한 레이아웃)에 대한 액션처리를 위해 추가
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    // 위치 서비스를 제공하는 클라이언트
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    // 위치 권한 요청 코드
    private val REQUEST_LOCATION_PERMISSION = 100

    // MainActivity 초기화 함수
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // 메인 화면 레이아웃 설정

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this) // 위치 서비스 초기화

        // 위치 권한 요청 전에 NX, NY 좌표를 설정하는 함수 호출
        requestLocationPermission()

        // 위치 정보를 버튼에 표시하는 함수 호출을 위치 서비스 초기화 이후로 이동함.
        setNxNyToLocationButton()

        // 시간대별 날씨 레이아웃을 생성하는 함수 호출
        generateTimeWeatherLayout()

        // 현재 위치 버튼을 클릭했을 때 위치 정보를 요청하도록 설정함.
        findViewById<Button>(R.id.checkCurrentLocation).setOnClickListener {
            getCurrentLocation()
        }

        // 리뷰 쓰기 버튼을 누르면 ReviewPopup이 나오도록
        findViewById<Button>(R.id.navigationBarBtn2).setOnClickListener {
            showReviewPopup()
        }

        // 상세 날씨 화면으로 이동하는 버튼 클릭 이벤트 설정
        findViewById<Button>(R.id.currentTempView_main).setOnClickListener {
            val intent = Intent(this, DetailedWeatherActivity::class.java)
            startActivity(intent)
        }

        //스와이프하면 위치정보를 갱신하여 새로고침
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            refreshLocation() // 새로고침 시 위치 갱신
        }

        // 설정 버튼을 클릭했을 떄
        findViewById<Button>(R.id.navigationBarBtn3).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    // 시간대별 날씨 레이아웃 생성 함수
    private fun generateTimeWeatherLayout() {
        val timeWeatherContainer = findViewById<LinearLayout>(R.id.hourlyWeatherScrollView_main)
        val defaultIcon = R.drawable.baseline_wb_sunny_24_30dp_with_outline // 아이콘 설정
        val defaultTemperature = "0°C" // 기본 온도 설정

        for (hour in 0..23) {
            val hourLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL // 세로 방향 레이아웃
                setPadding(10, 0, 10, 0) // 패딩 설정
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val hourText = TextView(this).apply {
                text = "${hour}시" // 시간 텍스트 설정
                textSize = 20f
                gravity = Gravity.CENTER
            }

            val weatherIcon = ImageView(this).apply {
                setImageResource(defaultIcon) // 기본 아이콘 설정
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val tempText = TextView(this).apply {
                text = defaultTemperature // 온도 텍스트 설정
                textSize = 20f
                gravity = Gravity.CENTER
            }

            // 각 요소를 시간별 레이아웃에 추가
            hourLayout.addView(hourText)
            hourLayout.addView(weatherIcon)
            hourLayout.addView(tempText)

            // 시간대별 레이아웃을 스크롤 뷰 컨테이너에 추가
            timeWeatherContainer.addView(hourLayout)
        }
    }

    // NX, NY 좌표를 checkCurrentLocation_main 버튼의 텍스트에 표시하는 함수임.
    private fun setNxNyToLocationButton() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission() // 권한이 없을 경우 권한 요청을 수행함.
            return
        }
        // 위치 정보를 가져와 NX, NY 좌표로 변환 후 버튼에 표시함.
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val (nx, ny) = convertToGrid(it.latitude, it.longitude) // 위도와 경도를 NX, NY로 변환함.
                findViewById<Button>(R.id.checkCurrentLocation_main).text = "NX: %d, NY: %d".format(nx, ny) // 정수형 좌표를 버튼 텍스트에 반영함.
            } ?: run {
                Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show() // 위치 정보를 가져오지 못했을 경우 메시지를 표시함.
            }
        }
    }

    // 위도와 경도를 NX, NY 격자 좌표로 변환하는 함수
    private fun convertToGrid(lat: Double, lon: Double): Pair<Int, Int> {
        val RE = 6371.00877 // 지구 반경(km)
        val GRID = 5.0 // 격자 간격(km)
        val SLAT1 = 30.0 // 표준 위도1(degree)
        val SLAT2 = 60.0 // 표준 위도2(degree)
        val OLON = 126.0 // 기준점 경도(degree)
        val OLAT = 38.0 // 기준점 위도(degree)
        val XO = 43.0 // 기준점 X좌표(GRID)
        val YO = 136.0 // 기준점 Y좌표(GRID)

        val DEGRAD = Math.PI / 180.0 // degree radian 변환
        val RADDEG = 180.0 / Math.PI // radian degree 변환

        val re = RE / GRID // 격자 단위로 변환된 지구 반경
        val slat1 = SLAT1 * DEGRAD // radian으로 변환된 표준 위도1
        val slat2 = SLAT2 * DEGRAD // radian으로 변환된 표준 위도2
        val olon = OLON * DEGRAD // radian으로 변환된 기준점 경도
        val olat = OLAT * DEGRAD // radian으로 변환된 기준점 위도

        var sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5) // 격자 변환에 필요한 sn 값
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn)
        var sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5) // sf 값 계산
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn
        var ro = Math.tan(Math.PI * 0.25 + olat * 0.5) // ro 값 계산
        ro = re * sf / Math.pow(ro, sn)
        var ra = Math.tan(Math.PI * 0.25 + lat * DEGRAD * 0.5) // ra 값 계산
        ra = re * sf / Math.pow(ra, sn)
        var theta = lon * DEGRAD - olon // theta 계산
        if (theta > Math.PI) theta -= 2.0 * Math.PI
        if (theta < -Math.PI) theta += 2.0 * Math.PI
        theta *= sn

        val x = (ra * Math.sin(theta) + XO).toInt() // 변환된 X 좌표를 정수형으로 변환함.
        val y = (ro - ra * Math.cos(theta) + YO).toInt() // 변환된 Y 좌표를 정수형으로 변환함.
        return Pair(x, y) // 변환된 정수형 X와 Y 좌표를 반환함.
    }

    // 위치 권한을 요청하는 함수
    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION // 권한 요청 코드
            )
        } else {
            getCurrentLocation() // 권한이 이미 있으면 현재 위치를 가져옴.
        }
    }

    // 위치 권한 요청 결과를 처리하는 함수
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) { // 요청 코드가 일치하는지 확인함.
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation() // 권한이 승인되었을 경우 현재 위치를 가져옴.
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show() // 권한이 거부되었을 경우 메시지를 표시함.
            }
        }
    }

    // 현재 위치를 가져오는 함수
    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission() // 권한이 없을 경우 권한 요청을 수행함.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val (nx, ny) = convertToGrid(it.latitude, it.longitude) // 위도와 경도를 NX, NY로 변환함.
                Toast.makeText(this, "NX: %d, NY: %d".format(nx, ny), Toast.LENGTH_SHORT).show() // 정수형 좌표를 토스트 메시지로 표시함.
            } ?: run {
                Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show() // 위치 정보를 가져오지 못했을 경우 메시지를 표시함.
            }
        }
    }

    //리뷰 팝업 나오게 하는 함수
    private fun showReviewPopup() {
        val reviewPopup = ReviewPopup(this)
        reviewPopup.show()
    }

    // 위치값을 새로 업데이트하여 MainActivity를 다시 시작
    private fun refreshLocation() {
        // 현재 액티비티를 종료하고 새로 시작하여 위치 정보 갱신
        finish() // 현재 액티비티 종료
        startActivity(intent) // MainActivity를 다시 시작하여 새로운 위치 정보를 반영
    }


}



