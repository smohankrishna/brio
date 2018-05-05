package net.hova_it.barared.brio.apis.setup;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import net.hova_it.barared.brio.apis.mail.MailService;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.Sesion;
import net.hova_it.barared.brio.apis.models.entities.Settings;
import net.hova_it.barared.brio.apis.models.entities.Ticket;
import net.hova_it.barared.brio.apis.models.entities.TipoTicket;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;
import net.hova_it.barared.brio.apis.sync.Remove0Sync;
import net.hova_it.barared.brio.apis.sync.RestSyncService;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.preferencias.Preferencias;

import java.util.ArrayList;
import java.util.List;

import lat.brio.core.BrioGlobales;

/**
 * Clase encargada del mantenimiento de la aplicacion.
 * <p>
 * Created by Herman Peralta y Mauricio Cerón 16/05/2016.
 */
public class Modificaciones {
    
    private static String ADD_PRECIO_COMPRA_IN_ITEMS_TICKET = "ADD_PRECIO_COMPRA_IN_ITEMS_TICKET";
    
    /**
     * Cosas que se deben hacer solo una vez
     * <p>
     * Esto es para mantener las versiones, en algun momento lo que esté dentro de esta función se irá eliminando.
     * @param context
     */
    public void oneTimeUpdate(Context context) {
        ModelManager managerModel = new ModelManager(context);
        
        Settings buzonSalidaSetting = managerModel.settings.getByNombre(MailService.BuzonSalidaTicketsService.SETTING_BUZON_SALIDA);
        if (buzonSalidaSetting==null) {
            buzonSalidaSetting = new Settings();
            buzonSalidaSetting.setNombre(MailService.BuzonSalidaTicketsService.SETTING_BUZON_SALIDA);
            buzonSalidaSetting.setValor("");
            
            managerModel.settings.save(buzonSalidaSetting);
        }
        
        //Insertando el setting LAST_CATALOG_SEQUENCE
        Settings lastCatalogSequenceSetting = managerModel.settings.getByNombre("LAST_CATALOG_SEQUENCE");
        if (lastCatalogSequenceSetting==null) {
            lastCatalogSequenceSetting = new Settings();
            lastCatalogSequenceSetting.setNombre("LAST_CATALOG_SEQUENCE");
            lastCatalogSequenceSetting.setValor("0");
            
            managerModel.settings.save(lastCatalogSequenceSetting);
        }
        
        //        //Insertando el setting LAST_CATALOG_SEQUENCE
        //        Settings commerceStatusSetting = managerModel.settings.getByNombre("SET_COMMERCE_STATUS");
        //        if (commerceStatusSetting == null) {
        //            commerceStatusSetting = new Settings();
        //            commerceStatusSetting.setNombre("LAST_CATALOG_SEQUENCE");
        //            commerceStatusSetting.setValor("0");
        //
        //            managerModel.settings.save(commerceStatusSetting);
        //        }
        
        
        
        //reparacion de timestamps en 0
        
        fixer(context, managerModel);
    }
    
    /**
     * Cosas que se deben hacer siempre
     * @param context
     */
    public void legacyUpdate(Context context) {
        
        closeOpenSessions(context); Utils.deleteDileApk();
    }
    
    /**
     * Cerrar todas las sesiones abiertas
     * antes de hacer el login de la nueva sesion
     */
    private void closeOpenSessions(Context context) {
        ModelManager managerModel = new ModelManager(context);
        
        List<Sesion> sesiones = managerModel.sesiones.getOpenSessions();
        if (sesiones!=null) {//Revisa si hay sesiones, si existen, las cierra, sino, no hace nada
            
            for (Sesion sesion : sesiones) {
                sesion.setFechaFin(Utils.getCurrentTimestamp());
                
                managerModel.sesiones.save(sesion);
            }
        }
    }
    
    private void forceLastTransaction(Context context, String lastTx) {
        
        ModelManager managerModel = new ModelManager(context);
        managerModel.settings.update("LAST_TRANSACTION", lastTx);
        
    }
    
    /**
     * Guarda la versión de la aplicación en la tabla de SETTINGS.
     * @param modelManager
     * @param context
     */
    
