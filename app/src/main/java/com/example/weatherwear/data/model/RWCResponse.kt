import com.example.weatherwear.data.model.ClothingSet
import com.example.weatherwear.data.model.Region
import com.example.weatherwear.data.model.Weather

data class RWCResponse(
    val region: Region,             // 지역 정보
    val weather: Weather,           // 날씨 정보
    val clothingSet: ClothingSet    // 추천 의상 목록
)