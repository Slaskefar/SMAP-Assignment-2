package com.example.jeppe.weatherapp.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jeppe.weatherapp.DAL.DataHelper;
import com.example.jeppe.weatherapp.Globals;
import com.example.jeppe.weatherapp.R;
import com.example.jeppe.weatherapp.models.CityWeather;
import com.example.jeppe.weatherapp.models.CityWeather;
import com.example.jeppe.weatherapp.models.weatherDataResponse.WeatherData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WeatherService extends Service {

    public static final String WEATHER_API_BASE_URL = "http://api.openweathermap.org/data/2.5";
    public static final String API_KEY = "&APPID=7bcb820624561125d1c7de1feed18777";
    public static final String WEATHER_API_GET_MULTIPLE = "/group?id=";
    public static final String WEATHER_API_GET_SINGLE = "/weather?q=";

    public static final String NOTIFICATION_CHANNEL = "weather_channel";
    private static final long weatherUpdateTime = 5000*60;
    private static final double kelvinConstant = 272.15;

    RequestQueue queue;
    Gson gson;
    CityWeather singleCityWeather;
    ArrayList<CityWeather> allCityWeather;
    ArrayList<Integer> cityIds;
    private DataHelper dataHelper;
    NotificationManager mNotificationManager;
    NotificationChannel mNotificationChannel;
    int mNotificationId = 1231;

    public WeatherService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //for starting the 5 minute tick inspired by https://stackoverflow.com/questions/6531950/how-to-execute-async-task-repeatedly-after-fixed-time-intervals
        final Handler timerHandler = new Handler();
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                timerHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateCityWeather();
                        Log.d("Timer", "Timer function call");
                    }
                });
            }
        };

        timer.schedule(timerTask,0,weatherUpdateTime);

        dataHelper = new DataHelper(this);

        // create notification channel
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // inspired from https://stackoverflow.com/questions/45395669/notifications-fail-to-display-in-android-oreo-api-26
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL, "Weather notifications", NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationChannel.setDescription("Weather channel");
            mNotificationManager.createNotificationChannel(mNotificationChannel);
        }
        Log.d("WeatherService", "Created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateCityWeather();
        return super.onStartCommand(intent, flags, startId);
    }

    public class WeatherBinder extends Binder {
         public WeatherService getService() {
            return WeatherService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new WeatherBinder();

    // method stolen from SMAP lecture demo L8
    private boolean checkNetwork() {
        ConnectivityManager connectMan = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectMan.getActiveNetworkInfo();
        boolean conn = netInfo.isConnected();
        if(conn) {
            return true;
        }
        return false;
    }

    // returns most recent weather data
    public ArrayList<CityWeather> getAllCityWeather() {
        return allCityWeather;
    }

    // gets new weather data from web API
    public void updateCityWeather() {
        if(!checkNetwork()) {return;}
        Log.d("WeatherService", "getting city weather from openWeatherMap API");
        if (queue == null) {
            queue = Volley.newRequestQueue(this);
        }
        cityIds = new ArrayList<Integer>();
        ArrayList<CityWeather> allCities = dataHelper.getCities();
        for (Iterator<CityWeather> i = allCities.iterator(); i.hasNext();) {
            CityWeather item = i.next();
            cityIds.add(item.id);
        }

        String url = WEATHER_API_BASE_URL + WEATHER_API_GET_MULTIPLE + cityIdsToString(cityIds) + API_KEY;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        allCityWeather = weatherJsonToArrayList(response);
                        //Push notification
                        broadcastWeather();
                        sendNotification();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                allCityWeather = null;
            }
        });

        queue.add(stringRequest);
    }

