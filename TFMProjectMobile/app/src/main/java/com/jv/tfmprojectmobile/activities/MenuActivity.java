package com.jv.tfmprojectmobile.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.jv.tfmprojectmobile.R;
import com.jv.tfmprojectmobile.util.NavigationViewConfiguration;

import java.io.Serializable;

public class MenuActivity extends AppCompatActivity implements Serializable {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        setUsernameIntoNavDrawer(); //metodo auxiliar

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationViewConfiguration.configurarNavView(drawerLayout, navigationView, this);

        //logica botones
        Button btn_crear_canal = findViewById(R.id.menu_btn_crear_canal);
        btn_crear_canal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this, CreateChannelActivity.class);
                startActivity(i);
            }
        });

        Button btn_ver_canales = findViewById(R.id.menu_btn_ver_canales_cercanos);
        btn_ver_canales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this, DescubrirCanalesActivity.class);
                startActivity(i);
            }
        });

        Button btn_enviar_fichero = findViewById(R.id.menu_btn_enviar_fichero);
        btn_enviar_fichero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aShortToast("enviar fichero");
            }
        });

        Button btn_ficheros_recibidos = findViewById(R.id.menu_btn_ficheros_canal);
        btn_ficheros_recibidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aShortToast("ficheros recibidos");
            }
        });
    }

    private void setUsernameIntoNavDrawer(){
        // Get Username from LoginActivity.
        Bundle bundle = getIntent().getExtras();
        String username = "Testing-User";
        // Get Header from navigationView
        View header = navigationView.getHeaderView(0);
        // Set Username into the header
        TextView tv_username = (TextView) header.findViewById(R.id.tv_username_title);
        tv_username.setText(username);
    }

    public void aShortToast(String msg){
        Toast.makeText(MenuActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}