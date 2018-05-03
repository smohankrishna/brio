package lat.brio.api.sincronizador;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.Articulo;
import net.hova_it.barared.brio.apis.models.entities.Categoria;
import net.hova_it.barared.brio.apis.models.entities.Familia;
import net.hova_it.barared.brio.apis.models.entities.Inventario;
import net.hova_it.barared.brio.apis.models.entities.Marca;
import net.hova_it.barared.brio.apis.models.entities.Presentacion;
import net.hova_it.barared.brio.apis.models.entities.SuperSyncData;
import net.hova_it.barared.brio.apis.models.entities.SyncData;
import net.hova_it.barared.brio.apis.models.entities.SyncDataJson;
import net.hova_it.barared.brio.apis.models.entities.UnidadesGranel;
import net.hova_it.barared.brio.apis.session.SessionManager;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;
import net.hova_it.barared.brio.apis.sync.SyncUploadTask;
import net.hova_it.barared.brio.apis.sync.entities.FTPData;
import net.hova_it.barared.brio.apis.utils.Utils;

import org.json.JSONObject;

import lat.brio.api.servicio.IServicio;
import lat.brio.api.servicio.Servicio;
import lat.brio.core.BrioGlobales;

/**
 * Created by Delgadillo on 11/04/17.
 */
public class Sincronizador implements IServicio {
    private final static int
            TASK_SYNC_LAST = 0, TASK_SYNC_UPLOAD = 1,
            TASK_SYNC_CATALOGS = 2, TASK_SYNC_FINAL = 3;
    
    private Context context;
    private SQLiteService sqLiteService;
    //public Boolean _Mensajes = true;
    private SyncUploadTask syncUploadTask;
    private SuperSyncData superSyncData;
    private LastSequenceRESTService lastSequenceRESTService;
    private SyncUploadRESTService syncUploadRESTService;
    private SyncCatalogsRESTService syncCatalogsRESTService;
    
    private SyncUploadTask.SyncUploadTaskListener superListener;
    
    public Sincronizador (Context context) {
        this.context = context;
        
    }
    
    /**
     * (0) subir archivos al FTP
     * (1) consultar lastSequence del server
     * (2) sincronizar con el servicio web (subida)
     * (3) sincronización de catálogos con servicio Web (bajada)
     * (4) descargar APK
     * @param ftpData
     * @param listener
     */
    public void sync (FTPData ftpData, SyncUploadTask.SyncUploadTaskListener listener) {
        superListener = listener;
        
        //(0) subir archivos al FTP
        syncUploadTask = new SyncUploadTask (context,
                new SyncUploadTask.SyncUploadTaskListener () {
                    @Override
                    public void onSyncUploadTask (boolean success) {
                        if (success) {
                            //(1) Consultar lastSequence del server
                            //if(_Mensajes) {
                            lastSequenceRESTService = new LastSequenceRESTService (context, Sincronizador.this, TASK_SYNC_LAST);
                            try {
                                lastSequenceRESTService.Sincronizar ();
                            } catch (Exception Ex) {
                                Sincronizador.this.alSincronizarServicio (TASK_SYNC_LAST, false);
                            }
                            //}else{
                            //    lastSequenceRESTService = new LastSequenceRESTService(context, Sincronizador.this, TASK_SYNC_UPLOAD);
                            //    lastSequenceRESTService.Sincronizar();
                            //}
                        } else {
                            //(4) Descargar APK
                            Log.i ("SyncManager", "RESTSync success? false --> no sync catalogos");
                            superListener.onSyncUploadTask (false);
                        }
                    }
                });
        
        if (syncUploadTask != null) {
            //syncUploadTask.SubirFTP = _Mensajes;
        }
        syncUploadTask.execute (ftpData);
    }
    
    /**
     * Obtiene/Establece si se mostraran mensajes en la aplicacion mietras sincrnoiza
     
     * @param showAllBanners
     * @return
     */
    //public Boolean Mensajes(Boolean showAllBanners){
    //this._Mensajes = showAllBanners;
    //return this._Mensajes;
    //}
    
