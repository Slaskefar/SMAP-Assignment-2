package com.example.jeppe.weatherapp.models;

import java.io.Serializable;

/**
 * Created by Jeppe on 27-10-2017.
 */

public class CityWeather implements Serializable {
    public int id;
    public String cityName;
    public Double temperature;
    public int humidity;
    public String weatherDescription;
    public String iconType;
}

