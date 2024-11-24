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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//class APITestActivity : AppCompatActivity() {
//
//    // UI 요소
//    private lateinit var resultTextView: TextView
//    private lateinit var sendLocationButton: Button
//
//    // 위치 서비스
//    private lateinit var fusedLocationClient: FusedLocationProviderClient
//
//    // 위치 권한 요청 코드
//    private val REQUEST_LOCATION_PERMISSION = 100
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_apitest)
//
//        // UI 초기화
//        resultTextView = findViewById(R.id.textViewResult)
//        sendLocationButton = findViewById(R.id.buttonSendLocation)
//
//        // 위치 서비스 초기화
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//
//        // 위치 권한 요청
//        requestLocationPermission()
//
//        // 버튼 클릭 시 NX, NY 좌표 계산 후 전송
//        sendLocationButton.setOnClickListener {
//            getCurrentLocation { latitude, longitude ->
//                val (nx, ny) = convertToGrid(latitude, longitude)
//                sendLocationToBackend(nx, ny)
//            }
//        }
//    }
//
//    // 위치 권한 요청 함수
//    private fun requestLocationPermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                REQUEST_LOCATION_PERMISSION
//            )
//        }
//    }
//
//    // 위치 권한 요청 결과 처리
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_LOCATION_PERMISSION) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    // 현재 위치 가져오기
//    private fun getCurrentLocation(callback: (latitude: Double, longitude: Double) -> Unit) {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            requestLocationPermission()
//            return
//        }
//
//        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
//            location?.let {
//                callback(it.latitude, it.longitude)
//            } ?: run {
//                Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    // NX, NY 좌표로 변환하는 함수
//    private fun convertToGrid(lat: Double, lon: Double): Pair<Int, Int> {
//        val RE = 6371.00877 // 지구 반경(km)
//        val GRID = 5.0 // 격자 간격(km)
//        val SLAT1 = 30.0 // 표준 위도1(degree)
//        val SLAT2 = 60.0 // 표준 위도2(degree)
//        val OLON = 126.0 // 기준점 경도(degree)
//        val OLAT = 38.0 // 기준점 위도(degree)
//        val XO = 43.0 // 기준점 X좌표(GRID)
//        val YO = 136.0 // 기준점 Y좌표(GRID)
//
//        val DEGRAD = Math.PI / 180.0 // degree radian 변환
//        val RADDEG = 180.0 / Math.PI // radian degree 변환
//
//        val re = RE / GRID // 격자 단위로 변환된 지구 반경
//        val slat1 = SLAT1 * DEGRAD // radian으로 변환된 표준 위도1
//        val slat2 = SLAT2 * DEGRAD // radian으로 변환된 표준 위도2
//        val olon = OLON * DEGRAD // radian으로 변환된 기준점 경도
//        val olat = OLAT * DEGRAD // radian으로 변환된 기준점 위도
//
//        var sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5) // 격자 변환에 필요한 sn 값
//        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn)
//        var sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5) // sf 값 계산
//        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn
//        var ro = Math.tan(Math.PI * 0.25 + olat * 0.5) // ro 값 계산
//        ro = re * sf / Math.pow(ro, sn)
//        var ra = Math.tan(Math.PI * 0.25 + lat * DEGRAD * 0.5) // ra 값 계산
//        ra = re * sf / Math.pow(ra, sn)
//        var theta = lon * DEGRAD - olon // theta 계산
//        if (theta > Math.PI) theta -= 2.0 * Math.PI
//        if (theta < -Math.PI) theta += 2.0 * Math.PI
//        theta *= sn
//
//        val x = (ra * Math.sin(theta) + XO).toInt() // 변환된 X 좌표를 정수형으로 변환함.
//        val y = (ro - ra * Math.cos(theta) + YO).toInt() // 변환된 Y 좌표를 정수형으로 변환함.
//        return Pair(x, y) // 변환된 정수형 X와 Y 좌표를 반환함.
//    }
//
//    // 서버 응답 데이터를 화면에 표시
//    private fun displayResponse(rwcResponse: RWCResponse?) {
//        rwcResponse?.let {
//            val sb = StringBuilder()
//
//            // Region 정보
//            sb.append("1. Region\n")
//            sb.append("지역 이름 : ${it.region ?: "Unknown"}\n\n")
//
//            // Weather 정보
//            sb.append("2. Weather\n")
//            sb.append("1시간 기온 : ${it.weather?.temp ?: "Unknown"} °C\n")
//            sb.append("강수 확률 : ${it.weather?.rainProbability ?: "Unknown"} %\n")
//            sb.append("강수량 : ${it.weather?.rainAmount ?: "Unknown"} mm\n")
//            sb.append("강수 형태 : ${it.weather?.rainType ?: "Unknown"}\n")
//            sb.append("하늘 상태 : ${it.weather?.skyCondition ?: "Unknown"}\n")
//            sb.append("최고 기온 : ${it.weather?.maxTemp ?: "Unknown"} °C\n")
//            sb.append("최저 기온 : ${it.weather?.minTemp ?: "Unknown"} °C\n")
//            sb.append("습도 : ${it.weather?.humid ?: "Unknown"} %\n")
//            sb.append("풍속 : ${it.weather?.windSpeed ?: "Unknown"} m/s\n\n")
//
//            // ClothingSet 정보
//            sb.append("3. ClothingSet\n")
//            sb.append("SetID : ${it.clothingSet.id ?: "Unknown"}\n")
//            it.clothingSet.recommendedClothings?.forEach { clothing ->
//                sb.append("${clothing.type} : ${clothing.name}\n")
//            }
//
//            // 결과 표시
//            resultTextView.text = sb.toString()
//        }
//    }
//}
