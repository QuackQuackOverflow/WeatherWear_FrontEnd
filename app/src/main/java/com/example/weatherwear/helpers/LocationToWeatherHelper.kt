package com.example.weatherwear.helpers

import RWResponse
import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.weatherwear.data.api.ApiService
import com.example.weatherwear.util.RetrofitInstance
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocationToWeatherHelper(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient,
    private val apiService: ApiService = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java),
    private val gson: Gson = Gson()
) {

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 100
        private const val PREF_WEATHER_KEY = "regionAndWeather"
        private const val PREF_NAME = "WeatherPrefs"
    }

    /**
     * GPS를 통해 현재 NX, NY 좌표를 가져온 뒤 RWResponse를 반환
     */
    fun fetchRegionAndWeatherFromGPS(callback: (RWResponse?) -> Unit) {
        if (!checkLocationPermission()) {
            requestLocationPermission()
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val (nx, ny) = convertToGrid(it.latitude, it.longitude)
                fetchRegionAndWeather(nx, ny, callback)
            } ?: run {
                showToast("위치 정보를 불러올 수 없습니다")
                callback(null)
            }
        }
    }

    /**
     * NX, NY 좌표를 사용해 백엔드로부터 RWResponse를 가져오는 함수
     */
    private fun fetchRegionAndWeather(nx: Int, ny: Int, callback: (RWResponse?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getRegionAndWeather(nx, ny)
                withContext(Dispatchers.Main) {
                    callback(response.body())
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("네트워크 오류 발생: ${e.message}")
                    callback(null)
                }
            }
        }
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
     * 위치 권한 요청 결과를 처리하는 함수
     */
    fun handlePermissionsResult(
        requestCode: Int,
        grantResults: IntArray,
        onPermissionGranted: () -> Unit
    ) {
        if (requestCode == REQUEST_LOCATION_PERMISSION &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            onPermissionGranted()
        } else {
            showToast("위치 권한이 거부되었습니다.")
        }
    }

    /**
     * SharedPreferences에 RWResponse 데이터를 저장
     */
    fun saveRegionAndWeatherToPreferences(weatherData: RWResponse) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.saveAsJson(PREF_WEATHER_KEY, weatherData, gson)
        showToast("날씨 정보가 SharedPreferences에 저장되었습니다.")
    }

    /**
     * SharedPreferences에서 RWResponse 데이터를 불러오기
     */
    fun loadRegionAndWeatherFromPreferences(): RWResponse? {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.loadFromJson(PREF_WEATHER_KEY, RWResponse::class.java, gson)
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

/**
 * SharedPreferences 확장 함수: 객체를 JSON 형태로 저장
 */
private fun <T> SharedPreferences.saveAsJson(key: String, data: T, gson: Gson) {
    val json = gson.toJson(data)
    edit().putString(key, json).apply()
}

/**
 * SharedPreferences 확장 함수: JSON 형태 데이터를 객체로 변환
 */
private fun <T> SharedPreferences.loadFromJson(key: String, clazz: Class<T>, gson: Gson): T? {
    val json = getString(key, null) ?: return null
    return gson.fromJson(json, clazz)
}
