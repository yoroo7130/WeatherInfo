package com.example.weatherinfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private String[] REQUIRED_PERMISSIONS =
            {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private WeatherRepo weatherRepo;
    private Context mContext;
    private TextView tvLocation, tvMain, tvTemp, tvHumidity, tvWind, tvCloud;
    private ImageView weatherIcon;
    private MyLocation myLocation;
    private ImageButton btnForecast;
    private ForecastRepo forecastRepo;
    private TextView[] tvInfoDate;
    private TextView[] tvInTemp;
    private ImageView[] ivInfoImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mContext = MainActivity.this;

        tvMain = findViewById(R.id.tvMain);
        tvTemp = findViewById(R.id.tvTemp);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvWind = findViewById(R.id.tvWind);
        tvCloud = findViewById(R.id.tvCloud);
        tvLocation = findViewById(R.id.tvLocation);
        btnForecast = findViewById(R.id.btnForecast);
        weatherIcon = findViewById(R.id.imageView);

        tvInfoDate = new TextView[9];
        tvInTemp = new TextView[9];
        ivInfoImg = new ImageView[9];

        for (int i=0; i<9; i++){
            tvInfoDate[i] = findViewById(R.id.infoDate1+i);
            tvInTemp[i] = findViewById(R.id.infoTemp1+i);
            ivInfoImg[i] = findViewById(R.id.infoImg1+i);
        }

        // GPS 설정 여부
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        }else {
            checkRunTimePermission();
        }
    }

    public void OnclickBtnWeather(View view) {
        myLocation = new MyLocation(mContext);
        myLocation.getLocation();
        getWeatherData(myLocation.getLatitude(), myLocation.getLongitude());
        getForecastData(myLocation.getLatitude(), myLocation.getLongitude());
        reverseCoding(mContext, myLocation.getLatitude(), myLocation.getLongitude());

        btnForecast.setVisibility(View.VISIBLE);
    }

    public void getWeatherData(double latitude, double longitude) {
        weatherRepo = new WeatherRepo();

        // 클라이언트 URL 설정
        Retrofit client = new Retrofit.Builder().baseUrl("https://api.openweathermap.org")
                .addConverterFactory(GsonConverterFactory.create()).build();

        // 인터페이스 호출
        final WeatherRepo.WeatherApiInterface weatherApi =
                client.create(WeatherRepo.WeatherApiInterface.class);

        // 값 세팅
        String appid = "14b5854251eafcae291db2c5db22a49d";
        String lat = String.valueOf(latitude);
        String lon = String.valueOf(longitude);

        // API 호출
        Call<WeatherRepo> call = weatherApi.get_Weather_retrofit(appid, lat, lon);

        // API 리턴
        call.enqueue(new Callback<WeatherRepo>() {
            @Override
            public void onResponse(Call<WeatherRepo> call, Response<WeatherRepo> response) {
                weatherRepo = response.body();

                // 호출 성공
                if (response.isSuccessful()) {
                    transferDescription(weatherRepo.getWeathers().get(0).getMain());
                    //String imgUrl = "http://openweathermap.org/img/w/" + weatherRepo.getWeathers().get(0).getIcon() + ".png";
                    //Glide.with(mContext).load(imgUrl).into(weatherIcon);
                    tvTemp.setText(tempChange(Double.parseDouble(weatherRepo.getMain().getTemp()))+"℃");
                    tvHumidity.setText(weatherRepo.getMain().getHumidity()+"%");
                    tvWind.setText(weatherRepo.getWind().getSpeed()+"m/s");
                    tvCloud.setText(weatherRepo.getClouds().getAll()+"%");
                } else {
                    Log.d("Error", "server return error code");
                }
            }

            // 호출 실패
            @Override
            public void onFailure(Call<WeatherRepo> call, Throwable t) {
                Log.d("Fail", "onFailure" + t);
            }
        });
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
                    for (int i=0; i<9; i++){
                        setDate2(forecastRepo.getList().get(i).getDt_txt(),tvInfoDate[i]);
                        tvInTemp[i].setText(tempChange(Double.parseDouble(forecastRepo.getList()
                                .get(i).getMain().getTemp())));
                        transferDescription(forecastRepo.getList()
                                .get(i).getWeathers().get(0).getMain(),ivInfoImg[i]);
                    }
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

    public void btnForecast(View view) {
        Intent in = new Intent(mContext, Forecast.class);
        in.putExtra("lat", myLocation.getLatitude());
        in.putExtra("lon", myLocation.getLongitude());
        startActivity(in);
    }

    // 날씨 설명 변환
    public void transferDescription(String description) {

        if(description.equals("Thunderstorm")){
            weatherIcon.setImageResource(R.drawable.thunderstorm);
            tvMain.setText("비가 내리고 천둥 번개가 쳐요.");
        }
        else if (description.equals("Drizzle")){
            weatherIcon.setImageResource(R.drawable.drizzle);
            tvMain.setText("이슬비가 내리고 있어요.");
        }
        else if (description.equals("Clouds")){
            weatherIcon.setImageResource(R.drawable.cloud);
            tvMain.setText("구름이 낀 흐린 날이네요.");
        }
        else if (description.equals("Rain")) {
            weatherIcon.setImageResource(R.drawable.rain);
            tvMain.setText("비가 내리고 있어요.");
        }
        else if (description.equals("Snow")) {
            weatherIcon.setImageResource(R.drawable.snow);
            tvMain.setText("눈이 내리고 있어요.");
        }
        else if (description.equals("Mist")) {
            weatherIcon.setImageResource(R.drawable.mist);
            tvMain.setText("안개 낀 날이네요. (박무)");
        }
        else if (description.equals("Smoke")) {
            weatherIcon.setImageResource(R.drawable.smoke);
            tvMain.setText("연기가 가득한 하늘이에요.");
        }
        else if (description.equals("Haze")) {
            weatherIcon.setImageResource(R.drawable.haze);
            tvMain.setText("안개 낀 날이네요. (연무)");
        }
        else if (description.equals("Fog")) {
            weatherIcon.setImageResource(R.drawable.fog);
            tvMain.setText("안개 가득한 날이네요.");
        }
        else if (description.equals("Sand")) {
            weatherIcon.setImageResource(R.drawable.dustsandash);
            tvMain.setText("황사에 주의하세요.");
        }
        else if (description.equals("Ash")) {
            weatherIcon.setImageResource(R.drawable.dustsandash);
            tvMain.setText("화산재에 주의하세요.");
        }
        else if (description.equals("Dust")) {
            weatherIcon.setImageResource(R.drawable.dustsandash);
            tvMain.setText("미세먼지에 주의하세요.");
        }
        else if (description.equals("Squall") || description.equals("Tornado")) {
            weatherIcon.setImageResource(R.drawable.squalltornado);
            tvMain.setText("태풍이 불고 있어요.");
        }
        else if (description.equals("Clear")) {
            weatherIcon.setImageResource(R.drawable.clear);
            tvMain.setText("맑은 하늘이에요.");
        }
        else {
            weatherIcon.setImageResource(R.drawable.init_weather);
            tvMain.setText("날씨를 불러올 수 없습니다.");
        }
    }

    // 날씨 이미지 설정
    public void transferDescription(String description, ImageView imageView) {

        if(description.equals("Thunderstorm")){
            imageView.setImageResource(R.drawable.thunderstorm);
        }
        else if (description.equals("Drizzle")){
            imageView.setImageResource(R.drawable.drizzle);
        }
        else if (description.equals("Clouds")){
            imageView.setImageResource(R.drawable.cloud);
        }
        else if (description.equals("Rain")) {
            imageView.setImageResource(R.drawable.rain);
        }
        else if (description.equals("Snow")) {
            imageView.setImageResource(R.drawable.snow);
        }
        else if (description.equals("Mist")) {
            imageView.setImageResource(R.drawable.mist);
        }
        else if (description.equals("Smoke")) {
            imageView.setImageResource(R.drawable.smoke);
        }
        else if (description.equals("Haze")) {
            imageView.setImageResource(R.drawable.haze);
        }
        else if (description.equals("Fog")) {
            imageView.setImageResource(R.drawable.fog);
        }
        else if (description.equals("Sand")) {
            imageView.setImageResource(R.drawable.dustsandash);
        }
        else if (description.equals("Ash")) {
            imageView.setImageResource(R.drawable.dustsandash);
        }
        else if (description.equals("Dust")) {
            imageView.setImageResource(R.drawable.dustsandash);
        }
        else if (description.equals("Squall") || description.equals("Tornado")) {
            imageView.setImageResource(R.drawable.squalltornado);
        }
        else if (description.equals("Clear")) {
            imageView.setImageResource(R.drawable.clear);
        }
        else {
            imageView.setImageResource(R.drawable.init_weather);
        }
    }

    // 데이터 변환
    public void setDate(String date, TextView textView) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date myDate = dateFormat.parse(date);

            Calendar cal = Calendar.getInstance();
            cal.setTime(myDate);

            int dayNum = cal.get(Calendar.DAY_OF_WEEK) ;
            String mDay = "";

            switch(dayNum){
                case 1:
                    mDay = "일";
                    break;
                case 2:
                    mDay = "월";
                    break;
                case 3:
                    mDay = "화";
                    break;
                case 4:
                    mDay = "수";
                    break;
                case 5:
                    mDay = "목";
                    break;
                case 6:
                    mDay = "금";
                    break;
                case 7:
                    mDay = "토";
                    break;
            }

            textView.setText(mDay);
            //Log.d("day", myDate+"");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // 데이터 변환
    public void setDate2(String date, TextView textView) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date myDate = dateFormat.parse(date);

            int month = myDate.getMonth()+1;
            int day = myDate.getDate();
            int hour = myDate.getHours();
            int minute = myDate.getMinutes();

            textView.setText(month+"/"+day+"\n"+hour+":"+minute+"0");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // 온도 변환 (켈빈식)
    public String tempChange(double kelvinTemp) {
        double celsiusTemp = kelvinTemp - 273.15;
        celsiusTemp = Math.round(celsiusTemp);
        return Double.toString((celsiusTemp));
    }

    // 역지오코딩
    public void reverseCoding(Context mContext, double lat, double lon) {

        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();

                strReturnedAddress.append(returnedAddress.getAdminArea() + " " +returnedAddress.getSubLocality()
                + " " + returnedAddress.getThoroughfare());
//                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
//                    strReturnedAddress.append(returnedAddress.getAddressLine(i));
//                }

                tvLocation.setText(strReturnedAddress.toString());
            } else {
                tvLocation.setText("위치가 정확하지 않습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            tvLocation.setText("위치 정보를 가져올 수 없습니다.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if ( check_result ) {
                //위치 값을 가져올 수 있음
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(mContext, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                }else {
                    Toast.makeText(mContext, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    void checkRunTimePermission(){

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
            // 3. 위치 값을 가져올 수 있음

        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(mContext, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }
                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}
