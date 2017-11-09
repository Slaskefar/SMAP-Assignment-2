package com.example.jeppe.weatherapp.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class WeatherService extends Service {

    public static final String WEATHER_API_BASE_URL = "http://api.openweathermap.org/data/2.5";
    public static final String API_KEY = "&APPID=7bcb820624561125d1c7de1feed18777";
    public static final String WEATHER_API_GET_MULTIPLE = "/group?id=";
    public static final String WEATHER_API_GET_SINGLE = "/weather?q=";

    RequestQueue queue;
    Gson gson;
    CityWeather singleCityWeather;
    List<CityWeather> allCityWeather;
    ArrayList<Integer> cityIds;
    private DataHelper dataHelper;

    public WeatherService() {
    }

    @Override
    public void onCreate(){
        super.onCreate();
        dataHelper = new DataHelper(this);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        checkNetwork();
        getAllCityWeather();
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
                        for(Iterator<CityWeather> city = allCityWeather.iterator(); city.hasNext();){

                            dataHelper.editCity(city.next());
                        }

                        broadcastWeather();
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
        cityWeather.temperature = weatherData.getMain().getTemp();
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
        Log.d("Weatherservice", "broadcasting weather");
        Intent intent = new Intent("weather-event");
//        if (gson == null) {
//            gson = new Gson();
//        }
//        intent.putExtra("weather", gson.toJson(allCityWeather));
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        Bundle args = new Bundle();
        args.putSerializable("weatherObj", (Serializable)allCityWeather);
        intent.putExtra("weather", args);
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
            cityWeather.temperature = list.getMain().getTemp();
            cityWeather.iconType = list.getWeather().iterator().next().getIcon();
            cityWeather.id = list.getId();

            cityWeatherList.add(cityWeather);
        }
        return cityWeatherList;
    }

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
                        getAllCityWeather();
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
}
