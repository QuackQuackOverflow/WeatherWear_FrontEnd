package com.example.weatherwear

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*

class MainActivity : AppCompatActivity() {

    private lateinit var customDialog: ClothingPopup
    private lateinit var reviewPopup: ReviewPopup

    // FusedLocationProviderClient는 위치 서비스에 접근하는 객체
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    // 위치 권한 요청 코드
    private val REQUEST_LOCATION_PERMISSION = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 위치 클라이언트 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // 위치 요청 설정 (5초마다 업데이트)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setMinUpdateIntervalMillis(5000) // 최소 업데이트 간격을 5초로 설정
            .build()

        // 위치 콜백 설정
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location: Location? = locationResult.lastLocation
                location?.let {
                    val latitude = it.latitude
                    val longitude = it.longitude
                    // 위치 정보를 Toast로 표시
                    Toast.makeText(
                        this@MainActivity,
                        "Updated Location: Latitude = $latitude, Longitude = $longitude",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // 위치 권한 요청
        requestLocationPermission()

        // 현재 위치 버튼 클릭 시 위치 정보를 가져와서 ShowLocation 액티비티로 전달
        findViewById<Button>(R.id.checkCurrentLocation).setOnClickListener {
            getCurrentLocation()
        }
    }

    // 위치 권한 요청 메서드
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
            startLocationUpdates() // 권한이 이미 있으면 위치 업데이트 시작
        }
    }

    // 권한 요청 응답 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 5초마다 위치 업데이트 시작
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                mainLooper
            )
        }
    }

    // 위치 업데이트 중지
    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    // 마지막 알려진 위치 가져오기 (버튼 클릭 시 1회 호출용)
    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val latitude = it.latitude
                    val longitude = it.longitude
                    // 위치 정보를 ShowLocation 액티비티로 전달
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
    }

    private fun showClothingPopup() {
        customDialog = ClothingPopup(this)
        customDialog.show()
    }

    private fun showReviewPopup() {
        reviewPopup = ReviewPopup(this)
        reviewPopup.show()
    }
}
