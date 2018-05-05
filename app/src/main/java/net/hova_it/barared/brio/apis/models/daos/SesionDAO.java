package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.Sesion;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**
 * Manejo de metodos para el acceso a la informacion del modelo Sesion
 * Created by Alejandro Gomez on 16/12/2015.
 */
/*
private int idSesion;
    private int idCaja;
    private int idUsuario;
    private String fechaInicio;
    private String fechaFin;
    private int activa;
    private String timestamp;

    id_sesion INTEGER PRIMARY KEY AUTOINCREMENT"+
    ", id_caja INTEGER  NOT NULL"+
    ", id_usuario INTEGER  NOT NULL"+
    ", f_inicio TEXT  NOT NULL"+
    ", f_fin TEXT  NULL"+
    ", activa INTEGER  NOT NULL"+
    ", timestamp TEXT  NOT NULL
 */
public class SesionDAO {
    private SQLiteService sqLiteService;

    public SesionDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }
    /**
     * Obtener todos los registros
     * @return
     */
    public List<Sesion> getAll() {
        return sqLiteService.sesiones.select("");
    }

    public Sesion getByIdSesion(int value) {
        List<Sesion> items = sqLiteService.sesiones.select("WHERE id_sesion = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registros por Id de Caja
     * @param value
     * @return
     */
    public List<Sesion> getByIdCaja(int value) {
        return sqLiteService.sesiones.select("WHERE id_caja = " + value);
    }
    /**
     * Obtener registros de sesiones abiertas
     * @return
     */
    public List<Sesion> getOpenSessions() {
        return sqLiteService.sesiones.select("WHERE f_fin = 0");
    }
    /**
     * Guardar registro
     * @return id de registro creado
     */
    public Long save(Sesion item){
        return sqLiteService.sesiones.insert(item);
    }
    /**
     * Obtener tiempo de duracion de movimientos en la tabla
     * @return
     */
    public long getExecutionTime() {
        return sqLiteService.sesiones.getExecutionTime();
    }
}
