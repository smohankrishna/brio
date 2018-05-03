package net.hova_it.barared.brio.apis.sqlite;

import net.hova_it.barared.brio.apis.models.daos.CorreosClientesDB;
import net.hova_it.barared.brio.apis.models.daos.TMAEVersionDB;
import net.hova_it.barared.brio.apis.models.daos.TicketsEnviadosClientesDB;

import lat.brio.core.BrioGlobales;

/**
 * Clase donde se crean vistas y en los replace se crean o se reemplazan registros en una tabla
 * Created by Alejandro Gomez on 07/12/2015.
 */

public class SQLiteInit {
    //////////////////////////VIEW´s//////////////////////////
    
    /**
     * Crea la vista de articulo donde se obtiene de diferentes tablas
     * @return la vista de articulo
     */
    public String createViewArticulo () {
        return
                "CREATE VIEW IF NOT EXISTS ViewArticulo AS " +
                        " SELECT A.id_articulo, A.id_central,A.precio_venta,A.precio_compra, A.codigo_barras, A.nombre,M.nombre AS marca, P.nombre AS presentacion, A.contenido, U.desc_unidad AS Unidad, A.Granel  " +
                        " FROM Articulos AS A, Marcas AS M, Unidades_granel AS U, Presentaciones AS P " +
                        " WHERE A.id_marca = M.id_marca  " +
                        " AND A.id_unidad = U.id_unidad " +
                        " AND A.id_presentacion = P.id_presentacion ";
        
    }
    
    /**
     * Crea la vista de inventario se obtiene de difentes tablas
     * @return regresa la vista de inventario
     */
    public String createViewInventario () {
        return
                " CREATE VIEW IF NOT EXISTS  ViewInventario AS \n" +
                        "SELECT A.id_articulo,I.id_inventario, A.nombre, ifnull(M.nombre,'SIN MARCA') AS marca,\n" +
                        "A.contenido, ifnull(U.desc_unidad,'SIN UNIDAD') AS Unidad,  ifnull(P.nombre,'SIN PRESENTACIÓN') AS presentacion,\n" +
                        "A.codigo_barras,A.precio_compra,A.precio_venta, I.cantidad As existencias\n" +
                        "FROM (((((Articulos AS A LEFT JOIN Marcas AS M ON A.id_marca = M.id_marca)\n" +
                        " LEFT JOIN Unidades_granel AS U ON A.id_unidad = U.id_unidad)\n" +
                        " LEFT JOIN (Categorias AS C LEFT JOIN Familia AS F ON C.id_categoria = F.id_categoria) C ON A.id_familia=C.id_familia) A\n" +
                        " LEFT JOIN Presentaciones AS P ON A.id_presentacion = P.id_presentacion) A \n" +
                        " LEFT JOIN Inventario AS I ON A.id_articulo = I.id_articulo) WHERE A.fecha_baja = 0 ORDER BY A.nombre ASC;";
    }
    
    /**
     * Borra la vista articulo
     */
    public String dropViewArticulo () {
        return
                "DROP VIEW 'ViewArticulo'";
    }
    
    /**
     * Borra la vista inventario
     */
    public String dropViewInventario () {
        return
                "DROP VIEW 'ViewInventario'";
    }
    
    ////////////////////////Drop´s/////////////////////////////////////////////
    
    /**
     * Borra la tabla grupo
     */
    public String dropTableGrupo () {
        return
                "DROP TABLE IF EXISTS Grupo";
    }
    
    /**
     * Borra la tabla grupo
     */
    public String dropTableComercio () {
        return
                "DROP TABLE IF EXISTS Comercio";
    }
    
    /**
     * Borra la tabla parametros
     */
    public String dropTableParametros () {
        return
                "DROP TABLE IF EXISTS Parametros";
    }
    
    /**
     * Borra la tabla usuarios
     */
    public String dropTableUsuarios () {
        return
                "DROP TABLE IF EXISTS Usuarios";
    }
    
    /**
     * Borra la tabla cajas
     */
    public String dropTableCajas () {
        return
                "DROP TABLE IF EXISTS Cajas";
    }
    
    /**
     * Borra la tabla sesiones
     */
    public String dropTableSesiones () {
        return
                "DROP TABLE IF EXISTS Sesiones";
    }
    
    /**
     * Borra la tabla TipoCliente
     */
    public String dropTableTipoCliente () {
        return
                "DROP TABLE IF EXISTS Tipo_cliente";
    }
    
    /**
     * Borra la tabla cliente
     */
    public String dropTableCliente () {
        return
                "DROP TABLE IF EXISTS Cliente";
    }
    
