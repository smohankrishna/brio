package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo Comercio
 * Created by Alejandro Gomez on 27/11/2015.
 */

/*
PK  "id_comercio INTEGER PRIMARY KEY AUTOINCREMENT"+
                        ", descripcion TEXT  NOT NULL"+
                        ", id_grupo INTEGER  NULL"+
                        ", nombre_legal TEXT  NOT NULL"+
                        ", rfc TEXT  NULL"+
                        ", direccion_legal TEXT  NULL"+
                        ", numero_exterior_legal TEXT  NULL"+
                        ", numero_interior_legal TEXT  NULL"+
                        ", colonia_legal TEXT  NULL"+
                        ", municipio_legal TEXT  NULL"+
                        ", estado_legal TEXT  NULL"+
                        ", postal_Legal TEXT  NULL"+
                        ", pais_legal TEXT  NULL"+
                        ", direccion_fisica TEXT  NOT NULL"+
                        ", numero_exterior_fisica TEXT  NOT NULL"+
                        ", numero_interior_fisica TEXT  NOT NULL"+
                        ", colonia_fisica TEXT  NOT NULL"+
                        ", municipio_fisica TEXT  NOT NULL"+
                        ", estado_fisica TEXT  NOT NULL"+
                        ", postal_fisica TEXT  NOT NULL"+
                        ", pais_fisica TEXT  NOT NULL"+
                        ", timestamp INTEGER NOT NULL DEFAULT (strftime('%s','now'))"+
                        ", FOREIGN KEY (id_grupo)REFERENCES Grupo (id_grupo))"
 */

public class Comercio {
    private int idComercio; // PK
    private String descComercio;
    private int idGrupo; // FK1
    private String nombreLegal;
    private String rfc;
    private String direccionLegal;
    private String numeroExteriorLegal;
    private String numeroInteriorLegal;
    private String coloniaLegal;
    private String municipioLegal;
    private String estadoLegal;
    private String codigoPostalLegal;
    private String paisLegal;
    private String direccionFisica;
    private String numeroExteriorFisica;
    private String numeroInteriorFisica;
    private String coloniaFisica;
    private String municipioFisica;
    private String estadoFisica;
    private String codigoPostalFisica;
    private String paisFisica;
    private long timestamp;

    public Comercio(){}

    public Comercio(String descComercio,int idGrupo ,String nombreLegal, String rfc, String direccionLegal, String numeroExteriorLegal, String numeroInteriorLegal, String coloniaLegal, String municipioLegal, String estadoLegal, String codigoPostalLegal, String paisLegal, String direccionFisica, String numeroExteriorFisica, String numeroInteriorFisica, String coloniaFisica, String municipioFisica, String estadoFisica, String codigoPostalFisica, String paisFisica, long timestamp) {
        this.descComercio = descComercio;
        this.idGrupo = idGrupo;
        this.nombreLegal = nombreLegal;
        this.rfc = rfc;
        this.direccionLegal = direccionLegal;
        this.numeroExteriorLegal = numeroExteriorLegal;
        this.numeroInteriorLegal = numeroInteriorLegal;
        this.coloniaLegal = coloniaLegal;
        this.municipioLegal = municipioLegal;
        this.estadoLegal = estadoLegal;
        this.codigoPostalLegal = codigoPostalLegal;
        this.paisLegal = paisLegal;
        this.direccionFisica = direccionFisica;
        this.numeroExteriorFisica = numeroExteriorFisica;
        this.numeroInteriorFisica = numeroInteriorFisica;
        this.coloniaFisica = coloniaFisica;
        this.municipioFisica = municipioFisica;
        this.estadoFisica = estadoFisica;
        this.codigoPostalFisica = codigoPostalFisica;
        this.paisFisica = paisFisica;
        this.timestamp = timestamp;
    }

    public int getIdComercio() {
        return idComercio;
    }

