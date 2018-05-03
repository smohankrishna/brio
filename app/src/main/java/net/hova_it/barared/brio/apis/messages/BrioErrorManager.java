package net.hova_it.barared.brio.apis.messages;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.hw.printer.PrinterManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Concentrado de todos los errores de la aplicacion.
 *
 * 3 pasos para agregar errores:
 *
 *  1) Definir en res/arrays.xml un string-array con name = "errors_<mi_modulo>".
 *  2) Definir en BrioErrorManager un public final static int "KEY_MODULE_<mi_modulo>".
 *  3) Agregar en BrioErrorManager.mapErrors():
 *          STRING_ARRAYS_IDS_ERRORS.put(KEY_MODULE_<mi_modulo>, R.array.errors_<mi_modulo>);
 *  4) De esta forma, se puede invocar BrioErrorManager.getError(KEY_MODULE_<mi_modulo>, idError),
 *          donde idError corresponde al error con dicho indice en el string-array errors_<mi_modulo>.
 *
 *
 * Created by Herman Peralta on 18/02/2016.
 */
public class BrioErrorManager {
    //Lista de modulos
    public final static int KEY_MODULE_PRINTER = 0;
    public final static int KEY_MODULE_KEYBOARD = 1;

    //Concentrado de todos los errores
    private static Map<Integer, BrioError[]> errors;

    //Mapeo de modulos
    private final static Map<Integer, Integer> STRING_ARRAYS_IDS_ERRORS;
    static  {
        STRING_ARRAYS_IDS_ERRORS = new HashMap<>();

        mapErrors();
    }

    //todo: agregar un archivo para escribir los errores

    private static BrioErrorManager daManager;
    private MessageManager managerMessage;

    private Context context;

    public static BrioErrorManager getInstance(Context context) {
        if(daManager == null) {
            revoverErrors(context);

            daManager = new BrioErrorManager(context);
        }
        return daManager;
    }

    private BrioErrorManager(Context context) {
        this.context = context;

        managerMessage = MessageManager.getInstance(context);
    }

    public BrioError getError(int KEY_MODULE, int ID_MODULE_ERROR) {
        BrioError found = null;
        if(errors.containsKey(KEY_MODULE) && ID_MODULE_ERROR>-1
                && ID_MODULE_ERROR<errors.get(KEY_MODULE).length) {

            found = errors.get(KEY_MODULE)[ID_MODULE_ERROR];
            writeToFile(found.toString());
        }

        return found;
    }

    public BrioError showError(final int KEY_MODULE, final int ID_MODULE_ERROR) {

        final BrioError error = getError(KEY_MODULE, ID_MODULE_ERROR);

        if(error!=null) {
            managerMessage.show(error.errorMsg);
        }

        return error;
    }

    private static void mapErrors() {
        STRING_ARRAYS_IDS_ERRORS.put(KEY_MODULE_PRINTER, PrinterManager.RES_ERROR_ARRAY);
        STRING_ARRAYS_IDS_ERRORS.put(KEY_MODULE_KEYBOARD, R.array.errors_keyboard);
    }

    private static void revoverErrors(Context context) {
        errors = new HashMap<>();
        Resources res = context.getResources();
        String[] error_array;
        int KEY_MODULE;
        for (Map.Entry<Integer, Integer> keymodule_reserrors : STRING_ARRAYS_IDS_ERRORS.entrySet()) {
            //res.getStringArray(module_errors.getValue())
            error_array = res.getStringArray(keymodule_reserrors.getValue());
            BrioError[] module_errors = new BrioError[error_array.length];
            KEY_MODULE = keymodule_reserrors.getKey();
            for(int i=0 ; i<error_array.length ; i++) {
                module_errors[i] = new BrioError(KEY_MODULE, i, error_array[i], "");
            }

            errors.put(KEY_MODULE, module_errors);
        }
    }

    private void writeToFile(String text) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("errorlog.log", Context.MODE_PRIVATE));
            outputStreamWriter.write(text);
            outputStreamWriter.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = null;// = openFileInput("config.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (Exception e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
}
