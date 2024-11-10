package com.example.weatherwear.ui

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
import com.example.weatherwear.R
import com.google.android.gms.location.*

class MainActivity : AppCompatActivity() {

    private lateinit var customDialog: ClothingPopup
    private lateinit var reviewPopup: ReviewPopup

    // FusedLocationProviderClient는 위치 서비스에 접근하는 객체
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // 위치 권한 요청 코드
    private val REQUEST_LOCATION_PERMISSION = 100

    // 화면 생성
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 위치 클라이언트 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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
            getCurrentLocation() // 권한이 이미 있으면 한 번만 위치를 가져옵니다.
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
                getCurrentLocation() // 권한이 허용된 경우 위치를 가져옵니다.
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 현재 위치를 가져오는 함수
    private fun getCurrentLocation() {
        // 위치 접근 권한이 허용되었는지 확인
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 있는 경우, 마지막으로 알려진 위치를 가져옴
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    // 위치 정보를 성공적으로 가져온 경우
                    val latitude = it.latitude  // 위도 값을 변수에 저장
                    val longitude = it.longitude  // 경도 값을 변수에 저장

                    // 위치 정보를 Toast로 표시
                    Toast.makeText(
                        this,
                        "Location: Latitude = $latitude, Longitude = $longitude",
                        Toast.LENGTH_SHORT
                    ).show()

                    // ShowLocation 액티비티로 위치 정보를 전달하기 위한 인텐트 생성
                    val intent = Intent(this, ShowLocation::class.java).apply {
                        putExtra("latitude", latitude)  // 위도 정보를 인텐트에 추가
                        putExtra("longitude", longitude)  // 경도 정보를 인텐트에 추가
                    }
                    // ShowLocation 액티비티 시작 (위치 정보를 전달)
                    startActivity(intent)
                } ?: run {
                    // 위치 정보를 가져올 수 없는 경우 사용자에게 알림 표시
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
