import com.example.weatherwear.data.model.Weather

//data class RWResponse(
//    val regionName: String,            // 지역 이름
//    val weather: Weather,           // 날씨 정보
//)

data class RegionAndWeather(
    val regionName: String,   // 지역 이름
    val weather : Weather,    // 날씨 정보
)