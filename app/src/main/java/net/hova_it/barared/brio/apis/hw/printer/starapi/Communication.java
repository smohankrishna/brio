package net.hova_it.barared.brio.apis.hw.printer.starapi;

import android.content.Context;
import android.util.Log;

import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;
import com.starmicronics.stario.StarPrinterStatus;

/**
 *
 * Parte del SDK de la impresora STAR TSP100 FuturePRNT.
 * La impresora requiere las librerías StarIOPort3.1.jar y StarIO_Extension.jar en app/libs.
 */
public class Communication {
    public enum Result {
        Success,
        ErrorUnknown,
        ErrorOpenPort,
        ErrorBeginCheckedBlock,
        ErrorEndCheckedBlock,
        ErrorWritePort,
        ErrorReadPort,
    }

    /**
     * Enviar comandos a la impresora.
     *
     * @param commands
     * @param port
     * @param context
     * @return
     */
    public static Result sendCommands(byte[] commands, StarIOPort port, Context context) {
        Result result = Result.ErrorUnknown;

        try {
            if (port == null) {
                result = Result.ErrorOpenPort;
                return result;
            }

//          // When using an USB interface, you may need to send the following data.
//          byte[] dummy = {0x00};
//          port.writePort(dummy, 0, dummy.length);

            StarPrinterStatus status;

            result = Result.ErrorBeginCheckedBlock;

            status = port.beginCheckedBlock();

            if (status.offline) {
                throw new StarIOPortException("A printer is offline");
            }

            result = Result.ErrorWritePort;

            port.writePort(commands, 0, commands.length);
            Log.e("Communications", "aqui");
            result = Result.ErrorEndCheckedBlock;

            port.setEndCheckedBlockTimeoutMillis(30000);     // 30000mS!!!

            status = port.endCheckedBlock();

            if (status.coverOpen) {
                throw new StarIOPortException("Printer cover is open");
            }
            else if (status.receiptPaperEmpty) {
                throw new StarIOPortException("Receipt paper is empty");
            }
            else if (status.offline) {
                throw new StarIOPortException("Printer is offline");
            }

            result = Result.Success;
        }
        catch (StarIOPortException e) {
            e.printStackTrace();
        }

        Log.e("Communications", "sendCommands(): final");

        return result;
    }

    /**
     * Enviar comandos a la impresora con un timeout.
     *
     * @param commands
     * @param portName
     * @param portSettings
     * @param timeout
     * @param context
     * @return
     */
    public static Result sendCommands(byte[] commands, String portName, String portSettings, int timeout, Context context) {
        Log.d("DEBUG", Communication.class.getSimpleName() + ".sendCommands(), printing...");
        Result result = Result.ErrorUnknown;

        StarIOPort port = null;

        try {
            result = Result.ErrorOpenPort;

            port = StarIOPort.getPort(portName, portSettings, timeout, context);

//          // When using an USB interface, you may need to send the following data.
//             byte[] dummy = {0x00};
//             port.writePort(dummy, 0, dummy.length);

            StarPrinterStatus status;

            result = Result.ErrorBeginCheckedBlock;

            status = port.beginCheckedBlock();

            if (status.offline) {
                throw new StarIOPortException("A printer is offline");
            }

            result = Result.ErrorWritePort;

            Log.d("DEBUG", "imprimiendo ticket");

            port.writePort(commands, 0, commands.length);

            result = Result.ErrorEndCheckedBlock;

            port.setEndCheckedBlockTimeoutMillis(30000);     // 30000mS!!!

            status = port.endCheckedBlock();

            if (status.coverOpen) {
                throw new StarIOPortException("Printer cover is open");
            }
            else if (status.receiptPaperEmpty) {
                throw new StarIOPortException("Receipt paper is empty");
            }
            else if (status.offline) {
                throw new StarIOPortException("Printer is offline");
            }

            result = Result.Success;
        }
        catch (StarIOPortException e) {
            e.printStackTrace();
        }
        finally {
            if (port != null) {
                try {
                    StarIOPort.releasePort(port);

                    port = null;
                }
                catch (StarIOPortException e) {
                }
            }
        }

        return result;
    }
}