    private void saveAppVersion(final ModelManager modelManager, Context context) {
        
        modelManager.settings.update("APP_VERSION", Utils.getAppData(context).versionName);
        DebugLog.log(Modificaciones.class, "BRIO_INSTANCE", String.format("Version de app guardada en settings: %s", modelManager.settings.getByNombre("APP_VERSION").getValor()));
        
        
        Settings strRollback = modelManager.settings.getByNombre(BrioGlobales.KEY_BRIO_VER_ROLLBACK);
        PackageInfo appData = Utils.getAppData(context);
        String roll = (strRollback==null) ? BrioGlobales.KEY_ROLLBACK_FALSE : strRollback.getValor();
        String update = (Preferencias.i.getBrioVerExistUpdate()) ? BrioGlobales.KEY_ROLLBACK_FALSE : BrioGlobales.KEY_ROLLBACK_TRUE;
        
        String keyIstall = appData.versionCode + appData.versionName + roll + update;
        String PrefInstall = Preferencias.i.getBrioVerInstalled();

        /*
         * Validamos que la versión que está en ejecución sea diferente a la versión que se guardo
         * antes de instalar la versión actual. Es decir, cuando se instale una versión p.ej.
         * Se tenía la V 57 y el string guardo "570.1.5700" antes de realizar la instalación, entonces
         * tomamos los nuevos valores, p. ej. la V59, sería ahora "590.1.5900", entonces si se
         * instalo la versión nueva y no se dio cancelar al botón de la instalación.
         */
        
        if (! PrefInstall.equals("")&&! keyIstall.equals(PrefInstall)) {
            
            
            Preferencias.i.setBrioVerExistUpdate(false);
            Preferencias.i.setBrioVerInstalled(BrioGlobales.EMPTY_STRING);
            
            
        }
    
    }
    
    
    private void fixer (final Context context, final ModelManager modelManager) {
        
        final Remove0Sync remove0Sync = new Remove0Sync(context);
        remove0Sync.setListener(new RestSyncService.RestSyncListener() {
            @Override
            public void onRestSyncFinished(int taskId, boolean synched) {
                if (synched) {
                    
                    //GUARDANDO VERSION DE LA APLICACION
                    saveAppVersion(modelManager, context);
                    //////////////////////////////////////////////////////////
                    //REPARACION CODIGO DE BARRAS GRANEL
                    fixBarecodeGranel();
                    //////////////////////////////////////////////////////////
                    
                    //REPARACION DE COMISIONES//1° EN EJECUTARSE//SE EJECUTA 1 VEZ
                    String fixCommission;
                    Settings fix_Commission = modelManager.settings.getByNombre("FIX_COMMISSION");
                    
                    fixCommission = fix_Commission==null ? "0" : fix_Commission.getValor();
                    if (fix_Commission==null) {
                        fix_Commission = new Settings(); fix_Commission.setNombre("FIX_COMMISSION");
                        fix_Commission.setValor("0");
                        
                        modelManager.settings.save(fix_Commission);
                    } Log.w("BRIO_INSTANCE", "fixCommission=" + fixCommission);
                    if (fixCommission.equals("0")) {
                        Log.w("BRIO_INSTANCE", "reparando comisiones");
                        fixCommission();
                        modelManager.settings.update("FIX_COMMISSION", "1");
                    }
                    //////////////////////////////////////////////////////////////////////////////
                    //ELIMINACION DE TICKETS DUPLICADOS//2° EN EJECUTARSE//SE EJECUTA 1 VEZ
                    Settings delete_duplicated_tickets = modelManager.settings.getByNombre("DELETE_DUPLICATED_TICKETS");
                    
                    String deleteCloneTickets = delete_duplicated_tickets==null ? "0" : delete_duplicated_tickets.getValor();
                    if (delete_duplicated_tickets==null) {
                        delete_duplicated_tickets = new Settings();
                        delete_duplicated_tickets.setNombre("DELETE_DUPLICATED_TICKETS");
                        delete_duplicated_tickets.setValor("0");
                        
                        modelManager.settings.save(delete_duplicated_tickets);
                    } Log.w("BRIO_INSTANCE", "delete_duplicated_tickets=" + deleteCloneTickets);
                    if (deleteCloneTickets.equals("0")) {
                        Log.w("BRIO_INSTANCE", "borrando tickets duplicados");
                        TipoTicket newTipo = new TipoTicket("ELIMINADO", "Otro");
                        modelManager.tipoTicket.save(newTipo);
                        ticketCloneMassacre();
                        modelManager.settings.update("DELETE_DUPLICATED_TICKETS", "1");
                    }
                    //////////////////////////////////////////////////////////////////////////////
                    
                    //ACTUALIZACION TIMESTAMP
                    //actualizacion de timestamp servicios//3° EN EJECUTARSE//SE EJECUTA SIEMPRE
                    
                    upDateTimestamp(remove0Sync.pojos, remove0Sync.in, modelManager);
                    //////////////////////////////////////////////////////////////////////////////
                    //actualizacion de timestamp POS//4° EN EJECUTARSE//
                    Settings fix_Timestamp_POS = modelManager.settings.getByNombre("FIX_TIMESTAMP_POS");
                    
                    String fixTimestampPOS = fix_Timestamp_POS==null ? "0" : fix_Timestamp_POS.getValor();
                    if (fix_Timestamp_POS==null) {
                        fix_Timestamp_POS = new Settings();
                        fix_Timestamp_POS.setNombre("FIX_TIMESTAMP_POS");
                        fix_Timestamp_POS.setValor("0");
                        
                        modelManager.settings.save(fix_Timestamp_POS);
                    } Log.w("BRIO_INSTANCE", "fix_Timestamp_POS=" + fixTimestampPOS);
                    if (fixTimestampPOS.equals("0")) {
                        Log.w("BRIO_INSTANCE", "reparando timestamp en 0 de tickets POS");
                        upDateTimestampPOS();
                        modelManager.settings.update("FIX_TIMESTAMP_POS", "1");
                    }
                    
                    //////////////////////////////////////////////////////////////////////////////
                    //Add columna precio_compra en la tabla Items_ticket
                    Settings addColumnPrecioCompraInItemsTicket = modelManager.settings.getByNombre(ADD_PRECIO_COMPRA_IN_ITEMS_TICKET);
                    String addColumn = addColumnPrecioCompraInItemsTicket==null ? "0" : addColumnPrecioCompraInItemsTicket.getValor();
                    if (addColumnPrecioCompraInItemsTicket==null) {
                        addColumnPrecioCompraInItemsTicket = new Settings();
                        addColumnPrecioCompraInItemsTicket.setNombre(ADD_PRECIO_COMPRA_IN_ITEMS_TICKET);
                        addColumnPrecioCompraInItemsTicket.setValor("0");
                        
                        modelManager.settings.save(addColumnPrecioCompraInItemsTicket);
                    } Log.w("BRIO_INSTANCE", "addColumn=" + addColumn); if (addColumn.equals("0")) {
                        Log.w("BRIO_INSTANCE", "agregando columna");
                        addPrecioCompraToItemsTicket();
                        modelManager.settings.update(ADD_PRECIO_COMPRA_IN_ITEMS_TICKET, "1");
                    }
                    
                    //////////////////////////////////////////////////////////////////////////////
                    //Insert tipos ticket Western y Seguros
                    Settings insertTTicketWesternSeguros = modelManager.settings.getByNombre("INSERT_TIPOS_TICKETS_WS");
                    String insertTiposTicket = insertTTicketWesternSeguros==null ? "0" : insertTTicketWesternSeguros.getValor();
                    if (insertTTicketWesternSeguros==null) {
                        insertTTicketWesternSeguros = new Settings();
                        insertTTicketWesternSeguros.setNombre("INSERT_TIPOS_TICKETS_WS");
                        insertTTicketWesternSeguros.setValor("0");
                        
                        modelManager.settings.save(insertTTicketWesternSeguros);
                    } Log.w("BRIO_INSTANCE", "insertTiposTicket=" + insertTiposTicket);
                    if (insertTiposTicket.equals("0")) {
                        Log.w("BRIO_INSTANCE", "agregando tipos tickets");
                        insertTipoTicketsWesternSeguros();
                        modelManager.settings.update("INSERT_TIPOS_TICKETS_WS", "1");
                    }
                    
                    DebugLog.log(Modificaciones.class, "BRIO_INSTANCE", "Ejecutando actualizaciones de una sola vez [fin]");
                }
            }
        });
        
        remove0Sync.sync();
        
    }
    
