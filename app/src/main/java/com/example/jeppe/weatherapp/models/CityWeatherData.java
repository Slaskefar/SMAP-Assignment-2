package com.example.jeppe.weatherapp.models;

/**
 * Created by Jeppe on 26-10-2017.
 */

public class CityWeatherData {
    public String id;
    public String cityName;
    public int temperature;
    public int humidity;
    public String weatherDescription;
    // To do - add all weather types to enum
    public enum iconType { SUN, CLOUDY, RAIN }
}
