package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.Cliente;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**
 * Manejo de metodos para el acceso a la informacion del modelo Cliente
 * Created by Alejandro Gomez on 16/12/2015.
 */
public class ClienteDAO {
    private SQLiteService sqLiteService;

    public ClienteDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }
    /**
     * Obtener todos los registros
     * @return
     */
    public List<Cliente> getAll() {
        return sqLiteService.clientes.select("");
    }
    /**
     * Obtener registro por Id de Cliente
     * @param value
     * @return
     */
    public Cliente getByIdCliente(int value) {
        List<Cliente> items = sqLiteService.clientes.select("WHERE id_cliente = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registro por numero de tarjeta
     * @param value
     * @return
     */
    public List<Cliente> getByNumeroTarjeta(String value) {
        return sqLiteService.clientes.select("WHERE numero_tarjeta LIKE '%" + value + "%'");
    }
    /**
     * Obtener registro por codigo de cliente
     * @param value
     * @return
     */
    public Cliente getByCodigoClienteBarared(String value) {
        List<Cliente> items = sqLiteService.clientes.select("WHERE numero_tarjeta = '" + value +"' COLLATE NOCASE");
        return (items == null) ? null : items.get(0);
    }

    /**
     * Obtener registros por nombre de cliente
     * @param value
     * @return
     */
    public List<Cliente> getSerchNombreCliente(String value) {
        return sqLiteService.clientes.select("WHERE nombre_cliente LIKE '%" + value + "%'");
    }
    /**
     * Obtener registro por nombre de cliente
     * @param value
     * @return
     */
    public Cliente getByNombreCliente(String value) {
        List<Cliente> items = sqLiteService.clientes.select("WHERE nombre_cliente = '" + value +"' COLLATE NOCASE");
        return (items == null) ? null : items.get(0);
    }
    /**
     * Guardar registro
     * @return id de registro creado
     */
    public Long save(Cliente item){
        return sqLiteService.clientes.insert(item);
    }
    /**
     * Obtener tiempo de duracion de movimientos en la tabla
     * @return
     */
    public long getExecutionTime() {
        return sqLiteService.clientes.getExecutionTime();
    }
}
