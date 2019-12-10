package com.example.weatherinfo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public class ForecastRepo {
    @SerializedName("list")
    public List<list> list = new ArrayList<>();

    public class list {
        @SerializedName("dt_txt")
        String dt_txt;
        @SerializedName("weather")
        public List<weather> weathers = new ArrayList<>();
        @SerializedName("main")
        main main;
        @SerializedName("wind")
        wind wind;

        public class weather {
            @SerializedName("main")
            String main;

            public String getMain() {
                return main;
            }
        }

        public class main {
            @SerializedName("temp")
            String temp;
            @SerializedName("temp_min")
            String temp_min;
            @SerializedName("temp_max")
            String temp_max;
            @SerializedName("humidity")
            String humidity;

            public String getTemp() {
                return temp;
            }
            public String getTemp_min() { return temp_min; }
            public String getTemp_max() { return temp_max; }
            public String getHumidity() {
                return humidity;
            }
        }

        public class wind {
            @SerializedName("speed")
            String speed;

            public String getSpeed(){
                return speed;
            }
        }

        public String getDt_txt() { return dt_txt; }
        public List<weather> getWeathers() {
            return weathers;
        }
        public main getMain() {
            return main;
        }
        public wind getWind() { return wind; }
    }

    public List<list> getList() { return list; }

    // 인터페이스 구현
    public interface ForecastApiInterface {
        @Headers({"Accept: application/json"})
        @GET("/data/2.5/forecast")
        Call<ForecastRepo> get_Forecast_retrofit(@Query("appid") String appid, @Query("lat") String lat, @Query("lon") String lon);
    }
}
