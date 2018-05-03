package lat.brio.core.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;

import lat.brio.core.BrioGlobales;
//import net.sqlcipher.database.SQLiteQueryBuilder;

/**
 * Esta clase se va a encargar de implementar los metodos de acceso a la base de datos necesarios todas las clases de acceso a datos deberian extender
 * de esta clase para poder utilizar sus metodos
 * @author Guillermo Ortiz Gonzalez
 * @version 2.0
 */
public class BrioAccesoDatos extends SQLiteOpenHelper {
    
    private SQLiteDatabase myDataBase;
    // private final Context myContext;
    private String DATABASE_TABLE;
    
    public static final String KEY_ROWID = BrioGlobales.KEY_ROWID;
    
    /**
     * Constructor de la clase
     * @param context
     * @param mAccess
     * @param nombreTabla
     */
    public BrioAccesoDatos (Context context, SQLiteDatabase mAccess, String nombreTabla) {
        
        super (context, BrioGlobales.DB_NAME, null, 1);
        this.myDataBase = mAccess;
        this.DATABASE_TABLE = nombreTabla;
    }
    
    /**
     * Metodo set necesario para dar nombre a la tabla en el caso de utilizar el objeto TelynetObjetoGenericoDB ya que con estos objetos no se
     * inicializa el nombre de la tabla al crear el objeto
     * @param dATABASETABLE cadena con el nombre de la tabla
     */
    public void setDATABASE_TABLE (String dATABASETABLE) {
        DATABASE_TABLE = dATABASETABLE;
    }
    
    //	@Override
    //	public void onCreate(SQLiteDatabase db)
    //	{
    //
    //	}
    //
    //	@Override
    //	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    //	{
    //
    //	}
    
    public SQLiteDatabase getDataBase () {
        return myDataBase;
    }
    
    /**
     * Metodo encargado de realizar la busqueda de las columnas que se pasan por parametro sobre la tabla DATABASE_TABLE
     * @param columnas
     *
     * @return Cursor con el resultado de la busqueda
     * @throws SQLiteException
     */
    public Cursor buscar (String columnas[]) throws SQLiteException {
        Cursor busquedaCursor = null;
        try {
            busquedaCursor = myDataBase.query (DATABASE_TABLE, columnas, null, null, null, null, null);
        } catch (SQLiteException e) {
            throw e;
        }
        return busquedaCursor;
    }
    
    /**
     * Metodo encargado de realizar la busqueda sobre la tabla DATABASE_TABLE
     * @param columnas
     * @param where
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderby
     *
     * @return Cursor con el resultado de la busqueda
     * @throws SQLiteException
     */
    public Cursor busquedaAvanzada (String columnas[], String where, String selectionArgs[], String groupBy, String having, String orderby)
            throws SQLiteException {
        Cursor busquedaAvanzadaCursor = null;
        try {
            busquedaAvanzadaCursor = myDataBase.query (DATABASE_TABLE, columnas, where, selectionArgs, groupBy, having, orderby);
        } catch (SQLiteException e) {
            throw e;
        }
        return busquedaAvanzadaCursor;
    }
    
    /**
     * Metodo encargado de realizar la busqueda sobre la tabla DATABASE_TABLE
     * @param columnas
     * @param where
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderby
     * @param limit
     *
     * @return Cursor con el resultado de la busqueda
     * @throws SQLiteException
     */
    public Cursor busquedaAvanzada (String columnas[], String where, String selectionArgs[], String groupBy, String having, String orderby,
            String limit)
            throws SQLiteException {
        Cursor busquedaAvanzadaCursor = null;
        try {
            busquedaAvanzadaCursor = myDataBase.query (DATABASE_TABLE, columnas, where, selectionArgs, groupBy, having, orderby, limit);
        } catch (SQLiteException e) {
            throw e;
        }
        return busquedaAvanzadaCursor;
    }
    
    
    /**
     * Metodo encargado de realizar la busqueda sobre la tabla DATABASE_TABLE
     * @param columnas
     * @param where
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderby
     * @param limit
     * @param distinct
     *
     * @return Cursor con el resultado de la busqueda
     * @throws SQLiteException
     */
    public Cursor busquedaAvanzada (String columnas[], String where, String selectionArgs[], String groupBy, String having, String orderby, String limit, boolean distinct)
            throws SQLiteException {
        Cursor busquedaAvanzadaCursor = null;
        try {
            busquedaAvanzadaCursor = myDataBase.query (distinct, DATABASE_TABLE, columnas, where, selectionArgs, groupBy, having, orderby, limit);
        } catch (SQLiteException e) {
            throw e;
        }
        return busquedaAvanzadaCursor;
    }
    
    /**
     * Metodo encargado de insertar u nuvo registro en la tabla DATABASE_TABLE
     * @param keys
     * @param values
     *
     * @return devuelve un long con el numero de la nueva fila insertada o -1 en caso de error
     * @throws SQLiteException
     */
    public long insertarNuevoRegistro (String keys[], String values[]) throws Exception {
        long resultadoInsercion = 0;
        
        ContentValues initialValues = new ContentValues ();
        if (keys.length == values.length && keys.length > 0) {
            for (int i = 0; i < keys.length; i++) {
                String valor = values[i];
                if (valor != null)
                    initialValues.put (keys[i], values[i]);
            }
        } else
            throw new Exception ("ERROR en insertarNuevoRegistro() el numero de columnas no se corresponde con el numero de valores " + initialValues.toString ());
        
        try {
            resultadoInsercion = myDataBase.insert (DATABASE_TABLE, null, initialValues);
            
        } catch (SQLiteException e) {
            throw e;
        }
        return resultadoInsercion;
        
    }
    
    /**
     * Metodo encargado de actualizar campos de la tabla DATABASE_TABLE
     * @param whereClause
     * @param keys
     * @param values
     *
     * @return el numero de registros actualizados al ejecutar la query
     * @throws SQLiteException
     */
    public long consolidarCambiosAvanzado (String whereClause, String keys[], String values[]) throws SQLiteException {
        ContentValues args = new ContentValues ();
        if (keys.length == values.length && keys.length > 0) {
            for (int i = 0; i < keys.length; i++) {
                String valor = values[i];
                
                if (valor != null) {
                    // valor = valor.replace("'", "''");
                    args.put (keys[i], valor);
                }
            }
        }
        long resultadoConsolidacion = 0;
        try {
            resultadoConsolidacion = myDataBase.update (DATABASE_TABLE, args, whereClause, null);
        } catch (SQLiteException e) {
            throw e;
        }
        return resultadoConsolidacion;
        
    }
    