    /**
     * Borra la tabla articulos
     */
    public String dropTableArticulos () {
        return
                "DROP TABLE IF EXISTS Articulos";
    }
    
    /**
     * Borra la tabla unidadesMedida
     */
    public String dropTableUnidadesMedida () {
        return
                "DROP TABLE IF EXISTS Unidades_granel";
    }
    
    /**
     * Borra la tabla monedas
     */
    public String dropTableMonedas () {
        return
                "DROP TABLE IF EXISTS Monedas";
    }
    
    /**
     * Borra la tabla registroCliente
     */
    public String dropTableRegistroCierre () {
        return
                "DROP TABLE IF EXISTS Registro_cierre";
    }
    
    /**
     * Borra la tabla registroApertura
     */
    public String dropTableRegistroApertura () {
        return
                "DROP TABLE IF EXISTS Registro_apertura";
    }
    
    /**
     * Borra la tabla perfiles
     */
    public String dropTablePerfiles () {
        return
                "DROP TABLE IF EXISTS Perfiles";
    }
    
    /**
     * Borra la tabla tickets
     */
    public String dropTableTickets () {
        return
                "DROP TABLE IF EXISTS Tickets";
    }
    
    /**
     * Borra la tabla itemsTicket
     */
    public String dropTableItemsTicket () {
        return
                "DROP TABLE IF EXISTS Items_Ticket";
    }
    
    /**
     * Borra la tabla familias
     */
    public String dropTableFamilias () {
        return
                "DROP TABLE IF EXISTS Familia";
    }
    
    /**
     * Borra la tabla impuestos
     */
    public String dropTableImpuestos () {
        return
                "DROP TABLE IF EXISTS Impuestos";
    }
    
    /**
     * Borra la tabla categorias
     */
    public String dropTableCategorias () {
        return
                "DROP TABLE IF EXISTS Categorias";
    }
    
    /**
     * Borra la tabla marcas
     */
    public String dropTableMarcas () {
        return
                "DROP TABLE IF EXISTS Marcas";
    }
    
    /**
     * Borra la tabla notificaciones
     */
    public String dropTableNotificaciones () {
        return
                "DROP TABLE IF EXISTS Notificaciones";
    }
    
    /**
     * Borra la tabla presentaciones
     */
    public String dropTablePresentaciones () {
        return
                "DROP TABLE IF EXISTS Presentaciones";
    }
    
    /**
     * Borra la tabla preguntas
     */
    public String dropTablePreguntas () {
        return
                "DROP TABLE IF EXISTS Preguntas";
    }
    
    /**
     * Borra la tabla inventario
     */
    public String dropTableInventario () {
        return
                "DROP TABLE IF EXISTS Inventario";
    }
    
    /**
     * Borra la tabla TipoTickets
     */
    public String dropTableTipoTickets () {
        return
                "DROP TABLE IF EXISTS Tipo_Tickets";
    }
    
    /**
     * Borra la tabla tipoPago
     */
    public String dropTableTipoPago () {
        return
                "DROP TABLE IF EXISTS Tipo_pago";
    }
    
    /**
     * Borra la tabla Syncdata
     */
    public String dropTableSyncData () {
        return
                "DROP TABLE IF EXISTS Sync_Data";
    }
    
    
  
    
    
 
    
    
    ////////////////////////Create´s/////////////////////////////////////////////
    
    /**
     * Crea la tabla SyncData
     */
    public String createTableSyncData () {
        return
                "CREATE TABLE IF NOT EXISTS Sync_Data (" +
                        "id_sequence INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "id_comercio INTEGER  NOT NULL, " +
                        "uuid TEXT  NOT NULL, " +
                        "sync_table TEXT  NOT NULL, " +
                        "sync_operation TEXT  NOT NULL, " +
                        "sync_data TEXT  NOT NULL, " +
                        "timestamp INTEGER NOT NULL DEFAULT (strftime('%s','now'))" +
                        ")";
    }
    
    /**
     * Crea la tabla Grupo
     */
    public String createTableGrupo () {
        return
                
                "CREATE TABLE IF NOT EXISTS Grupo(" +
                        "id_grupo INTEGER PRIMARY KEY AUTOINCREMENT" +
                        ", desc_grupo TEXT  NOT NULL)";
        
    }
    
