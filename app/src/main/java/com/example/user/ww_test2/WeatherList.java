package com.example.user.ww_test2;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by USER on 2017-11-04.
 */

public class WeatherList {
    //단일 값은 그냥 변수로 선언해도 되지만, {}는 무조건 클래스 생성, []는 List생성
    @SerializedName("cod") public String cod;
    @SerializedName("message") public String message;
    @SerializedName("cnt") public String cnt;

    @SerializedName("list") public List<list_> list;

    public List<list_> getMyList(){
        return list;
    }

    public class list_{
        @SerializedName("dt") public String dt;
        @SerializedName("main") public Main_ main;
        @SerializedName("weather") public List<Weather_> weather;
        @SerializedName("wind") public Wind_ wind;

        public class Main_{
            @SerializedName("temp") public String temp;
            @SerializedName("temp_min") public String temp_min;
            @SerializedName("temp_max") public String temp_max;
            @SerializedName("pressure") public String pressure;
            @SerializedName("sea_level") public String sea_level;
            @SerializedName("grnd_level") public String grnd_level;
            @SerializedName("humidity") public String humidity;
            @SerializedName("temp_kf") public String temp_kf;
        }

        public class Weather_{
            @SerializedName("id") public String id;
            @SerializedName("main") public String main;
            @SerializedName("description") public String description;
            @SerializedName("icon") public String icon;
        }

        //@SerializedName("clouds") public String clouds;

        public class Wind_{
            @SerializedName("speed") public String speed;
            @SerializedName("deg") public String deg;
        }
        //@SerializedName("rain") public String rain;
        //@SerializedName("sys") public String sys;
        @SerializedName("dt_txt") public String dt_txt;

        public String getDt() { return dt;}
        public String getDt_txt() {
            return dt_txt;
        }
        public String getTemp(){
            return main.temp;
        }
        public String getWeather()
        {
            return weather.get(0).id;
        }
    }

    public class city{
        @SerializedName("id") public String id;
        @SerializedName("name") public String name;
        public class coord{
            @SerializedName("lat") public String lat;
            @SerializedName("lon") public String lon;
        }
        @SerializedName("country") public String country;
    }

}