    /**
     * Metodo encargado de borrar un registro de la tabla DATABASE_TABLE si se quere borrar toda la tabla se ha de pasar "1" como parametro
     * whereClause
     * @param whereClause
     * @param whereArgs
     *
     * @return true si el registro se ha borrado con exito. Devuele false en caso contrario
     * @throws SQLiteException
     */
    public boolean borrarRegistro (String whereClause, String whereArgs[]) throws SQLiteException {
        boolean resultadoBorrado = false;
        try {
            long borrados = myDataBase.delete (DATABASE_TABLE, whereClause, whereArgs);
            resultadoBorrado = borrados > 0;
        } catch (SQLiteException e) {
            throw e;
        }
        return resultadoBorrado;
    }
    
    /**
     * Metodo encargado de vaciar la tabla DATABASE_TABLE
     * @return true si el registro se ha borrado con exito. Devuele false en caso contrario
     * @throws SQLiteException
     */
    public boolean vaciarTabla () throws SQLiteException {
        boolean resultadoBorrado = false;
        try {
            long borrados = myDataBase.delete (DATABASE_TABLE, "1", null);
            resultadoBorrado = borrados > 0;
        } catch (SQLiteException e) {
            throw e;
        }
        return resultadoBorrado;
    }
    
    /**
     * Metodo encargado de devolver el cursor relleno con los datos de la tabla
     * @param keys columnas que se quieren obtener de la query
     * @param order orden de los valores de la query
     * @param limit clausula limit de la query
     * @param where clausula where de la query
     * @param group clausula group de la query
     * @param having clausula having de la query
     *
     * @return objeto Cursor con el resultado de la query que se ejecuta
     */
    public Cursor rellenaCombo (String keys[], String order, String limit, String where, String group, String having) throws SQLiteException {
        Cursor cursor = null;
        try {
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder ();
            qb.setTables (DATABASE_TABLE);
            qb.setDistinct (true);
            
            String sqlquery = qb.buildQuery (keys, where, null, group, having, order, limit);
            cursor = myDataBase.rawQuery (sqlquery, null);
            
        } catch (SQLiteException e) {
            throw e;
        }
        return cursor;
    }
    
    /**
     * Metodo encargado de devolver el cursor relleno con los datos de la tabla mas los datos que se pasan en el string sub1 con los primeros valores
     * que se quieren rellenar
     * @param keysSub columnas que se quieren obtener de la query
     * @param sub1 valor que se quiere a?adir al cursor
     * @param orderGeneral orden de los valores de la query final
     * @param limitGeneral limete de la query final
     * @param whereSub clausula where de la subquery
     * @param groupSub clausula group de la subquery
     * @param havingSub clausula having de la subquery
     * @param orderSub clausula order de la subquery
     * @param limitSub clausula limit de la subquery
     *
     * @return objeto Cursos con el resultado de la query que se ejecuta
     */
    public Cursor rellenaComboValorDefecto (String keysSub[], String sub1, String orderGeneral, String limitGeneral, String whereSub, String groupSub,
            String havingSub, String orderSub, String limitSub) throws SQLiteException {
        Cursor cursor = null;
        try {
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder ();
            qb.setTables (DATABASE_TABLE);
            qb.setDistinct (true);
            
            String sub = qb.buildQuery (keysSub, whereSub, null, groupSub, havingSub, orderSub, limitSub);
            String sqlquery = qb.buildUnionQuery (new String[] { sub1, sub }, orderGeneral, limitGeneral);
            cursor = myDataBase.rawQuery (sqlquery, null);
            
        } catch (SQLiteException e) {
            throw e;
        }
        return cursor;
    }
    
    /**
     * Metodo encargado de devolver el cursor relleno con los datos de la tabla mas los datos que se pasan en los strings defaultValues1 y
     * defaultValues2 con los primeros valores que se quieren rellenar
     * @param keysSub columnas que se quieren obtener de la query
     * @param sub1 valor que se quiere a?adir al cursor
     * @param sub2 valor que se quiere a?adir al cursor
     * @param orderGeneral orden de los valores de la query general
     * @param limitGeneral limite de la query general
     * @param whereSub clausula where de la subquery
     * @param groupSub clausula group de la subquery
     * @param havingSub clausula having de la subquery
     * @param orderSub clausula order de la subquery
     * @param limitSub clausula limit de la subquery
     *
     * @return objeto Cursos con el resultado de la query que se ejecuta
     */
    public Cursor rellenaCombo2ValoresDefecto (String keysSub[], String sub1, String sub2, String orderGeneral, String limitGeneral, String whereSub,
            String groupSub, String havingSub, String orderSub, String limitSub) throws SQLiteException {
        Cursor cursor = null;
        try {
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder ();
            qb.setTables (DATABASE_TABLE);
            qb.setDistinct (true);
            
            String sub = qb.buildQuery (keysSub, whereSub, null, groupSub, havingSub, orderSub, limitSub);
            String sqlquery = qb.buildUnionQuery (new String[] { sub1, sub2, sub }, orderGeneral, limitGeneral);
            cursor = myDataBase.rawQuery (sqlquery, null);// rawQueryWithFactory(null, sqlquery, null, DATABASE_TABLE2);//(sqlquery,
            // null);//.query(myDataBase,sqlquery, null, null, null, null, null);
        } catch (SQLiteException e) {
            throw e;
        }
        return cursor;
    }
    
