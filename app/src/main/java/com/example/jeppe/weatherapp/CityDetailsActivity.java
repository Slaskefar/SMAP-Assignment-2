package com.example.jeppe.weatherapp;

import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    private Boolean bound = false;


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
    }

    private void removeCity() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Globals.CITY_DETAILS_REMOVE_CITY, currentCityWeather.id);
        setResult(Globals.REMOVE_CITY, returnIntent);
        finish();
    }

    private BroadcastReceiver weatherReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getBundleExtra("weather");
            ArrayList<CityWeather> weather = (ArrayList<CityWeather>)bundle.getSerializable("weatherObj");
            //do something with weather
        }
    };

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
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(weatherReceiver, new IntentFilter("weather-event"));
        super.onResume();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(weatherReceiver);
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, WeatherService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
        bound = false;
    }

    private void ok() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
