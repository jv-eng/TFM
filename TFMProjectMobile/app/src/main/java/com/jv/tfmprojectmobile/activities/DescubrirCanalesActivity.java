package com.jv.tfmprojectmobile.activities;

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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.jv.tfmprojectmobile.R;
import com.jv.tfmprojectmobile.util.AuxiliarUtil;
import com.jv.tfmprojectmobile.util.ClavesUtil;
import com.jv.tfmprojectmobile.util.NavigationViewConfiguration;
import com.jv.tfmprojectmobile.util.storage.PreferencesManage;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;

public class DescubrirCanalesActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private String opponentEndpointId;
    private static final String TAG = "DescubrirCanal";
    private String canal = null;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descubrir_canales);

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
        TextView nameView = findViewById(R.id.descubrir_canales_tv_usuario);
        nameView.setText(PreferencesManage.userMail(this));

        connectionsClient = Nearby.getConnectionsClient(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
            //aShortToast("revisando permisos");
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void prepareUIForDownload() {
        progressDialog  = new ProgressDialog(this);
        progressDialog.setMessage("Comprobando usuario");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void prepareUIAfterDownload() {
        progressDialog.dismiss();
    }

    private void setButtonLogic() {
        Button descubrir_canales_btn_buscar = findViewById(R.id.descubrir_canales_btn_buscar);
        descubrir_canales_btn_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDiscovery();
            }
        });

        Button descubrir_canales_btn_fin = findViewById(R.id.descubrir_canales_btn_fin);
        descubrir_canales_btn_fin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });

        Button descubrir_canales_btn_suscribir = findViewById(R.id.descubrir_canales_btn_suscribir);
        descubrir_canales_btn_suscribir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canal != null && !canal.isEmpty()) {
                    Intent i = new Intent(DescubrirCanalesActivity.this, FicherosCanalActivity.class);
                    i.putExtra("canal", canal);
                    startActivity(i);
                } else {
                    aShortToast(DescubrirCanalesActivity.this.getString(R.string.discover_channel_msg_no_channel_found));
                }
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
        Toast.makeText(DescubrirCanalesActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    public void disconnect() {
        connectionsClient.disconnectFromEndpoint(opponentEndpointId);
    }





    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private void startDiscovery() {
        // Note: Discovery may fail. To keep this demo simple, we don't handle failures.
        //connectionsClient.startDiscovery(getPackageName(), endpointDiscoveryCallback,new DiscoveryOptions.Builder().setStrategy(STRATEGY).build());
        connectionsClient
                .startDiscovery(
                        getPackageName(),
                        endpointDiscoveryCallback,
                        new DiscoveryOptions.Builder().setStrategy(STRATEGY).build())
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unusedResult) {
                                aShortToast(DescubrirCanalesActivity.this.getString(R.string.discover_channel_msg_start_searching));
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                aShortToast(DescubrirCanalesActivity.this.getString(R.string.discover_channel_msg_error_searching));
                            }
                        });
    }

    private final PayloadCallback payloadCallback =
            new PayloadCallback() {
                @Override
                public void onPayloadReceived(String endpointId, Payload payload) {
                    String payloadMessage = new String(payload.asBytes(), StandardCharsets.UTF_8);
                    //Toast.makeText(DescubrirCanalesActivity.this, String.format("onPayloadReceived(endpointId=%s, payload=%s)", endpointId, payloadMessage), Toast.LENGTH_SHORT).show();
                    //payloadMessage = ClavesUtil.decryptPubKey(DescubrirCanalesActivity.this, payloadMessage);

                    ((TextView)findViewById(R.id.descubrir_canales_tv_msg)).setText(payloadMessage);
                    canal = payloadMessage;
                }

                @Override
                public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
                    //lo que queramos hacer
                }
            };


    private final EndpointDiscoveryCallback endpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
                    Log.i(TAG, "onEndpointFound: endpoint found, connecting");
                    connectionsClient.requestConnection(PreferencesManage.userMail(DescubrirCanalesActivity.this), endpointId, connectionLifecycleCallback);
                }

                @Override
                public void onEndpointLost(String endpointId) {}
            };

    // Callbacks for connections to other devices
    private final ConnectionLifecycleCallback connectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                    Log.i(TAG, "onConnectionInitiated: accepting connection");
                    connectionsClient.stopDiscovery();
                    connectionsClient.stopAdvertising();
                    aShortToast(DescubrirCanalesActivity.this.getString(R.string.discover_channel_msg_device_found));
                    connectionsClient.acceptConnection(endpointId, payloadCallback)
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(DescubrirCanalesActivity.this, "Error al crear conexion", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                }

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {
                    if (result.getStatus().isSuccess()) {
                        Log.i(TAG, "onConnectionResult: connection successful");

                        connectionsClient.stopDiscovery();

                        opponentEndpointId = endpointId;

                        //conexion exitosa
                    } else {
                        Log.i(TAG, "onConnectionResult: connection failed");
                        aShortToast(DescubrirCanalesActivity.this.getString(R.string.discover_channel_msg_connect_fail));
                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    Log.i(TAG, "onDisconnected: disconnected from the opponent");
                }
            };
}