    /**
     * (2) sincronizar con el servicio web (subida)
     * @param ftpUploadSuccess
     */
    private void syncRESTUpload (boolean ftpUploadSuccess) {
        
        superSyncData = syncUploadTask.getSuperSyncData ();
        
        //2) sincronizar con el servicio web (subida)
        if (ftpUploadSuccess && superSyncData != null) {
            // Si hay datos de sync los subo al rest
            syncUploadRESTService = new SyncUploadRESTService (context, lastSequenceRESTService.getLastSequence (), syncUploadTask.getCurrentSequence (), Sincronizador.this, TASK_SYNC_UPLOAD);
            syncUploadRESTService.Sincronizar ();
        } else {
            syncRESTCatalogs (ftpUploadSuccess);
        }
    }
    
    /**
     * (3) sincronización de catálogos con servicio Web (bajada)
     * @param syncUploadSuccess
     */
    private void syncRESTCatalogs (boolean syncUploadSuccess) {
        Log.i ("SyncManager", "RESTSync success? " + syncUploadSuccess);
        Log.i ("SyncManager", "doSyncCatalogsService? " + syncUploadSuccess);
        
        if (syncUploadSuccess) {
            Log.i ("SyncManager", "SyncCatalogsRESTService init");
            syncCatalogsRESTService = new SyncCatalogsRESTService (context, Sincronizador.this, TASK_SYNC_CATALOGS);
            syncCatalogsRESTService.Sincronizar (); //--> de aqui regresa al switch case TASK_SYNC_CATALOGS
        } else {
            //4) descargar APK
            Log.i ("SyncManager", "RESTSync success? false --> no sync catalogos");
            superListener.onSyncUploadTask (false);
        }
    }
    
    @Override
    public void alSincronizarServicio (Integer Tarea, Boolean Sincronizado) {
        switch (Tarea) {
            case TASK_SYNC_LAST:
                //(2) sincronizar con el servicio web (subida)
                syncRESTUpload (Sincronizado);
                break;
            case TASK_SYNC_UPLOAD:
                if (syncUploadTask.hayPendientes ()) {
                    // Rellenar pendientes
                    syncUploadTask.fillSuperSyncData ();
                    // Sincronizar con el servidor
                    syncRESTUpload (Sincronizado);
                } else {
                    //(3) sincronización de catálogos con servicio Web (bajada)
                    syncRESTCatalogs (Sincronizado);
                }
                break;
            case TASK_SYNC_CATALOGS:
                //(4) descargar APK
                superListener.onSyncUploadTask (Sincronizado);
                break;
            case TASK_SYNC_FINAL:
                
                break;
        }
    }
    
    
    /**
     * Sincronización.
     *
     * (Subida)
     *
     * Subida de datos de la aplicación
     * al servidor.
     */
    class SyncUploadRESTService extends Servicio {
        private final static String url = "/cabws/api/sync/upload";
        
        protected long lastSequence = 1, currentSequence = 1;
        
        private ModelManager modelManager;
        private Gson gson;
        
        public SyncUploadRESTService (Context context, long lastSequence, long currentSequence, IServicio restSyncListener, int taskId) {
            super (context, restSyncListener, ("http://" + BrioGlobales.URL_SYNC_IP_BRIO + url), com.android.volley.Request.Method.POST, taskId);
            this.lastSequence = lastSequence;
            this.currentSequence = currentSequence;
            modelManager = new ModelManager (context);
            gson = new Gson ();
        }
        
        @Override
        protected String Consulta () {
            return gson.toJson (superSyncData);
        }
        
        @Override
        protected Boolean Respuesta (JSONObject response) {
            boolean syncSuccess = true;
            try {
                Long result = response.getLong ("lastSequence");
                if (result == - 1) { //OK
                    lastSequence = currentSequence;
                } else
                    if (result == 0) { //ERROR SERVIDOR, NO HACER NADA
                        syncSuccess = false;
                    } else
                        if (result > 0) { //ERROR SERVIDOR, incompleto
                            lastSequence = result;
                        } else { //Cualquier otro negativo es error
                            syncSuccess = false;
                        }
            } catch (Exception e) {
                syncSuccess = false;
                e.printStackTrace ();
            } finally {
                if (syncSuccess) {
                    syncUploadTask.limpiarAnteriores (currentSequence);
                    modelManager.settings.update ("LAST_SEQUENCE", String.valueOf (lastSequence));
                }
            }
            return syncSuccess;
        }
        
