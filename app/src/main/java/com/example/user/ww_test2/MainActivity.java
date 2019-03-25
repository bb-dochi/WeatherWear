package com.example.user.ww_test2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

/* 20151819송현경 WW프로젝트 ver20171020_01:45
//
// 나중에 변수명 정리하기
 */
public class MainActivity extends AppCompatActivity implements LocationListener{
    TextView curLocation, curDate, curTemp, curWeather,w1,w2,w3,w4;
    ImageView weatherIcon, icon1,icon2,icon3,icon4;

    private Timer mTimer;
    private LocationManager locationManager;

    double lat = 37.566535;
    double lon = 126.977969;

    //다음 액티비티로 넘어갈때 넘겨줄 값
    String getTime,weather_str,location;
    int temp,temp_max,temp_min;
    int weather_id;
    int[] weather_id2 = new int[4];
    int[] weather_temp = new int[4];
    String[] weather_date = new String[4];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        curLocation = (TextView) findViewById(R.id.location);
        curDate = (TextView) findViewById(R.id.currentDate);
        curTemp = (TextView) findViewById(R.id.temp);
        curWeather = (TextView) findViewById(R.id.weather);
        weatherIcon = (ImageView) findViewById(R.id.weatherIcon);
        icon1 = (ImageView) findViewById(R.id.icon1); icon2 = (ImageView) findViewById(R.id.icon2); icon3 = (ImageView) findViewById(R.id.icon3); icon4 = (ImageView) findViewById(R.id.icon4);
        w1 = (TextView)findViewById(R.id.w1); w2 = (TextView)findViewById(R.id.w2); w3 = (TextView)findViewById(R.id.w3); w4 = (TextView)findViewById(R.id.w4);

