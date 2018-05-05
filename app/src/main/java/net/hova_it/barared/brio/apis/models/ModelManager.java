package net.hova_it.barared.brio.apis.models;


import android.content.Context;

import net.hova_it.barared.brio.apis.models.daos.ArticulosDAO;
import net.hova_it.barared.brio.apis.models.daos.BalanceDAO;
import net.hova_it.barared.brio.apis.models.daos.CajaDAO;
import net.hova_it.barared.brio.apis.models.daos.CategoriaDAO;
import net.hova_it.barared.brio.apis.models.daos.ClienteDAO;
import net.hova_it.barared.brio.apis.models.daos.ComercioDAO;
import net.hova_it.barared.brio.apis.models.daos.EstadoPosDAO;
import net.hova_it.barared.brio.apis.models.daos.FamiliaDAO;
import net.hova_it.barared.brio.apis.models.daos.GrupoDAO;
import net.hova_it.barared.brio.apis.models.daos.ImpuestoDAO;
import net.hova_it.barared.brio.apis.models.daos.InventarioDAO;
import net.hova_it.barared.brio.apis.models.daos.ItemsTicketDAO;
import net.hova_it.barared.brio.apis.models.daos.CorreosClientesDB;
import net.hova_it.barared.brio.apis.models.daos.MarcasDAO;
import net.hova_it.barared.brio.apis.models.daos.MonedaDAO;
import net.hova_it.barared.brio.apis.models.daos.ReportesDAO;
import net.hova_it.barared.brio.apis.models.daos.SettingsDAO;
import net.hova_it.barared.brio.apis.models.daos.PerfilDAO;
import net.hova_it.barared.brio.apis.models.daos.PreguntaDAO;
import net.hova_it.barared.brio.apis.models.daos.PresentacionDAO;
import net.hova_it.barared.brio.apis.models.daos.RegistroAperturaDAO;
import net.hova_it.barared.brio.apis.models.daos.RegistroCierreDAO;
import net.hova_it.barared.brio.apis.models.daos.SesionDAO;
import net.hova_it.barared.brio.apis.models.daos.SyncTicketLocalDAO;
import net.hova_it.barared.brio.apis.models.daos.TelefonoUsersDAO;
import net.hova_it.barared.brio.apis.models.daos.TicketDAO;
import net.hova_it.barared.brio.apis.models.daos.TicketTipoPagoDAO;
import net.hova_it.barared.brio.apis.models.daos.TipoClienteDAO;
import net.hova_it.barared.brio.apis.models.daos.TipoTicketDAO;
import net.hova_it.barared.brio.apis.models.daos.TransaccionesDAO;
import net.hova_it.barared.brio.apis.models.daos.TransaccionesTarjetaDAO;
import net.hova_it.barared.brio.apis.models.daos.UnidadesGranelDAO;
import net.hova_it.barared.brio.apis.models.daos.UsuarioDAO;
import net.hova_it.barared.brio.apis.models.daos.ViewArticuloDAO;
import net.hova_it.barared.brio.apis.models.daos.ViewInventarioDAO;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * Clase encargada de facilitar el acceso a los metodos del DAO de cada modelo de datos.
 * Created by Alejandro Gomez on 27/11/2015.
 */

public class ModelManager {
    private SQLiteService sqLiteService;

    public ArticulosDAO articulos;
    public CajaDAO cajas;
    public CategoriaDAO categorias;
    public ClienteDAO clientes;
    public ComercioDAO comercios;
    public FamiliaDAO familias;
    public GrupoDAO grupos;
    public ImpuestoDAO impuestos;
    public ItemsTicketDAO itemsTicket;
    public MarcasDAO marcas;
    public MonedaDAO monedas;
    public SettingsDAO settings;
    public PreguntaDAO preguntas;
    public PresentacionDAO presentaciones;
    public RegistroAperturaDAO registrosApertura;
    public RegistroCierreDAO registrosCierre;
    public SesionDAO sesiones;
    public TicketDAO tickets;
    public TipoClienteDAO tiposCliente;
    public UnidadesGranelDAO unidades;
    public UsuarioDAO usuarios;
    public InventarioDAO inventario;
    public ViewArticuloDAO viewArticulos;
    public ViewInventarioDAO viewInventario;
    public TicketTipoPagoDAO ticketTipoPago;
    public TipoTicketDAO tipoTicket;
    public BalanceDAO balance;
    public PerfilDAO perfil;
    public ReportesDAO reporte;
    public EstadoPosDAO estadoPos;
    public TransaccionesDAO transacciones;
    public SyncTicketLocalDAO syncTicketLocal;
    public TransaccionesTarjetaDAO transaccionesTarjeta;
    public CorreosClientesDB mailUser;
    public TelefonoUsersDAO telefonoUser;

    public ModelManager(Context context) {
        this.sqLiteService = SQLiteService.getInstance(context);
        initDAOs();

    }

    private void initDAOs(){
        this.articulos = new ArticulosDAO(sqLiteService);
        this.cajas = new CajaDAO(sqLiteService);
        this.categorias = new CategoriaDAO(sqLiteService);
        this.clientes = new ClienteDAO(sqLiteService);
        this.comercios = new ComercioDAO(sqLiteService);
        this.familias = new FamiliaDAO(sqLiteService);
        this.grupos = new GrupoDAO(sqLiteService);
        this.impuestos = new ImpuestoDAO(sqLiteService);
        this.itemsTicket = new ItemsTicketDAO(sqLiteService);
        this.marcas = new MarcasDAO(sqLiteService);
        this.monedas = new MonedaDAO(sqLiteService);
        this.settings = new SettingsDAO(sqLiteService);
        this.preguntas = new PreguntaDAO(sqLiteService);
        this.presentaciones = new PresentacionDAO(sqLiteService);
        this.registrosApertura = new RegistroAperturaDAO(sqLiteService);
        this.registrosCierre = new RegistroCierreDAO(sqLiteService);
        this.sesiones = new SesionDAO(sqLiteService);
        this.tickets = new TicketDAO(sqLiteService);
        this.tiposCliente = new TipoClienteDAO(sqLiteService);
        this.unidades = new UnidadesGranelDAO(sqLiteService);
        this.usuarios = new UsuarioDAO(sqLiteService);
        this.inventario = new InventarioDAO(sqLiteService);
        this.viewArticulos = new ViewArticuloDAO(sqLiteService);
        this.viewInventario = new ViewInventarioDAO(sqLiteService);
        this.ticketTipoPago = new TicketTipoPagoDAO(sqLiteService);
        this.tipoTicket = new TipoTicketDAO(sqLiteService);
        this.balance = new BalanceDAO(sqLiteService);
        this.perfil = new PerfilDAO(sqLiteService);
        this.reporte = new ReportesDAO(sqLiteService);
        this.estadoPos = new EstadoPosDAO(sqLiteService);
        this.transacciones = new TransaccionesDAO(sqLiteService);
        this.syncTicketLocal = new SyncTicketLocalDAO(sqLiteService);
        this.transaccionesTarjeta = new TransaccionesTarjetaDAO(sqLiteService);
        //this.mailUser = new CorreosClientesDB(sqLiteService);
        this.telefonoUser = new TelefonoUsersDAO(sqLiteService);

    }

    /**
     * Creacion de registros Dummy para test
     * @param type
     * @param <T>
     * @return
     */
    public <T> T createDummy(Class<T> type){
        PodamFactory factory = new PodamFactoryImpl();
        return factory.manufacturePojo(type);
    }


    }

