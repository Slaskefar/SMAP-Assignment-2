package com.example.jeppe.weatherapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jeppe.weatherapp.models.CityWeather;

import java.util.List;

/**
 * Created by kaspe on 27-10-2017.
 */

public class WeatherAdapter extends BaseAdapter {

    private Context context;
    public List<CityWeather> weatherData;
    private CityWeather weatherDataItem;

    public WeatherAdapter(@NonNull Context context, @NonNull List<CityWeather> weatherData) {
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
    public CityWeather getItem(int position) {
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
            LayoutInflater weatherInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = weatherInflater.inflate(R.layout.weather_list_item, null);
        }

        weatherDataItem = weatherData.get(position);
        if(weatherDataItem!=null){
            //set the properties from weatherDataItem
            TextView txtListCityName = convertView.findViewById(R.id.txtListCityName);
            txtListCityName.setText(weatherDataItem.cityName);

            TextView txtListHumidity = convertView.findViewById(R.id.txtListHumidity);
            txtListHumidity.setText(Integer.toString(weatherDataItem.humidity));

            TextView txtListTemperature = convertView.findViewById(R.id.txtListTemperature);
            txtListTemperature.setText(Double.toString(weatherDataItem.temperature));

            /* Need to be set somehow
            ImageView imgListWeatherIcon = convertView.findViewById(R.id.imgListWeatherIcon);
            imgListWeatherIcon.setImageBitmap(something);
            */
        }
        return convertView;
    }


}
