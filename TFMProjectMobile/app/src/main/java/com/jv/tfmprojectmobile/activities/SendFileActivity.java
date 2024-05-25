package com.jv.tfmprojectmobile.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.jv.tfmprojectmobile.R;
import com.jv.tfmprojectmobile.util.AuxiliarUtil;
import com.jv.tfmprojectmobile.util.NavigationViewConfiguration;
import com.jv.tfmprojectmobile.util.storage.PreferencesManage;
import com.jv.tfmprojectmobile.util.threads.LoginThread;
import com.jv.tfmprojectmobile.util.threads.SendFileThread;
import com.jv.tfmprojectmobile.util.threads.SuscribirThread;

public class SendFileActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Uri selectedFileUri;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_file);

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

        String msgIntent = getIntent().getStringExtra("canal");
        if (msgIntent != null) {
           ((TextView)findViewById(R.id.send_file_te_channel)).setText(msgIntent);
        }


        Button buscarFich = findViewById(R.id.send_file_btn_seleccionar);
        buscarFich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent para seleccionar el archivo
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                // Iniciar el selector de archivos
                startActivityForResult(Intent.createChooser(intent, SendFileActivity.this.getString(R.string.send_file_btn_select)), 1);
            }
        });

        Button enviarFich = findViewById(R.id.send_file_btn_enviar);
        enviarFich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedFileUri != null) {
                    String txt = ((TextInputEditText)findViewById(R.id.send_file_te_channel)).getText().toString();
                    Thread th = new Thread(new SendFileThread(SendFileActivity.this, selectedFileUri, txt));
                    th.start();

                    Log.e("nombre fich", AuxiliarUtil.getFileName(SendFileActivity.this,selectedFileUri));
                } else {
                    aShortToast(SendFileActivity.this.getString(R.string.send_file_msg_no_file_selected));
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Obtener la URI del archivo seleccionado
            selectedFileUri = data.getData();
            ((TextView)findViewById(R.id.send_file_tv_file)).setText(
                    this.getString(R.string.send_file_tv_file) + " " + AuxiliarUtil.getFileName(this, selectedFileUri));
        }
    }

    public void prepareUIForDownload() {
        progressDialog  = new ProgressDialog(this);
        progressDialog.setMessage(this.getString(R.string.send_file_msg_sending));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void prepareUIAfterDownload() {
        progressDialog.dismiss();
    }

    private void setUsernameIntoNavDrawer(){
        // Get Username from LoginActivity.
        Bundle bundle = getIntent().getExtras();
        String username = PreferencesManage.userName(this);
        // Get Header from navigationView
        View header = navigationView.getHeaderView(0);
        // Set Username into the header
        TextView tv_username = (TextView) header.findViewById(R.id.tv_username_title);
        tv_username.setText(username);
    }

    public void aShortToast(String msg){
        Toast.makeText(SendFileActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}