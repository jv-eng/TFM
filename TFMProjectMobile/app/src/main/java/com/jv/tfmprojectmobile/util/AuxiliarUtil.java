package com.jv.tfmprojectmobile.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class AuxiliarUtil {
    public static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            // Si la URI es de tipo "content", intentamos obtener el nombre del archivo del proveedor de contenido.
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
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
}
