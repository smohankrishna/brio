package net.hova_it.barared.brio.apis.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.hova_it.barared.brio.apis.models.entities.MailUsers;
import net.hova_it.barared.brio.apis.models.entities.TelefonoUsers;
import net.hova_it.barared.brio.apis.sqlite.templates.ArticuloTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.BalanceTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.CajaTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.CategoriaTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.ClienteTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.ComercioTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.EstadoPosTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.FamiliaTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.GrupoTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.ImpuestoTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.ImpuestosDeArticuloTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.InventarioTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.ItemsTicketTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.MarcaTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.MonedaTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.NotificacionTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.ReporteTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.SettingsTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.PerfilTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.PreguntaTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.PresentacionTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.RegistroAperturaTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.RegistroCierreTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.SesionTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.SyncDataTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.SyncTicketLocalTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.TarifaCantidadTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.TelefonoUsersTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.TicketTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.TicketTipoPagoTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.TipoClienteTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.TipoPagoTemplete;
import net.hova_it.barared.brio.apis.sqlite.templates.TipoTicketTemplete;
import net.hova_it.barared.brio.apis.sqlite.templates.TransaccionTarjetaTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.TransaccionesTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.UnidadesGranelTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.UsuarioTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.ViewArticuloTemplate;
import net.hova_it.barared.brio.apis.sqlite.templates.ViewInventarioTemplate;

import lat.brio.core.BrioGlobales;


/**
 * Cuando una base es nueva  se manda a construir, manda a llamar los createTables
 * Created by Alejandro Gomez on 02/12/2015.
 */

public class SQLiteService extends SQLiteOpenHelper {
    private final String TAG = "BARA_LOG";
    private static SQLiteService mInstance = null;
    private SQLiteInit query;
    private Context context;

    //Templates
    public ArticuloTemplate articulos;
    public CajaTemplate cajas;
    public MarcaTemplate marcas;
    public UsuarioTemplate usuarios;
    public ComercioTemplate comercios;
    public SesionTemplate sesiones;
    public ClienteTemplate clientes;
    public TipoClienteTemplate tiposCliente;
    public TicketTemplate tickets;
    public ItemsTicketTemplate itemsTicket;
    public MonedaTemplate monedas;
    public GrupoTemplate grupos;
    public FamiliaTemplate familias;
    public ImpuestoTemplate impuestos;
    public SettingsTemplate settings;
    public UnidadesGranelTemplate unidades;
    public CategoriaTemplate categorias;
    public ViewArticuloTemplate viewArticulo;
    public ViewInventarioTemplate viewInventario;
    public TarifaCantidadTemplate tarifaCantidad;
    public ImpuestosDeArticuloTemplate impuestoDeArticulo;
    public NotificacionTemplate notificacion;
    public PresentacionTemplate presentacion;
    public RegistroCierreTemplate registrosCierre;
    public RegistroAperturaTemplate registrosApertura;
    public PreguntaTemplate preguntas;
    public InventarioTemplate inventario;
    public TicketTipoPagoTemplate ticketTipoPago;
    public TipoTicketTemplete tipoTicket;
    public TipoPagoTemplete tipoPago;
    public BalanceTemplate balance;
    public SyncDataTemplate syncData;
    public PerfilTemplate perfil;
    public ReporteTemplate reporte;
    public EstadoPosTemplate estadoPos;
    public TransaccionesTemplate transacciones;
    public SyncTicketLocalTemplate syncTicketLocal;
    public TransaccionTarjetaTemplate transaccionTarjeta;
//    public MailUsersTemplate mailsUsers;
    public TelefonoUsersTemplate telefonosUsers;


