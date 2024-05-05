package com.jv.tfmprojectmobile.util.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FileStoreHelper extends SQLiteOpenHelper {

    private static final String NOMBRE_MI_BBDD = "fileStore.db";
    private static final int version = 1;

    public FileStoreHelper(Context ctx) {
        super(ctx, NOMBRE_MI_BBDD, null, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE file(" +
                "_id TEXT," +
                "name TEXT," +
                "canal TEXT," +
                "descargado INT," +
                "ruta TEXT" +
                ");");
        db.execSQL("CREATE TABLE sub(" +
                "_id TEXT, name TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