    /**
     * Metodo encargado de devolver el cursor relleno con los datos de la tabla mas los datos que se pasan en los arrarys subquerys relleno con las
     * querys de valores que se quieren a?adir
     * @param keysSub columnas que se quieren obtener de la query
     * @param subquerysDefault array rellenos con las querys que se quiere a?adir al cursor
     * @param orderGeneral orden de los valores de la query general
     * @param limitGeneral limite de la query general
     * @param whereSub clausula where de la subquery
     * @param groupSub clausula group de la subquery
     * @param havingSub clausula having de la subquery
     * @param orderSub clausula order de la subquery
     * @param limitSub clausula limit de la subquery
     *
     * @return objeto Cursos con el resultado de la query que se ejecuta
     */
    public Cursor rellenaComboValoresDefecto (String keysSub[], String subquerysDefault[], String orderGeneral, String limitGeneral, String whereSub,
            String groupSub, String havingSub, String orderSub, String limitSub) throws SQLiteException {
        Cursor cursor = null;
        try {
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder ();
            qb.setTables (DATABASE_TABLE);
            qb.setDistinct (true);
            String sub = qb.buildQuery (keysSub, whereSub, null, groupSub, havingSub, orderSub, limitSub);
            String[] querys;
            if (subquerysDefault != null) {
                querys = new String[subquerysDefault.length + 1];
                
                for (int i = 0; i < subquerysDefault.length; i++) {
                    querys[i] = subquerysDefault[i];
                }
                querys[subquerysDefault.length] = sub;
            } else {
                querys = new String[] { sub };
            }
            String sqlquery = qb.buildUnionQuery (querys, orderGeneral, limitGeneral);
            cursor = myDataBase.rawQuery (sqlquery, null);// rawQueryWithFactory(null, sqlquery, null, DATABASE_TABLE2);//(sqlquery,
            // null);//.query(myDataBase,sqlquery, null, null, null, null, null);
        } catch (SQLiteException e) {
            throw e;
        }
        return cursor;
    }
    
    public Cursor ejecutaQuery (String sql, String[] selectionArgs) throws SQLiteException {
        Cursor cursor = null;
        try {
            cursor = myDataBase.rawQuery (sql, selectionArgs);// rawQueryWithFactory(null, sqlquery, null, DATABASE_TABLE2);//(sqlquery,
            // null);//.query(myDataBase,sqlquery, null, null, null, null, null);
        } catch (SQLiteException e) {
            throw e;
        }
        
        return cursor;
    }
    
    public void ejecutaQuery (String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy)
            throws SQLiteException {
        try {
            myDataBase.query (DATABASE_TABLE, columns, selection, selectionArgs, groupBy, having, orderBy);
        } catch (SQLiteException e) {
            throw e;
        }
        
    }
    
    public Cursor ejecutaQuery (boolean distinct, String tabla, String[] columns, String selection, String[] selectionArgs, String where,
            String groupBy, String having, String orderBy, String limit) throws SQLiteException {
        Cursor cursor = null;
        try {
            String sql = SQLiteQueryBuilder.buildQueryString (distinct, tabla, columns, where, groupBy, having, orderBy, limit);
            cursor = myDataBase.rawQuery (sql, selectionArgs);// rawQueryWithFactory(null, sqlquery, null, DATABASE_TABLE2);//(sqlquery,
            // null);//.query(myDataBase,sqlquery, null, null, null, null, null);
        } catch (SQLiteException e) {
            throw e;
        }
        return cursor;
    }
    
    public void ejecutaQuery (String sql) throws SQLiteException {
        
        try {
            myDataBase.execSQL (sql);
        } catch (SQLiteException e) {
            throw e;
        }
        
    }
    
    /**
     * Metodo encargado de realizar
     * @param tablaFrom
     * @param tipoINNER
     * @param tablasINNER
     * @param condicionesINNER
     * @param where
     * @param columnas
     * @param selectionArgs
     * @param Distinct
     * @param group
     * @param orden
     *
     * @return devuelve un objeto Cursor con el resultado de la busqueda
     * @throws SQLiteException
     */
    public Cursor ejecutaQueryJOIN (String tablaFrom, String[] tipoINNER, String[] tablasINNER, String[] condicionesINNER, String where,
            String[] columnas, String[] selectionArgs, boolean Distinct, String group, String orden) throws SQLiteException {
        Cursor cursor = null;
        try {
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder ();
            String tablas;
            if (tablaFrom != null)
                tablas = tablaFrom;
            else
                tablas = DATABASE_TABLE;
            
            if ((tablasINNER != null && condicionesINNER != null) && (tablasINNER.length == condicionesINNER.length)) {
                for (int i = 0; i < tablasINNER.length; i++) {
                    tablas += tipoINNER[i] + tablasINNER[i] + " ON " + condicionesINNER[i];
                }
            }
            qb.setTables (tablas);
            qb.setDistinct (Distinct);
            if (where != null)
                qb.appendWhere (where);
            
            cursor = qb.query (myDataBase, columnas, null, selectionArgs, group, null, orden);
            
            
        } catch (SQLiteException e) {
            throw e;
        }
        return cursor;
    }
    