    /**
     * Crea la tabla Comercio
     */
    public String createTableComercio () {
        return
                "CREATE TABLE IF NOT EXISTS Comercio (" +
                        "id_comercio INTEGER PRIMARY KEY AUTOINCREMENT" +
                        ", descripcion TEXT  NOT NULL" +
                        ", id_grupo INTEGER  NULL" +
                        ", nombre_legal TEXT  NOT NULL" +
                        ", rfc TEXT  NULL" +
                        ", direccion_legal TEXT  NULL" +
                        ", numero_exterior_legal TEXT  NULL" +
                        ", numero_interior_legal TEXT  NULL" +
                        ", colonia_legal TEXT  NULL" +
                        ", municipio_legal TEXT  NULL" +
                        ", estado_legal TEXT  NULL" +
                        ", postal_Legal TEXT  NULL" +
                        ", pais_legal TEXT  NULL" +
                        ", direccion_fisica TEXT  NOT NULL" +
                        ", numero_exterior_fisica TEXT  NOT NULL" +
                        ", numero_interior_fisica TEXT  NOT NULL" +
                        ", colonia_fisica TEXT  NOT NULL" +
                        ", municipio_fisica TEXT  NOT NULL" +
                        ", estado_fisica TEXT  NOT NULL" +
                        ", postal_fisica TEXT  NOT NULL" +
                        ", pais_fisica TEXT  NOT NULL" +
                        ", timestamp INTEGER NOT NULL DEFAULT (strftime('%s','now'))" +
                        ", FOREIGN KEY (id_grupo)REFERENCES Grupo (id_grupo))";
        
    }
    
    /**
     * Crea la tabla Settings
     */
    public String createTableSettings () {
        return
                "CREATE TABLE IF NOT EXISTS Settings (" +
                        "id_settings INTEGER PRIMARY KEY AUTOINCREMENT" +
                        ", nombre TEXT  NOT NULL" +
                        ", valor TEXT  NOT NULL" +
                        ")";
        
    }
    
    /**
     * Crea la tabla Usuarios
     */
    public String createTableUsuarios () {
        return
                "CREATE TABLE IF NOT EXISTS Usuarios (" +
                        "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "usuario TEXT  NOT NULL, " +
                        "password TEXT  NOT NULL, " +
                        "id_perfil INTEGER  NULL, " +
                        "nombre TEXT  NOT NULL, " +
                        "apellidos TEXT  NOT NULL, " +
                        "pregunta1 INTEGER  NULL, " +
                        "respuesta1 TEXT  NULL, " +
                        "activo INTEGER DEFAULT 1, " +
                        "bloqueado INTEGER DEFAULT 0, " +
                        "timestamp INTEGER NOT NULL DEFAULT (strftime('%s','now')), " +
                        "FOREIGN KEY (id_perfil)REFERENCES Perfiles (id_perfil) " +
                        ")";
    }
    
    /**
     * Crea la tabla Cajas
     */
    public String createTableCajas () {
        return
                "CREATE TABLE IF NOT EXISTS Cajas (" +
                        "id_caja INTEGER PRIMARY KEY AUTOINCREMENT" +
                        ", uuid TEXT  NOT NULL" +
                        ", nombre TEXT NOT NULL" +
                        ")";
    }
    
    /**
     * Crea la tabla TipoTickets
     */
    public String createTableTipoTickets () {
        return
                "CREATE TABLE IF NOT EXISTS Tipo_Tickets(" +
                        "id_tipo_ticket INTEGER PRIMARY KEY AUTOINCREMENT" +
                        ", descripcion TEXT  NOT NULL" +
                        ", movimiento TEXT  NOT NULL" +
                        ")";
    }
    
    /**
     * Crea la tabla Sesiones
     */
    public String createTableSesiones () {
        return
                "CREATE TABLE IF NOT EXISTS Sesiones (" +
                        " id_sesion INTEGER PRIMARY KEY AUTOINCREMENT" +
                        ", id_caja INTEGER  NOT NULL" +
                        ", id_usuario INTEGER  NOT NULL" +
                        ", f_inicio INTEGER  NULL" +
                        ", f_fin INTEGER  NULL" +
                        ", FOREIGN KEY (id_caja)REFERENCES Cajas (id_caja)" +
                        ", FOREIGN KEY (id_usuario)REFERENCES Usuarios (id_usuario)" +
                        ")";
        
        
    }
    
    /**
     * Crea la tabla TipoCliente
     */
    public String createTableTipoCliente () {
        return
                "CREATE TABLE IF NOT EXISTS Tipo_cliente(" +
                        "id_tipo_cliente INTEGER PRIMARY KEY AUTOINCREMENT" +
                        ", desc_tipo_cliente TEXT  NOT NULL" +
                        ", Descuento REAL  NULL" +
                        ")";
        
        
    }
    
    /**
     * Crea la tabla Cliente
     */
    public String createTableCliente () {
        return
                "CREATE TABLE IF NOT EXISTS Cliente(" +
                        "id_cliente INTEGER PRIMARY KEY" +
                        ", numero_tarjeta TEXT  NULL" +
                        ", nombre_cliente TEXT  NOT NULL" +
                        ", id_tipo_cliente INTEGER" +
                        ")";
    }
    
