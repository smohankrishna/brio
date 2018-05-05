package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.Perfil;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**
 * Manejo de metodos para el acceso a la informacion del modelo Perfil
 * Created by Mauricio Cerón on 10/03/2016.
 */
public class PerfilDAO {
    private SQLiteService sqLiteService;

    public PerfilDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }
    /**
     * Obtener todos los registros
     * @return
     */
    public List<Perfil> getAll() {
        return sqLiteService.perfil.select("");
    }
    /**
     * Obtener registro por Id de Perfil
     * @param value
     * @return
     */
    public Perfil getByIdPerfil(int value) {
        List<Perfil> items = sqLiteService.perfil.select("WHERE id_perfil = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registros por nombre
     * @param value
     * @return
     */
    public Perfil getByPerfil(String value) {
        List<Perfil> items = sqLiteService.perfil.select("WHERE perfil = '" + value + "'");
        return (items == null) ? null : items.get(0);
    }
    /**
     * Guardar registro
     * @return id de registro creado
     */
    public Long save(Perfil item){
        return sqLiteService.perfil.insert(item);
    }
    /**
     * Obtener tiempo de duracion de movimientos en la tabla
     * @return
     */
    public long getExecutionTime() {
        return sqLiteService.perfil.getExecutionTime();
    }
}

