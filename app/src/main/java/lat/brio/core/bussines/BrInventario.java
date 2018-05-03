package lat.brio.core.bussines;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.hova_it.barared.brio.apis.models.DLCorreosClientes;
import net.hova_it.barared.brio.apis.models.daos.CorreosClientesDB;
import net.hova_it.barared.brio.apis.models.daos.ItemsTicketDB;
import net.hova_it.barared.brio.apis.models.daos.ViewArticuloDB;
import net.hova_it.barared.brio.apis.models.daos.ViewInventarioDB;
import net.hova_it.barared.brio.apis.models.views.ViewArticulo;
import net.hova_it.barared.brio.apis.models.views.ViewInventario;

import java.util.ArrayList;

import lat.brio.core.BrioGlobales;

public class BrInventario extends BRMain {


    private ViewInventarioDB viewInventarioDB;
    private ViewArticuloDB viewArticuloDB;
    private ItemsTicketDB itemsTicketDB;


    private static BrInventario instance;

    public static BrInventario getInstance(Context context, SQLiteDatabase mAcess) {
        if (instance == null) {
            instance = new BrInventario(context.getApplicationContext(), mAcess);
        }

        return instance;
    }

    public BrInventario(Context contexto, SQLiteDatabase mAccess) {
        super(contexto, mAccess);
        viewInventarioDB = new ViewInventarioDB(contexto, mAccess);
        viewArticuloDB = new ViewArticuloDB(contexto, mAccess);
        itemsTicketDB = new ItemsTicketDB(contexto, mAccess);
    }


    public ArrayList<ViewInventario> getListInventario(String ColumnWhere, String value) {
        ArrayList<ViewInventario> dl = new ArrayList<ViewInventario>();
        try {
            String where = null;
            if (value != null)
                where = viewInventarioDB.generarCondicionStr(ColumnWhere, value);
            Cursor cursor = viewInventarioDB.busquedaAvanzada(ViewInventarioDB.columnas, where, null, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    dl.add(new ViewInventario(cursor));
                } while (cursor.moveToNext());
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
            return dl;

        } catch (Exception e) {
            BrioGlobales.writeLog("BrCustomer", "saveCustomerEmail()", e.getMessage());
            return dl;
        }
    }

    public ArrayList<ViewArticulo> getListArticulos(boolean isGranel) {

        ArrayList<ViewArticulo> dl = new ArrayList<ViewArticulo>();

        try {
            String COLUMN_VENDIDOS_ARTICULOS="Vendidos";

            String colVendidos = itemsTicketDB.getSUMConAS(itemsTicketDB.getKeyTabla(
                    ItemsTicketDB.KEY_ID_ARTICULO), COLUMN_VENDIDOS_ARTICULOS);
            String columns[] = new String[]{
                    viewArticuloDB.getKeyTabla(ViewArticuloDB.KEY_ID_ARTICULO),
                    viewArticuloDB.getKeyTabla(ViewArticuloDB.KEY_ID_CENTRAL),
                    viewArticuloDB.getKeyTabla(ViewArticuloDB.KEY_PRECIO_VENTA),
                    viewArticuloDB.getKeyTabla(ViewArticuloDB.KEY_PRECIO_COMPRA),
                    viewArticuloDB.getKeyTabla(ViewArticuloDB.KEY_CODE_BAR),
                    viewArticuloDB.getKeyTabla(ViewArticuloDB.KEY_NOMBRE),
                    viewArticuloDB.getKeyTabla(ViewArticuloDB.KEY_MARCA),
                    viewArticuloDB.getKeyTabla(ViewArticuloDB.KEY_PRESENTACION),
                    viewArticuloDB.getKeyTabla(ViewArticuloDB.KEY_CONTENIDO),
                    viewArticuloDB.getKeyTabla(ViewArticuloDB.KEY_UNIDAD),
                    viewArticuloDB.getKeyTabla(ViewArticuloDB.KEY_granel),
                    colVendidos
            };
            
            String where=null;
            if(isGranel)
            where = viewArticuloDB.generarCondicion (viewArticuloDB.getKeyTabla(ViewArticuloDB.KEY_granel),String.valueOf (1));
    
    
            String condicionInner = viewArticuloDB.getKeyTabla(ViewArticuloDB.KEY_ID_ARTICULO) + " = " + itemsTicketDB.getKeyTabla(
                    ItemsTicketDB.KEY_ID_ARTICULO);

            String[] condicionesINNER = new String[]{condicionInner};
            String[] tablasINNER = new String[]{itemsTicketDB.getNombreTabla()};
            String[] tipoINNER = new String[]{" LEFT JOIN "};

            String groupBy = viewArticuloDB.getKeyTabla(ViewArticuloDB.KEY_ID_ARTICULO);
            String orderBy = COLUMN_VENDIDOS_ARTICULOS + " DESC, " + viewArticuloDB.getKeyTabla(ViewArticuloDB.KEY_NOMBRE) + " ASC";

            Cursor cursor = viewArticuloDB.ejecutaQueryJOIN(viewArticuloDB.getNombreTabla(), tipoINNER, tablasINNER, condicionesINNER,
                    where, columns, null, false, groupBy, orderBy);

            if (cursor.moveToFirst()) {
                do {
                    dl.add(new ViewArticulo(cursor,viewArticuloDB));
                } while (cursor.moveToNext());
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
            return dl;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dl;
    }
 

}
