package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.Settings;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**
 * Manejo de metodos para el acceso a la informacion del modelo Settings
 * Created by Alejandro Gomez on 16/12/2015.
 */

public class SettingsDAO {
    private SQLiteService sqLiteService;

    public SettingsDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }
    /**
     * Obtener todos los registros
     * @return
     */
    public List<Settings> getAll() {
        return sqLiteService.settings.select("");
    }

    public Settings getByIdParametro(int value) {
        List<Settings> items = sqLiteService.settings.select("WHERE id_settings = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registro por nombre
     * @param value
     * @return
     */
    public Settings getByNombre(String value) {

        List<Settings> items = sqLiteService.settings.select("WHERE nombre = '" + value +"' COLLATE NOCASE");
        return (items == null) ? null : items.get(0);
    }

    /**
     * Actualizar registro
     * @param nombre
     * @param valor
     * @return
     */
    public  boolean update(String nombre,String valor){
        return sqLiteService.settings.update(nombre,valor);
    }
    /**
     * Guardar registro
     * @return id de registro creado
     */
    public Long save(Settings item){
        return sqLiteService.settings.insert(item);
    }
    /**
     * Obtener tiempo de duracion de movimientos en la tabla
     * @return
     */
    public long getExecutionTime() {
        return sqLiteService.settings.getExecutionTime();
    }
}
