package com.example.jeppe.weatherapp.DAL;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.jeppe.weatherapp.models.CityWeatherData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;


public class DataHelper {
    private SharedPreferences sharedPref;
    private final String CITYKEY = "CITY_LIST";
    private Gson gson;
    private Type cityWeatherType;
    public DataHelper(Context context){
        sharedPref = context.getSharedPreferences("Pref", Context.MODE_PRIVATE);
        gson = new Gson();
        cityWeatherType = new TypeToken<ArrayList<CityWeatherData>>(){}.getType();
    }

    public ArrayList<CityWeatherData> getCities(){
        String citiesString = sharedPref.getString(CITYKEY,null);
        if(citiesString != null){
            ArrayList<CityWeatherData> weatherData = gson.fromJson(citiesString, cityWeatherType);
            return weatherData;
        }
        return new ArrayList<>();
    }

    public void addCity(CityWeatherData city){
        String citiesString = sharedPref.getString(CITYKEY, null);
        ArrayList<CityWeatherData> weatherData = null;
        if (citiesString != null) {
            weatherData = gson.fromJson(citiesString, cityWeatherType);
            weatherData.add(city);
            String newWeatherDataString = gson.toJson(weatherData, cityWeatherType);
            sharedPref.edit().putString(CITYKEY, newWeatherDataString).commit();
        } else {
            weatherData = new ArrayList<>();
            weatherData.add(city);
            String newWeatherDataString = gson.toJson(weatherData, cityWeatherType);
            sharedPref.edit().putString(CITYKEY, newWeatherDataString).commit();
        }
    }

    public void deleteCity(CityWeatherData city){
        String citiesString = sharedPref.getString(CITYKEY, null);
        ArrayList<CityWeatherData> weatherData = null;
        if (citiesString != null) {
            weatherData = gson.fromJson(citiesString, cityWeatherType);
            for (Iterator<CityWeatherData> iter = weatherData.listIterator(); iter.hasNext(); ) {
                CityWeatherData w = iter.next();
                if (w.id == city.id) {
                    iter.remove();
                }
            }
            String newWeatherDataString = gson.toJson(weatherData, cityWeatherType);
            sharedPref.edit().putString(CITYKEY, newWeatherDataString).commit();
        }
    }


}
