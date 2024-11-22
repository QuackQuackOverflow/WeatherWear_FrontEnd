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

class GetClothingHelper(
    private val context: Context,
    private val apiService: ApiService = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java),
    private val gson: Gson = Gson()
) {

    companion object {
        private const val PREF_NAME = "ClothingPrefs"
        private const val PREF_CLOTHING_SET_KEY = "clothingSet"
    }

    /**
     * 의류 추천 데이터 가져오기
     */
    fun fetchClothingSet(userType: String, callback: (ClothingSet?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getClothingSet(userType)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        callback(response.body())
                    } else {
                        showToast("Failed to fetch clothing set: ${response.code()}")
                        callback(null)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Network error: ${e.message}")
                    callback(null)
                }
            }
        }
    }

    /**
     * SharedPreferences에 의류 세트 데이터를 저장
     */
    fun saveClothingSetToPreferences(clothingSet: ClothingSet) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.saveAsJson(PREF_CLOTHING_SET_KEY, clothingSet, gson)
        showToast("Clothing set saved to preferences")
    }

    /**
     * SharedPreferences에서 의류 세트 데이터를 불러오기
     */
    fun loadClothingSetFromPreferences(): ClothingSet? {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.loadFromJson(PREF_CLOTHING_SET_KEY, ClothingSet::class.java, gson)
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
private fun <T> android.content.SharedPreferences.saveAsJson(key: String, data: T, gson: Gson) {
    val json = gson.toJson(data)
    edit().putString(key, json).apply()
}

/**
 * SharedPreferences 확장 함수: JSON 형태 데이터를 객체로 변환
 */
private fun <T> android.content.SharedPreferences.loadFromJson(key: String, clazz: Class<T>, gson: Gson): T? {
    val json = getString(key, null) ?: return null
    return gson.fromJson(json, clazz)
}