    /**
     * Actualiza codigo de barras de productos a granel ingresados por el usuario.
     */
    public void fixBarecodeGranel () {
        SQLiteDatabase db = BrioGlobales.getAccess ();
        Cursor resource = db.rawQuery("SELECT COUNT(*) " + "                FROM Articulos" + "                WHERE codigo_barras = 0 AND granel =1", null);
        
        int count = 0; if (resource.moveToFirst()) {
            
            count = resource.getInt(0);
            
        } resource.close();
        
        DebugLog.log(Modificaciones.class, "BRIO_INSTANCE", "Articulos a granel encontrados con barcode 0=" + count);
        
        db.execSQL("UPDATE Articulos " + "   SET codigo_barras = \"T-\" || timestamp " + "   WHERE id_articulo IN (SELECT id_articulo " + "   FROM Articulos " + "WHERE codigo_barras = 0 AND granel = 1)", new String[]{});
        
        Cursor resource2 = db.rawQuery("SELECT COUNT(*) " + "                FROM Articulos" + "                WHERE codigo_barras = 0 AND granel =1", null);
        
        int count2 = 0; if (resource2.moveToFirst()) {
            
            count2 = resource2.getInt(0);
            
        }
        
        DebugLog.log(Modificaciones.class, "BRIO_INSTANCE", "Articulos a granel encontrados con barcode 0 despues de update=" + count2);
        
    }
    
