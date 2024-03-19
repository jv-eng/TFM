package com.jv.tfmprojectmobile.util.storage;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.jv.tfmprojectmobile.util.ClavesUtil;

public class PreferencesManage {
    private final static String PREFERENCES_FILE_NAME = "prefs", PREFERENCES_ATTR_1_NAME = "user_name",
            PREFERENCES_ATTR_2_PASS = "user_pass", PREFERENCES_ATTR_3_MAIL = "user_mail", PREFERENCES_ATTR_4_PUBKEY = "user_pub_key",
            PREFERENCES_ATTR_5_PRIVKEY = "user_priv_key";

    public static boolean userExists(Context ctx) {
        SharedPreferences pref = ctx.getSharedPreferences(PREFERENCES_FILE_NAME, MODE_PRIVATE);
        String username = pref.getString(PREFERENCES_ATTR_1_NAME, "");
        //comprobar si hay algo
        return (!username.equals(""));
    }

    public static void storeUser(Context ctx, String name, String mail, String pass, String pubKey, String privKey) {
        SharedPreferences pref = ctx.getSharedPreferences(PREFERENCES_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor ed = pref.edit();
        ed.putString(PREFERENCES_ATTR_1_NAME, name);
        ed.putString(PREFERENCES_ATTR_2_PASS, String.valueOf(pass.hashCode()));
        ed.putString(PREFERENCES_ATTR_3_MAIL, mail);
        ed.putString(PREFERENCES_ATTR_4_PUBKEY, pubKey);
        ed.putString(PREFERENCES_ATTR_5_PRIVKEY, privKey);
        ed.apply();
    }

    public static void removeUser(Context ctx) {
        SharedPreferences pref = ctx.getSharedPreferences(PREFERENCES_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor ed = pref.edit();
        ed.remove(PREFERENCES_ATTR_1_NAME);
        ed.apply();
    }

    public static String getPrivKey(Context ctx) {
        SharedPreferences pref = ctx.getSharedPreferences(PREFERENCES_FILE_NAME, MODE_PRIVATE);
        return pref.getString(PREFERENCES_ATTR_5_PRIVKEY, "");
    }
}