    /**
     * Crea la tabla Articulos
     */
    public String createTableArticulos () {
        return
                "CREATE TABLE IF NOT EXISTS Articulos(" +
                        "  id_articulo INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "  id_central INTEGER DEFAULT 0 ," +
                        "  codigo_barras TEXT NOT NULL," +
                        "  nombre TEXT NOT NULL," +
                        "  id_marca INTEGER NULL," +
                        "  id_presentacion INTEGER NULL," +
                        "  id_unidad INTEGER NULL," +
                        "  contenido REAL NULL," +
                        "  precio_venta REAL NULL," +
                        "  precio_compra REAL NULL," +
                        "  id_familia INTEGER NULL," +
                        "  granel INTEGER NULL ," +
                        "  imagen TEXT NULL," +
                        "  timestamp INTEGER NOT NULL DEFAULT (strftime('%s','now'))," +
                        "  id_usuario INTEGER NOT NULL," +
                        "  fecha_baja INTEGER DEFAULT 0 ," +
                        "  FOREIGN KEY (id_usuario) REFERENCES Usuarios(id_usuario)," +
                        "  FOREIGN KEY (id_unidad) REFERENCES Unidades_de_medida(id_unidad)," +
                        "  FOREIGN KEY (id_familia) REFERENCES Familia(id_familia)," +
                        "  FOREIGN KEY (id_marca) REFERENCES Marcas(id_marca)" +
                        "  )";
    }
    
    /**
     * Crea la tabla UnidadesGranel
     */
    public String createTableUnidadesGranel () {
        return
                "CREATE TABLE IF NOT EXISTS Unidades_granel(" +
                        " id_unidad INTEGER PRIMARY KEY AUTOINCREMENT" +
                        ", desc_unidad TEXT  NULL" +
                        ")";
    }
    
    /**
     * Crea la tabla Monedas
     */
    public String createTableMonedas () {
        return
                "CREATE TABLE IF NOT EXISTS Monedas(" +
                        "id_moneda INTEGER PRIMARY KEY AUTOINCREMENT" +
                        ", desc_moneda TEXT  NOT NULL" +
                        ", defecto INTEGER  NOT NULL" +
                        ", tipo_cambio REAL  NOT NULL" +
                        ", timestamp INTEGER NOT NULL DEFAULT (strftime('%s','now'))" +
                        ")";
    }
    
    /**
     * Crea la tabla RegistroCierre
     */
    public String createTableRegistroCierre () {
        return
                "CREATE TABLE IF NOT EXISTS Registro_cierre(" +
                        " id_registro_cierre INTEGER PRIMARY KEY AUTOINCREMENT" +
                        ", id_caja INTEGER  NULL" +
                        ", id_usuario INTEGER  NOT NULL" +
                        ", fecha_cierre INTEGER  NOT NULL" +
                        ", importe_real REAL  NULL" +
                        ", importe_contable REAL  NULL" +
                        ", importe_remanente REAL  NULL" +
                        ", timestamp INTEGER NOT NULL DEFAULT (strftime('%s','now'))" +
                        ", FOREIGN KEY (id_usuario)REFERENCES Usuarios (id_usuario)" +
                        ", FOREIGN KEY (id_caja)REFERENCES Cajas (id_caja)" +
                        ")";
    }
    
