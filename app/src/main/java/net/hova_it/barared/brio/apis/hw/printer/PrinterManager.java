package net.hova_it.barared.brio.apis.hw.printer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.widget.Toast;

import com.starmicronics.stario.PortInfo;
import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;
import com.starmicronics.starioextension.starioextmanager.StarIoExtManager;
import com.starmicronics.starioextension.starioextmanager.StarIoExtManagerListener;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.BrioBaseActivity;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.hw.printer.document.api.Block;
import net.hova_it.barared.brio.apis.hw.printer.document.api.Section;
import net.hova_it.barared.brio.apis.hw.printer.document.api.TextBlock;
import net.hova_it.barared.brio.apis.hw.printer.document.brio.BrioPrintableTicket;
import net.hova_it.barared.brio.apis.hw.printer.starapi.PrinterFunctions;
import net.hova_it.barared.brio.apis.hw.printer.starapi.PrinterSetting;
import net.hova_it.barared.brio.apis.models.entities.SuperTicket;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import brio.sdk.logger.Business.GlobalesLogger;
import brio.sdk.logger.DataAccess.AccesoArchivosGeneral;
import brio.sdk.logger.util.BrioUtilsFechas;
import brio.sdk.logger.log.BrioLog;


/**
 * Maneja la impresora STAR TSP100 para imprimir tickets.
 *
 * La impresora requiere las librerías StarIOPort3.1.jar y StarIO_Extension.jar en app/libs.
 *
 * Created by Herman Peralta on 16/02/2016.
 */
public class PrinterManager {
    private final static int PAPER_WIDTH_3INCH = 576;
    
    public final static int RES_ERROR_ARRAY = R.array.errors_printer;
    private final static int status_didAccessoryConnectSuccess = 0;
    private final static int status_didPrinterOnline = 1;
    private final static int status_didPrinterPaperReady = 2;
    private final static int status_didPrinterCoverClose = 3;
    private final static int status_didPrinterPaperNearEmpty = 4;
    private final static int status_didPrinterImpossible = 5;
    
    private static PrinterManager daManager;
    
    private BrioActivityMain context;
    
    private PrinterSetting printerSetting;
    private StarIoExtManagerListener mStarIoExtManagerListener;
    private StarIoExtManager mStarIoExtManager;
    private PrinterManagerListener listener;
    
    
    public static BrioActivityMain activityMain;
    
    public static PrinterManager getInstance (BrioActivityMain context) {
        if (daManager == null) {
            daManager = new PrinterManager (context);
        }
        activityMain = context;
        return daManager;
    }
    
    
    private PrinterManager (BrioActivityMain context) {
        this.context = context;
        
        printerSetting = new PrinterSetting (context);
        mStarIoExtManagerListener = new StarIoExtManagerListener () {
            
            /* ---------------------------------------------------------------------
             *                         Impresora OK
             * ---------------------------------------------------------------------
             * Los siguientes callbacks se llaman en el siguiente orden cuando la
             * impresora esta correctamente configurada y lista para imprimir:
             *   1) didAccessoryConnectSuccess()
             *   2) didPrinterOnline()
             *   3) didPrinterPaperReady()
             *   4) didPrinterCoverClose()
             * ---------------------------------------------------------------------
             */
            @Override
            public void didAccessoryConnectSuccess () {
                super.didAccessoryConnectSuccess ();
                DebugLog.log (this.getClass (), "Printer", "");
                
                setStatus (true, status_didAccessoryConnectSuccess);
            }
            
            @Override
            public void didPrinterOnline () {
                super.didPrinterOnline ();
                DebugLog.log (this.getClass (), "Printer", "");
                
                setStatus (true, status_didPrinterOnline);
            }
            
            @Override
            public void didPrinterPaperReady () {
                super.didPrinterPaperReady ();
                DebugLog.log (this.getClass (), "Printer", "");
                
                setStatus (true, status_didPrinterPaperReady);
            }
            
            @Override
            public void didPrinterCoverClose () {
                super.didPrinterCoverClose ();
                DebugLog.log (this.getClass (), "Printer", "");
                
                setStatus (true, status_didPrinterCoverClose);
            }
            
            /* ---------------------------------------------------------------------
             *                         Impresora ERROR
             * ---------------------------------------------------------------------
             * Si alguno de los siguientes callbacks se invoca, entonces hay
             * problemas con la impresora.
             * ---------------------------------------------------------------------
             */
            @Override
            public void didAccessoryConnectFailure () {
                super.didAccessoryConnectFailure ();
                DebugLog.log (this.getClass (), "Printer", "");
                
                setStatus (false, status_didAccessoryConnectSuccess);
            }
            
            @Override
            public void didPrinterOffline () {
                super.didPrinterOffline ();
                DebugLog.log (this.getClass (), "Printer", "");
                
                setStatus (false, status_didPrinterOnline);
            }
            
            @Override
            public void didPrinterPaperEmpty () {
                super.didPrinterPaperEmpty ();
                DebugLog.log (this.getClass (), "Printer", "");
                
                setStatus (false, status_didPrinterPaperReady);
            }
            
            @Override
            public void didPrinterCoverOpen () {
                super.didPrinterCoverOpen ();
                DebugLog.log (this.getClass (), "Printer", "");
                
                setStatus (false, status_didPrinterCoverClose);
            }
            
            @Override
            public void didAccessoryDisconnect () {
                super.didAccessoryDisconnect ();
                DebugLog.log (this.getClass (), "Printer", "");
                
                setStatus (false, status_didPrinterOnline);
            }
            
            @Override
            public void didPrinterImpossible () {
                super.didPrinterImpossible ();
                DebugLog.log (this.getClass (), "Printer", "");
                
                setStatus (true, status_didPrinterImpossible);
            }

            /* ---------------------------------------------------------------------
             *                         Impresora AVISOS
             * ---------------------------------------------------------------------
             * Si alguno de los siguientes callbacks se invoca, entonces hay
             * problemas con la impresora PERO PUEDE IMPRIMIR.
             * ---------------------------------------------------------------------
             */
            
            @Override
            public void didPrinterPaperNearEmpty () {
                super.didPrinterPaperNearEmpty ();
                DebugLog.log (this.getClass (), "Printer", "");
                
                setStatus (true, status_didPrinterPaperNearEmpty);
            }
        };
    }
    
