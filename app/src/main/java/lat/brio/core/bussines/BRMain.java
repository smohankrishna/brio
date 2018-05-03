package lat.brio.core.bussines;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


public abstract class BRMain {
    protected static Context context;
    protected String TAG = getClass().getName();

    public BRMain (Context contexto, SQLiteDatabase mAccess) {
        context = contexto;
    }

}
