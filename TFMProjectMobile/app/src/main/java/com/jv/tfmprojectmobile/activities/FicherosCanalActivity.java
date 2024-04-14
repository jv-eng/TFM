package com.jv.tfmprojectmobile.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.jv.tfmprojectmobile.R;
import com.jv.tfmprojectmobile.util.NavigationViewConfiguration;
import com.jv.tfmprojectmobile.util.storage.FileStoreDB;
import com.jv.tfmprojectmobile.util.storage.FileStoreHelper;

public class FicherosCanalActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ficheros_canal);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        setUsernameIntoNavDrawer();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationViewConfiguration.configurarNavView(drawerLayout, navigationView, this);

        Button btn = findViewById(R.id.ficheros_canal_btn_lista);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String canal = ((TextView)findViewById(R.id.ficheros_canal_et_txt)).getText().toString();

                if (!canal.equals("")) {
                    logicaBtn();
                } else {
                    aShortToast("No hay ningun fichero asociado a este canal");
                }
            }
        });
    }

    private void logicaBtn() {
        FileStoreHelper helper = new FileStoreHelper(FicherosCanalActivity.this);
        FileStoreDB fileStoreDB = new FileStoreDB(helper);
        //miListView.setEnabled(true);
        //preguntar si descargar fichero (alert dialog)
        //habilitar la lista
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
        Toast.makeText(FicherosCanalActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}