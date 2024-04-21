package com.jv.tfmprojectmobile.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class AuxiliarUtil {
    public static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri != null) {
            if ("content".equals(uri.getScheme())) {
                if (DocumentsContract.isDocumentUri(context, uri)) {
                    // Si la URI es de tipo "content" y es un documento de medios, intentamos obtener el nombre del archivo del proveedor de contenido.
                    try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                        if (cursor != null && cursor.moveToFirst()) {
                            // Verificar si la columna DISPLAY_NAME existe en el cursor
                            int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                            if (displayNameIndex != -1) {
                                result = cursor.getString(displayNameIndex);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // Si la URI es de tipo "content" pero no es un documento de medios, intentamos obtener el nombre del archivo de la URI.
                    result = uri.getLastPathSegment();
                }
            } else {
                // Si la URI no es de tipo "content", intentamos obtener el nombre del archivo de la URI.
                result = uri.getLastPathSegment();
            }
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
