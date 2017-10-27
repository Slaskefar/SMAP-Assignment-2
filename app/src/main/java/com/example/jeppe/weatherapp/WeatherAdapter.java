package com.example.jeppe.weatherapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.jeppe.weatherapp.models.CityWeatherData;

import java.util.List;

/**
 * Created by kaspe on 27-10-2017.
 */

public class WeatherAdapter extends BaseAdapter {

    private Context context;
    private List<CityWeatherData> weatherData;
    private CityWeatherData weatherDataItem;

    public WeatherAdapter(@NonNull Context context, @NonNull List<CityWeatherData> weatherData) {
        this.context = context;
        this.weatherData = weatherData;
    }

    @Override
    public int getCount() {
        if(weatherData!=null) {
            return weatherData.size();
        } else {
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public CityWeatherData getItem(int position) {
        if(weatherData!=null) {
            return weatherData.get(position);
        } else {
            return null;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //we only need to create the views once, if not null we will reuse the existing view and update its values
        if (convertView == null) {
            LayoutInflater weatherInflator = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = weatherInflator.inflate(R.layout.weather_list_item, null);
        }

        weatherDataItem = weatherData.get(position);
        if(weatherDataItem!=null){
            //set the title text from the demo list
            /*TextView txtTitle = (TextView) convertView.findViewById(R.id.txtDemoTitle);
            txtTitle.setText(demo.getName());

            //set the description text from the demo list
            TextView txtDescription = (TextView) convertView.findViewById(R.id.txtDemoDescription);
            txtDescription.setText(demoprefix + "! " + demo.getDescription());*/
        }
        return convertView;
    }


}
