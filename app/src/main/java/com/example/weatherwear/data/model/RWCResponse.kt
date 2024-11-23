import com.example.weatherwear.data.model.ClothingSet
import com.example.weatherwear.data.model.GPSreport
import com.example.weatherwear.data.model.Weather

data class RWCResponse(
    val regionAndWeather : List<RWResponse>,  // 지역과 날씨
    val clothingSet: ClothingSet        // 추천 의상 목록
)