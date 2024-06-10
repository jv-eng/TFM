package com.jv.tfmprojectmobile.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.jv.tfmprojectmobile.R;
import com.jv.tfmprojectmobile.util.ClavesUtil;
import com.jv.tfmprojectmobile.util.storage.PreferencesManage;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (PreferencesManage.userExists(this) && PreferencesManage.userExists(this) && PreferencesManage.dateValid(this)) {
            Intent i = new Intent(MainActivity.this, MenuActivity.class);
            startActivity(i);
        } else {
            PreferencesManage.removeUser(this);
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        }
    }
}