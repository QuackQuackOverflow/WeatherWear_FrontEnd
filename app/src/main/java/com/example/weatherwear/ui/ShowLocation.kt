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

        // NX, NY 좌표 계산
        val (nx, ny) = convertToGrid(latitude, longitude)

        // TextView에 NX와 NY 표시 (소수점 아래 5번째 자리까지)
        findViewById<TextView>(R.id.nxView).text = "NX: %.5f".format(nx)
        findViewById<TextView>(R.id.nyView).text = "NY: %.5f".format(ny)
    }

    private fun convertToGrid(lat: Double, lon: Double): Pair<Double, Double> {
        val RE = 6371.00877     // 지구 반경(km)
        val GRID = 5.0          // 격자 간격(km)
        val SLAT1 = 30.0        // 표준 위도1(degree)
        val SLAT2 = 60.0        // 표준 위도2(degree)
        val OLON = 126.0        // 기준점 경도(degree)
        val OLAT = 38.0         // 기준점 위도(degree)
        val XO = 43.0           // 기준점 X좌표(GRID)
        val YO = 136.0          // 기준점 Y좌표(GRID)

        val DEGRAD = Math.PI / 180.0
        val RADDEG = 180.0 / Math.PI

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

        val x = ra * Math.sin(theta) + XO
        val y = ro - ra * Math.cos(theta) + YO
        return Pair(x, y)
    }
}