        @Override
        protected void Error (VolleyError Error) {
            Log.e ("SyncUploadRESTService", "error " + Error.getMessage ());
            Error.printStackTrace ();
        }
    }
    
    /**
     * Sincronización de catálogos de BD.
     *
     * (Bajada)
     *
     * Obtener los cambios de los catálogos y aplicarlos en la base de datos local.
     */
    class SyncCatalogsRESTService extends Servicio {
        private final static String url = "/cabws/api/sync/catalogs?lastCatalogSequence={0}";
        private final static String OP_INSERT = "INSERT";
        
        private long
                lastCatalogSequence = 1,
                currentCatalogSequence = 1;
        
        private ModelManager modelManager;
        private SessionManager sessionManager;
        
        private Gson gson;
        
        public SyncCatalogsRESTService (Context context, IServicio restSyncListener, int taskId) {
            super (context, restSyncListener, ("http://" + BrioGlobales.URL_SYNC_IP_BRIO + url), com.android.volley.Request.Method.GET, taskId);
            modelManager = new ModelManager (context);
            sessionManager = SessionManager.getInstance (context);
            gson = new Gson ();
        }
        
        @Override
        protected String Consulta () {
            
            currentCatalogSequence = Long.parseLong (modelManager.settings.getByNombre ("LAST_CATALOG_SEQUENCE").getValor ());
            
            _ServicioURL = ("http://" + BrioGlobales.URL_SYNC_IP_BRIO + url).replace ("{0}", String.valueOf (currentCatalogSequence));
            
            
            return null;
        }
        
        /**
         * Sincronizar los datos locales con los datos remotos.
         * <p/>
         * Los datos remotos se convierten en pojos locales, las
         * operaciones que se realizan son
         * -INSERT: un elemento nuevo desde el servidor.
         * -UPDATE: un elemento existente que se debe modificar
         * con los datos obtenidos desde el servidor.
         * @param Respuesta - La respuesta obtenida del web service.
         *
         * @return true si se realizo la sincronización
         */
        @Override
        protected Boolean Respuesta (JSONObject Respuesta) {
            boolean syncSuccess = true;
            
            try {
                
                SuperSyncData superSyncData = gson.fromJson (Respuesta.toString (1), SuperSyncData.class);
                
                for (SyncData tx : superSyncData.getTransacciones ()) {
                    SyncDataJson syncDataJson = new SyncDataJson (gson, tx.getData ());
                    
                    try{
                        switch (tx.getTable ()) {
                            case "ARTICULOS":
                                syncArticulo (syncDataJson, tx);
                                break;
        
                            case "UNIDADES_GRANEL":
                                syncUnidad (syncDataJson, tx);
                                break;
        
                            case "MARCAS":
                                syncMarca (syncDataJson, tx);
                                break;
        
                            case "FAMILIA":
                                syncFamilia (syncDataJson, tx);
                                break;
        
                            case "PRESENTACIONES":
                                syncPresentacion (syncDataJson, tx);
                                break;
        
                            case "CATEGORIAS":
                                syncCategoria (syncDataJson, tx);
                                break;
        
                        }
    
                        lastCatalogSequence = tx.getIdSequence ();
                        modelManager.settings.update ("LAST_CATALOG_SEQUENCE", String.valueOf (lastCatalogSequence));
                    } catch (Exception e) {
                        Log.e ("SyncCatalogsRESTService", "processResponse ex: " + e.getMessage ());
                        e.printStackTrace ();
                    }
  
                }
                
            } catch (Exception e) {
                syncSuccess = false;
                Log.e ("SyncCatalogsRESTService", "processResponse ex: " + e.getMessage ());
                e.printStackTrace ();
            }
            
            return syncSuccess;
        }
        
