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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

        aShortToast("empezamos");


        //comprobar si es un intent desde descubrir canales
        String msgIntent = getIntent().getStringExtra("canal");
        if (msgIntent != null) {
            canal = msgIntent;

            aShortToast("iniciamos suscripcion a " + canal);

            Thread th = new Thread(new SuscribirThread(this, canal));
            th.start();

            ((TextView)findViewById(R.id.ficheros_canal_et_txt)).setText(canal);

            activarTV();
        }

        Button btn_2 = findViewById(R.id.ficheros_canal_btn_sub);
        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canal = ((TextView)findViewById(R.id.ficheros_canal_et_txt)).getText().toString();
                if (!canal.isEmpty()) {
                    Thread th = new Thread(new SuscribirThread(FicherosCanalActivity.this, canal));
                    th.start();
                    activarTV();
                } else {
                    aShortToast("indique algún canal");
                }
            }
        });

        Button btn = findViewById(R.id.ficheros_canal_btn_lista);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canal = ((TextView)findViewById(R.id.ficheros_canal_et_txt)).getText().toString();
                Log.e("aqui", canal);

                if (!canal.isEmpty()) {
                    activarTV();
                } else {
                    aShortToast("indique algún canal");
                }
            }
        });
    }

    private void activarTV() {
        FileStoreHelper helper = new FileStoreHelper(FicherosCanalActivity.this);
        FileStoreDB fileStoreDB = new FileStoreDB(helper);

        //activar list view
        ListView lv = findViewById(R.id.ficheros_canal_lv_lista_fich);
        adapter = new FicherosAdapter(this, fileStoreDB.getFilesChannel(canal));
        lv.setEnabled(true);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                aShortToast("aqui tocaria hacer algo");
                //obtener elemento
                model = fileStoreDB.getFilesChannel(canal).get(position);

                //valor a revisar
                int descargado = model.getDescargado();

                //preguntar si descargar fichero (alert dialog)
                AlertDialog.Builder builder = new AlertDialog.Builder(FicherosCanalActivity.this);
                if (descargado == 0) {
                    builder.setTitle("Opciones")
                            .setItems(new String[]{"Descargar", "Cancelar"}, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            //lanzar hilo de descarga
                                            Thread th = new Thread(new DescargarFicheroThread(FicherosCanalActivity.this,
                                                    model.getCanal(), model.getName()));
                                            th.start();
                                            Log.e("alertdialog","boton");
                                            break;
                                        case 1:
                                            break;
                                    }
                                }
                            }).show();
                } else {
                    builder.setTitle("Opciones")
                            .setItems(new String[]{"Abrir", "Cancelar"}, new DialogInterface.OnClickListener() {
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
                                                aShortToast("Error, no hay app para manejar este fichero");
                                                //aShortToast(obtenerTipoMIME(extension));
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

    public String obtenerTipoMIME(String extension) {
        switch (extension.toLowerCase()) {
            case "pdf":
                return "application/pdf";
            case "jpg":
            case "jpeg":
            case "png":
                return "image/*";
            case "txt":
                return "text/plain";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "mp3":
                return "audio/mpeg";
            default:
                return "application/*";
        }
    }


    public void prepareUIForDownload() {
        progressDialog  = new ProgressDialog(this);
        progressDialog.setMessage("Comprobando usuario");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void prepareUIAfterDownload(String ruta) {
        //almacenar ruta del fichero
        FileStoreHelper helper = new FileStoreHelper(this);
        FileStoreDB fileStoreDB = new FileStoreDB(helper);
        model.setRuta(ruta);
        fileStoreDB.descargaFichero(model);

        adapter.setDatos(fileStoreDB.getFilesChannel(canal));
        adapter.notifyDataSetChanged();

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