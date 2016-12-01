package es.ivan.syd.Clases;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Gonzalo on 09/11/2016.
 */

public class Pesadilla implements Serializable {

    @Exclude
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private String titulo;
    private Date dia;
    private String id;
    private String descripcion;
    private int iconoId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDia() {
        return dia;
    }

    public void setDia(Date dia) {
        this.dia = dia;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Exclude
    public String getFormattedTime(){
        return dateFormat.format(dia);
    }

    public int getIconoId() {
        return iconoId;
    }

    public void setIconoId(int iconoId) {
        this.iconoId = iconoId;
    }
}