        /**
         * Sincronizar un artículo (nuevo o existente) con datos
         * del servidor.
         * @param syncDataJson
         * @param syncData
         *
         * @return el id del articulo en la base de datos local.
         */
        private Long syncArticulo (SyncDataJson syncDataJson, SyncData syncData) {
            Log.i ("SyncCatalogsRESTService", "syncArticulo entro Long syncArticulo");
            Articulo artRemoto = (Articulo) syncDataJson.getObject (gson, Articulo.class);
            Log.i ("SyncCatalogsRESTService", "syncArticulo articulo remoto " + Utils.toJSON (artRemoto));
            Long timestamp = 0l, savedId = 0l;
            //1) buscar si existe el articulo con el codigo de barras
            //2) si ya hay un articulo con ese codigo (articulo.getCodigoBarras()), hacer syncData.setOperation("UPDATE");, si no, no tocar el setOperation.
            // buscarlo con  Articulo local = modelManager.articulos.getByCodigoBarras(articulo.getCodigoBarras()); if local!= null no existe
            //3) si no hay articulo con el codigo syncData.setOperation(OP_INSERT);
            
            Articulo local = modelManager.articulos.getByCodigoBarras (artRemoto.getCodigoBarras ());
            Log.i ("SyncCatalogsRESTService", "syncArticulo entro Long syncArticulo " + artRemoto.getCodigoBarras ());
            //     Log….i("SyncCatalogsRESTService", "articulo local: " + Utils.pojoToString(local));
            
            boolean isNewData = local == null;
            
            if (! isNewData) {
                // Log.i("SyncCatalogsRESTService", "pojo remoto: " + syncDataJson.getJson());
                Log.i ("SyncCatalogsRESTService", "syncArticulo entro en update *******");
                Log.i ("SyncCatalogsRESTService", "syncArticulo articulo local: " + Utils.pojoToString (local));
                //existe
                //update
                //local.setIdArticulo(local.getIdArticulo());
                //local.setCodigoBarras(local.getCodigoBarras());
                //local.setPrecioVenta(local.getPrecioVenta());
                //local.setPrecioCompra(local.getPrecioCompra());
                local.setIdCentral (artRemoto.getIdCentral ());
                local.setNombre (artRemoto.getNombre ());
                local.setIdMarca (artRemoto.getIdMarca ());
                local.setIdPresentacion (artRemoto.getIdPresentacion ());
                local.setIdUnidad (artRemoto.getIdUnidad ());
                local.setContenido (artRemoto.getContenido ());
                local.setIdFamilia (artRemoto.getIdFamilia ());
                local.setGranel (artRemoto.getGranel ());
                local.setImagen (artRemoto.getImagen ());
                //local.setIdUsuario(articulo.getIdUsuario());
                timestamp = artRemoto.getTimestamp () / 1000;
                local.setTimestamp (timestamp);
                
                
            } else {
                
                //no existe
                
                local = new Articulo ();
                
                Log.i ("SyncCatalogsRESTService", "syncArticulo entro en insert *******");
                //insert
                local.setIdCentral (artRemoto.getIdCentral ());
                local.setCodigoBarras (artRemoto.getCodigoBarras ());
                local.setNombre (artRemoto.getNombre ());
                local.setIdMarca (artRemoto.getIdMarca ());
                local.setIdPresentacion (artRemoto.getIdPresentacion ());
                local.setIdUnidad (artRemoto.getIdUnidad ());
                local.setContenido (artRemoto.getContenido ());
            //    local.setPrecioVenta (0.0);
//                local.setPrecioCompra (0.0);
                local.setIdFamilia (artRemoto.getIdFamilia ());
                local.setGranel (artRemoto.getGranel ());
                local.setImagen (artRemoto.getImagen ());
                local.setIdUsuario (artRemoto.getIdUsuario ());
                timestamp = artRemoto.getTimestamp () / 1000;
                local.setTimestamp (timestamp);
            }
            
            savedId = modelManager.articulos.save (local);
            
            //prueba
            
            Log.i ("SyncCatalogsRESTService", "articulo en bd: " + Utils.toJSON (modelManager.articulos.getByIdArticulo (savedId.intValue ())));
            
            //Actualizando inventario para producto nuevo
            
            if (isNewData) {
                Inventario inventario = new Inventario ();
                
                inventario.setIdArticulo (savedId.intValue ());
                inventario.setExistencias (0);
                inventario.setFechaModificacion (timestamp);
                inventario.setIdSesion (sessionManager.readInt ("idSesion"));
                
                long inv = modelManager.inventario.save (inventario);
                Log.i ("SyncCatalogsRESTService", "syncArticulo inventario guardado: " + inv);
                
                Log.i ("SyncCatalogsRESTService", "inventario en bd: " + Utils.toJSON (modelManager.inventario.getByIdArticulo (savedId.intValue ())));
            }
            
            return savedId;
            
        }
        