    /**
     * Crea la tabla RegistroApertura
     */
    public String createTableRegistroApertura () {
        return
                "CREATE TABLE IF NOT EXISTS Registro_apertura(" +
                        "id_registro_apertura INTEGER PRIMARY KEY AUTOINCREMENT" +
                        ", id_registro_cierre INTEGER  NULL" +
                        ", id_caja INTEGER  NULL" +
                        ", id_usuario INTEGER  NOT NULL" +
                        ", fecha_apertura INTEGER  NULL" +
                        ", importe_real REAL  NULL" +
                        ", importe_contable REAL  NULL" +
                        ", timestamp INTEGER NOT NULL DEFAULT (strftime('%s','now'))" +
                        ", FOREIGN KEY (id_usuario)REFERENCES Usuarios (id_usuario)" +
                        ", FOREIGN KEY (id_caja)REFERENCES Cajas (id_caja)" +
                        ", FOREIGN KEY (id_registro_cierre)REFERENCES Registro_cierre (id_registro_cierre)" +
                        ")";
    }
    
    
    /**
     * Crea la tabla Perfiles
     */
    public String createTablePerfiles () {
        return
                "CREATE TABLE IF NOT EXISTS Perfiles(" +
                        "id_perfil INTEGER PRIMARY KEY AUTOINCREMENT" +
                        ", perfil TEXT  NOT NULL" +
                        ", modulos TEXT  NULL" +
                        ")";
    }
    
    
    /**
     * Crea la tabla TipoPago
     */
    public String createTableTipoPago () {
        return
                "CREATE TABLE IF NOT EXISTS Tipo_pago(" +
                        "id_tipo_pago INTEGER PRIMARY KEY AUTOINCREMENT" +
                        ", descripcion TEXT  NOT NULL" +
                        " )";
    }
    
    
    /**
     * Crea la tabla Tickets
     */
    public String createTableTickets () {
        return
                "CREATE TABLE IF NOT EXISTS Tickets(" +
                        "id_ticket INTEGER PRIMARY KEY AUTOINCREMENT" +
                        ", importe_bruto REAL  NOT NULL" +
                        ", importe_neto REAL  NULL" +
                        ", impuestos REAL  NULL" +
                        ", descuento REAL  NULL" +
                        ", id_moneda INTEGER  NULL" +
                        ", id_cliente INTEGER  NULL" +
                        ", id_comercio INTEGER NOT NULL" +
                        ", id_usuario INTEGER  NULL" +
                        ", id_caja INTEGER  NULL" +
                        ", cambio REAL  NULL" +
                        ", id_tipo_ticket INTEGER NOT NULL" +
                        ", descripcion TEXT  NOT NULL" +
                        ", timestamp INTEGER NOT NULL DEFAULT (strftime('%s','now'))" +
                        ", FOREIGN KEY (id_cliente)REFERENCES Cliente (id_cliente)" +
                        ", FOREIGN KEY (id_caja)REFERENCES Cajas (id_caja)" +
                        ", FOREIGN KEY (id_usuario)REFERENCES Usuarios (id_usuario)" +
                        ", FOREIGN KEY (id_moneda)REFERENCES Monedas (id_moneda)" +
                        ")";
    }
    
    /**
     * Crea la tabla ItemsTicket
     */
    public String createTableItemsTicket () {
        return
                "CREATE TABLE IF NOT EXISTS Items_Ticket(" +
                        "id_item_ticket INTEGER PRIMARY KEY AUTOINCREMENT" +
                        ",id_ticket INTEGER  NOT NULL" +
                        ", id_articulo INTEGER  NULL" +
                        ", codigo_barras TEXT  NOT NULL" +
                        ", descripcion TEXT  NOT NULL" +
                        ", cantidad REAL  NOT NULL" +
                        ", importe_unitario REAL  NOT NULL" +
                        ", importe_total REAL  NOT NULL" +
                        ", timestamp INTEGER NOT NULL DEFAULT (strftime('%s','now'))" +
                        ", FOREIGN KEY (id_articulo)REFERENCES Articulos (id_articulo)" +
                        ", FOREIGN KEY (id_ticket)REFERENCES Tickets (id_ticket)" +
                        ")";
    }
    
    /**
     * Crea la tabla Familias
     */
    public String createTableFamilias () {
        return
                "CREATE TABLE IF NOT EXISTS Familia(" +
                        "id_familia INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "nombre TEXT NULL," +
                        "id_categoria INTEGER " +
                        ")";
    }
    
    /**
     * Crea la tabla Impuestos
     */
    public String createTableImpuestos () {
        return
                "CREATE TABLE IF NOT EXISTS Impuestos(" +
                        "  id_impuestos INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "  nombre TEXT NOT NULL," +
                        "  impuesto REAL NOT NULL," +
                        "  prioridad INTEGER NULL " +
                        "  )";
    }
    
    /**
     * Crea la tabla Categorias
     */
    public String createTableCategorias () {
        return
                "CREATE TABLE IF NOT EXISTS Categorias(" +
                        "  id_categoria INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "  nombre TEXT NOT NULL " +
                        "  )";
    }
    
    /**
     * Crea la tabla Marcas
     */
    public String createTableMarcas () {
        return
                "CREATE TABLE IF NOT EXISTS Marcas(" +
                        "  id_marca INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "  nombre TEXT NOT NULL " +
                        "  )";
    }
    
    /**
     * Crea la tabla Notificaciones
     */
    public String createTableNotificaciones () {
        return
                "CREATE TABLE IF NOT EXISTS Notificaciones(" +
                        "  id_notificacion INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "  id_articulo INTEGER NULL," +
                        "  mensaje TEXT NULL," +
                        "  activo INTEGER NOT NULL," +
                        "  etapa INTEGER NOT NULL" +
                        "  )";
    }
    
    /**
     * Crea la tabla Presentaciones
     */
    public String createTablePresentaciones () {
        return
                "CREATE TABLE IF NOT EXISTS Presentaciones(" +
                        "  id_presentacion INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "  nombre TEXT NOT NULL " +
                        "  )";
    }
    