    /**
     * Metodo encargado de realizar
     * @param tablaFrom
     * @param tipoINNER
     * @param tablasINNER
     * @param condicionesINNER
     * @param where
     * @param columnas
     * @param selectionArgs
     * @param Distinct
     * @param group
     * @param orden
     * @param having
     *
     * @return devuelve un objeto Cursor con el resultado de la busqueda
     * @throws SQLiteException
     */
    public Cursor ejecutaQueryJOIN (String tablaFrom, String[] tipoINNER, String[] tablasINNER, String[] condicionesINNER, String where,
            String[] columnas, String[] selectionArgs, boolean Distinct, String group, String orden, String having) throws SQLiteException {
        Cursor cursor = null;
        try {
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder ();
            String tablas;
            if (tablaFrom != null)
                tablas = tablaFrom;
            else
                tablas = DATABASE_TABLE;
            
            if ((tablasINNER != null && condicionesINNER != null) && (tablasINNER.length == condicionesINNER.length)) {
                for (int i = 0; i < tablasINNER.length; i++) {
                    tablas += tipoINNER[i] + tablasINNER[i] + " ON " + condicionesINNER[i];
                }
            }
            qb.setTables (tablas);
            qb.setDistinct (Distinct);
            if (where != null)
                qb.appendWhere (where);
            
            cursor = qb.query (myDataBase, columnas, null, selectionArgs, group, having, orden);
            
        } catch (SQLiteException e) {
            throw e;
        }
        return cursor;
    }
    
    
    /**
     * Metodo encargado de realizar
     * @param tablaFrom
     * @param tipoINNER
     * @param tablasINNER
     * @param condicionesINNER
     * @param where
     * @param columnas
     * @param selectionArgs
     * @param Distinct
     * @param group
     * @param orden
     * @param having
     * @param limit
     *
     * @return devuelve un objeto Cursor con el resultado de la busqueda
     * @throws SQLiteException
     */
    public Cursor ejecutaQueryJOIN (String tablaFrom, String[] tipoINNER, String[] tablasINNER, String[] condicionesINNER, String where,
            String[] columnas, String[] selectionArgs, boolean Distinct, String group, String orden, String having, String limit) throws SQLiteException {
        Cursor cursor = null;
        try {
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder ();
            String tablas;
            if (tablaFrom != null)
                tablas = tablaFrom;
            else
                tablas = DATABASE_TABLE;
            
            if ((tablasINNER != null && condicionesINNER != null) && (tablasINNER.length == condicionesINNER.length)) {
                for (int i = 0; i < tablasINNER.length; i++) {
                    tablas += tipoINNER[i] + tablasINNER[i] + " ON " + condicionesINNER[i];
                }
            }
            qb.setTables (tablas);
            qb.setDistinct (Distinct);
            if (where != null)
                qb.appendWhere (where);
            
            cursor = qb.query (myDataBase, columnas, null, selectionArgs, group, having, orden, limit);
            
        } catch (SQLiteException e) {
            throw e;
        }
        return cursor;
    }
    
    
    public Cursor ejecutaQueryUNION (String subQuerys[], String order, String limit) throws SQLiteException {
        Cursor cursor = null;
        try {
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder ();
            qb.setTables (DATABASE_TABLE);
            qb.setDistinct (true);
            
            int totalsubQuerys = subQuerys.length;
            String querys[] = new String[totalsubQuerys];
            
            for (int i = 0; i < totalsubQuerys; i++) {
                querys[i] = subQuerys[i];
            }
            
            String sqlquery = qb.buildUnionQuery (querys, order, limit);
            cursor = myDataBase.rawQuery (sqlquery, null);
            
        } catch (SQLiteException e) {
            throw e;
        }
        return cursor;
    }
    
    public String rellenaQueryINNERJOIN (String tablaFrom, String[] tipoINNER, String[] tablasINNER, String[] condicionesINNER, String where,
            String[] columnas, String[] selectionArgs, boolean distinct, String group, String orden, String having, String orderBy, String limit)
            throws SQLiteException {
        String sql;
        
        try {
            String tablas;
            if (tablaFrom != null)
                tablas = tablaFrom;
            else
                tablas = DATABASE_TABLE;
            
            if ((tablasINNER != null && condicionesINNER != null) && (tablasINNER.length == condicionesINNER.length)) {
                for (int i = 0; i < tablasINNER.length; i++) {
                    tablas += tipoINNER[i] + tablasINNER[i] + " ON " + condicionesINNER[i];
                }
            }
            sql = SQLiteQueryBuilder.buildQueryString (distinct, tablas, columnas, where, group, having, orderBy, limit);
        } catch (SQLiteException e) {
            throw e;
        }
        return sql;
    }
    
    public String rellenaQuery (String tablaFrom, String where, String[] columnas, String[] selectionArgs, boolean distinct, String group, String orden, String having, String orderBy, String limit)
            throws SQLiteException {
        String sql;
        
        try {
            String tablas;
            if (tablaFrom != null)
                tablas = tablaFrom;
            else
                tablas = DATABASE_TABLE;
            
            sql = SQLiteQueryBuilder.buildQueryString (distinct, tablas, columnas, where, group, having, orderBy, limit);
        } catch (SQLiteException e) {
            throw e;
        }
        return sql;
    }
    
