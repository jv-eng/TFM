package com.jv.tfmprojectmobile.models;

import com.google.gson.annotations.SerializedName;

public class FileStoreModel {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("descargado")
    private int descargado;

    @SerializedName("ruta")
    private String ruta;

    @SerializedName("canal")
    private String canal;

    public FileStoreModel(String id, String name, int descargado, String ruta, String canal) {
        this.id = id;
        this.name = name;
        this.descargado = descargado;
        this.ruta = ruta;
        this.canal = canal;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDescargado() {
        return descargado;
    }

    public void setDescargado(int descargado) {
        this.descargado = descargado;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }
}
