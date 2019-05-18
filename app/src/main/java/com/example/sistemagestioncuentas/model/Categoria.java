package com.example.sistemagestioncuentas.model;

public class Categoria {
    private String id_cat; //Id Autoincrementable
    private String nomb_cat; // Nombre de la Categoria
    private String desc_cat; // Descripcion de la Categoria
    private String tipo_cat; //Egresos / Ingresos

    public Categoria() {
    }

    public Categoria(String id_cat, String nomb_cat, String desc_cat, String tipo_cat) {
        this.id_cat = id_cat;
        this.nomb_cat = nomb_cat;
        this.desc_cat = desc_cat;
        this.tipo_cat = tipo_cat;
    }

    public String getId_cat() {
        return id_cat;
    }

    public void setId_cat(String id_cat) {
        this.id_cat = id_cat;
    }

    public String getNomb_cat() {
        return nomb_cat;
    }

    public void setNomb_cat(String nomb_cat) {
        this.nomb_cat = nomb_cat;
    }

    public String getDesc_cat() {
        return desc_cat;
    }

    public void setDesc_cat(String desc_cat) {
        this.desc_cat = desc_cat;
    }

    public String getTipo_cat() {
        return tipo_cat;
    }

    public void setTipo_cat(String tipo_cat) {
        this.tipo_cat = tipo_cat;
    }

    @Override
    public String toString() {
        return nomb_cat;
    }

}
