package com.example.jeppe.weatherapp.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jeppe.weatherapp.models.CityWeather;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

public class WeatherService extends Service {

    public static final String WEATHER_API_BASE_URL = "http://api.openweathermap.org/data/2.5";
    public static final String API_KEY = "&APPID=7bcb820624561125d1c7de1feed18777";
    public static final String WEATHER_API_GET_MULTIPLE = "/group?id=";
    public static final String WEATHER_API_GET_SINGLE = "/weather?q=";

    RequestQueue queue;
    Gson gson;
    CityWeather singleCityWeather;
    ArrayList<CityWeather> allCityWeather;
    ArrayList<Integer> cityIds;

    public WeatherService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // insert mock data
        cityIds = new ArrayList<>();
        cityIds.add(524901);
        cityIds.add(703448);

//        checkNetwork();
        getAllCityWeather();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //method stolen from SMAP lecture demo L8
    private boolean checkNetwork() {
        ConnectivityManager connectMan = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectMan.getActiveNetworkInfo();
        boolean conn = netInfo.isConnected();
        if(conn == true) {
            return true;
        } else {
            return false;
        }
    }

    public void getAllCityWeather() {
        if (queue == null) {
            queue = Volley.newRequestQueue(this);
        }

        String url = WEATHER_API_BASE_URL + WEATHER_API_GET_MULTIPLE + cityIdsToString(cityIds) + API_KEY;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        allCityWeather = weatherJsonToPojo(response);
                        //Push notification
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                allCityWeather = null;
                //Push notification
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

    private ArrayList<CityWeather> weatherJsonToPojo(String json) {
        if (gson == null) {
            gson = new Gson();
        }
        Type collectionType = new TypeToken<ArrayList<CityWeather>>(){}.getType();
        ArrayList<CityWeather> weather = gson.fromJson(json, collectionType);
        return weather;
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
}
