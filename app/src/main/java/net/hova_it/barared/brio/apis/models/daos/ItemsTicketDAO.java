package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.ItemsTicket;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**
 * Manejo de metodos para el acceso a la informacion del modelo ItemsTicket
 * Created by Alejandro Gomez on 17/12/2015.
 */

/*
    private int idItemTicket;
    private int idTicket;
    private int idArticulo;
    private String referencia;
    private String codigoBarras;
    private String descripcion;
    private int cantidad;
    private double importeUnitario;
    private double importeTotal;
    private String timestamp;

    id_item_ticket INTEGER PRIMARY KEY AUTOINCREMENT"+
    ",id_ticket INTEGER  NOT NULL"+
    ", id_articulo TEXT  NULL"+
    ", referencia TEXT  NOT NULL"+
    ", codigo_barras TEXT  NOT NULL"+
    ", descripcion TEXT  NOT NULL"+
    ", cantidad REAL  NOT NULL"+
    ", importe_unitario REAL  NOT NULL"+
    ", importe_total REAL  NOT NULL"+
    ", timestamp
 */
public class ItemsTicketDAO {
    private SQLiteService sqLiteService;

    public ItemsTicketDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }
    /**
     * Obtener todos los registros
     * @return
     */
    public List<ItemsTicket> getAll() {
        return sqLiteService.itemsTicket.select("");
    }
    /**
     * Obtener registro por Id de Item Ticket
     * @param value
     * @return
     */
    public ItemsTicket getByIdItemTicket(int value) {
        List<ItemsTicket> items = sqLiteService.itemsTicket.select("WHERE id_item_ticket = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registro por Id de ticket
     * @param value
     * @return
     */
    public List<ItemsTicket> getByIdTicket(int value) {
        return sqLiteService.itemsTicket.select("WHERE id_ticket = " + value);
    }
    /**
     * Obtener registro por Id de Articulo
     * @param value
     * @return
     */
    public List<ItemsTicket> getByIdArticulo(int value) {
        return sqLiteService.itemsTicket.select("WHERE id_articulo = " + value);
    }
    /**
     * Obtener registros por codigo de barras
     * @param value
     * @return
     */
    public List<ItemsTicket> getSearchCodigoBarras(String value) {
        return sqLiteService.itemsTicket.select("WHERE codigo_barras LIKE '" + value + "'");
    }
    /**
     * Obtener registro por codigo de barras
     * @param value
     * @return
     */
    public ItemsTicket getByCodigoBarras (String value) {
        List<ItemsTicket> items = sqLiteService.itemsTicket.select("WHERE codigo_barras = '" + value +"' COLLATE NOCASE");
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener numeros de autorizcion de tickets
     */
    public List<ItemsTicket> getAutorizationByIdTicket (String ids) {
        return sqLiteService.itemsTicket.select("WHERE id_ticket IN (" + ids + ") AND descripcion LIKE 'Autoriza%'");
    }
    /**
     * Guardar registro
     * @return id de registro creado
     */
    public Long save(ItemsTicket item){
        return sqLiteService.itemsTicket.insert(item);
    }
    /**
     * Obtener tiempo de duracion de movimientos en la tabla
     * @return
     */
    public long getExecutionTime() {
        return sqLiteService.itemsTicket.getExecutionTime();
    }
    /**
     * Eliminar los items de los tickets con mas de 6 meses
     * @return
     */
    public long deleteOutdatedTicketsItems(){
        return sqLiteService.itemsTicket.delete(" id_ticket in (select id_ticket from Items_Ticket  where datetime(timestamp ,'unixepoch', 'localtime') < datetime('now', '-2 month'))");
    }
}
