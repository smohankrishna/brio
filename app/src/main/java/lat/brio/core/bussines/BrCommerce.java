package lat.brio.core.bussines;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import net.hova_it.barared.brio.apis.models.daos.SettingsDB;
import net.hova_it.barared.brio.apis.models.entities.Settings;
import net.hova_it.barared.brio.apis.utils.Utils;

import java.util.List;

import lat.brio.core.BrioGlobales;

public class BrCommerce extends BRMain {


    /**
     * En esta clase se deberá contener la logica para TICKETS, SETTIGNS y modulos comunes
     */

    private static BrCommerce instance;
    private SettingsDB settingsDB;

    public static BrCommerce getInstance(Context context, SQLiteDatabase mAcess) {
        if (instance == null) {
            instance = new BrCommerce(context.getApplicationContext(), mAcess);
        }

        return instance;
    }


    public BrCommerce(Context contexto, SQLiteDatabase mAccess) {
        super(contexto, mAccess);

        settingsDB = new SettingsDB(contexto, mAccess);
    }

    public Settings getByNombre(String value) {
        Settings dl = null;

        try {
            String where = settingsDB.generarCondicionStr(SettingsDB.KEY_NOMBRE, value);

            where += " COLLATE NOCASE";
            Cursor cursor = settingsDB.busquedaAvanzada(SettingsDB.columnas,
                    where, null, null, null, null);

            if (cursor.moveToFirst()) {
                dl = new Settings(cursor);

            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
            return dl;

        } catch (Exception e) {
            BrioGlobales.writeLog("BrCommerce", "getByNombre()", e.getMessage());
            return dl;
        }
    }

    public long updateSetting (Settings settings) {

        long actualizado = 0;
        try {
            String where = settingsDB.generarCondicionStr (SettingsDB.KEY_NOMBRE, settings.getNombre());
            String keys[] = new String[] {
                    SettingsDB.KEY_NOMBRE, SettingsDB.KEY_VALOR
            };
            String values[] = new String[] {settings.getNombre(),
                    settings.getValor()
            };

            actualizado = settingsDB.consolidarCambiosAvanzado (where, keys, values);

            if (actualizado <= 0) {
                actualizado = settingsDB.insertarNuevoRegistro(keys, values);
            }
            if (actualizado <= 0) {
                Log.i("BrCommerce", "guardarClienteAccounts->Fallo insertar en tabla SettingsDB '" + settings.getNombre());
            }
        } catch (Exception e) {
            BrioGlobales.writeLog ("BrCommerce", "updateSetting()", e.getMessage ());
        }
        return actualizado;
    }

}