    public String rellenaQueryUNION (String subQuerys[], String order, String limit) throws SQLiteException {
        String sqlUNION = null;
        try {
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder ();
            qb.setTables (DATABASE_TABLE);
            qb.setDistinct (true);
            
            int totalsubQuerys = subQuerys.length;
            String querys[] = new String[totalsubQuerys];
            
            for (int i = 0; i < totalsubQuerys; i++) {
                querys[i] = subQuerys[i];
            }
            
            sqlUNION = qb.buildUnionQuery (querys, order, limit);
            
        } catch (SQLiteException e) {
            throw e;
        }
        return sqlUNION;
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY =? se utilizara cuando se quiera pasar a las query valores para rellenar el campo
     * SelectionArgs
     * @param key
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionSelectionArgs (String key) {
        String CondicionWhere = null;
        
        if (key != null)
            CondicionWhere = key + "=?";
        
        return CondicionWhere;
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY =? se utilizara cuando se quiera pasar a las query valores para rellenar el campo
     * SelectionArgs
     * @param keys
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionSelectionArgs (String[] keys) {
        String CondicionWhere = null;
        
        for (int i = 0; i < keys.length; i++) {
            if (i == 0)
                CondicionWhere = "";
            if (i > 0)
                CondicionWhere += " AND ";
            CondicionWhere += keys[i] + "=? ";
        }
        
        return CondicionWhere;
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY =? se utilizara cuando se quiera pasar a las query valores para rellenar el campo
     * SelectionArgs
     * @param keys
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionSelectionArgsOR (String[] keys) {
        String CondicionWhere = null;
        
        for (int i = 0; i < keys.length; i++) {
            if (i == 0)
                CondicionWhere = "(";
            if (i > 0)
                CondicionWhere += " OR ";
            CondicionWhere += keys[i] + "=? ";
        }
        CondicionWhere += ")";
        return CondicionWhere;
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY =? se utilizara cuando se quiera pasar a las query valores para rellenar el campo
     * SelectionArgs
     * @param key
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionLIKESelectionArgs (String key) {
        String CondicionWhere = null;
        
        if (key != null)
            CondicionWhere = key + " LIKE ?";
        
        return CondicionWhere;
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY LIKE value se utilizara cuando se quiera pasar a las query valores para rellenar el campo
     * SelectionArgs
     * @param key
     * @param value
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionLIKE (String key, String value) {
        String CondicionWhere = null;
        
        if (key != null)
            CondicionWhere = key + " LIKE " + value;
        
        return CondicionWhere;
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY LIKE value se utilizara cuando se quiera pasar a las query valores para rellenar el campo
     * value. El campo value no debe incluir el %.
     * @param key
     * @param value
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionStrAvanzadoLIKE (String key, String value) {
        String condicionWhere = null;
        
        if (key != null)
            condicionWhere = key + " LIKE " + "'%" + value + "%'";
        
        return condicionWhere;
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY LIKE 'value' se utilizara cuando se quiera pasar a las query valores para rellenar el campo
     * SelectionArgs
     * @param key
     * @param value
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionStrLIKE (String key, String value) {
        String CondicionWhere = null;
        
        if (key != null)
            CondicionWhere = key + " LIKE '" + value + "'";
        
        return CondicionWhere;
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY = 'VALOR' concatenando la key y el valor que se pasan por parametro
     * @param key
     * @param value
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionStr (String key, String value) {
        String CondicionWhere = null;
        
        if (key != null && value != null) {
            value = value.replace ("'", "''");
            CondicionWhere = key + "='" + value + "'";
        }
        return CondicionWhere;
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY = VALOR concatenando la key y el valor que se pasan por parametro
     * @param key
     * @param value
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicion (String key, String value) {
        String CondicionWhere = null;
        
        if (key != null && value != null) {
            //value = value.replace("'", "''");
            CondicionWhere = key + "=" + value;
        }
        return CondicionWhere;
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY <> 'VALOR' concatenando la key y el valor que se pasan por parametro
     * @param key
     * @param value
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionStrDistinto (String key, String value) {
        String CondicionWhere = null;
        
        if (key != null && value != null) {
            value = value.replace ("'", "''");
            CondicionWhere = key + "<>'" + value + "'";
        }
        
        return CondicionWhere;
        
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY <> VALOR concatenando la key y el valor que se pasan por parametro
     * @param key
     * @param value
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionDistinto (String key, String value) {
        String CondicionWhere = null;
        
        if (key != null && value != null) {
            value = value.replace ("'", "''");
            CondicionWhere = key + "<>" + value + "";
        }
        
        return CondicionWhere;
        
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY IS NULL concatenando la key y el valor que se pasan por parametro
     * @param key
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionIsNULL (String key) {
        String CondicionWhere = null;
        
        if (key != null)
            CondicionWhere = key + " IS NULL ";
        
        return CondicionWhere;
        
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY IS NOT NULL concatenando la key y el valor que se pasan por parametro
     * @param key
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionIsNotNULL (String key) {
        String CondicionWhere = null;
        
        if (key != null)
            CondicionWhere = key + " IS NOT NULL ";
        
        return CondicionWhere;
        
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY > 'VALOR' concatenando la key y el valor que se pasan por parametro
     * @param key
     * @param value
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionStrMayor (String key, String value) {
        String CondicionWhere = null;
        
        if (key != null && value != null) {
            value = value.replace ("'", "''");
            CondicionWhere = key + ">'" + value + "'";
        }
        
        return CondicionWhere;
        
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY >= 'VALOR' concatenando la key y el valor que se pasan por parametro
     * @param key
     * @param value
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionStrMayorIgual (String key, String value) {
        String CondicionWhere = null;
        
        if (key != null && value != null) {
            value = value.replace ("'", "''");
            CondicionWhere = key + ">='" + value + "'";
        }
        
        return CondicionWhere;
        
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY < 'VALOR'
     * concatenando la key y el valor que se pasan por parametro
     * @param key
     * @param value
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionStrMenor (String key, String value) {
        String CondicionWhere = null;
        
        if (key != null && value != null) {
            value = value.replace ("'", "''");
            CondicionWhere = key + "<'" + value + "'";
        }
        
        return CondicionWhere;
        
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY <= 'VALOR' concatenando la key y el valor que se pasan por parametro
     * @param key
     * @param value
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionStrMenorIgual (String key, String value) {
        String CondicionWhere = null;
        
        if (key != null && value != null) {
            value = value.replace ("'", "''");
            CondicionWhere = key + "<= '" + value + "'";
        }
        
        return CondicionWhere;
        
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY > VALOR concatenando la key y el valor que se pasan por parametro
     * @param key
     * @param value
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionMayor (String key, String value) {
        String CondicionWhere = null;
        
        if (key != null && value != null)
            CondicionWhere = key + "> " + value;
        
        return CondicionWhere;
        
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY >= VALOR concatenando la key y el valor que se pasan por parametro
     * @param key
     * @param value
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionMayorIgual (String key, String value) {
        String CondicionWhere = null;
        
        if (key != null && value != null)
            CondicionWhere = key + ">= " + value;
        
        return CondicionWhere;
        
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY < VALOR concatenando la key y el valor que se pasan por parametro
     * @param key
     * @param value
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionMenor (String key, String value) {
        String CondicionWhere = null;
        
        if (key != null && value != null)
            CondicionWhere = key + "< " + value;
        
        return CondicionWhere;
        
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY <= VALUE concatenando la key y el valor que se pasan por parametro
     * @param key
     * @param value
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionMenorIgual (String key, String value) {
        String CondicionWhere = null;
        
        if (key != null && value != null)
            CondicionWhere = key + "<= " + value;
        
        return CondicionWhere;
        
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY IN ('VALUE') concatenando la key y el valor que se pasan por parametro
     * @param key
     * @param value
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionStrIN (String key, String value) {
        String CondicionWhere = null;
        
        if (key != null && value != null) {
            
            value = value.replace ("'", "''");
            String valuesStr = "'" + value + "' ";
            CondicionWhere = key + " IN (" + valuesStr + ")";
            
            //CondicionWhere = key + " IN (" + value + ")";
        }
        
        return CondicionWhere;
        
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY IN (VALUE) concatenando la key y el valor que se pasan por parametro
     * @param key
     * @param value
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionIN (String key, String value) {
        String CondicionWhere = null;
        
        if (key != null && value != null) {
            
            value = value.replace ("'", "''");
            CondicionWhere = key + " IN (" + value + ")";
        }
        
        return CondicionWhere;
        
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY IN (VALUE1, VALUE2, ..., VALUEN) concatenando la key y el valor que se pasan por parametro
     * @param key
     * @param values
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionIN (String key, String[] values) {
        String CondicionWhere = null;
        
        if (key != null && values != null) {
            String valuesStr = "";
            for (int i = 0; i < values.length; i++) {
                if (i > 0)
                    valuesStr += " , ";
                
                String value = values[i];
                value = value.replace ("'", "''");
                valuesStr += value;
            }
            
            CondicionWhere = key + " IN (" + valuesStr + ")";
        }
        
        return CondicionWhere;
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY IN ('VALOR1', 'VALOR2', ..., 'VALORN' ) concatenando la key y el valor que se pasan
     * por parametro
     * @param key
     * @param values
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionStrIN (String key, String[] values) {
        String CondicionWhere = null;
        
        if (key != null && values != null) {
            String valuesStr = "";
            for (int i = 0; i < values.length; i++) {
                if (i > 0)
                    valuesStr += " , ";
                
                String value = values[i];
                value = value.replace ("'", "''");
                valuesStr += "'" + value + "' ";
            }
            
            CondicionWhere = key + " IN (" + valuesStr + ")";
        }
        
        return CondicionWhere;
        
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY NOT IN (VALOR) concatenando la key y el valor que se pasan por parametro
     * @param key
     * @param value
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionNOTIN (String key, String value) {
        String CondicionWhere = null;
        
        if (key != null && value != null)
            CondicionWhere = key + " NOT IN (" + value + ")";
        
        return CondicionWhere;
        
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY NOT IN ('VALOR1', 'VALOR2', ..., 'VALORN' ) concatenando la key y el valor que se
     * pasan por parametro
     * @param key
     * @param values
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionStrNOTIN (String key, String[] values) {
        String CondicionWhere = null;
        
        if (key != null && values != null) {
            String valuesStr = "";
            for (int i = 0; i < values.length; i++) {
                if (i > 0)
                    valuesStr += " , ";
                
                String value = values[i];
                value.replace ("'", "''");
                valuesStr += "'" + value + "' ";
            }
            
            CondicionWhere = key + " NOT IN (" + valuesStr + ")";
        }
        
        return CondicionWhere;
        
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY1='VALOR1' AND KEY2='VALOR2' AND ...KEYN='VALORN' concatenando la key y el valor que
     * se pasan por parametro
     * @param keys
     * @param values
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionStrAvanzado (String[] keys, String[] values) {
        String CondicionWhere = null;
        
        if (keys.length == values.length && keys.length > 0) {
            
            for (int i = 0; i < keys.length; i++) {
                String value = values[i];
                
                if (value != null)
                    value = value.replace ("'", "''");
                
                if (i == 0)
                    CondicionWhere = "";
                if (i > 0)
                    CondicionWhere += " AND ";
                
                CondicionWhere += keys[i] + "='" + value + "' ";
                
            }
        }
        return CondicionWhere;
        
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY='VALOR1' AND KEY='VALOR2' AND ...KEY='VALORN' concatenando la key y el valor que
     * se pasan por parametro
     * @param key
     * @param values
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionStrAvanzado (String key, String[] values) {
        String CondicionWhere = null;
        
        if (values != null && values.length > 0) {
            for (int i = 0; i < values.length; i++) {
                String value = values[i];
                
                if (value != null)
                    value = value.replace ("'", "''");
                
                if (i == 0)
                    CondicionWhere = "";
                if (i > 0)
                    CondicionWhere += " AND ";
                
                CondicionWhere += key + "='" + value + "' ";
                
            }
        }
        return CondicionWhere;
        
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY1=VALOR1 AND KEY2=VALOR2 AND ...KEYN=VALORN concatenando la key y el valor que
     * se pasan por parametro
     * @param keys
     * @param values
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionAvanzado (String[] keys, String[] values) {
        String CondicionWhere = null;
        
        if (keys.length == values.length && keys.length > 0) {
            
            for (int i = 0; i < keys.length; i++) {
                String value = values[i];
                
                if (value != null)
                    value = value.replace ("'", "''");
                
                if (i == 0)
                    CondicionWhere = "";
                if (i > 0)
                    CondicionWhere += " AND ";
                
                CondicionWhere += keys[i] + "=" + value;
                
            }
        }
        return CondicionWhere;
        
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY1='VALOR1' OR KEY2='VALOR2' OR .... concatenando la key y el valor que se pasan por
     * parametro
     * @param keys
     * @param values
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionStrAvanzadoOR (String[] keys, String[] values) {
        String CondicionWhere = null;
        
        if (keys.length == values.length && keys.length > 0) {
            
            for (int i = 0; i < keys.length; i++) {
                if (i == 0)
                    CondicionWhere = "";
                if (i > 0)
                    CondicionWhere += " OR ";
                
                String value = values[i];
                value.replace ("'", "''");
                CondicionWhere += keys[i] + "='" + value + "' ";
            }
        }
        return CondicionWhere;
        
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY='VALOR1' OR KEY='VALOR2' OR .... concatenando la key y el valor que se pasan por
     * parametro
     * @param key
     * @param values
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionStrAvanzadoOR (String key, String[] values) {
        String CondicionWhere = null;
        
        if (values.length > 0) {
            for (int i = 0; i < values.length; i++) {
                if (i == 0)
                    CondicionWhere = "";
                if (i > 0)
                    CondicionWhere += " OR ";
                
                String value = values[i];
                value.replace ("'", "''");
                CondicionWhere += key + "='" + value + "' ";
            }
        }
        return CondicionWhere;
        
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY1=VALOR1 OR KEY2=VALOR2 OR .... concatenando la key y el valor que se pasan por
     * parametro
     * @param keys
     * @param values
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionAvanzadoOR (String[] keys, String[] values) {
        String CondicionWhere = null;
        
        if (keys.length == values.length && keys.length > 0) {
            
            for (int i = 0; i < keys.length; i++) {
                if (i == 0)
                    CondicionWhere = "";
                if (i > 0)
                    CondicionWhere += " OR ";
                
                String value = values[i];
                //value.replace("'", "''");
                CondicionWhere += keys[i] + "=" + value;
            }
        }
        return CondicionWhere;
        
    }
    
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY1 LIKE '%VALOR1%' OR KEY2 LIKE '%VALOR2%' OR .... concatenando la key y el valor que
     * se pasan por parametro
     * @param keys []
     * @param values []
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionStrAvanzadoLIKEOR (String[] keys, String[] values) {
        String CondicionWhere = null;
        
        if (keys.length == values.length && keys.length > 0) {
            
            for (int i = 0; i < keys.length; i++) {
                if (i == 0)
                    CondicionWhere = "";
                if (i > 0)
                    CondicionWhere += " OR ";
                
                String value = values[i];
                value.replace ("'", "''");
                CondicionWhere += keys[i] + " LIKE '%" + value + "%' ";
            }
        }
        return CondicionWhere;
        
    }
    
    /**
     * Metodo encargado de generar la condicion WHERE de tipo KEY LIKE '%VALOR1' concatenando la key y el valor que se pasan por parametro
     * @param key
     * @param value
     *
     * @return cadena con las condicion where generada
     */
    public String generarCondicionStrEND (String key, String value) {
        String condicionWhere = null;
        
        if (key != null)
            condicionWhere = key + " LIKE '%" + value + "'";
        
        return condicionWhere;
    }
    
    /**
     * Metodo encargado de generar una condicion de tipo SUBSTR(KEY, inicio, longitud) = 'VALOR' concatenando la key, los valores de inicio y longitud y el
     * valor que se pasan como parametros
     * @param key
     * @param inicio
     * @param longitud
     * @param value
     *
     * @return cadena con las condicion generada
     */
    public String generarCondicionStrSubstring (String key, int inicio, int longitud, String value) {
        String condicionWhere = null;
        
        if (key != null)
            condicionWhere = "SUBSTR(" + key + "," + String.valueOf (inicio) + "," + String.valueOf (longitud) + ") = '" + value + "'";
        
        return condicionWhere;
    }
    
    /**
     * Metodo encargado de generar una condicion de tipo SUBSTR(KEY, inicio, longitud) = 'VALOR' concatenando la key, los valores de inicio y longitud y el
     * valor que se pasan como parametros
     * @param key
     * @param inicio
     * @param longitud
     * @param value
     *
     * @return cadena con las condicion generada
     */
    public String generarCondicionStrSubstringConAS (String key, int inicio, int longitud, String value) {
        String condicionWhere = null;
        
        if (key != null)
            condicionWhere = "SUBSTR(" + key + "," + String.valueOf (inicio) + "," + String.valueOf (longitud) + ") = '" + value + "' AS " + key;
        
        return condicionWhere;
    }
    
    /**
     * Metodo encargado de generar una condicion de tipo SUBSTR(KEY, inicio, fin) = 'VALOR' concatenando la key, los valores de inicio y fin y el
     * valor que se pasan como parametros
     * @param key
     * @param inicio
     * @param longitud
     * @param value
     * @param keyAS
     *
     * @return cadena con las condicion generada
     */
    public String generarCondicionStrSubstringConAS (String key, int inicio, int longitud, String value, String keyAS) {
        String condicionWhere = null;
        
        if (key != null)
            condicionWhere = "SUBSTR(" + key + "," + String.valueOf (inicio) + "," + String.valueOf (longitud) + ") = '" + value + "' AS " + keyAS;
        
        return condicionWhere;
    }
    
    /**
     * Metodo que devuleve el nombre de la tabla
     * @return devuelve una cadena con el nombre de la tabla
     */
    public String getNombreTabla () {
        return DATABASE_TABLE;
    }
    
    /**
     * Metodo que devuleve el nombre de la tabla as nameRenamed
     * @param nameRenamed nombre para utilizar como alias de la tabla
     *
     * @return devuelve una cadena con el nombre de la tabla nombreTabla AS nameRenamed
     */
    public String getNombreTablaRenamed (String nameRenamed) {
        return DATABASE_TABLE + " AS " + nameRenamed;
    }
    
    /**
     * Metodo que devuleve el nombre del campo con el de la tabla 'tabla.nombrecampo'
     * @param key con el campo que se quiere formatear
     *
     * @return devuelve una cadena con la cadena 'tabla.nombrecampo'
     */
    public String getKeyTabla (String key) {
        return DATABASE_TABLE + "." + key;
    }
    
    
    /**
     * Metodo que devuleve el nombre del campo con el de la tabla 'tabla.nombrecampo as nombrecampo'
     * @param key con el campo que se quiere formatear
     *
     * @return devuelve una cadena con la cadena 'tabla.nombrecampo'
     */
    public String getKeyTablaConAS (String key) {
        return DATABASE_TABLE + "." + key + " as " + key;
    }
    
    /**
     * Metodo que devuleve el nombre del campo con el de la tabla 'tabla.nombrecampo as nombreAs'
     * @param key con el campo que se quiere formatear
     *
     * @return devuelve una cadena con la cadena 'tabla.nombrecampo'
     */
    public String getKeyTablaConAS (String key, String nombreAs) {
        return DATABASE_TABLE + "." + key + " as " + nombreAs;
    }
    
    /**
     * Metodo que devuleve el nombre del campo con el de la tabla 't1.nombrecampo'
     * @param key con el campo que se quiere formatear
     * @param renamedTable el nombre para renombrar la tabla
     *
     * @return devuelve una cadena con la cadena 'tabla.nombrecampo'
     */
    public String getKeyTablaRenamed (String key, String renamedTable) {
        return renamedTable + "." + key;
    }
    
    /**
     * Metodo que devuleve el nombre del campo con el de la tabla 't1.nombrecampo as nombrecampo'
     * @param key con el campo que se quiere formatear
     * @param renamedTable el nombre para renombrar la tabla
     *
     * @return devuelve una cadena con la cadena 'tabla.nombrecampo'
     */
    public String getKeyTablaRenamedConAS (String key, String renamedTable) {
        return renamedTable + "." + key + " as " + key;
    }
    
    
    /**
     * Metodo que devuleve el nombre del campo con el de la tabla 't1.nombrecampo as nombreAs'
     * @param key con el campo que se quiere formatear
     * @param nombreAs el nombre para utilizar de alias del campo
     * @param renamedTable el nombre para renombrar la tabla
     *
     * @return devuelve una cadena con la cadena 'tabla.nombrecampo'
     */
    public String getKeyTablaRenamedConAS (String key, String nombreAs, String renamedTable) {
        return renamedTable + "." + key + " as " + nombreAs;
    }
    
    /**
     * Metodo que devuleve el nombre del campo con el de la tabla 'tabla.nombrecampo'
     * @param key con el campo que se quiere formatear
     *
     * @return devuelve una cadena con la cadena 'tabla.nombrecampo'
     */
    public String getKeyTablaCastFloatConAS (String key, int division) {
        return " cast(" + DATABASE_TABLE + "." + key + " as float)/" + division + " as " + key;
    }
    
    /**
     * @param key
     * @param division
     * @param nombreAs
     *
     * @return
     */
    public String getKeyTablaCastFloatConAS (String key, int division, String nombreAs) {
        return " cast(" + DATABASE_TABLE + "." + key + " as float)/" + division + " as " + nombreAs;
    }
    
    /**
     * @param key
     * @param division
     *
     * @return
     */
    public String getCastFloatConAS (String key, int division) {
        return " cast(" + key + " as float)/" + division + " as " + key;
    }
    
    /**
     * @param key
     * @param division
     *
     * @return
     */
    public String getSUMCastFloatConAS (String key, int division) {
        return " cast(SUM(" + key + ") as float)/" + division + " as " + key;
    }
    
    /**
     * @param key
     * @param division
     *
     * @return
     */
    public String getSUMCastFloat (String key, int division) {
        return " cast(SUM(" + key + ") as float)/" + division;
    }
    
    /**
     * @param key
     * @param division
     *
     * @return
     */
    public String getCastDoubleConAS (String key, int division) {
        return " cast(" + key + " as double)/" + division + " as " + key;
        
    }
    
    /**
     * @param key
     * @param division
     *
     * @return
     */
    public String getCastDoubleTEXTConAS (String key, int division) {
        return " cast(cast(" + key + " as double)/" + division + " as TEXT) as " + key;
        
    }
    
    /**
     * @param key
     * @param division
     *
     * @return
     */
    public String getCastDoubleSinAS (String key, int division) {
        return " cast(" + key + " as double)/" + division;
        
    }
    
    /**
     * @param key
     * @param division
     *
     * @return
     */
    public String getCastIntConAS (String key, int division) {
        return " cast(" + key + " as int)/" + division + " as " + key;
        
    }
    
    /**
     * @param key
     * @param division
     *
     * @return
     */
    public String getCastFloatSinAS (String key, int division) {
        return " cast(" + key + " as float)/" + division;
    }
    
    /**
     * @param key
     *
     * @return
     */
    public String getSUMConAS (String key) {
        return " sum(" + key + " ) as " + key;
    }
    
    /**
     * @param key
     * @param asStr
     *
     * @return
     */
    public String getSUMConAS (String key, String asStr) {
        return " sum(" + key + " ) as " + asStr;
    }
    
    /**
     * @param key
     *
     * @return
     */
    public String getSUMSinAS (String key) {
        return " sum(" + key + " )";
    }
    
    
    public String getMax (String key) {
        return " max(" + key + ") ";
    }
    
    public String getMaxConAS (String key) {
        return " max(" + key + ") as " + key;
    }
    
    public String getMaxConAS (String key, String asStr) {
        return " max(" + key + ") as " + asStr;
    }
    
    public String getMin (String key) {
        return " min(" + key + ") ";
    }
    
    /**
     * Metodo que devuelve el MIN del valor por parametro con AS, de la forma MIN ( key ) as kei
     * @param key
     *
     * @return MIN ( key ) as key
     */
    public String getMinConAS (String key) {
        return " min(" + key + ") as " + key;
    }
    
    /**
     * Metodo que devuelve el COUMINNT del valor por parametro con AS, de la forma MIN ( key ) as asStr
     * @param key
     * @param asStr
     *
     * @return MIN ( key ) as asStr
     */
    public String getMinConAS (String key, String asStr) {
        return " min(" + key + ") as " + asStr;
    }
    
    /**
     * Metodo que devuelve el COUNT del valor por parametro, de la forma COUNT ( key )
     * @param key
     *
     * @return COUNT ( key )
     */
    public String getCountSinAS (String key) {
        return " COUNT (" + key + " )";
    }
    
    /**
     * Metodo que devuelve el COUNT del valor por parametro con AS, de la forma COUNT ( key ) as key
     * @param key
     *
     * @return COUNT ( key ) as key
     */
    public String getCountConAS (String key) {
        return " COUNT (" + key + " ) as " + key;
    }
    
    /**
     * Metodo que devuelve el COUNT del valor por parametro con AS, de la forma COUNT ( key ) as asStr
     * @param key
     * @param asStr
     *
     * @return COUNT ( key ) as asStr
     */
    public String getCountConAS (String key, String asStr) {
        return " COUNT (" + key + " ) as " + asStr;
    }
    
    /**
     * Metodo que devuelve el COUNT del valor por parametro con DISTINCT, de la forma COUNT ( DISTINCT key )
     * @param key
     *
     * @return COUNT ( DISTINCT key )
     */
    public String getCountDistinctSinAS (String key) {
        return " COUNT ( DISTINCT " + key + " )";
    }
    
    /**
     * Metodo que devuelve el COUNT del valor por parametro con AS y DISTINCT, de la forma COUNT ( DISTINCT key ) AS asStr
     * @param key
     * @param asStr
     *
     * @return COUNT ( DISTINCT key ) AS asStr
     */
    public String getCountDistinctConAS (String key, String asStr) {
        return " COUNT ( DISTINCT " + key + " ) as " + asStr;
    }
    
    /**
     * Metodo que devuelve el valor pasado por parametro con As, de la forma value as asStr
     * @param value
     * @param asStr
     *
     * @return value as asStr
     */
    public String getValueConAS (String value, String asStr) {
        return " " + value + " as " + asStr;
    }
    
    /**
     * Metodo que devuelve el valor pasado por parametro con As, de la forma 'value' as asStr
     * @param value
     * @param asStr
     *
     * @return 'value' as asStr
     */
    public String getValueStrConAS (String value, String asStr) {
        return " '" + value + "' as " + asStr;
    }
    
    @Override
    public void onCreate (SQLiteDatabase db) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void onUpgrade (SQLiteDatabase db,
            int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        
    }
}
