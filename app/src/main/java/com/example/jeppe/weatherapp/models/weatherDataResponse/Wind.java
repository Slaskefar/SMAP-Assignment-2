package com.example.jeppe.weatherapp.models.weatherDataResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jeppe on 02-11-2017.
 */

public class Wind {
    @SerializedName("speed")
    @Expose
    private Integer speed;
    @SerializedName("deg")
    @Expose
    private Integer deg;

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public Integer getDeg() {
        return deg;
    }

    public void setDeg(Integer deg) {
        this.deg = deg;
    }
}