        /**
         * Sincronizar una unidad (nueva o existente) con datos
         * del servidor.
         * @param syncDataJson
         * @param syncData
         *
         * @return el id de la unidad en la base de datos local.
         */
        private Long syncUnidad (SyncDataJson syncDataJson, SyncData syncData) {
            Long savedId;
            
            UnidadesGranel unidad = (UnidadesGranel) syncDataJson.getObject (gson, UnidadesGranel.class);
            
            if (syncData.getOperation ().equals (OP_INSERT)) {
                unidad.setIdUnidad (syncDataJson.getId ().intValue ());
            }
            
            /*DEBUG*/
            else {
                UnidadesGranel local = modelManager.unidades.getByIdUnidad (unidad.getIdUnidad ());
                Log.i ("SyncCatalogsRESTService", "unidad modelmanager: " + (local == null ? "*****null*****" : Utils.pojoToString (local)));
            }
            Log.i ("SyncCatalogsRESTService", "unidad syncDataJson: " + Utils.pojoToString (unidad));
            /*DEBUG*/
            
            savedId = modelManager.unidades.save (unidad);
            
            /*DEBUG*/
            Log.i ("SyncCatalogsRESTService", "unidad local guardado: " + Utils.pojoToString (modelManager.unidades.getByIdUnidad (savedId.intValue ())));
            
            return savedId;
        }
        
        /**
         * Sincronizar una marca (nueva o existente) con datos
         * del servidor.
         * @param syncDataJson
         * @param syncData
         *
         * @return el id de la marca en la base de datos local.
         */
        private Long syncMarca (SyncDataJson syncDataJson, SyncData syncData) {
            Long savedId;
            
            Marca marca = (Marca) syncDataJson.getObject (gson, Marca.class);
            
            if (syncData.getOperation ().equals (OP_INSERT)) {
                marca.setIdMarca (syncDataJson.getId ().intValue ());
            }
            
            /*DEBUG*/
            else {
                Marca local = modelManager.marcas.getByIdMarca (marca.getIdMarca ());
                Log.i ("SyncCatalogsRESTService", "marca modelmanager: " + (local == null ? "*****null*****" : Utils.pojoToString (local)));
            }
            Log.i ("SyncCatalogsRESTService", "marca syncDataJson: " + Utils.pojoToString (marca));
            /*DEBUG*/
            
            savedId = modelManager.marcas.save (marca);
            
            /*DEBUG*/
            Log.i ("SyncCatalogsRESTService", "marca local guardado: " + Utils.pojoToString (modelManager.marcas.getByIdMarca (savedId.intValue ())));
            
            return savedId;
        }
        
        /**
         * Sincronizar una familia (nueva o existente) con datos
         * del servidor.
         * @param syncDataJson
         * @param syncData
         *
         * @return el id de la familia en la base de datos local.
         */
        private Long syncFamilia (SyncDataJson syncDataJson, SyncData syncData) {
            Long savedId;
            
            Familia familia = (Familia) syncDataJson.getObject (gson, Familia.class);
            
            if (syncData.getOperation ().equals (OP_INSERT)) {
                familia.setIdFamilia (syncDataJson.getId ().intValue ());
            }
            
            /*DEBUG*/
            else {
                Familia local = modelManager.familias.getByIdFamilia (familia.getIdFamilia ());
                Log.i ("SyncCatalogsRESTService", "familia modelmanager: " + (local == null ? "*****null*****" : Utils.pojoToString (local)));
            }
            Log.i ("SyncCatalogsRESTService", "familia syncDataJson: " + Utils.pojoToString (familia));
            /*DEBUG*/
            
            savedId = modelManager.familias.save (familia);
            
            /*DEBUG*/
            Log.i ("SyncCatalogsRESTService", "familia local guardado: " + Utils.pojoToString (modelManager.familias.getByIdFamilia (savedId.intValue ())));
            
            return savedId;
        }
        
