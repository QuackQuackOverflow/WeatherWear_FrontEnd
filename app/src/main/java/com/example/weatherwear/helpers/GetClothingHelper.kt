package com.example.weatherwear.helpers

import android.content.Context
import android.widget.Toast
import com.example.weatherwear.data.api.ApiService
import com.example.weatherwear.data.model.ClothingSet
import com.example.weatherwear.util.RetrofitInstance
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GetClothingHelper(private val context: Context) {

    // 의류 추천 데이터 가져오기
    fun fetchClothingSet(userType: String, callback: (ClothingSet?) -> Unit) {
        val apiService = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getClothingSet(userType = userType)
                withContext(Dispatchers.Main) {
                    callback(response.body())
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "네트워크 오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                    callback(null)
                }
            }
        }
    }

    // SharedPreferences에 의류 세트 데이터를 저장
    fun saveClothingSetToPreferences(clothingSet: ClothingSet) {
        val sharedPreferences = context.getSharedPreferences("ClothingPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val clothingSetJson = gson.toJson(clothingSet) // ClothingSet 객체를 JSON 문자열로 변환
        editor.putString("clothingSet", clothingSetJson)
        editor.apply()
    }
}