    /**
     * Actualiza el timestamp de transacciones de POS que no guardaron su fecha correctamente.
     */
    public void upDateTimestampPOS () {
        
        SQLiteDatabase db = BrioGlobales.getAccess ();
        
        Cursor resource = db.rawQuery("SELECT delta.tickets_0, delta.t_min, " + "   IFNULL((SELECT timestamp FROM Tickets WHERE id_ticket = (delta.t_min)-1),(SELECT timestamp FROM Comercio LIMIT 1)) AS timestamp_min, " + "        delta2.t_max,IFNULL((SELECT timestamp FROM Tickets WHERE id_ticket= (delta2.t_max)+1),strftime('%s','now')) AS timestamp_max " + "   FROM (SELECT COUNT(*) AS tickets_0, MIN(id_ticket) AS t_min " + "   FROM Tickets " + "   WHERE id_tipo_ticket IN (1,2,3) AND timestamp = 0) AS  delta, " + "   (SELECT MAX(id_ticket) AS t_max " + "   FROM Tickets" + "   WHERE id_tipo_ticket IN (1,2,3) AND timestamp = 0) AS  delta2", null);
        
        DebugLog.log(Modificaciones.class, "BRIO_INSTANCE", "query terminadoooooo");
        
        long tickets_0 = 0, timestampMin = 0, timestampMax = 0, idMin = 0, idMax = 0, delta, timestampTemp;
        List<Long> listIdTickets = new ArrayList<>();
        
        if (resource.moveToFirst()) {
            
            tickets_0 = resource.getLong(0); idMin = resource.getLong(1);
            timestampMin = resource.getLong(2); idMax = resource.getLong(3);
            timestampMax = resource.getLong(4);
            
        }
        
        resource.close(); if (tickets_0 > 0) {
            delta = ((Double) (Math.floor((timestampMax - timestampMin) / tickets_0))).longValue();
            timestampTemp = timestampMin;
            DebugLog.log(Modificaciones.class, "BRIO_INSTANCE", "tickets_0=" + tickets_0 + " timestampMin=" + timestampMin + " timestampMax=" + timestampMax + " idMin=" + idMin + " idMax=" + idMax + " delta=" + delta);
            Cursor resource2 = db.rawQuery("SELECT id_ticket " + "FROM Tickets " + "WHERE id_tipo_ticket IN (1,2,3) AND id_ticket BETWEEN " + idMin + " AND " + idMax, null);
            while(resource2.moveToNext()) {
                
                listIdTickets.add(resource2.getLong(0));
                
            }
            
            resource2.close();
            
            DebugLog.log(Modificaciones.class, "BRIO_INSTANCE", "listIdTickets size=" + listIdTickets.size());
            for (int i = 0; i < listIdTickets.size(); i++) {
                
                timestampTemp += delta;
                db.execSQL("UPDATE Tickets " + "        SET timestamp = " + timestampTemp + "        WHERE id_ticket = " + listIdTickets.get(i), new String[]{});
                
            }
        }
    }
    
