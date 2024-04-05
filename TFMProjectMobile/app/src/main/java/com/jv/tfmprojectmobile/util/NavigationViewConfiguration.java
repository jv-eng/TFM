package com.jv.tfmprojectmobile.util;

import android.content.Context;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.jv.tfmprojectmobile.R;

public class NavigationViewConfiguration {
    public static void configurarNavView(DrawerLayout drawerLayout, NavigationView navigationView, Context ctx) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                drawerLayout.closeDrawer(GravityCompat.START);

                if (id == R.id.nav_profile) {
                    Toast.makeText(ctx, "profile", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_start) {
                    Toast.makeText(ctx, "start", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_subscriptions) {
                    Toast.makeText(ctx, "subscriptions", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_files_downloaded) {
                    Toast.makeText(ctx, "files downloaded", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_logout) {
                    Toast.makeText(ctx, "logout", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }
}
