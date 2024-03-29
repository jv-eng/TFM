package com.jv.tfmprojectmobile.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.jv.tfmprojectmobile.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Intent i = new Intent(MainActivity.this, LoginActivity.class);
        Intent i = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(i);
    }
}