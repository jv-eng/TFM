package com.jv.tfmprojectmobile.activities;

import static java.nio.charset.StandardCharsets.UTF_8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.jv.tfmprojectmobile.R;
import com.jv.tfmprojectmobile.util.ClavesUtil;
import com.jv.tfmprojectmobile.util.NavigationViewConfiguration;
import com.jv.tfmprojectmobile.util.storage.PreferencesManage;
import com.jv.tfmprojectmobile.util.threads.CreateChannelThread;

import java.nio.charset.StandardCharsets;

public class CreateChannelActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private String opponentEndpointId;
    private static final String TAG = "CrearCanal";
    private ConnectionsClient connectionsClient;
    private static final String[] REQUIRED_PERMISSIONS =
            new String[] {
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            };
    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;
    private static final Strategy STRATEGY = Strategy.P2P_STAR;

    public void prepareUIForDownload() {
        progressDialog  = new ProgressDialog(this);
        progressDialog.setMessage(String.valueOf(R.string.create_channel_msg_create));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    public void prepareUIAfterDownload() {
        progressDialog.dismiss();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_channel);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        setUsernameIntoNavDrawer();
        setButtonLogic();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationViewConfiguration.configurarNavView(drawerLayout, navigationView, this);
        TextView nameView = findViewById(R.id.create_channel_tv);
        nameView.append(PreferencesManage.userMail(this));

        connectionsClient = Nearby.getConnectionsClient(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!hasPermissions(this, getRequiredPermissions())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(getRequiredPermissions(), REQUEST_CODE_REQUIRED_PERMISSIONS);
            }
        }
    }
    protected String[] getRequiredPermissions() {
        return REQUIRED_PERMISSIONS;
    }
    public static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onStop() {
        //connectionsClient.stopAllEndpoints();
        super.onStop();
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
        Toast.makeText(CreateChannelActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void setButtonLogic() {
        Button create_channel_btn_create = findViewById(R.id.create_channel_btn_create);
        create_channel_btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = ((TextInputEditText)findViewById(R.id.create_channel_txt_name)).getText().toString();

                if (!str.isEmpty()) {
                    Thread th = new Thread(new CreateChannelThread(CreateChannelActivity.this, str));
                    th.start();
                } else aShortToast(CreateChannelActivity.this.getString(R.string.create_channel_msg_name));
            }
        });

        Button create_channel_btn_announce = findViewById(R.id.create_channel_btn_anounce);
        create_channel_btn_announce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAdvertising();
            }
        });

        Button create_channel_btn_disconnect = findViewById(R.id.create_channel_btn_disconnect);
        create_channel_btn_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });

        Button create_channel_btn_send = findViewById(R.id.create_channel_btn_send);
        create_channel_btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String canal = ((TextView)findViewById(R.id.create_channel_txt_name)).getText().toString();
                if (!canal.isEmpty()) {
                    Intent i = new Intent(CreateChannelActivity.this, SendFileActivity.class);
                    i.putExtra("canal", canal);
                    startActivity(i);
                } else {
                    aShortToast(CreateChannelActivity.this.getString(R.string.discover_channel_msg_no_channel_found));
                }
            }
        });
    }




/////////////////////////////////////////////////////////////////////////////////////////////////////

    public void disconnect() {
        connectionsClient.disconnectFromEndpoint(opponentEndpointId);
        aShortToast(this.getString(R.string.create_channel_msg_disconnect));
    }

    // Callbacks for receiving payloads
    private final PayloadCallback payloadCallback =
            new PayloadCallback() {
                @Override
                public void onPayloadReceived(String endpointId, Payload payload) {
                    String payloadMessage = new String(payload.asBytes(), StandardCharsets.UTF_8);
                    Toast.makeText(CreateChannelActivity.this, String.format("onPayloadReceived(endpointId=%s, payload=%s)", endpointId, payloadMessage), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
                    //lo que queramos hacer
                    Toast.makeText(CreateChannelActivity.this, "payloadCallback", Toast.LENGTH_SHORT).show();
                }
            };

    // Callbacks for connections to other devices
    private final ConnectionLifecycleCallback connectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                    Log.i(TAG, "onConnectionInitiated: accepting connection");
                    connectionsClient.stopDiscovery();
                    connectionsClient.stopAdvertising();
                    Toast.makeText(CreateChannelActivity.this,
                            String.format("onConnectionInitiated(endpointId=%s, endpointName=%s)",
                                    endpointId, connectionInfo.getEndpointName())
                            , Toast.LENGTH_SHORT).show();
                    connectionsClient.acceptConnection(endpointId, payloadCallback)
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(CreateChannelActivity.this, "Error al crear conexion", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                }

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {
                    if (result.getStatus().isSuccess()) {
                        Log.i(TAG, "onConnectionResult: connection successful");

                        connectionsClient.stopDiscovery();
                        connectionsClient.stopAdvertising();

                        opponentEndpointId = endpointId;

                        //conexion exitosa
                        aShortToast(CreateChannelActivity.this.getString(R.string.create_channel_msg_connection_correct));
                        sendMSG();
                    } else {
                        Log.i(TAG, "onConnectionResult: connection failed");
                        aShortToast(CreateChannelActivity.this.getString(R.string.create_channel_msg_connection_fail));
                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    Log.i(TAG, "onDisconnected: disconnected from the opponent");
                }
            };

    private void startAdvertising() {
        // Note: Advertising may fail. To keep this demo simple, we don't handle failures.
        connectionsClient
                .startAdvertising(
                        PreferencesManage.userMail(this), getPackageName(), connectionLifecycleCallback,
                        new AdvertisingOptions.Builder().setStrategy(STRATEGY).build())
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unusedResult) {
                                aShortToast(CreateChannelActivity.this.getString(R.string.create_channel_msg_announcing));
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //aShortToast(CreateChannelActivity.this.getString(R.string.create_channel_msg_announcing_fail));
                                aShortToast(CreateChannelActivity.this.getString(R.string.create_channel_msg_announcing));
                            }
                        });
    }

    //enviar datos
    private void sendMSG() {
        String str = ((TextInputEditText)findViewById(R.id.create_channel_txt_name)).getText().toString();
        //str = ClavesUtil.encryptPrivKey(this, str);
        //cifrar
        connectionsClient.sendPayload(
                        opponentEndpointId, Payload.fromBytes(str.getBytes(UTF_8)))
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                aShortToast(CreateChannelActivity.this.getString(R.string.create_channel_msg_send_data_fail));
                            }
                        });

    }

}