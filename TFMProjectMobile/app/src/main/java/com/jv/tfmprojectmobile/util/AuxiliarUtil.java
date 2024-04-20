package com.jv.tfmprojectmobile.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class AuxiliarUtil {
    public static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            // Si la URI es de tipo "content", intentamos obtener el nombre del archivo del proveedor de contenido.
            try (Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow("_display_name"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (result == null) {
            // Si no pudimos obtener el nombre del archivo del proveedor de contenido, intentamos obtenerlo de la URI.
            result = uri.getLastPathSegment();
        }
        return result;
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public static String dateString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    public static Date stringDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date res = null;
        try {
            res = sdf.parse(date);
        } catch (Exception e) {
            Log.e("parse", "error al parsear fecha");
        }
        return res;
    }
}
