package com.jv.tfmprojectmobile.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.jv.tfmprojectmobile.R;
import com.jv.tfmprojectmobile.activities.FicherosCanalActivity;
import com.jv.tfmprojectmobile.activities.LoginActivity;
import com.jv.tfmprojectmobile.activities.MenuActivity;
import com.jv.tfmprojectmobile.activities.SuscriptionesActivity;
import com.jv.tfmprojectmobile.util.storage.PreferencesManage;

public class NavigationViewConfiguration {
    public static void configurarNavView(DrawerLayout drawerLayout, NavigationView navigationView, Context ctx) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                drawerLayout.closeDrawer(GravityCompat.START);

                if (id == R.id.nav_profile) {
                    String message = ctx.getString(R.string.drawer_alert_dialog_username) + PreferencesManage.userName(ctx) +
                        "\n" + ctx.getString(R.string.drawer_alert_dialog_usermail) + PreferencesManage.userMail(ctx);
                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                    builder.setTitle(ctx.getString(R.string.drawer_alert_dialog_info));
                    builder.setMessage(message);
                    builder.setPositiveButton(ctx.getString(R.string.drawer_alert_dialog_accept), null);
                    builder.show();
                } else if (id == R.id.nav_start) {
                    Intent i = new Intent(ctx, MenuActivity.class);
                    ctx.startActivity(i);
                } else if (id == R.id.nav_subscriptions) {
                    Intent i = new Intent(ctx, SuscriptionesActivity.class);
                    ctx.startActivity(i);
                } else if (id == R.id.nav_files_downloaded) {
                    Intent i = new Intent(ctx, FicherosCanalActivity.class);
                    ctx.startActivity(i);
                } else if (id == R.id.nav_logout) {
                    PreferencesManage.removeUser(ctx);
                    Intent i = new Intent(ctx, LoginActivity.class);
                    ctx.startActivity(i);
                }
                return true;
            }
        });
    }
}
