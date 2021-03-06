package com.example.jeppe.weatherapp;

import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jeppe.weatherapp.DAL.DataHelper;
import com.example.jeppe.weatherapp.models.CityWeather;
import com.example.jeppe.weatherapp.services.WeatherService;

import java.util.ArrayList;

public class CityDetailsActivity extends AppCompatActivity {
    Button btnRemove;
    Button btnOk;
    TextView txtHumidity;
    TextView txtTemperature;
    TextView txtCityName;
    TextView txtDescription;
    CityWeather currentCityWeather;
    WeatherService weatherService;
    ImageView imageIcon;
    private Boolean bound = false;
    private BroadcastReceiver weatherReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_details);

        Intent fromListIntent = getIntent();
        Bundle bundle = fromListIntent.getBundleExtra(Globals.CITY_DETAILS_BUNDLE);
        currentCityWeather = (CityWeather) bundle.getSerializable(Globals.CITY_DETAILS_SINGLE_CITY);

        txtCityName = findViewById(R.id.txtCityName);
        txtCityName.setText(currentCityWeather.cityName);

        txtDescription = findViewById(R.id.txtDescription);
        txtDescription.setText(currentCityWeather.weatherDescription);

       imageIcon = findViewById(R.id.imgWeatherIcon);

        int id = getResources().getIdentifier("icon_"+currentCityWeather.iconType, "drawable", getPackageName());
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                id);
        imageIcon.setImageBitmap(icon);

        txtHumidity = findViewById(R.id.txtHumidity);
        txtHumidity.setText(Integer.toString(currentCityWeather.humidity));

        txtTemperature = findViewById(R.id.txtTemperature);
        txtTemperature.setText(Double.toString(currentCityWeather.temperature));

        btnRemove = findViewById(R.id.btnRemove);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeCity();
            }
        });

        btnOk = findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ok();
            }
        });

        Intent intent = new Intent(this, WeatherService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        this.weatherReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                currentCityWeather = weatherService.getSingleCity(currentCityWeather);
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(this.weatherReceiver, new IntentFilter("weather-event"));
    }

    private void removeCity() {
        CityWeather cityToRemove = new CityWeather();
        cityToRemove.id = currentCityWeather.id;
        weatherService.removeCity(cityToRemove);

        finish();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            WeatherService.WeatherBinder binder = (WeatherService.WeatherBinder)iBinder;
            weatherService = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.weatherReceiver);
        unbindService(mConnection);
        bound = false;
    }

    private void ok() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
