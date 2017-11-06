package com.example.jeppe.weatherapp.DAL;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.jeppe.weatherapp.models.CityWeather;
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
        cityWeatherType = new TypeToken<ArrayList<CityWeather>>(){}.getType();
    }

    public ArrayList<CityWeather> getCities(){
        String citiesString = sharedPref.getString(CITYKEY,null);
        if(citiesString != null){
            ArrayList<CityWeather> weatherData = gson.fromJson(citiesString, cityWeatherType);
            return weatherData;
        }
        return new ArrayList<>();
    }

    public void addCity(CityWeather city){
        String citiesString = sharedPref.getString(CITYKEY, null);
        ArrayList<CityWeather> weatherData = null;
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

    public void deleteCity(CityWeather city){
        String citiesString = sharedPref.getString(CITYKEY, null);
        ArrayList<CityWeather> weatherData = null;
        if (citiesString != null) {
            weatherData = gson.fromJson(citiesString, cityWeatherType);
            for (Iterator<CityWeather> iter = weatherData.listIterator(); iter.hasNext(); ) {
                CityWeather w = iter.next();
                if (w.id == city.id) {
                    iter.remove();
                }
            }
            String newWeatherDataString = gson.toJson(weatherData, cityWeatherType);
            sharedPref.edit().putString(CITYKEY, newWeatherDataString).commit();
        }
    }


}
