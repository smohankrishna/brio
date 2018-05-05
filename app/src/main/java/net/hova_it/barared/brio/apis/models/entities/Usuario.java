package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo Usuario
 * Created by Alejandro Gomez on 30/11/2015.
 */
/*
Usuarios
                        id_usuario INTEGER PRIMARY KEY AUTOINCREMENT
                        usuario TEXT  NOT NULL
                        password TEXT  NOT NULL
                        id_perfil INTEGER  NULL
                        nombre TEXT  NOT NULL
                        apellidos TEXT  NOT NULL
                        pregunta1 INTEGER  NULL
                        respuesta1 TEXT  NULL
                        activo INTEGER DEFAULT 1
                        timestamp INTEGER NOT NULL DEFAULT (strftime('%s','now'))
                        FOREIGN KEY (id_perfil)REFERENCES Perfiles (id_perfil)
*/
public class Usuario {
    private int idUsuario;
    private String usuario;
    private String password;
    private int idPerfil;
    private String nombre;
    private String apellidos;
    private int pregunta1;
    private String respuesta1;
    private boolean activo;
    private boolean bloqueado;
    private long timestamp;

    public Usuario(){}

    public Usuario(String usuario, String password, int idPerfil, String nombre, String apellidos,
                   int pregunta1, String respuesta1) {
        this.usuario = usuario;
        this.password = password;
        this.idPerfil = idPerfil;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.pregunta1 = pregunta1;
        this.respuesta1 = respuesta1;
        /*this.bloqueado = bloqueado;
        this.activo = activo;
        this.timestamp = timestamp;*/
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIdPerfil() {
        return idPerfil;
    }

    public void setIdPerfil(int idPerfil) {
        this.idPerfil = idPerfil;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public int getPregunta1() {
        return pregunta1;
    }

    public void setPregunta1(int pregunta1) {
        this.pregunta1 = pregunta1;
    }

    public String getRespuesta1() {
        return respuesta1;
    }

    public void setRespuesta1(String respuesta1) {
        this.respuesta1 = respuesta1;
    }

    public boolean getBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public boolean getActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