    /**
     * Actualiza el timestamp de las transacciones de Servicios, TAE e Internet en base a la fecha
     * de autorizacion de transaccion guardada en el servidor.
     * @param pojos Arreglo de objetos que contienen la informacion de cada transaccion.
     * @param transacciones Descripcion de la transaccion que se quiere buscar.
     * @param modelManager
     */
    public void upDateTimestamp(ArrayList<Remove0Sync.PatchPojo> pojos, String transacciones, ModelManager modelManager) {
        
        SQLiteDatabase db = BrioGlobales.getAccess ();
        
        List<Ticket> listTickets = new ArrayList<>();
        
        
        Cursor resource = db.rawQuery("SELECT id_ticket,descripcion,timestamp " + "    FROM Tickets" + "    WHERE descripcion IN (" + transacciones + ") ", null);
        
        long idTicket; String descripcion; long timestamp; while(resource.moveToNext()) {
            
            idTicket = resource.getLong(0); descripcion = resource.getString(1);
            timestamp = resource.getLong(2);
            
            for (int j = 0; j < pojos.size(); j++) {
                if (descripcion.equals(pojos.get(j).transaccion)) {
                    if (timestamp < pojos.get(j).timestamp) {
                        
                        db.execSQL("UPDATE Tickets " + "    SET timestamp = " + pojos.get(j).timestamp + "    WHERE id_ticket = " + idTicket, new String[]{});
                        db.execSQL("UPDATE Items_Ticket " + "    SET timestamp = " + pojos.get(j).timestamp + "    WHERE id_ticket = " + idTicket, new String[]{});
                        
                        DebugLog.log(Modificaciones.class, "BRIO_INSTANCE", "ticket editado=" + idTicket + " timestamp viejo=" + timestamp + " timestamp nuevo=" + pojos.get(j).timestamp);
                        
                    }
                }
                
            }
            
        }
        
        resource.close();
        
        
    }
    
    /**
     * Gestor de comisiones de Servicios y TAE.
     */
    private void fixCommission () {
        SQLiteDatabase db = BrioGlobales.getAccess ();
        
        
        Cursor cr = db.rawQuery("SELECT COUNT( * ) " + "   FROM Tickets AS t INNER JOIN Items_Ticket AS it ON t.id_ticket = it.id_ticket   " + "   WHERE t.id_tipo_ticket =4 AND t.timestamp  LIKE '%0000' AND it.importe_unitario > 9", null);
        
        
        int count = - 1; if (cr.moveToFirst()) {
            count = cr.getInt(0);
        }
        
        DebugLog.log(Modificaciones.class, "BRIO_INSTANCE", String.format("antes Hay %d con timestampo 0 listos para editarse", count));
        cr.close();
        
        db.execSQL("UPDATE Tickets  " + "   SET importe_neto = (importe_neto-9.0) , importe_bruto =(importe_bruto-9.0) , timestamp=(timestamp+1)    " + "   WHERE id_ticket IN (SELECT t.id_ticket  " + "   FROM Tickets AS t INNER JOIN Items_Ticket AS it ON t.id_ticket = it.id_ticket   " + "   WHERE t.id_tipo_ticket =4 AND t.timestamp  LIKE '%0000' AND it.importe_unitario > 9    " + "   )  ");
        db.execSQL("   UPDATE Items_Ticket " + "   SET importe_unitario = (importe_unitario-9.0) , importe_total =(importe_total-9.0)  " + "   WHERE id_item_ticket IN (SELECT it.id_item_ticket  " + "   FROM Tickets AS t INNER JOIN Items_Ticket AS it ON t.id_ticket = it.id_ticket   " + "   WHERE t.id_tipo_ticket =4 AND it.timestamp LIKE '%0000' AND it.importe_unitario > 9    " + "   ) ");
        db.execSQL("   UPDATE Items_Ticket " + "   SET timestamp = (timestamp+1)  " + "   WHERE id_item_ticket IN (SELECT it.id_item_ticket   " + "   FROM Tickets AS t INNER JOIN Items_Ticket AS it ON t.id_ticket = it.id_ticket   " + "   WHERE t.id_tipo_ticket =4 AND it.timestamp LIKE '%0000')");
        
        Cursor cr2 = db.rawQuery("SELECT COUNT( * ) " + "   FROM Tickets AS t INNER JOIN Items_Ticket AS it ON t.id_ticket = it.id_ticket   " + "   WHERE t.id_tipo_ticket =4 AND t.timestamp  LIKE '%0000' AND it.importe_unitario > 9", null);
        
        count = - 1; if (cr2.moveToFirst()) {
            count = cr2.getInt(0);
        }
        
        DebugLog.log(Modificaciones.class, "BRIO_INSTANCE", String.format("despues Hay %d con timestampo 0 listos para editarse", count));
        cr2.close();
        
    }
    
