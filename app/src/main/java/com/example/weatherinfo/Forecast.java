package com.example.weatherinfo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Forecast extends AppCompatActivity {

    private ForecastRepo forecastRepo;

    private double latitude, longitude;
    private ImageView imageView1, imageView2, imageView3, imageView4, imageView5;
    private TextView tvTemp1, tvTemp2, tvTemp3, tvTemp4, tvTemp5;
    private TextView tvDate1, tvDate2, tvDate3, tvDate4, tvDate5;
    private TextView tvHumidity1, tvHumidity2, tvHumidity3, tvHumidity4, tvHumidity5;
    private TextView tvWind1, tvWind2, tvWind3, tvWind4, tvWind5;
    private MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forecast);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            latitude = extras.getDouble("lat");
            longitude = extras.getDouble("lon");
        }

        mainActivity = new MainActivity();

        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);
        imageView5 = findViewById(R.id.imageView5);

        tvTemp1 = findViewById(R.id.tvTemp1);
        tvTemp2 = findViewById(R.id.tvTemp2);
        tvTemp3 = findViewById(R.id.tvTemp3);
        tvTemp4 = findViewById(R.id.tvTemp4);
        tvTemp5 = findViewById(R.id.tvTemp5);
        tvDate1 = findViewById(R.id.tvDate1);
        tvDate2 = findViewById(R.id.tvDate2);
        tvDate3 = findViewById(R.id.tvDate3);
        tvDate4 = findViewById(R.id.tvDate4);
        tvDate5 = findViewById(R.id.tvDate5);
        tvHumidity1 = findViewById(R.id.tvHumidity1);
        tvHumidity2 = findViewById(R.id.tvHumidity2);
        tvHumidity3 = findViewById(R.id.tvHumidity3);
        tvHumidity4 = findViewById(R.id.tvHumidity4);
        tvHumidity5 = findViewById(R.id.tvHumidity5);
        tvWind1 = findViewById(R.id.tvWind1);
        tvWind2 = findViewById(R.id.tvWind2);
        tvWind3 = findViewById(R.id.tvWind3);
        tvWind4 = findViewById(R.id.tvWind4);
        tvWind5 = findViewById(R.id.tvWind5);

        //Log.d("test","lat" + latitude + "lon" + longitude + "");
        getForecastData(latitude,longitude);
    }

    public void getForecastData(double latitude, double longitude) {
        forecastRepo = new ForecastRepo();

        // 클라이언트 URL 설정
        Retrofit client = new Retrofit.Builder().baseUrl("https://api.openweathermap.org")
                .addConverterFactory(GsonConverterFactory.create()).build();

        // 인터페이스 호출
        final ForecastRepo.ForecastApiInterface forecastApi =
                client.create(ForecastRepo.ForecastApiInterface.class);

        // 값 세팅
        String appid = "14b5854251eafcae291db2c5db22a49d";
        String lat = String.valueOf(latitude);
        String lon = String.valueOf(longitude);

        // API 호출
        Call<ForecastRepo> call = forecastApi.get_Forecast_retrofit(appid, lat, lon);

        // API 리턴
        call.enqueue(new Callback<ForecastRepo>() {
            @Override
            public void onResponse(Call<ForecastRepo> call, Response<ForecastRepo> response) {
                forecastRepo = response.body();

                // 호출 성공
                if (response.isSuccessful()) {
                    // 1
                    mainActivity.transferDescription(forecastRepo.getList().get(11).getWeathers().get(0).getMain(), imageView1);
                    mainActivity.setDate(forecastRepo.getList().get(11).getDt_txt(), tvDate1);
                    tvTemp1.setText(mainActivity.tempChange(Double.parseDouble
                            (forecastRepo.getList().get(11).getMain().getTemp())) + "℃");
                    tvHumidity1.setText(forecastRepo.getList().get(11).getMain().getHumidity() + "%");
                    tvWind1.setText(forecastRepo.getList().get(11).getWind().getSpeed() + "m/s");
                    // 2
                    mainActivity.transferDescription(forecastRepo.getList().get(19).getWeathers().get(0).getMain(), imageView2);
                    mainActivity.setDate(forecastRepo.getList().get(19).getDt_txt(), tvDate2);
                    tvTemp2.setText(mainActivity.tempChange(Double.parseDouble
                            (forecastRepo.getList().get(19).getMain().getTemp())) + "℃");
                    tvHumidity2.setText(forecastRepo.getList().get(19).getMain().getHumidity() + "%");
                    tvWind2.setText(forecastRepo.getList().get(19).getWind().getSpeed() + "m/s");
                    // 3
                    mainActivity.transferDescription(forecastRepo.getList().get(27).getWeathers().get(0).getMain(), imageView3);
                    mainActivity.setDate(forecastRepo.getList().get(27).getDt_txt(), tvDate3);
                    tvTemp3.setText(mainActivity.tempChange(Double.parseDouble
                            (forecastRepo.getList().get(27).getMain().getTemp())) + "℃");
                    tvHumidity3.setText(forecastRepo.getList().get(27).getMain().getHumidity() + "%");
                    tvWind3.setText(forecastRepo.getList().get(27).getWind().getSpeed() + "m/s");
                    // 4
                    mainActivity.transferDescription(forecastRepo.getList().get(35).getWeathers().get(0).getMain(), imageView4);
                    mainActivity.setDate(forecastRepo.getList().get(35).getDt_txt(), tvDate4);
                    tvTemp4.setText(mainActivity.tempChange(Double.parseDouble
                            (forecastRepo.getList().get(35).getMain().getTemp())) + "℃");
                    tvHumidity4.setText(forecastRepo.getList().get(35).getMain().getHumidity() + "%");
                    tvWind4.setText(forecastRepo.getList().get(35).getWind().getSpeed() + "m/s");
                    // 5
                    mainActivity.transferDescription(forecastRepo.getList().get(39).getWeathers().get(0).getMain(), imageView5);
                    mainActivity.setDate(forecastRepo.getList().get(39).getDt_txt(), tvDate5);
                    tvTemp5.setText(mainActivity.tempChange(Double.parseDouble
                            (forecastRepo.getList().get(39).getMain().getTemp())) + "℃");
                    tvHumidity5.setText(forecastRepo.getList().get(39).getMain().getHumidity() + "%");
                    tvWind5.setText(forecastRepo.getList().get(39).getWind().getSpeed() + "m/s");
                } else {
                    Log.d("Error", "server return error code");
                }
            }

            // 호출 실패
            @Override
            public void onFailure(Call<ForecastRepo> call, Throwable t) {
                Log.d("Fail", "onFailure" + t);
            }
        });
    }

    public void btnBack(View view) {
        finish();
    }
}
