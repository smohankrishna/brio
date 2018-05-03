package lat.brio.core.support;

/**
 * Created by guillermo.ortiz on 07/03/18.
 */

public enum EFormatos {
    
    FECHA_BASEDATOS("yyyyMMdd"),
    FECHA_HORA_BASEDATOS("yyyyMMddHHmmss"),
    FECHA_PANTALLA("dd/MM/yyyy"),
    FECHA_HORA_PANTALLA("dd/MM/yyyy HH:mm"),
    HORA_MINUTOS_BASEDATOS("HHmm"),
    HORA_MINUTOS_PANTALLA("HH:mm"),
    HORA_MINUTOS_SEGUNDOS_BASEDATOS("HHmmss"),
    HORA_MINUTOS_SEGUNDOS_PANTALLA("HH:mm:ss"),
    FMT_NUMERO_NORMALIZADO("####0000"),
    FMT_NUMERO_NORMALIZADO_CERO_DECIMAL("######0"),
    FMT_NUMERO_NORMALIZADO_UN_DECIMAL("######0.0"),
    FMT_NUMERO_NORMALIZADO_UN_DECIMAL_DISTINTO_CERO("######0.#"),
    FMT_NUMERO_NORMALIZADO_DOS_DECIMALES("#####0.00"),
    FMT_NUMERO_NORMALIZADO_DOS_DECIMALES_SEPARADOR_MILES("###,###,##0.00"),
    FMT_NUMERO_NORMALIZADO_TRES_DECIMALES("####0.000"),
    FMT_NUMERO_NORMALIZADO_TRES_DECIMALES_SEPARADOR_MILES("###,###,##0.000"),
    FECHA_CORTA_HORA_BASEDATOS("yyMMddHHmmss"),
    FECHA_CALENDARIO("yyyy-MM-dd"),
    FECHA_CALENDARIO_YYYY_MM("yyyyMM");
    
    private String valor;
    
    EFormatos(String valor)
    {
        this.valor = valor;
    }
    
    public String valor()
    {
        return this.valor;
    }
    
}