    /**
     * Busqueda de tickets duplicados para su eliminacion.
     */
    private void ticketCloneMassacre () {
        SQLiteDatabase db = BrioGlobales.getAccess ();
        
        
        Cursor cr = db.rawQuery("SELECT id_ticket " + "   FROM Tickets, " + "   (SELECT descripcion, COUNT(*) AS existencias " + "   FROM Tickets " + "   WHERE id_tipo_ticket=4 " + "   GROUP BY descripcion) AS encontrados " + "   WHERE Tickets.descripcion = encontrados.descripcion AND encontrados.existencias >1 AND importe_bruto > 0", null);
        
        
        List<Long> idTickets = new ArrayList<>(); String tickets = ""; while(cr.moveToNext()) {
            idTickets.add(cr.getLong(0));
            DebugLog.log(Modificaciones.class, "BRIO_INSTANCE", "ticket encontrado= " + idTickets.get(0));
        }
        
        cr.close(); for (int i = 0; i < idTickets.size(); i++) {
            tickets += idTickets.get(i); if (i!=(idTickets.size() - 1)) {
                tickets += ", ";
            }
        }
        
        DebugLog.log(Modificaciones.class, "BRIO_INSTANCE", "tickets a analizar= ( " + tickets + " )");
        
        
        db.execSQL("UPDATE Tickets " + "   SET id_tipo_ticket = 9 " + "   WHERE id_ticket IN(" + tickets + ")");
        
        
        Cursor cr2 = db.rawQuery("SELECT  id_ticket\n" + "                   FROM Tickets \n" + "                   WHERE id_tipo_ticket=9", null);
        
        idTickets.clear(); tickets = ""; while(cr2.moveToNext()) {
            idTickets.add(cr2.getLong(0));
        }
        
        cr2.close();
        
        
        for (int i = 0; i < idTickets.size(); i++) {
            tickets += idTickets.get(i); if (i!=(idTickets.size() - 1)) {
                tickets += ", ";
            }
        }
        
        DebugLog.log(Modificaciones.class, "BRIO_INSTANCE", "tickets editados= ( " + tickets + " )");
        
    }
    
    private static void addPrecioCompraToItemsTicket () {
        SQLiteDatabase db = BrioGlobales.getAccess ();
        
        db.execSQL("ALTER TABLE Items_Ticket ADD COLUMN precio_compra REAL");
        
        db.execSQL("    UPDATE Items_Ticket " + "    SET precio_compra = IFNULL((SELECT precio_compra from Articulos AS a WHERE Items_Ticket.id_articulo = a.id_articulo),0)");
    }
    
    
    private static void insertTipoTicketsWesternSeguros () {
        
        SQLiteDatabase db = BrioGlobales.getAccess ();
        
        db.execSQL("INSERT INTO Tipo_Tickets ( descripcion, movimiento) " + " VALUES('REMESAS','salida') ");
        
        db.execSQL(" INSERT INTO Tipo_Tickets ( descripcion, movimiento)" + " VALUES('SEGUROS','entrada') ");
        
    }
    
}
