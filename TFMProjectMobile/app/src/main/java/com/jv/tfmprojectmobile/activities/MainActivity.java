package com.jv.tfmprojectmobile.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.jv.tfmprojectmobile.R;
import com.jv.tfmprojectmobile.models.FileStoreModel;
import com.jv.tfmprojectmobile.util.AuxiliarUtil;
import com.jv.tfmprojectmobile.util.storage.FileStoreDB;
import com.jv.tfmprojectmobile.util.storage.FileStoreHelper;
import com.jv.tfmprojectmobile.util.storage.PreferencesManage;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*PreferencesManage.storeUser(this, "pepe", "pepe@mail.com", "123456", "88", "888");
        FileStoreHelper helper = new FileStoreHelper(this);
        FileStoreDB fileStoreDB = new FileStoreDB(helper);
        FileStoreModel model = new FileStoreModel(
                AuxiliarUtil.generateUUID(), "fich", 0, "", "channel"
        );
        fileStoreDB.save(model);*/


        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        //Intent i = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(i);
    }

    private void test() {
        //revisar permisos

        //activar actividad
        if (PreferencesManage.userExists(this) && PreferencesManage.dateValid(this)) {
            Intent i = new Intent(MainActivity.this, MenuActivity.class);
            startActivity(i);
        } else {
            PreferencesManage.removeUser(this);
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        }
    }
}