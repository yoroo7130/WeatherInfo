package com.example.weatherinfo;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class MyLocation {

    private LocationManager locationManager;
    private Location location;

    private Context mContext;

    private double latitude;
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    // 현재 GPS 사용 유무
    private boolean isGPSEnabled = false;

    // 네트워크 사용 우무
    private boolean isNetworkEnabled = false;

    // GPS 상태값
    private boolean isGetLocation = false;

    // 최소 GPS 정보 업데이트 거리 1000미터
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1000;

    // 최소 업데이트 시간 1분
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    MainActivity mainActivity = new MainActivity();

    public MyLocation(Context mContext) {
        this.mContext = mContext;
    }

    @SuppressLint("MissingPermission")
    public Location getLocation(){
        try{
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

            // GPS 정보 가져오기
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // 현재 네트워크 상태 값 얻어오기
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && isNetworkEnabled){
                // GPS 와 네트워크사용이 가능하지 않을경우
            }
            else {
                this.isGetLocation = true;
                // 네트워크 정보로 부터 위치값 가져오기
                  if (isNetworkEnabled){
                      locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                              MIN_TIME_BW_UPDATES, MIN_TIME_BW_UPDATES, locationListener);
                      if (locationManager != null) {
                          location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                          if (location != null) {
                              // 위도 경도 저장
                              this.latitude = location.getLatitude();
                              this.longitude = location.getLongitude();
                          }
                      }
                  }
                  if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                this.latitude = location.getLatitude();
                                this.longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // GPS의 주소 값이 바뀔 때 마다 호출
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // API29레벨부터 deprecated
        }

        @Override
        public void onProviderEnabled(String provider) {
            //사용자가 GPS를 on하는 등의 행동을 해서 위치값에 접근할 수 있게 되었을 때 호출
        }

        @Override
        public void onProviderDisabled(String provider) {
            //사용자가 GPS를 끄는 등의 행동을 해서 위치값에 접근할 수 없을 때 호출
        }
    };
}
