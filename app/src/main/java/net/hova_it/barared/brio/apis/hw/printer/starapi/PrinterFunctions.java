package net.hova_it.barared.brio.apis.hw.printer.starapi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;
import com.starmicronics.stario.StarPrinterStatus;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.BrioBaseActivity;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.hw.printer.document.api.PrintableDocument;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioAlertDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Funciones de utileria para la impresora.
 *
 * Parte del SDK de la impresora STAR TSP100 FuturePRNT.
 * La impresora requiere las librerías StarIOPort3.1.jar y StarIO_Extension.jar en app/libs.
 */
public class PrinterFunctions {
    
    static int printableArea;
    
    public enum RasterCommand {
        Standard, Graphics
    }
    
    
    /**
     * Concatenar un List de byte[] en un solo byte[].
     * @param ByteArray
     *
     * @return
     */
    private static byte[] convertFromListByteArrayTobyteArray (List<byte[]> ByteArray) {
        int dataLength = 0;
        for (int i = 0; i < ByteArray.size (); i++) {
            dataLength += ByteArray.get (i).length;
        }
        
        int distPosition = 0;
        byte[] byteArray = new byte[dataLength];
        for (int i = 0; i < ByteArray.size (); i++) {
            System.arraycopy (ByteArray.get (i), 0, byteArray, distPosition, ByteArray.get (i).length);
            distPosition += ByteArray.get (i).length;
        }
        
        return byteArray;
    }
    
    /**
     * This function is used to print a Java bitmap directly to the printer. There are 2 ways a printer can print images: through raster commands or line mode commands This function uses raster commands to print an image. Raster is supported on the TSP100 and all Star Thermal POS printers. Line mode printing is not supported by the TSP100. There is no example of using this method in this sample.
     * @param context Activity for displaying messages to the user
     * @param portName Port name to use for communication. This should be (TCP:<IPAddress>)
     * @param portSettings Should be blank
     * @param source The bitmap to convert to Star Raster data
     * @param maxWidth The maximum width of the image to print. This is usually the page width of the printer. If the image exceeds the maximum width then the image is scaled down. The ratio is maintained.
     */
    public static void PrintBitmap (BrioActivityMain context, String portName, String portSettings, Bitmap source, int maxWidth, boolean compressionEnable, RasterCommand rasterType) {
        try {
            ArrayList<byte[]> commands = new ArrayList<byte[]> ();
            
            RasterDocument rasterDoc = new RasterDocument (RasterDocument.RasSpeed.Medium, RasterDocument.RasPageEndMode.FeedAndFullCut, RasterDocument.RasPageEndMode.FeedAndFullCut, RasterDocument.RasTopMargin.Standard, 0, 0, 0);
            StarBitmap starbitmap = new StarBitmap (source, false, maxWidth);
            
            if (rasterType == RasterCommand.Standard) {
                commands.add (rasterDoc.BeginDocumentCommandData ());
                
                commands.add (starbitmap.getImageRasterDataForPrinting_Standard (compressionEnable));
                
                commands.add (rasterDoc.EndDocumentCommandData ());
            } else {
                commands.add (starbitmap.getImageRasterDataForPrinting_graphic (compressionEnable));
                commands.add (new byte[] { 0x1b, 0x64, 0x02 }); // Feed to cutter position
            }
            
            sendCommand (context, portName, portSettings, commands);
        } catch (OutOfMemoryError e) {
            throw e;
        }
        
    }
    
    /**
     * Enviar un comando a la impresora
     * @param context
     * @param portName
     * @param portSettings
     * @param byteList
     */
    private static void sendCommand (final BrioActivityMain context, String portName, String portSettings, ArrayList<byte[]> byteList) {
        StarIOPort port = null;
        try {
            
            port = StarIOPort.getPort (portName, portSettings, 10000, context);
            
            if (port != null) {
                StarPrinterStatus status = port.beginCheckedBlock ();
                
                if (true == status.offline) {
                    throw new StarIOPortException ("A printer is offline");
                }
                
                byte[] commandToSendToPrinter = convertFromListByteArrayTobyteArray (byteList);
                port.writePort (commandToSendToPrinter, 0, commandToSendToPrinter.length);
                
                port.setEndCheckedBlockTimeoutMillis (30000);// Change the timeout time of endCheckedBlock method.
                status = port.endCheckedBlock ();
                if (status.coverOpen == true) {
                    throw new StarIOPortException ("Printer cover is open");
                } else
                    if (status.receiptPaperEmpty == true) {
                        throw new StarIOPortException ("Receipt paper is empty");
                    } else
                        if (status.offline == true) {
                            throw new StarIOPortException ("Printer is offline");
                        }
            } else {
                throw new StarIOPortException ("Printer is not connected");
            }
        } catch (StarIOPortException e) {
            e.printStackTrace ();
            
            context.runOnUiThread (new Runnable () {
                @Override
                public void run () {
                    BrioAlertDialog bad = new BrioAlertDialog (context, context.getString (R.string.dialog_title_advertencia), context.getString (R.string.dialog_msg_printer_not_found));
                    bad.show ();
                    
                }
            });
            
        } finally {
            if (port != null) {
                try {
                    StarIOPort.releasePort (port);
                } catch (StarIOPortException e) {
                }
            }
        }
    }
    
    public static boolean isActive (String portName, String portSettings) {
        StarIOPort port = null;
        try {
            port = StarIOPort.getPort (portName, portSettings, 10000);
            StarPrinterStatus status = port.beginCheckedBlock ();
            StarPrinterStatus status2 = port.retreiveStatus ();
            //return !(status.offline || status.coverOpen || status.receiptPaperEmpty);
            if (status.offline) {
                Log.i ("PRINTER STATUS", "PRINTER IS OFFLINE");
                return false;
            } else
                if (status.coverOpen == true) {
                    Log.i ("PRINTER STATUS", "PRINTER COVER IS OPEN");
                    return false;
                } else
                    if (status.receiptPaperEmpty == true) {
                        Log.i ("PRINTER STATUS", "RECEIPT PAPER IS EMPTY");
                        return false;
                    }
        } catch (StarIOPortException e) {
            e.printStackTrace ();
        }
        return false;
    }
}
