package net.hova_it.barared.brio.apis.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Clase personalizada para loguear información a la consola.
 *
 * Esta clase obtiene el nombre del método desde el cual fue invocada,
 * incluyendo la jerarquía de clases superiores.
 *
 * Created by Herman Peralta on 16/02/2016.
 */
public class DebugLog {
    private static int methodLevel = 0;
    private static String mmsg = "";

    /**
     * Loguear un mensaje
     *
     * @param clazz - La clase desde donde se invoca el método
     * @param tag - El tag del mensaje
     * @param msg - El mensaje a loguearse
     */
    public static void log(Class clazz, String tag, String msg) {
        methodLevel = 1;

        setMsg(clazz, msg);
        Log.e(tag, mmsg);
    }

    /**
     * Loguea el mensaje especificado
     * @param clazz
     * @param msg
     */
    private static void setMsg(Class clazz, String msg) {
        String methodName = getMethodName();
        mmsg = clazz.getSimpleName() + "." + methodName + "(): " + msg;
    }

    public static String getLastMsg() {
        return mmsg;
    }

    private static String getMethodName() {
        ///Todo encontrar forma de obtener el nombre del metodo con reflect
        //String methodName = new Object(){}.getClass().getEnclosingMethod().getName();
        if(methodLevel!=0) { methodLevel++; } else {methodLevel = 1;}
        String methodName = Thread.currentThread().getStackTrace()[methodLevel+4].getMethodName();
        methodLevel = 0;

        return methodName;
    }

    private static void showToast(String tag, String msg, Context context, char type) {
        try {
            Toast.makeText(context, type + "/[" + tag + "] " + msg, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(tag, "No se pudo mostrar el toast: [" + tag + "]: " + msg + ", " + e.getCause());
            //e.printStackTrace();
        }
    }
}
