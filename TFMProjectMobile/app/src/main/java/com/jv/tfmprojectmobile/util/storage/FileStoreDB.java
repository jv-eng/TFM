package com.jv.tfmprojectmobile.util.storage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jv.tfmprojectmobile.models.FileStoreModel;

import java.util.LinkedList;
import java.util.List;

public class FileStoreDB {

    private FileStoreHelper helper;
    public FileStoreDB(FileStoreHelper helper) {
        this.helper = helper;
    }

    public void save(FileStoreModel model) {
        String query = "INSERT INTO file(" +
                "_id, name, descargado, ruta, canal" +
                ") VALUES (?,?,?,?,?);";
        SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
        sqLiteDatabase.execSQL(query, new String[] {
            model.getId(), model.getName(), String.valueOf(model.getDescargado()), "",
                model.getCanal()
        });
    }

    public void eliminarPorNombre(String nombreArchivo) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("file", "name = ?", new String[]{nombreArchivo});
    }

    public void descargaFichero(FileStoreModel model) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("descargado", String.valueOf(1));
        valores.put("ruta", model.getRuta());
        db.update("file", valores, "name = ?", new String[] {model.getName()});
    }

    public FileStoreModel getFile(String name) {
        FileStoreModel model = null;
        String query = "";

        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Cursor c = sqLiteDatabase.rawQuery(query, new String[]{name});

        while (c.moveToNext()) {
            model = new FileStoreModel(c.getString(0), c.getString(1),
                    c.getInt(2), c.getString(3), c.getString(4));
        }

        c.close();
        return model;
    }

    public List<FileStoreModel> getAll() {
        List<FileStoreModel> lista = new LinkedList<>();

        String query = "SELECT _id, name, descargado, ruta FROM file;";
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{});

        while (cursor.moveToNext()) {
            lista.add(new FileStoreModel(
                    cursor.getString(0), cursor.getString(1),
                    cursor.getInt(2), cursor.getString(3),
                    cursor.getString(4)
            ));
        }

        cursor.close();
        return lista;
    }

    public List<FileStoreModel> getFilesChannel(String canal) {
        List<FileStoreModel> lista = new LinkedList<>();

        String query = "SELECT _id, name, descargado, ruta FROM file WHERE canal=?;";
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{canal});

        while (cursor.moveToNext()) {
            lista.add(new FileStoreModel(
                    cursor.getString(0), cursor.getString(1),
                    cursor.getInt(2), cursor.getString(3),
                    canal
            ));
        }

        cursor.close();
        return lista;
    }

    public boolean existeCanal(String canal) {
        boolean existe = false;

        String query = "SELECT _id FROM file WHERE canal=?;";
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{canal});
        if (cursor.moveToNext()) existe = true;

        return existe;
    }

    public List<FileStoreModel> getChannels() {
        List<FileStoreModel> lista = new LinkedList<>();
        String query = "SELECT _id, name, descargado, ruta, canal FROM file;";
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        while (cursor.moveToNext()) {
            lista.add(new FileStoreModel(
                    cursor.getString(0), cursor.getString(1),
                    cursor.getInt(2), cursor.getString(3),
                    cursor.getString(4)
            ));
        }

        cursor.close();

        return lista;
    }

    public List<String> getChannelsStr() {
        List<String> lista = new LinkedList<>();
        String query = "SELECT name FROM sub;";
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        while (cursor.moveToNext()) {
            lista.add(cursor.getString(0));
        }

        cursor.close();

        return lista;
    }

    public void deleteChannel(String channel) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("sub", "name = ?", new String[]{channel});
        db.close();
    }

    public void insertChannel(String channel) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", channel);

        db.insert("sub", null, values);

        db.close();
    }
}
