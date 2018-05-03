package net.hova_it.barared.brio.apis.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase que permite loguear datos a un archivo interno de la
 * aplicación.
 *
 * @version 1.0 11/28/2016
 * @author Herman Peralta
 * @since 11/28/2016
 */
public class FileLogger {
    private final static String LOG_DIR = "";//logs/

    private static Map<String, FileLogger> instances = new HashMap<>();

    public final static int TYPE_MSG = 0;

    private final Gson gson = new Gson();

    private Context context;
    private String filename;
    private FileOutputStream fileOutputStream;

    /**
     * Obtiene un objeto FileLogger para un archivo dado.
     * Si ya existe un FileLogger para el archivo indicado, regresa ese, de
     * lo contrario, crea uno y lo devuelve.
     *
     * @param context
     * @param filename
     * @return
     */
    public static synchronized FileLogger get(Context context, String filename) {
        FileLogger fileLogger;

        if(instances.containsKey(filename)) {
            fileLogger = instances.get(filename);
            if(fileLogger == null) {fileLogger = new FileLogger(context, filename);}
        } else {
            fileLogger = new FileLogger(context, filename);
            instances.put(filename, fileLogger);
        }

        return fileLogger;
    }

    /**
     * Remueve el logger de las intancias y elimina el objeto
     * por lo que si el logger es accesado después de
     * removerlo, mandara nullpointerexception
     *
     * // todo mejorar para que no lo remueva mientras este escribiendo
     * @param logger
     */
    public static synchronized void delete(FileLogger logger) {
        if(instances.containsValue(logger)) {
            Log.i("LOG", "Si lo contiene");
            instances.remove(logger.getFilename());
            logger = null;
        } else {
            Log.i("LOG", "No lo contiene");
        }

        Log.i("LOG", "isnull? " + (logger==null));
        Log.i("LOG", listAll());
    }

    public static String listAll() {
        String txt = "total " + instances.size();
        for(Map.Entry<String, FileLogger> entry : instances.entrySet()) {
            txt += "\n\t" + entry.getKey();
        }

        return txt;
    }

    /**
     * Inicia el FileLogger
     *
     * @param context (activity)
     * @param filename (el nombre del archivo, sin paths)
     */
    private FileLogger(Context context, String filename) {
        this.context = context;
        this.filename = LOG_DIR + filename;
    }

    /**
     * Loguear el objeto Message indicado como JSON
     *
     * @param msg
     */
    public synchronized void log(Message msg) {
        open();

        String str = Utils.toJSONString(msg) + ", ";
        try {
            fileOutputStream.write(str.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Agrega un string al log
     * 
     * @param str
     */
    public synchronized void log(String str) {
        open();
        log(new Message(0, System.currentTimeMillis(), str, "String"));
    }

    /**
     * Loguear un objeto cualquiera como
     * un JSON escapado dentro de un Objeto Message
     *
     * @param object
     * @param from
     */
    public synchronized void log(Object object, String from) {
        Message msg = new Message(0, System.currentTimeMillis(), "Object: " + Utils.escapeText(Utils.toJSONString(object)), from);
        log(msg);
    }

    /**
     * @return el archivo de log completo como String JSON
     */
    public synchronized String readAsJSONString() {
        open();

        return "[" + Utils.readInternalFile(context, filename) + "]";
    }

    /**
     * @return El archivo de log como array de objetos Message
     */
    public synchronized Message[] readAsObjects() {
        return gson.fromJson(readAsJSONString(), Message[].class);
    }

    /**
     * Abrir el archivo
     *
     * @return true si se abrio o false de lo contrario
     */
    public boolean open() {
        boolean success = true;

        if(fileOutputStream == null) {
            try {
                //context.getDir(LOG_DIR, Context.MODE_PRIVATE);

                fileOutputStream = context.openFileOutput(filename, Context.MODE_APPEND);
            } catch (Exception e) {
                e.printStackTrace();
                success = false;
            }
        }

        return success;
    }

    /**
     * Cerrar el archivo
     */
    public void close() {
        if(fileOutputStream != null) {
            try {
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            fileOutputStream = null;
        }
    }

    /**
     * Elimina el archivo de log
     */
    public synchronized void delete() {
        open();
        context.deleteFile(filename);
        close();
    }

    public String getFilename() {
        return filename;
    }

    /**
     * Clase para guardar un mensaje
     */
    public static class Message {
        private int TYPE;
        private long timestamp; //milisegundos System.currentMillis()
        private String msg;
        private String from;

        public Message() {

        }

        public Message(int TYPE, long timestamp, String msg, String from) {
            this.TYPE = TYPE;
            this.timestamp = timestamp;
            this.msg = msg;
            this.from = from;
        }

        public int getTYPE() {
            return TYPE;
        }

        public void setTYPE(int TYPE) {
            this.TYPE = TYPE;
        }

        /**
         *
         * @return timestamp en milisegundos
         */
        public long getTimestamp() {
            return timestamp;
        }

        /**
         *
         * @param timestamp milisegundos
         */
        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }
    }
}