    public static SQLiteService getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SQLiteService(context);
        }

        return mInstance;
    }

    /**Manda a llamar al SQLiteInit y ejecuta el query respecto a lo que se manda a llamar
     * @param context contexto de la aplicacion
     */
    private SQLiteService(Context context) {
        super(context, BrioGlobales.DB_NAME, null, BrioGlobales.DB_VERSION);
        //context.deleteDatabase(SQLiteInit.DATABASE_NAME); // TODO * Debug Only
        this.query = new SQLiteInit();
        this.context = context;

        //Templates
        this.articulos = new ArticuloTemplate(this);
        this.cajas = new CajaTemplate(this);
        this.marcas = new MarcaTemplate(this);
        this.usuarios = new UsuarioTemplate(this);
        this.comercios = new ComercioTemplate(this);
        this.sesiones = new SesionTemplate(this);

        this.clientes = new ClienteTemplate(this);
        this.tiposCliente = new TipoClienteTemplate(this);
        this.tickets = new TicketTemplate(this);
        this.itemsTicket = new ItemsTicketTemplate(this);
        this.monedas = new MonedaTemplate(this);
        this.grupos = new GrupoTemplate(this);
        this.familias = new FamiliaTemplate(this);
        this.impuestos = new ImpuestoTemplate(this);
        this.settings = new SettingsTemplate(this);
        this.unidades = new UnidadesGranelTemplate(this);
        this.categorias = new CategoriaTemplate(this);
        this.viewArticulo = new ViewArticuloTemplate(this);
        this.viewInventario = new ViewInventarioTemplate(this);
        this.tarifaCantidad = new TarifaCantidadTemplate(this);
        this.impuestoDeArticulo = new ImpuestosDeArticuloTemplate(this);
        this.notificacion = new NotificacionTemplate(this);
        this.presentacion = new PresentacionTemplate(this);
        this.registrosCierre = new RegistroCierreTemplate(this);
        this.registrosApertura = new RegistroAperturaTemplate(this);
        this.preguntas = new PreguntaTemplate(this);
        this.inventario = new InventarioTemplate(this);
        this.ticketTipoPago = new TicketTipoPagoTemplate(this);
        this.tipoTicket = new TipoTicketTemplete(this);
        this.tipoPago = new TipoPagoTemplete(this);
        this.balance = new BalanceTemplate(this);
        this.syncData = new SyncDataTemplate(this);
        this.perfil = new PerfilTemplate(this);
        this.reporte = new ReporteTemplate(this);
        this.estadoPos = new EstadoPosTemplate(this);
        this.transacciones = new TransaccionesTemplate(this);
        this.syncTicketLocal = new SyncTicketLocalTemplate(this);
        this.transaccionTarjeta = new TransaccionTarjetaTemplate(this);
//        this.mailsUsers = new MailUsersTemplate(this);
//        this.telefonosUsers = new TelefonoUsersTemplate(this);
    }

    /**Ejecuta el query de la creacion de las tablas en la base de datos
     * @param db nombre de la base
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(query.createTableGrupo());
        db.execSQL(query.createTableFamilias());
        db.execSQL(query.createTableImpuestos());
        db.execSQL(query.createTableCategorias());
        db.execSQL(query.createTableMarcas());
        db.execSQL(query.createTableComercio());
        db.execSQL(query.createTablePerfiles());
        db.execSQL(query.createTableSettings());
        db.execSQL(query.createTableUsuarios());
        db.execSQL(query.createTableTipoCliente());
        db.execSQL(query.createTableCliente());
        db.execSQL(query.createTableMonedas());
        db.execSQL(query.createTableCajas());
        db.execSQL(query.createTableSesiones());
        db.execSQL(query.createTableArticulos());
        db.execSQL(query.createTableTipoTickets());
        db.execSQL(query.createTableTickets());
        db.execSQL(query.createTableUnidadesGranel());
        db.execSQL(query.createTableItemsTicket());
        db.execSQL(query.createTableTipoPago());
        db.execSQL(query.createTableTicketTiposPago());
        db.execSQL(query.createTableSyncData());
        db.execSQL(query.createTableRegistroCierre());
        db.execSQL(query.createTableRegistroApertura());
        db.execSQL(query.createTableNotificaciones());
        db.execSQL(query.createTablePresentaciones());
        db.execSQL(query.createViewArticulo());

        db.execSQL(query.createTablePreguntas());
        db.execSQL(query.createTableInventario());
        db.execSQL(query.createViewInventario());

        db.execSQL(query.createTableMailUser());
        db.execSQL(query.createTableTickesEnciados () );
//        db.execSQL(query.createTableTelefonoUser());


    }

    /**Actualiza la version de la base de datos
     * @param db base de datos
     * @param oldVersion version anterior
     * @param newVersion version nueva
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    /**
     * @return retorna el contexto
     */
    public Context getContext() {
        return context;
    }

    /**
     * @param context contexto de la aplicacion
     */
    public void setContext(Context context) {
        this.context = context;
    }

    /**Retorna el ultimo registro
     * @param table tabla en la que se va a buscar
     * @param column columna de la tabla
     * @param db base de datos
     * @return retorna un entero del id del ultimo registro en la tabla
     */
    public int getLastId(String table, String column, SQLiteDatabase db){
        Cursor cursor = db.rawQuery("SELECT " + column + " FROM " + table + " ORDER BY " + column + " DESC LIMIT 1", null);
        int _id = cursor.moveToFirst()? cursor.getInt(0) : 0;
        cursor.close();
        return _id;
    }
}