    public void setPrinterManagerListener (PrinterManagerListener listener) {
        this.listener = listener;
    }
    
    /**
     * Buscar una impresora conectada en el puerto OTG.
     */
    public void searchPrinter () {
        DebugLog.log (this.getClass (), "Printer", "Buscando impresora USB...");
        if (BrioBaseActivity.DEBUG_SHOW_TOASTS) {
            Toast.makeText (context, "Buscando impresora USB...", Toast.LENGTH_SHORT).show ();
        }
        SearchTask usbSearchTask = new SearchTask (context);
        usbSearchTask.execute ("USB:");
        
        
    }
    
    /**
     * Imprimir un ticket
     * @param ticketid - el id del ticket a imprimir
     */
    public void printTicket (int ticketid) {
        
        printTicket (Utils.getSuperTicketFromDB (context, ticketid));
    }
    
    /**
     * Imprimir un pojo SuperTicket
     * @param superTicket - el ticket a imprimir
     */
    public void printTicket (SuperTicket superTicket) {
        
        StarTicketPrint printToast = new StarTicketPrint (context);
        printToast.execute (superTicket);
    }
    
    private ScheduledExecutorService scheduleTaskExecutor;
    private boolean searching = false;
    

    
    /**
     * Establecer el estatus de la impresora.
     * @param ready
     * @param idstatus
     */
    private void setStatus (boolean ready, int idstatus) {
        switch (idstatus) {
            case status_didPrinterPaperNearEmpty:
                listener.paperNearEmpty ();
                break;
            
            case status_didPrinterCoverClose:
                if (ready) {
                    //La tapa esta bajada, hay que ver si hay papel...
                    switch (mStarIoExtManager.getPrinterPaperReadyStatus ()) {
                        case PrinterPaperNearEmpty:
                            listener.paperNearEmpty ();
                            //SIN BREAK
                        case PrinterPaperReady:
                            listener.onPrinterReady ();
                            break;
                        
                        case PrinterOffline:
                            listener.onPrinterReady ();
                            break;
                        
                        case PrinterPaperEmpty:
                            idstatus = status_didPrinterPaperReady;
                            ready = false;
                            //Permito que continue al default...
                    }
                }
                //SIN BREAK
            
            default:
                if (! ready) {
                    listener.onPrinterError (idstatus);
                }
                break;
        }
    }
    
    
    /**
     * Imprimir un ticket utilizando una impresora STAR TSP100 FuturePRNT
     * (Probado en modelo normal y ECO)
     * @param superTicket - el pojo del ticket a imprimir
     */
    private void printTicketWithStarTSP100 (SuperTicket superTicket) {
        boolean compressionEnabled = false;
        BrioPrintableTicket printableTicket = new BrioPrintableTicket (context, superTicket);
        
        int height = 0;
        ArrayList<StaticLayout> layouts = new ArrayList<> ();
        for (Section section : printableTicket.sections) {
            for (Block block : section.blocks) {
                StaticLayout staticLayout = getStarRasterTicket (block);
                if (staticLayout != null) {
                    layouts.add (staticLayout);
                    height += staticLayout.getHeight ();
                }
            }
        }
        
        Bitmap bitmap = Bitmap.createBitmap (PAPER_WIDTH_3INCH, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas (bitmap);
        canvas.drawColor (Color.WHITE);

      
        
        //Dibujo el primer layout
        canvas.translate (0, 0);
        layouts.get (0).draw (canvas);
        
        for (int i = 1; i < layouts.size (); i++) {
            StaticLayout staticLayout = layouts.get (i);
            
            canvas.translate (0, layouts.get (i - 1).getHeight ());
            staticLayout.draw (canvas);
        }
        
        PrinterFunctions.RasterCommand rasterType = PrinterFunctions.RasterCommand.Standard;
        PrinterFunctions.PrintBitmap (context, printerSetting.getPortName (), printerSetting.getPrinterType (), bitmap, PAPER_WIDTH_3INCH, compressionEnabled, rasterType);
    }
    
    /**
     * Esta función se utiliza para generar una imagen de un bloque del
     * ticket a imprimir. Esto es especial para impresora STAR TSP100.
     * @param block
     *
     * @return
     */
    private StaticLayout getStarRasterTicket (Object block) {
        StaticLayout staticLayout = null;
        
        if (block instanceof TextBlock) {
            
            TextBlock theBlock = (TextBlock) block;
            
            Paint paint = new Paint ();
            paint.setStyle (Paint.Style.FILL);
            paint.setColor (Color.BLACK);
            paint.setAntiAlias (true);
            
            int style = Typeface.NORMAL;
            if (theBlock.attr_bold) {
                style = style | Typeface.BOLD;
            }
            
            Typeface typeface = Typeface.create (Typeface.MONOSPACE, style);
            paint.setTypeface (typeface);
            paint.setTextSize (theBlock.attr_size * 2);
            
            TextPaint textpaint = new TextPaint (paint);
            if (theBlock.attr_italic) {
                textpaint.setTextSkewX ((float) - 0.25);
            }
            
            staticLayout = new StaticLayout (theBlock.text, textpaint, PAPER_WIDTH_3INCH, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
        }
        
        return staticLayout;
    }
    
    /**
     * Búsqueda asincrona de la impresora.
     */
    public class SearchTask extends AsyncTask<String, Integer, Integer> {
        private List<PortInfo> mPortList;
        private Context context;
        
        public SearchTask (Context context) {
            super ();
            
            this.context = context;
        }
        
        @Override
        protected Integer doInBackground (String... interfaceType) {
            try {
                mPortList = StarIOPort.searchPrinter (interfaceType[0], context);
            } catch (StarIOPortException e) {
                mPortList = new ArrayList<> ();
            }
            
            return 0;
        }
        
        @Override
        protected void onPostExecute (Integer doNotUse) {
            if (mPortList.size () == 0) {
                DebugLog.log (this.getClass (), "Printer", "Printer not found");
            }
            
            for (PortInfo info : mPortList) {
                // addItem(info);
                DebugLog.log (this.getClass (), "Printer", "found: portName: " + info.getPortName ());
                
                printerSetting.write (info.getPortName (), info.getMacAddress ());
                //el manager se espera hasta 10 segundos para crearse, i.e. el tiempo suficiente para que usbSearchTask termine
                mStarIoExtManager = new StarIoExtManager (StarIoExtManager.Type.Standard, printerSetting.getPortName (), printerSetting.getPrinterType (), 10000, context); // 10000mS!!!
                mStarIoExtManager.setListener (mStarIoExtManagerListener);
                
            }

            /*if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }*/
        }
    }
    
    /**
     * Imprimir un ticket de forma asíncrona
     */
    public class StarTicketPrint extends AsyncTask<SuperTicket, Void, Boolean> {
        private Context context;
        
        public StarTicketPrint (Context context) {
            this.context = context;
        }
        
        @Override
        protected void onPreExecute () {
            ((BrioActivityMain) context).splash.show ();
            ((BrioActivityMain) context).splash.publish ("Imprimiendo ticket");
        }
        
        @Override
        protected Boolean doInBackground (SuperTicket... params) {
            printTicketWithStarTSP100 (params[0]);
            
            return true;
        }
        
        @Override
        protected void onPostExecute (Boolean print) {
            ((BrioActivityMain) context).splash.dismiss ();
        }
    }
    
    /**
     * Inrerfaz para indicar el estatus de la impresora
     */
    public interface PrinterManagerListener {
        //void onPrinterOK(String msg);
        
        void onPrinterReady ();
        
        void onPrinterError (int error);
        
        void paperNearEmpty ();
    }
}