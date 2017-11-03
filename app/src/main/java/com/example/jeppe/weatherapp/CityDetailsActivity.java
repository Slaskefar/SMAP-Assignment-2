package com.example.jeppe.weatherapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.jeppe.weatherapp.DAL.DataHelper;

public class CityDetailsActivity extends AppCompatActivity {
    Button btnRemove;
    Button btnOk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_details);

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
        DataHelper dataHelper = new DataHelper(this);
        //dataHelper.deleteCity();
    }

    private void ok() {
        finish();
    }
}
