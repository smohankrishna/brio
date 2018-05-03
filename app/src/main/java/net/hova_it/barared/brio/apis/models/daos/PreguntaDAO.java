package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.Pregunta;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**
 * Manejo de metodos para el acceso a la informacion del modelo Pregunta
 * Created by Alejandro Gomez on 06/01/2016.
 */
public class PreguntaDAO {
    private SQLiteService sqLiteService;

    public PreguntaDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }
    /**
     * Obtener todos los registros
     * @return
     */
    public List<Pregunta> getAll() {
        return sqLiteService.preguntas.select("");
    }
    /**
     * Obtener registro por Id de Pregunta
     * @param value
     * @return
     */
    public Pregunta getByIdPregunta(int value) {
        List<Pregunta> items = sqLiteService.preguntas.select("WHERE id_pregunta = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Guardar registro
     * @return id de registro creado
     */
    public Long save(Pregunta item){
        return sqLiteService.preguntas.insert(item);
    }
    /**
     * Obtener tiempo de duracion de movimientos en la tabla
     * @return
     */
    public long getExecutionTime() {
        return sqLiteService.preguntas.getExecutionTime();
    }
}