    /**
     * Crea la tabla Preguntas
     */
    public String createTablePreguntas () {
        return
                "CREATE TABLE IF NOT EXISTS Preguntas(" +
                        "  id_pregunta INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "  pregunta TEXT NOT NULL" +
                        "  )";
    }
    
    /**
     * Crea la tabla Inventario
     */
    public String createTableInventario () {
        return
                "CREATE TABLE IF NOT EXISTS Inventario (" +
                        "  id_inventario INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "  id_articulo INTEGER NOT NULL," +
                        "  cantidad REAL NOT NULL," +
                        "  fecha_modificacion INTEGER DEFAULT 0," +
                        "  id_sesion INTEGER NOT NULL," +
                        "  FOREIGN KEY (id_articulo) REFERENCES Articulos(id_articulo)," +
                        "  FOREIGN KEY (id_sesion) REFERENCES Sesiones(id_sesion)" +
                        "  )";
    }
    
    /**
     * Crea la tabla TicketsTiposPago
     */
    public String createTableTicketTiposPago () {
        return
                "CREATE TABLE IF NOT EXISTS Ticket_tipos_pago (" +
                        "  id_ticket_tipos_pago INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "  id_ticket INTEGER NOT NULL," +
                        "  id_tipo_pago INTEGER NOT NULL," +
                        "  monto REAL  NOT NULL," +
                        "  descripcion TEXT  NOT NULL," +
                        "  id_moneda INTEGER NOT NULL" +
                        "  )";
    }
    
    /**
     * Crea la tabla MailUser
     */
    public String createTableMailUser () {
        
        
        String sql = "CREATE TABLE IF NOT EXISTS " + CorreosClientesDB.DATABASE_TABLE + " (" +
                CorreosClientesDB.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CorreosClientesDB.KEY_EMAIL + " TEXT ," +
                CorreosClientesDB.KEY_FECHA_CREACION + "  TEXT  ," +
                CorreosClientesDB.KEY_FECHA_MODIFICACION + "  TEXT  ," +
                CorreosClientesDB.KEY_ID_REMOTO + "  TEXT  ," +
                CorreosClientesDB.KEY_STATUS_MOBILE + " NUMBER  )";
        
        
        return sql;
    } /**
     * Crea la tabla MailUser
     */
    public String createTableTickesEnciados () {
        
        
        String sql = "CREATE TABLE IF NOT EXISTS " + TicketsEnviadosClientesDB.DATABASE_TABLE + " (" +
                TicketsEnviadosClientesDB.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TicketsEnviadosClientesDB.KEY_ID_TICKETS + " INTEGER  ," +
                TicketsEnviadosClientesDB.KEY_EMAIL + " TEXT ," +
                TicketsEnviadosClientesDB.KEY_FECHA_CREACION + "  TEXT  ," +
                TicketsEnviadosClientesDB.KEY_FECHA_MODIFICACION + "  TEXT  ," +
                TicketsEnviadosClientesDB.KEY_ID_REMOTO + "  TEXT  ," +
                TicketsEnviadosClientesDB.KEY_STATUS_MOBILE + " NUMBER  )";
        
        
        return sql;
    }
    
    /**
     * Tabla que ayuda a controlar el versionamiento de la bd
     * @return script tabla TMAEVersion
     */
    public String createTableTMAEVersion(){
        String sql = "CREATE TABLE IF NOT EXISTS " + TMAEVersionDB.DATABASE_TABLE + " ( " +
                TMAEVersionDB.KEY_VERSION + " TEXT PRIMARY KEY ); ";
        
        return sql;
    }
    
    /**
     * Crea la tabla TelefonoUser
     */
    public String createTableTelefonoUser () {
        return
                "CREATE TABLE IF NOT EXISTS Telefono_Users (" +
                        "  id_telefono INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "  num_telefono_user TEXT NOT NULL" +
                        "  )";
    }
    
    
    ////////////////////////Insert´s/////////////////////////////////////////////
    
    /**
     * @return reemplaza o agrega un registro a la tabla Grupo
     */
    public String replaceIntoGrupo () {
        return
                "REPLACE INTO " +
                        "Grupo(id_grupo,desc_grupo) " +
                        "VALUES (?,?)";
    }
    
