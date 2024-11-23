package com.example.weatherwear.helpers

import RWCResponse
import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.weatherwear.data.api.ApiService
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
        private const val PREF_LOGIN_KEY = "loginPrefs"
        private const val PREF_MEMBER_KEY = "member"
    }

    /**
     * GPS를 통해 현재 NX, NY 값을 가져온 뒤 userType과 함께 getRWC를 호출
     */
    fun fetchRWCFromGPS(callback: (RWCResponse?) -> Unit) {
        if (!checkLocationPermission()) {
            requestLocationPermission()
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val (nx, ny) = convertToGrid(it.latitude, it.longitude)
                val userType = loadUserTypeFromPreferences()
                if (userType != null) {
                    fetchRWC(nx, ny, userType, callback)
                } else {
                    showToast("사용자 유형(userType)을 불러올 수 없습니다.")
                    callback(null)
                }
            } ?: run {
                showToast("위치 정보를 불러올 수 없습니다")
                callback(null)
            }
        }
    }

    /**
     * NX, NY 좌표와 userType을 사용해 백엔드로부터 RWCResponse를 가져오는 함수
     */
    fun fetchRWC(nx: Int, ny: Int, userType: String, callback: (RWCResponse?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getRWC(nx, ny, userType)
                if (response.isSuccessful) {
                    val rwcResponse = response.body()
                    Log.d("GetRWCHelper", "Success! Response body: $rwcResponse")
                    withContext(Dispatchers.Main) {
                        callback(rwcResponse)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("GetRWCHelper", "Error! HTTP code: ${response.code()}, error body: $errorBody")
                    withContext(Dispatchers.Main) {
                        callback(null)
                    }
                }
            } catch (e: Exception) {
                Log.e("GetRWCHelper", "Exception occurred during API call: ${e.message}")
                withContext(Dispatchers.Main) {
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
        val memberJson = sharedPreferences.getString(PREF_MEMBER_KEY, null)
        return if (memberJson != null) {
            val member = com.google.gson.Gson().fromJson(memberJson, com.example.weatherwear.data.model.Member::class.java)
            member.userType
        } else {
            null
        }
    }

    /**
     * 위도와 경도를 NX, NY 격자 좌표로 변환하는 함수
     */
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

    /**
     * 위치 권한을 요청하는 함수
     */
    private fun requestLocationPermission() {
        if (!checkLocationPermission()) {
            ActivityCompat.requestPermissions(
                context as androidx.appcompat.app.AppCompatActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    /**
     * 위치 권한을 확인하는 함수
     */
    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Toast 메시지를 간단히 보여주는 함수
     */
    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
