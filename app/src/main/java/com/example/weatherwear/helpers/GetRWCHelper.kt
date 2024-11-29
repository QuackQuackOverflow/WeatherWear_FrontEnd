package com.example.weatherwear.helpers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.weatherwear.data.api.ApiService
import com.example.weatherwear.data.api.getRWC
import com.example.weatherwear.data.model.RWCResponse
import com.example.weatherwear.ui.MainActivity
import com.example.weatherwear.util.RetrofitInstance
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GetRWCHelper(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient,
    private val apiService: ApiService = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)
) {

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 100
        private const val PREF_LOGIN_KEY = "LoginPrefs"
        private const val PREF_MEMBER_KEY = "member"
    }

    /**
     * GPS를 통해 NX, NY 값을 가져온 뒤 userType과 함께 백엔드에 요청
     */
    fun fetchRWCFromGPS(callback: (RWCResponse?) -> Unit) {
        // 위치 권한 확인
        if (!hasLocationPermission()) {
            requestLocationPermission()
            return
        }

        try {
            // 위치 정보 가져오기
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val (nx, ny) = convertToGrid(it.latitude, it.longitude)
                    fetchRWCWithParams(nx, ny, callback)
                } ?: run {
                    showToast("위치 정보를 불러올 수 없습니다")
                    callback(null)
                }
            }.addOnFailureListener {
                // 실패 핸들링
                showToast("위치 정보를 가져오는 데 실패했습니다: ${it.message}")
                callback(null)
            }
        } catch (e: SecurityException) {
            // 위치 권한과 관련된 보안 예외 처리
            showToast("위치 권한이 없어 위치 정보를 가져올 수 없습니다.")
            callback(null)
        }
    }

    /**
     * NX, NY 값과 userType을 사용하여 RWC 데이터를 가져오는 함수
     */
    fun fetchRWCWithParams(nx: Int, ny: Int, callback: (RWCResponse?) -> Unit) {
        val userType = loadUserTypeFromPreferences()
        if (userType == null) {
            showToast("사용자 유형(userType)을 불러올 수 없습니다.")
            callback(null)
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // ApiService의 확장 함수 getRWC 호출
                val rwcResponse = apiService.getRWC(nx, ny, userType)
                withContext(Dispatchers.Main) {
                    callback(rwcResponse)
                }
            } catch (e: Exception) {
                Log.e("GetRWCHelper", "예외 발생: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    showToast("네트워크 오류가 발생했습니다.")
                    callback(null)
                }
            }
        }
    }

    /**
     * SharedPreferences에서 userType 불러오기
     */
    private fun loadUserTypeFromPreferences(): String? {
        val sharedPreferences = context.getSharedPreferences(PREF_LOGIN_KEY, Context.MODE_PRIVATE)
        val userType = sharedPreferences.getString("userType", null)

        if (userType == null) {
            Log.e("GetRWCHelper", "userType이 SharedPreferences에 저장되지 않았습니다.")
        }

        return userType
    }


    /**
     * 위도와 경도를 NX, NY 격자 좌표로 변환하는 함수
     */
    private fun convertToGrid(lat: Double, lon: Double): Pair<Int, Int> {

        val RE = 6371.00877 // 지구 반경 (단위: km)
        val GRID = 5.0      // 격자 간격 (단위: km)
        val SLAT1 = 30.0    // 표준 위도 1 (단위: 도)
        val SLAT2 = 60.0    // 표준 위도 2 (단위: 도)
        val OLON = 126.0    // 기준점 경도 (단위: 도)
        val OLAT = 38.0     // 기준점 위도 (단위: 도)
        val XO = 43.0       // 기준점 X 좌표
        val YO = 136.0      // 기준점 Y 좌표

        // 각도를 라디안으로 변환하는 상수
        val DEGRAD = Math.PI / 180.0
        // 격자 크기를 기준으로 지구 반경을 재조정
        val re = RE / GRID
        // 표준 위도 1을 라디안으로 변환
        val slat1 = SLAT1 * DEGRAD
        // 표준 위도 2를 라디안으로 변환
        val slat2 = SLAT2 * DEGRAD
        // 기준점 경도를 라디안으로 변환
        val olon = OLON * DEGRAD
        // 기준점 위도를 라디안으로 변환
        val olat = OLAT * DEGRAD

        // 투영에서 사용하는 비율(sn) 계산
        var sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5)
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn)
        // 투영 계수(sf) 계산
        var sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5)
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn
        // 기준점 반경(ro) 계산
        var ro = Math.tan(Math.PI * 0.25 + olat * 0.5)
        ro = re * sf / Math.pow(ro, sn)

        // 입력 위도에 따른 반경(ra) 계산
        var ra = Math.tan(Math.PI * 0.25 + lat * DEGRAD * 0.5)
        ra = re * sf / Math.pow(ra, sn)
        // 입력 경도에 따른 각도(theta) 계산
        var theta = lon * DEGRAD - olon
        // 각도를 -π와 π 사이로 조정
        if (theta > Math.PI) theta -= 2.0 * Math.PI
        if (theta < -Math.PI) theta += 2.0 * Math.PI
        // 각도를 비율(sn)에 맞게 조정
        theta *= sn

        // 격자 X 좌표 계산
        val x = (ra * Math.sin(theta) + XO).toInt()
        // 격자 Y 좌표 계산
        val y = (ro - ra * Math.cos(theta) + YO).toInt()
        return Pair(x, y)
    }

    /**
     * 위치 권한 확인
     */
    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 위치 권한 요청
     */
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            context as androidx.appcompat.app.AppCompatActivity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION_PERMISSION
        )
    }

    /**
     * Toast 메시지를 간단히 출력
     */
    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
