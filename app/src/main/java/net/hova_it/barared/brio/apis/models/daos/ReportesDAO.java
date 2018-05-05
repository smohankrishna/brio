package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.Reporte;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**
 * Manejo de metodos para el acceso a la informacion del modelo Reportes
 * Created by Mauricio Cerón on 18/04/2016.
 */
public class ReportesDAO {
    private SQLiteService sqLiteService;

    public ReportesDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }




    /**
     * Obtener todos los registros
     * @return
     */
    public List<Reporte> getReport(Long fechaInicio, Long fechaFin, int tipoReporte) {
        return sqLiteService.reporte.select(fechaInicio,fechaFin,tipoReporte);
    }

    /**
     * Obtener registros de un Articulo dentro de un ragno de fecha especificado
     * @param fechaInicio
     * @param fechaFin
     * @param idArticulo
     * @return
     */
    public Reporte getReportByIdArticulo(Long fechaInicio, Long fechaFin, int idArticulo) {
        List<Reporte> items = sqLiteService.reporte.selectById(fechaInicio, fechaFin, idArticulo);
        return (items == null) ? null : items.get(0);
    }
}