//    public CityWeather getCityWeather(String cityName) {
//        if (queue == null) {
//            queue = Volley.newRequestQueue(this);
//        }
//        String url = WEATHER_API_BASE_URL + WEATHER_API_GET_SINGLE + cityName + API_KEY;
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        singleCityWeather = weatherJsonToPojo(response);
//                        //broadcast
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                singleCityWeather = null;
//            }
//        });
//
//        queue.add(stringRequest);
//
//    }

    private Bitmap getWeatherIcon(String id) {
        return null;
    }

    private ArrayList<CityWeather> weatherJsonToArrayList(String json) {
        if (gson == null) {
            gson = new Gson();
        }
        Type collectionType = new TypeToken<WeatherData>(){}.getType();

        WeatherData weatherData = gson.fromJson(json, collectionType);
        ArrayList<CityWeather> cityWeatherList = weatherDataToCityWeatherList(weatherData);

        return cityWeatherList;
    }

    private CityWeather weatherJsonToCityWeather(String json) {
        if (gson == null) {
            gson = new Gson();
        }
        Type collectionType = new TypeToken<com.example.jeppe.weatherapp.models.weatherDataResponse.List>(){}.getType();

        com.example.jeppe.weatherapp.models.weatherDataResponse.List weatherData = gson.fromJson(json, collectionType);
        CityWeather CityWeather = convertToWCityWeather(weatherData);

        return CityWeather;
    }

    private CityWeather convertToWCityWeather(com.example.jeppe.weatherapp.models.weatherDataResponse.List weatherData)
    {
        CityWeather cityWeather = new CityWeather();

        cityWeather.weatherDescription = weatherData.getWeather().iterator().next().getDescription();
        cityWeather.cityName = weatherData.getName();
        cityWeather.humidity = weatherData.getMain().getHumidity();
        cityWeather.temperature = weatherData.getMain().getTemp() - kelvinConstant;
        cityWeather.iconType = weatherData.getWeather().iterator().next().getIcon();
        cityWeather.id = weatherData.getId();

        return cityWeather;
    }

    // turns an arraylist of names into a single comma-separated string
    private String cityIdsToString(ArrayList<Integer> cityIds) {
        String strCityIds = "";
        Iterator<Integer> cityIterator = cityIds.iterator();
        while(cityIterator.hasNext()) {
            strCityIds += cityIterator.next();
            if(cityIterator.hasNext()) {
                strCityIds += ",";
            }
        }
        return strCityIds;
    }

    private void broadcastWeather() {
        Log.d("WeatherService", "broadcasting weather");
        Intent intent = new Intent("weather-event");
//        Bundle args = new Bundle();
//        args.putSerializable("weatherObj", (Serializable)allCityWeather);
//        intent.putExtra("weather", args);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private ArrayList<CityWeather> weatherDataToCityWeatherList(WeatherData weatherData)
    {
        ArrayList<CityWeather> cityWeatherList = new ArrayList<CityWeather>();

        Iterator<com.example.jeppe.weatherapp.models.weatherDataResponse.List> iter = weatherData.getList().iterator();

        while(iter.hasNext())
        {
            CityWeather cityWeather = new CityWeather();
            com.example.jeppe.weatherapp.models.weatherDataResponse.List list = iter.next();

            cityWeather.weatherDescription = list.getWeather().iterator().next().getDescription();
            cityWeather.cityName = list.getName();
            cityWeather.humidity = list.getMain().getHumidity();
            cityWeather.temperature = list.getMain().getTemp() - kelvinConstant;
            cityWeather.iconType = list.getWeather().iterator().next().getIcon();
            cityWeather.id = list.getId();

            cityWeatherList.add(cityWeather);
        }
        return cityWeatherList;
    }

    // Gets city ID from cityName and adds the city to the list in sharedPrefs
    public void addWeatherCity(String cityName){
        if (queue == null) {
            queue = Volley.newRequestQueue(this);
        }

        String url = WEATHER_API_BASE_URL + WEATHER_API_GET_SINGLE + cityName + API_KEY;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        singleCityWeather = weatherJsonToCityWeather(response);
                        dataHelper.addCity(singleCityWeather);
                        Context context = getApplicationContext();
                        Toast.makeText(context, "City added", Toast.LENGTH_LONG).show();
                        //Push notification
                        updateCityWeather();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                singleCityWeather = null;
                //Push notification
            }
        });

        queue.add(stringRequest);
    }

    private void sendNotification() {
        String currentTime = Calendar.getInstance().getTime().toString();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.icon_01d)
                .setContentTitle("Weather has been updated")
                .setContentText(currentTime);
        mNotificationManager.notify(mNotificationId, mBuilder.build());
    }

    public void removeCity(CityWeather city) {
        dataHelper.deleteCity(city);
        updateCityWeather();
    }
}