        try {
            Location lo = getLocation();
            //lat=lo.getLatitude();
            //lon=lo.getLongitude();
            MainTimerTask timerTask = new MainTimerTask();
            mTimer = new Timer();
            mTimer.schedule(timerTask, 500, 1000);

            new GetWeather().start();
            new GetWeather5day3time().start();
        }catch (NullPointerException e) {
            Toast.makeText(this,"위치정보를 받아오지 못했습니다",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this,"에러다:"+e,Toast.LENGTH_LONG).show();
        }

    }

    public void getStyle(View v){
        try {
            Intent it = new Intent(this, GetStyle.class);
            it.putExtra("CurDate", getTime);
            it.putExtra("CurWeather", weather_str);
            it.putExtra("CurTemp", (double)((temp_max+temp_min)/2));
            this.startActivity(it);
            this.finish();
        }catch (Exception e){
            System.out.println("에러2-------------:" + e.getMessage());
        }
    }


    class GetWeather extends Thread {
        public void run() {
            try {
                //OpenAPI call하는 URL
                String urlstr = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&units=metric&appid=8f5437af74a38d00b26d075de28d7da3";
                URL url = new URL(urlstr);
                BufferedReader bf;
                String line;
                String result = "";

                //날씨 정보를 받아온다.
                bf = new BufferedReader(new InputStreamReader(url.openStream()));

                //버퍼에 있는 정보를 문자열로 변환.
                while ((line = bf.readLine()) != null) {
                    result = result.concat(line);
                    //System.out.println(line);
                }

                //문자열을 JSON으로 파싱
                JsonParser jsonParser = new JsonParser();
                JsonElement element = jsonParser.parse(result);

                //지역이름 가져오기
                location=element.getAsJsonObject().get("name").getAsString();

                //날씨id가져오기
                JsonArray weatherArray = (JsonArray) element.getAsJsonObject().get("weather");
                JsonObject obj = (JsonObject) weatherArray.get(0);
                weather_id = obj.get("id").getAsInt();
                weather_str = transferWeather(weather_id);

                //온도
                JsonObject mainArray = (JsonObject) element.getAsJsonObject().get("main");
                temp = (int)Math.round(Double.parseDouble(mainArray.get("temp").toString()));
                temp_max = (int)Math.round(Double.parseDouble(mainArray.get("temp_max").toString()));
                temp_min = (int)Math.round(Double.parseDouble(mainArray.get("temp_min").toString()));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        curLocation.setText(location);
                        SetIcon(weather_id,weatherIcon);
                        curWeather.setText(weather_str);
                        curTemp.setText(temp+"° ("+temp_max+"°/"+temp_min+"°)");
                    }
                });
                bf.close();
            } catch (Exception e) {
                System.out.println("에러-------------:" + e.getMessage());
            }
        }
    }
    class GetWeather5day3time extends Thread {
        public void run() {
            try {

                String urlstr = "http://api.openweathermap.org/data/2.5/forecast?lat=" + lat + "&lon=" + lon + "&units=metric&appid=8f5437af74a38d00b26d075de28d7da3";
                URL url = new URL(urlstr);
                BufferedReader bf;
                String line;
                String result = "";

                bf = new BufferedReader(new InputStreamReader(url.openStream()));
                while ((line = bf.readLine()) != null) {
                    result = result.concat(line);
                    //System.out.println(line);
                }

                Gson gson = new Gson();
                WeatherList wl = gson.fromJson(result,WeatherList.class); //result를 따로 생성한 클래스에 직렬화시킴

                for(int i=0;i<4;i++){ //제일 최근 날씨 3시간 단위로 가져오기
                    weather_id2[i]=Integer.parseInt(wl.getMyList().get(i).getWeather());
                    weather_temp[i]=(int)Math.round(Double.parseDouble(wl.getMyList().get(i).getTemp()));
                    /*-----------날짜 가져오기-----------*/
                    SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    transFormat.setTimeZone ( TimeZone.getTimeZone ( "UTC+9" ) );
                    Date d=transFormat.parse(wl.getMyList().get(i).getDt_txt()); //String -> Date

                    transFormat = new SimpleDateFormat("yyyy-MM-dd a HH:mm:ss");

                    String a = transFormat.format(d).toString().substring(11,16).replace("오전","AM").replace("오후","PM");
                    System.out.println(a);


                    weather_date[i]=a; //Date -> Korea_Date -> Get Hour

                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SetIcon(weather_id2[0],icon1);
                        SetIcon(weather_id2[1],icon2);
                        SetIcon(weather_id2[2],icon3);
                        SetIcon(weather_id2[3],icon4);

                        w1.setText(weather_date[0]+"\n"+weather_temp[0]+"°");
                        w2.setText(weather_date[1]+"\n"+weather_temp[1]+"°");
                        w3.setText(weather_date[2]+"\n"+weather_temp[2]+"°");
                        w4.setText(weather_date[3]+"\n"+weather_temp[3]+"°");

                    }
                });
                bf.close();
            } catch (Exception e) {
                System.out.println("에러-------------:" + e);
            }
        }
    }


    private void SetIcon(int weather,ImageView weatherIcon) //날씨 id에 맞춰 아이콘 출력
    {

        if(weather == 781|| weather ==900 || weather == 901 || weather==962||weather==771)//허리케인 토네이도 화산재 돌풍
            weatherIcon.setImageResource(R.drawable.hurricane);
        else if(200<=weather&&weather<=232)//뇌우
            weatherIcon.setImageResource(R.drawable.thunderstorm);
        else if(weather == 503 || weather ==504)//폭우
            weatherIcon.setImageResource(R.drawable.heavyrain);
        else if(300<=weather&&weather<=321 || 500<=weather&&weather<=531) //비
            weatherIcon.setImageResource(R.drawable.rain);
        else if(weather==602) //폭설
            weatherIcon.setImageResource(R.drawable.heavysnow);
        else if(600<=weather&&weather<=622)//눈
            weatherIcon.setImageResource(R.drawable.snow);
        else if(700<=weather&&weather<=781)
            weatherIcon.setImageResource(R.drawable.sanddust);
        else if(weather == 800||weather==951)//맑음
            weatherIcon.setImageResource(R.drawable.clear);
        else if(weather ==801 || weather==802)//구름조금
            weatherIcon.setImageResource(R.drawable.fewcloud);
        else if(weather ==803||weather ==804)//흐림
            weatherIcon.setImageResource(R.drawable.overcastcloud);
        else if(weather == 902)
            weatherIcon.setImageResource(R.drawable.cold);
        else if(weather == 903)
            weatherIcon.setImageResource(R.drawable.hot);
        else if(weather == 904||952<=weather&&weather<=956)
            weatherIcon.setImageResource(R.drawable.wind);
        else if(weather == 905)
            weatherIcon.setImageResource(R.drawable.hail);
        else if(957<=weather&&weather<=959)
            weatherIcon.setImageResource(R.drawable.highwind);
        else if(weather == 960 || weather==961)
            weatherIcon.setImageResource(R.drawable.storm);

    }
    private String transferWeather(int weather) //날씨 id를 한글로 변환
    {

        if(200<=weather&&weather<=232)
            return "뇌우";
        else if(300<=weather&&weather<=321)
            return "이슬비";

        else if(500<=weather&&weather<=531){
            if(weather == 503 || weather ==504)
                return "폭우";
            else
                return "비";
        }
        else if(600<=weather&&weather<=622){
            if(weather==602)
                return "폭설";
            else
                return "눈";
        }

        else if(weather == 781|| weather ==900)
            return "토네이도";
        else if(700<=weather&&weather<=781){
            if(weather==731||weather==751||weather==761)
                return "모래와 먼지";
            else if(weather ==762)
                return "화산재";
            else if(weather==771)
                return "돌풍";
            else
                return "안개";
        }
        else if(weather == 800)
            return "맑은 하늘";
        else if(weather ==801 || weather==802)
            return "구름 조금";
        else if(weather ==803||weather ==804)
            return "흐린 하늘";
        else if(weather == 901 || weather==962)
            return "허리케인";
        else if(weather == 902)
            return "추움";
        else if(weather == 903)
            return "더움";
        else if(weather == 904)
            return "바람";
        else if(weather == 905)
            return "우박";
        else if(951<=weather&&weather<=962){
            if(weather==951)
                return "잔잔한 하늘";
            else if(952<=weather&&weather<=956)
                return "바람";
            else if(957<=weather&&weather<=959)
                return "강풍";
            else if(weather == 960 || weather==961)
                return "폭풍우";
        }
        return "error";
    }
    private Location getLocation() //현재 위치 구하기
    {
        Location currentLocation=null;
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, this);
            String locationProvider = LocationManager.GPS_PROVIDER;
            currentLocation = locationManager.getLastKnownLocation(locationProvider);

        }catch (SecurityException e){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
        return currentLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        lat=location.getLatitude();
        lon=location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private Handler mHandler = new Handler();
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            Date rightNow = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "yyyy.MM.dd a hh:mm");
            String dateString = formatter.format(rightNow);
            getTime = dateString;
            curDate.setText(dateString);

        }
    };

    class MainTimerTask extends TimerTask {
        public void run() {
            mHandler.post(mUpdateTimeTask);
        }
    }
    @Override
    protected void onDestroy() {
        if(mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if(mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        try {
            if(mTimer == null) {
                MainTimerTask timerTask = new MainTimerTask();
                mTimer = new Timer();
                mTimer.schedule(timerTask, 500, 3000);
            }
            super.onResume();
        }
        catch (Exception e){
            System.out.println("에러:"+e);
        }

    }

};


     /* APK 23이상일때 사용할 것
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //수락
            } else {
            }
        }
    }*/
