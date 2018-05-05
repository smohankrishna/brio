package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo ArticuloTest
 * Created by Alejandro Gomez on 27/11/2015.
 */

public class ArticuloTest {
    private int idArticuloCentral;
    private int idArticulo; // PK
    private int idTipoArticulo;
    private String sku;
    private String descArticulo;
    private boolean granel;
    private String referencia;
    private String codigoBarras;
    private Long modificado;

    public ArticuloTest(){}

    public ArticuloTest(int idArticuloCentral, int idTipoArticulo, String sku, String descArticulo, boolean granel, String referencia, String codigoBarras, Long modificado) {
        this.idArticuloCentral = idArticuloCentral;
        this.idTipoArticulo = idTipoArticulo;
        this.sku = sku;
        this.descArticulo = descArticulo;
        this.granel = granel;
        this.referencia = referencia;
        this.codigoBarras = codigoBarras;
        this.modificado = modificado;
    }

    public int getIdArticuloCentral() {
        return idArticuloCentral;
    }

    public void setIdArticuloCentral(int idArticuloCentral) {
        this.idArticuloCentral = idArticuloCentral;
    }

    public int getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(int idArticulo) {
        this.idArticulo = idArticulo;
    }

    public int getIdTipoArticulo() {
        return idTipoArticulo;
    }

    public void setIdTipoArticulo(int idTipoArticulo) {
        this.idTipoArticulo = idTipoArticulo;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getDescArticulo() {
        return descArticulo;
    }

    public void setDescArticulo(String descArticulo) {
        this.descArticulo = descArticulo;
    }

    public boolean isGranel() {
        return granel;
    }

    public void setGranel(boolean granel) {
        this.granel = granel;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public Long getModificado() {
        return modificado;
    }

    public void setModificado(Long modificado) {
        this.modificado = modificado;
    }
}
