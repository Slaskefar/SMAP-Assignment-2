package com.example.jeppe.weatherapp.DAL;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;


public class DataHelper {
    private SharedPreferences sharedPref;
    private final String CITYKEY = "CITY_LIST";
    public DataHelper(Context context){
        sharedPref = context.getSharedPreferences("Pref", Context.MODE_PRIVATE);
    }

    public Set<String> getCitys(){
        Set<String> citys = sharedPref.getStringSet(CITYKEY, null);
        return citys;
    }

    public void addCity(String city){
        Set<String> citys = sharedPref.getStringSet(CITYKEY, new HashSet<String>());
        if (citys != null) {
            citys.add(city);
        }
        sharedPref.edit().putStringSet(CITYKEY, citys).commit();
    }

    public void deleteCity(String city){
        Set<String> citys = sharedPref.getStringSet(CITYKEY, null);
        citys.remove(city);
        sharedPref.edit().putStringSet(CITYKEY, citys).commit();
    }


}
