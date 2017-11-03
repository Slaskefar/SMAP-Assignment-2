package com.example.jeppe.weatherapp.models.weatherDataResponse;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherData {
    @SerializedName("cnt")
    @Expose
    private Integer cnt;
    @SerializedName("list")
    @Expose
    private java.util.List<com.example.jeppe.weatherapp.models.weatherDataResponse.List> list = null;

    public Integer getCnt() {
        return cnt;
    }

    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }

    public java.util.List<com.example.jeppe.weatherapp.models.weatherDataResponse.List> getList() {
        return list;
    }

    public void setList(java.util.List<com.example.jeppe.weatherapp.models.weatherDataResponse.List> list) {
        this.list = list;
    }

}