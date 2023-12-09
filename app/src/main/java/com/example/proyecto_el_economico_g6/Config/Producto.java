package com.example.proyecto_el_economico_g6.Config;

public class Producto {
    private String id;
    private String nombre;
    private String cantidad;
    private String precio;
    private String idCategoria; // ID de la categoría a la que pertenece el producto

    public Producto() {
        // Constructor vacío requerido por Firebase
    }

    public Producto(String nombre, String cantidad, String precio, String idCategoria) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.idCategoria = idCategoria;
    }

    // Getters y setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(String idCategoria) {
        this.idCategoria = idCategoria;
    }
}

