package com.example.weatherinfo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/*
    Retrofit2를 사용한 REST API 호출
    자바 객체와 JSON간의 직렬화 및 역직렬화를 위한
    오픈소스인 GSON 라이브러리 사용
 */

public class WeatherRepo {
    @SerializedName("weather")
    public List<weather> weathers = new ArrayList<>();
    @SerializedName("main")
    main main;
    @SerializedName("wind")
    wind wind;
    @SerializedName("clouds")
    clouds clouds;

    // 날씨 설명, 날씨 아이콘
    public class weather {
        @SerializedName("main")
        String main;
        @SerializedName("icon")
        String icon;

        public String getMain() {
            return main;
        }
        public String getIcon() {
            return icon;
        }
    }

    // 온도, 습도
    public class main {
        @SerializedName("temp")
        String temp;
        @SerializedName("humidity")
        String humidity;

        public String getTemp() {
            return temp;
        }
        public String getHumidity() {
            return humidity;
        }
    }

    // 풍속
    public class wind {
        @SerializedName("speed")
        String speed;

        public String getSpeed(){
            return speed;
        }
    }

    // 구름의 양
    public class clouds {
        @SerializedName("all")
        String all;

        public String getAll(){
            return all;
        }
    }

    public List<weather> getWeathers() {
        return weathers;
    }
    public main getMain() {
        return main;
    }
    public wind getWind() { return wind; }
    public clouds getClouds() {
        return clouds;
    }

    // 인터페이스 구현
    public interface WeatherApiInterface {
        @Headers({"Accept: application/json"})
        @GET("/data/2.5/weather")
        Call<WeatherRepo> get_Weather_retrofit(@Query("appid") String appid, @Query("lat") String lat, @Query("lon") String lon);
    }
}