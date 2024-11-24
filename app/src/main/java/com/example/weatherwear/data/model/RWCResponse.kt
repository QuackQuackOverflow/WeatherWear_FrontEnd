import com.example.weatherwear.data.model.ClothingSet

data class RWCResponse(
    val regionAndWeather : List<RegionAndWeather>,  // 지역과 날씨
    val clothingSet: ClothingSet        // 추천 의상 목록
)