    /**
     * @return reemplaza o agrega un registro a la tabla Comercio
     */
    public String replaceIntoComercio () {
        return
                "REPLACE INTO " +
                        "Comercio(id_comercio,descripcion,id_grupo,nombre_legal,rfc," +
                        "direccion_legal,numero_exterior_legal,numero_interior_legal," +
                        "colonia_legal,municipio_legal,estado_legal,postal_Legal," +
                        "pais_legal,direccion_fisica,numero_exterior_fisica,numero_interior_fisica," +
                        "colonia_fisica,municipio_fisica,estado_fisica,postal_fisica,pais_fisica) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    }
    
    /**
     * @return reemplaza o agrega un registro a la tabla Settings
     */
    public String replaceIntoSettings () {
        return
                "REPLACE INTO " +
                        "Settings(id_settings,nombre,valor)" +
                        "VALUES (?,?,?)";
    }
    
    /**
     * @return reemplaza o agrega un registro a la tabla Usuarios
     */
    public String replaceIntoUsuarios () {
        return
                "REPLACE INTO " +
                        "Usuarios(id_usuario,usuario,password,id_perfil,nombre,apellidos," +
                        "pregunta1,respuesta1,activo,bloqueado)" +
                        "VALUES (?,?,?,?,?,?,?,?,?,?)";
        
    }
    
    /**
     * @return reemplaza o agrega un registro a la tabla Cajas
     */
    public String replaceIntoCajas () {
        return
                "REPLACE INTO " +
                        "Cajas(id_caja,uuid,nombre)" +
                        "VALUES (?,?,?)";
    }
    
    /**
     * @return reemplaza o agrega un registro a la tabla Sesiones
     */
    public String replaceIntoSesiones () {
        return
                "REPLACE INTO " +
                        "Sesiones(id_sesion,id_caja,id_usuario,f_inicio,f_fin)" +
                        "VALUES (?,?,?,?,?)";
    }
    
    /**
     * @return reemplaza o agrega un registro a la tabla TipoCliente
     */
    public String replaceIntoTipoCliente () {
        return
                "REPLACE INTO " +
                        "Tipo_cliente(id_tipo_cliente,desc_tipo_cliente,Descuento)" +
                        "VALUES (?,?,?)";
    }
    
    /**
     * @return reemplaza o agrega un registro a la tabla Cliente
     */
    public String replaceIntoCliente () {
        
        return
                "REPLACE INTO " +
                        "Cliente(id_cliente,numero_tarjeta,nombre_cliente,id_tipo_cliente)" +
                        "VALUES (?,?,?,?)";
    }
    
    /**
     * @return reemplaza o agrega un registro a la tabla Articulos
     */
    public String replaceIntoArticulos () {
        return
                "REPLACE INTO " +
                        "Articulos(id_articulo,id_central,codigo_barras,nombre,id_marca,id_presentacion,id_unidad" +
                        ",contenido,precio_venta,precio_compra,id_familia,granel,imagen,id_usuario,fecha_baja)" +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    }
    
    /**
     * @return reemplaza o agrega un registro a la tabla UnidadesGranel
     */
    public String replaceIntoUnidadesGranel () {
        return
                "REPLACE INTO " +
                        "Unidades_granel(id_unidad,desc_unidad)" +
                        "VALUES (?,?)";
    }
    
    /**
     * @return reemplaza o agrega un registro a la tabla Monedas
     */
    public String replaceIntoMonedas () {
        return
                "REPLACE INTO " +
                        "Monedas(id_moneda,desc_moneda,defecto,tipo_cambio)" +
                        "VALUES (?,?,?,?)";
    }
    
    /**
     * @return reemplaza o agrega un registro a la tabla RegistroCierre
     */
    public String replaceIntoRegistroCierre () {
        return
                "REPLACE INTO " +
                        "Registro_cierre(id_registro_cierre,id_caja,id_usuario,fecha_cierre," +
                        "importe_real,importe_contable,importe_remanente)" +
                        "VALUES (?,?,?,?,?,?,?)";
    }
    
    /**
     * @return reemplaza o agrega un registro a la tabla RegistroApertura
     */
    public String replaceIntoRegistroApertura () {
        return
                "REPLACE INTO " +
                        "Registro_apertura(id_registro_apertura,id_registro_cierre,id_caja,id_usuario," +
                        "fecha_apertura,importe_real,importe_contable) " +
                        "VALUES (?,?,?,?,?,?,?)";
    }
    
    /**
     * @return reemplaza o agrega un registro a la tabla Perfiles
     */
    public String replaceIntoPerfiles () {
        return
                "REPLACE INTO " +
                        "Perfiles(id_perfil,perfil,modulos) " +
                        "VALUES (?,?,?)";
    }
    
    /**
     * @return reemplaza o agrega un registro a la tabla Tickets
     */
    public String replaceIntoTickets () {
        return
                "REPLACE INTO " +
                        "Tickets(id_ticket,importe_bruto,importe_neto," +
                        "impuestos,descuento,id_moneda,id_cliente,id_comercio,id_usuario,id_caja," +
                        "cambio, id_tipo_ticket, descripcion,timestamp)" +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    }
    
    /**
     * @return reemplaza o agrega un registro a la tabla ItemsTickets
     */
    public String replaceIntoItemsTicket () {
        return
                "REPLACE INTO " +
                        "Items_Ticket(id_item_ticket,id_ticket,id_articulo,codigo_barras,descripcion," +
                        "cantidad,importe_unitario,importe_total,timestamp,precio_compra)" +
                        "VALUES (?,?,?,?,?,?,?,?,?,?)";
    }
    
    /**
     * @return reemplaza o agrega un registro a la tabla Familias
     */
    public String replaceIntoFamilias () {
        return
                "REPLACE INTO " +
                        "Familia(id_familia,nombre,id_categoria) " +
                        "VALUES (?,?,?)";
    }
    
    /**
     * @return reemplaza o agrega un registro a la tabla Impuestos
     */
    public String replaceIntoImpuestos () {
        return
                "REPLACE INTO " +
                        "Impuestos(id_impuestos,nombre,impuesto,prioridad)" +
                        "VALUES (?,?,?,?)";
    }
    
    /**
     * @return reemplaza o agrega un registro a la tabla Categorias
     */
    public String replaceIntoCategorias () {
        return
                "REPLACE INTO " +
                        "Categorias(id_categoria,nombre) " +
                        "VALUES (?,?)";
    }
    
    /**
     * @return reemplaza o agrega un registro a la tabla Marcas
     */
    public String replaceIntoMarcas () {
        return
                "REPLACE INTO " +
                        "Marcas(id_marca,nombre)" +
                        "VALUES (?,?)";
    }
    
    /**
     * @return reemplaza o agrega un registro a la tabla notificaciones
     */
    public String replaceIntoNotificaciones () {
        return
                "REPLACE INTO " +
                        "Notificaciones(id_notificacion,id_articulo,mensaje,activo,etapa)" +
                        "VALUES (?,?,?,?,?)";
    }
    
    /**
     * @return reemplaza o agrega un registro a la tabla presentaciones
     */
    public String replaceIntoPresentaciones () {
        return
                "REPLACE INTO " +
                        "Presentaciones(id_presentacion,nombre)" +
                        "VALUES (?,?)";
    }
    
    /**
     * @return reemplaza o agrega un registro a la tabla preguntas
     */
    public String replaceIntoPreguntas () {
        return
                
                "REPLACE INTO " +
                        "Preguntas(id_pregunta,pregunta)" +
                        "VALUES (?,?)";
    }
    
    /**
     * @return reemplaza o agrega un registro a la tabla Inventario
     */
    public String replaceIntoInventario () {
        return
                "REPLACE INTO " +
                        "Inventario(id_inventario,id_articulo,cantidad,fecha_modificacion,id_sesion)" +
                        "VALUES (?,?,?,?,?)";
    }
    
    /**
     * @return reemplaza o agrega un registro a la tabla IicketTiposPagos
     */
    public String replaceIntoTicketTiposPagos () {
        return
                
                "REPLACE INTO " +
                        "Ticket_tipos_pago(id_Ticket_tipos_pago,id_ticket,id_tipo_pago,monto,descripcion,id_moneda)" +
                        "VALUES (?,?,?,?,?,?)";
    }
    
    /**
     * @return reemplaza o agrega un registro a la tabla TipoTickets
     */
    public String replaceIntoTipoTickets () {
        return
                
                "REPLACE INTO " +
                        "Tipo_Tickets(id_tipo_ticket,descripcion,movimiento)" +
                        "VALUES (?,?,?)";
    }
    
    /**
     * @return reemplaza o agrega un registro a la tabla TipoPago
     */
    public String replaceIntoTipoPago () {
        return
                
                "REPLACE INTO " +
                        "Tipo_pago(id_tipo_pago,descripcion)" +
                        "VALUES (?,?)";
    }
    
    /**
     * @return reemplaza o agrega un registro a la tabla SyncData
     */
    public String replaceTableSyncData () {
        return
                
                "REPLACE INTO " +
                        "Sync_Data(id_sequence,id_comercio,uuid,sync_table,sync_operation,sync_data)" +
                        "VALUES (?,?,?,?,?,?)";
    }
    
 
    /**
     * @return reemplaza o agrega un registro a la tabla TelefonoUsers
     */
    public String replaceIntoTelefonoUsers () {
        return
                
                "REPLACE INTO " +
                        "Telefono_Users(id_telefono,num_telefono_user)" +
                        "VALUES (?,?)";
    }
    
    
}
