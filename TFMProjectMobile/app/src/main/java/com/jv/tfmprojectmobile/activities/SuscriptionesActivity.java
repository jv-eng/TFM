package com.jv.tfmprojectmobile.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.jv.tfmprojectmobile.R;
import com.jv.tfmprojectmobile.util.NavigationViewConfiguration;
import com.jv.tfmprojectmobile.util.storage.FileStoreDB;
import com.jv.tfmprojectmobile.util.storage.FileStoreHelper;
import com.jv.tfmprojectmobile.util.storage.PreferencesManage;
import com.jv.tfmprojectmobile.util.threads.DescargarFicheroThread;
import com.jv.tfmprojectmobile.util.threads.DesuscribirThread;

import java.util.List;

public class SuscriptionesActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private List<String> lista;
    private ArrayAdapter<String> adapter;
    private FileStoreHelper helper;
    private FileStoreDB fileStoreDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suscriptiones);
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

        helper = new FileStoreHelper(SuscriptionesActivity.this);
        fileStoreDB = new FileStoreDB(helper);
        lista = fileStoreDB.getChannelsStr();
        ListView lv = findViewById(R.id.suscripciones_lv);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lista);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String elementoSeleccionado = lista.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(SuscriptionesActivity.this);
                builder.setTitle(SuscriptionesActivity.this.getString(R.string.suscripciones_alert_dialog_title) + elementoSeleccionado)
                    .setItems(new String[]{
                            SuscriptionesActivity.this.getString(R.string.suscripciones_alert_dialog_title),
                            SuscriptionesActivity.this.getString(R.string.suscripciones_alert_dialog_close)},
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    Thread th = new Thread(new DesuscribirThread(SuscriptionesActivity.this, elementoSeleccionado));
                                    th.start();
                                    break;
                                case 1:
                                    break;
                            }
                        }
                }).show();
            }
        });
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
        Toast.makeText(SuscriptionesActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    public void operationFinnished(int res, String channel) {
        String msg;
        if (res > 0) {
            msg = this.getString(R.string.suscripciones_msg_ok);

            //borrar sub
            fileStoreDB.deleteChannel(channel);
            //obtener lista
            lista = fileStoreDB.getChannelsStr();
            //actualizar
            adapter.notifyDataSetChanged();
        }
        else msg = this.getString(R.string.suscripciones_msg_not_ok);
        aShortToast(msg);
    }
}