        /**
         * Sincronizar una presentacion (nueva o existente) con datos
         * del servidor.
         * @param syncDataJson
         * @param syncData
         *
         * @return el id de la presentacion en la base de datos local.
         */
        private Long syncPresentacion (SyncDataJson syncDataJson, SyncData syncData) {
            Long savedId;
            
            Presentacion presentacion = (Presentacion) syncDataJson.getObject (gson, Presentacion.class);
            
            if (syncData.getOperation ().equals (OP_INSERT)) {
                presentacion.setIdPresentacion (syncDataJson.getId ().intValue ());
            }
            
            /*DEBUG*/
            else {
                Presentacion local = modelManager.presentaciones.getByIdPresentacion (presentacion.getIdPresentacion ());
                Log.i ("SyncCatalogsRESTService", "presentacion modelmanager: " + (local == null ? "*****null*****" : Utils.pojoToString (local)));
            }
            Log.i ("SyncCatalogsRESTService", "presentacion syncDataJson: " + Utils.pojoToString (presentacion));
            /*DEBUG*/
            
            savedId = modelManager.presentaciones.save (presentacion);
            
            /*DEBUG*/
            Log.i ("SyncCatalogsRESTService", "presentacion local guardado: " + Utils.pojoToString (modelManager.presentaciones.getByIdPresentacion (savedId.intValue ())));
            
            return savedId;
        }
        
        /**
         * Sincronizar una categoria (nueva o existente) con datos
         * del servidor.
         * @param syncDataJson
         * @param syncData
         *
         * @return el id de la categoria en la base de datos local.
         */
        private Long syncCategoria (SyncDataJson syncDataJson, SyncData syncData) {
            Long savedId;
            
            Categoria categoria = (Categoria) syncDataJson.getObject (gson, Categoria.class);
            
            if (syncData.getOperation ().equals (OP_INSERT)) {
                categoria.setIdCategoria (syncDataJson.getId ().intValue ());
            }
            
            /*DEBUG*/
            else {
                Categoria local = modelManager.categorias.getByIdCategoria (categoria.getIdCategoria ());
                Log.i ("SyncCatalogsRESTService", "categoria modelmanager: " + (local == null ? "*****null*****" : Utils.pojoToString (local)));
            }
            Log.i ("SyncCatalogsRESTService", "categoria syncDataJson: " + Utils.pojoToString (categoria));
            /*DEBUG*/
            
            savedId = modelManager.categorias.save (categoria);
            
            /*DEBUG*/
            Log.i ("SyncCatalogsRESTService", "categoria local guardado: " + Utils.pojoToString (modelManager.categorias.getByIdCategoria (savedId.intValue ())));
            
            return savedId;
        }
        
        @Override
        protected void Error (VolleyError Error) {
            if (Error != null) {
                Error.printStackTrace ();
            }
        }
    }
}

/**
 * Obtener el lastSequence (la última secuencia que tiene el servidor) de los datos
 * de subida de la tablet al servidor (SyncUploadRESTService).
 */
class LastSequenceRESTService extends Servicio {
    private final static String url = "/cabws/api/sync/last?idComercio={0}&idCaja={1}";
    
    private Long lastSequence = - 1l;
    
    public LastSequenceRESTService (Context Contexto, IServicio restSyncListener, int taskId) {
        super (Contexto, restSyncListener, ("http://" + BrioGlobales.URL_SYNC_IP_BRIO + url), com.android.volley.Request.Method.GET, taskId);
    }
    
    @Override
    protected Boolean Respuesta (JSONObject response) {
        Boolean success = true;
        
        try {
            this.lastSequence = response.getLong ("lastSequence");
        } catch (Exception e) {
            e.printStackTrace ();
            success = false;
        }
        
        return success;
    }
    
    public Long getLastSequence () {
        return lastSequence;
    }
    
    @Override
    protected String Consulta () {
        
        this._ServicioURL = ("http://" + BrioGlobales.URL_ZERO_BRIO + url)
                .replace ("{0}", _Modelo.settings.getByNombre ("ID_COMERCIO").getValor ())
                .replace ("{1}", _Modelo.settings.getByNombre ("ID_CAJA").getValor ());
        
        return "{}";
    }
    
    @Override
    protected void Error (VolleyError Error) {
        Error.printStackTrace ();
    }
}