    public void setIdComercio(int idComercio) {
        this.idComercio = idComercio;
    }

    public String getDescComercio() {
        return descComercio;
    }

    public void setDescComercio(String descComercio) {
        this.descComercio = descComercio;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public String getNombreLegal() {
        return nombreLegal;
    }

    public void setNombreLegal(String nombreLegal) {
        this.nombreLegal = nombreLegal;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getDireccionLegal() {
        return direccionLegal;
    }

    public void setDireccionLegal(String direccionLegal) {
        this.direccionLegal = direccionLegal;
    }

    public String getNumeroExteriorLegal() {
        return numeroExteriorLegal;
    }

    public void setNumeroExteriorLegal(String numeroExteriorLegal) {
        this.numeroExteriorLegal = numeroExteriorLegal;
    }

    public String getNumeroInteriorLegal() {
        return numeroInteriorLegal;
    }

    public void setNumeroInteriorLegal(String numeroInteriorLegal) {
        this.numeroInteriorLegal = numeroInteriorLegal;
    }

    public String getColoniaLegal() {
        return coloniaLegal;
    }

    public void setColoniaLegal(String coloniaLegal) {
        this.coloniaLegal = coloniaLegal;
    }

    public String getMunicipioLegal() {
        return municipioLegal;
    }

    public void setMunicipioLegal(String municipioLegal) {
        this.municipioLegal = municipioLegal;
    }

    public String getEstadoLegal() {
        return estadoLegal;
    }

    public void setEstadoLegal(String estadoLegal) {
        this.estadoLegal = estadoLegal;
    }

    public String getCodigoPostalLegal() {
        return codigoPostalLegal;
    }

    public void setCodigoPostalLegal(String codigoPostalLegal) {
        this.codigoPostalLegal = codigoPostalLegal;
    }

    public String getPaisLegal() {
        return paisLegal;
    }

    public void setPaisLegal(String paisLegal) {
        this.paisLegal = paisLegal;
    }

    public String getDireccionFisica() {
        return direccionFisica;
    }

    public void setDireccionFisica(String direccionFisica) {
        this.direccionFisica = direccionFisica;
    }

    public String getNumeroExteriorFisica() {
        return numeroExteriorFisica;
    }

    public void setNumeroExteriorFisica(String numeroExteriorFisica) {
        this.numeroExteriorFisica = numeroExteriorFisica;
    }

    public String getNumeroInteriorFisica() {
        return numeroInteriorFisica;
    }

    public void setNumeroInteriorFisica(String numeroInteriorFisica) {
        this.numeroInteriorFisica = numeroInteriorFisica;
    }

    public String getColoniaFisica() {
        return coloniaFisica;
    }

    public void setColoniaFisica(String coloniaFisica) {
        this.coloniaFisica = coloniaFisica;
    }

    public String getMunicipioFisica() {
        return municipioFisica;
    }

    public void setMunicipioFisica(String municipioFisica) {
        this.municipioFisica = municipioFisica;
    }

    public String getEstadoFisica() {
        return estadoFisica;
    }

    public void setEstadoFisica(String estadoFisica) {
        this.estadoFisica = estadoFisica;
    }

    public String getCodigoPostalFisica() {
        return codigoPostalFisica;
    }

    public void setCodigoPostalFisica(String codigoPostalFisica) {
        this.codigoPostalFisica = codigoPostalFisica;
    }

    public String getPaisFisica() {
        return paisFisica;
    }

    public void setPaisFisica(String paisFisica) {
        this.paisFisica = paisFisica;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDireccionCompleta() {
        return  getDireccionFisica() + " " +
                "No. " + getNumeroExteriorFisica() + " " + getNumeroInteriorFisica() + ", " +
                "Col. " + getColoniaFisica() + ", " +
                "Del./Mpio. " + getMunicipioFisica() + ", " +
                getEstadoFisica() + ", " + " C.P. " + getCodigoPostalFisica();
    }
}
