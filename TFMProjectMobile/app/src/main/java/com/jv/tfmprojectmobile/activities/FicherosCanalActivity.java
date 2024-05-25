package com.jv.tfmprojectmobile.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.jv.tfmprojectmobile.R;
import com.jv.tfmprojectmobile.models.FileStoreModel;
import com.jv.tfmprojectmobile.util.FicherosAdapter;
import com.jv.tfmprojectmobile.util.NavigationViewConfiguration;
import com.jv.tfmprojectmobile.util.storage.FileStoreDB;
import com.jv.tfmprojectmobile.util.storage.FileStoreHelper;
import com.jv.tfmprojectmobile.util.storage.PreferencesManage;
import com.jv.tfmprojectmobile.util.threads.DescargarFicheroThread;
import com.jv.tfmprojectmobile.util.threads.SuscribirThread;

import java.io.File;

public class FicherosCanalActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private String canal;
    private ProgressDialog progressDialog;
    private FileStoreModel model; //cuando se descargue, almacenar
    private FicherosAdapter adapter;

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

        //comprobar si es un intent desde descubrir canales
        String msgIntent = getIntent().getStringExtra("canal");
        if (msgIntent != null) {
            canal = msgIntent;

            aShortToast(this.getString(R.string.ficheros_canal_msg_start_subscription) + canal);

            Thread th = new Thread(new SuscribirThread(this, canal));
            th.start();

            ((TextView)findViewById(R.id.ficheros_canal_et_txt)).setText(canal);

            activarTV(true);
        } else {
            activarTV(false);
        }

        Button btn_2 = findViewById(R.id.ficheros_canal_btn_sub);
        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canal = ((TextView)findViewById(R.id.ficheros_canal_et_txt)).getText().toString();
                if (!canal.isEmpty()) {
                    Thread th = new Thread(new SuscribirThread(FicherosCanalActivity.this, canal));
                    th.start();
                    activarTV(true);
                } else {
                    aShortToast(FicherosCanalActivity.this.getString(R.string.ficheros_canal_msg_write_channel));
                }
            }
        });

        Button btn = findViewById(R.id.ficheros_canal_btn_lista);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canal = ((TextView)findViewById(R.id.ficheros_canal_et_txt)).getText().toString();

                if (!canal.isEmpty()) {
                    activarTV(true);
                } else {
                    aShortToast(FicherosCanalActivity.this.getString(R.string.ficheros_canal_msg_write_channel));
                }
            }
        });
    }

    private void activarTV(boolean channel) {
        FileStoreHelper helper = new FileStoreHelper(FicherosCanalActivity.this);
        FileStoreDB fileStoreDB = new FileStoreDB(helper);

        //activar list view
        ListView lv = findViewById(R.id.ficheros_canal_lv_lista_fich);
        if (channel) adapter = new FicherosAdapter(this, fileStoreDB.getFilesChannel(canal));
        else adapter = new FicherosAdapter(this, fileStoreDB.getChannels());
        lv.setEnabled(true);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //obtener elemento
                model = fileStoreDB.getFilesChannel(canal).get(position);

                //valor a revisar
                int descargado = model.getDescargado();

                //preguntar si descargar fichero (alert dialog)
                AlertDialog.Builder builder = new AlertDialog.Builder(FicherosCanalActivity.this);
                if (descargado == 0) {
                    builder.setTitle(FicherosCanalActivity.this.getString(R.string.ficheros_canal_msg_alertdialog_options))
                            .setItems(new String[]{
                                    FicherosCanalActivity.this.getString(R.string.ficheros_canal_msg_alertdialog_download),
                                    FicherosCanalActivity.this.getString(R.string.ficheros_canal_msg_alertdialog_cancel)}, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            //lanzar hilo de descarga
                                            Thread th = new Thread(new DescargarFicheroThread(FicherosCanalActivity.this,
                                                    model.getCanal(), model.getName()));
                                            th.start();
                                            break;
                                        case 1:
                                            break;
                                    }
                                }
                            }).show();
                } else {
                    builder.setTitle(FicherosCanalActivity.this.getString(R.string.ficheros_canal_msg_alertdialog_options))
                            .setItems(new String[]{
                                    FicherosCanalActivity.this.getString(R.string.ficheros_canal_msg_alertdialog_open),
                                            FicherosCanalActivity.this.getString(R.string.ficheros_canal_msg_alertdialog_cancel)}, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            //lanzar un intent
                                            String filePath = model.getRuta(); // Ruta de tu archivo

                                            File file = new File(filePath);
                                            //Uri uri = Uri.fromFile(file);
                                            Uri uri = FileProvider.getUriForFile(FicherosCanalActivity.this,
                                                    getApplicationContext().getPackageName() + ".fileprovider", file);

                                            int indicePunto = model.getName().lastIndexOf('.');
                                            String extension = model.getName().substring(indicePunto + 1);

                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.addFlags( Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                            intent.setDataAndType(uri, "*/*");

                                            if (intent.resolveActivity(getPackageManager()) != null) {
                                                startActivity(intent);
                                            } else {
                                                aShortToast(FicherosCanalActivity.this.getString(R.string.ficheros_canal_msg_no_app));
                                            }

                                            break;
                                        case 1:
                                            break;
                                    }
                                }
                            }).show();
                }
            }
        });

    }

    public void prepareUIForDownload() {
        progressDialog  = new ProgressDialog(this);
        progressDialog.setMessage(this.getString(R.string.ficheros_canal_msg_download));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void prepareUIAfterDownload(String ruta) {
        //almacenar ruta del fichero
        if (ruta != null) {
            FileStoreHelper helper = new FileStoreHelper(this);
            FileStoreDB fileStoreDB = new FileStoreDB(helper);
            model.setRuta(ruta);
            fileStoreDB.descargaFichero(model);
            fileStoreDB.insertChannel(canal);

            adapter.setDatos(fileStoreDB.getFilesChannel(canal));
            adapter.notifyDataSetChanged();
        }

        //quitar barra de progreso
        progressDialog.dismiss();
    }

    public void updateAdapter() {
        FileStoreHelper helper = new FileStoreHelper(this);
        FileStoreDB fileStoreDB = new FileStoreDB(helper);
        adapter = new FicherosAdapter(this, fileStoreDB.getFilesChannel(canal));
        adapter.notifyDataSetChanged();
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
        Toast.makeText(FicherosCanalActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}