package lat.brio.core.DataAccess;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDiskIOException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import brio.sdk.logger.DataAccess.AccesoArchivosGeneral;
import brio.sdk.logger.log.BrioLog;
import lat.brio.core.BrioGlobales;

//import net.sqlcipher.database.SQLiteDatabase;

/**
 * Clase que se usa para abrir base de datos se le indica la ruta en terminal y nombre base datos funciones de abrir base de datos, copiar..
 * 
 * @version 1.0, 10/10/2017
 * @author Guillermo Ortiz
 */

public class DataBaseGeneral extends SQLiteOpenHelper
{

	private SQLiteDatabase myDataBase;
	private final Context myContext;
	

	/**
	 * Metodo constructor
	 * 
	 * @param context
	 */
	public DataBaseGeneral (Context context)
	{

		super(context, BrioGlobales.DB_NAME, null, 1);

		this.myContext = context;
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void crearDataBase() throws Exception
	{

		try
		{
			boolean dbExist = checkDataBase();
			if (!dbExist)
			{
					Log.i("DataBaseGeneral", "crearDataBase La BD no existe");

				// se crea la base de datos en el path y copiamos la nuestra
				this.getReadableDatabase();
			 
					Log.i("DataBaseGeneral", "crearDataBaseCopiamos la BD de assets" );
				copiarBaseDatos();
			}
		}
		catch (IOException ioe)
		{
			throw new Exception("[crearDataBase] Error copiando la BD (IOException): " + ioe.getMessage());
		}
		catch (Exception e)
		{
			throw new Exception("[crearDataBase] Error copiando la BD (Exception): " + e.getMessage());
		}
	}


	
	private boolean checkDataBase()
	{

		boolean result = false;
		boolean exist = false;
		SQLiteDatabase checkDB = null;

		try
		{

			String myPath = BrioGlobales.DB_PATH + BrioGlobales.DB_NAME;
			// se comprueba si el fichero de la base de datos existe fisicamente
			AccesoArchivosGeneral fichBD = new AccesoArchivosGeneral(myContext);
			exist = fichBD.existeFichero(BrioGlobales.DB_PATH, BrioGlobales.DB_NAME);

			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

			if (checkDB != null)
			{
				result = true;

				checkDB.close();
			}
		}
		catch (SQLiteDiskIOException e)
		{
			
			// si el fichero existe y da IOException es porque no se ha cerro bien la BD
            result = exist;
		}
		catch (SQLiteException e)
		{
			result = false;
			// throw new Exception("[checkDataBase] Error chequeando la BD (SQLiteException): " + e.getMessage());
		}
		catch (Exception e)
		{
			result = false;
			// throw new Exception("[checkDataBase] Error chequeando la BD (Exception): " + e.getMessage());
		}

		return result;
	}
	private void copiarBaseDatos() throws Exception
	{

		try
		{
			InputStream myInput = myContext.getAssets().open(BrioGlobales.DB_NAME);
			String outFileName = BrioGlobales.DB_PATH + BrioGlobales.DB_NAME;
			OutputStream myOutput = new FileOutputStream(outFileName);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer)) > 0)
			{
				myOutput.write(buffer, 0, length);
			}

			myOutput.flush();
			myOutput.close();
			myInput.close();
		}
		catch (Exception e)
		{
			throw new Exception("[copiarBaseDatos] Error copiando la BD (Exception): " + e.getMessage());
		}

	}

	/**
	 * @throws Exception
	 */
	public void recuperarBaseDatos() throws Exception
	{
		try
		{
			String inputfile = BrioGlobales.DB_PATH + BrioGlobales.DB_NAME;
			InputStream myInput = new FileInputStream(inputfile);
			String outFileName = BrioGlobales.DIR_APP_BACKUP + BrioGlobales.DB_NAME;
			OutputStream myOutput = new FileOutputStream(outFileName);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer)) > 0)
			{
				myOutput.write(buffer, 0, length);
			}
			myOutput.flush();
			myOutput.close();
			myInput.close();
		}
		catch (Exception e)
		{
			throw new Exception("[recuperarBaseDatos] Error recuperando la BD (Exception): " + e.getMessage());
		}
	}

 
	
	public SQLiteDatabase abrirBaseDatos() throws SQLException
	{

		String myPath = BrioGlobales.DB_PATH + BrioGlobales.DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
		return myDataBase;
	}
	
	
	@Override
	public synchronized void close()
	{

		if (myDataBase != null)
			myDataBase.close();

		super.close();
	}

 
	/**
	 * @throws Exception
	 */
	public void backupBaseDatos() throws Exception
	{
		try
		{
			if (BrioGlobales.DB_PATH != null && BrioGlobales.DB_NAME != null)
			{
				String inputfile = BrioGlobales.DB_PATH + BrioGlobales.DB_NAME;
				InputStream myInput = new FileInputStream(inputfile);
				String outFileName = BrioGlobales.DIR_APP_BACKUP + BrioGlobales.DB_NAME;
				OutputStream myOutput = new FileOutputStream(outFileName);
				byte[] buffer = new byte[1024];
				int length;
				while ((length = myInput.read(buffer)) > 0)
				{
					myOutput.write(buffer, 0, length);
				}
				myOutput.flush();
				myOutput.close();
				myInput.close();
 
					Log.i("DataBaseGeneral", "backupBaseDatos Backup de la BD realizado correctamente");
			}
			else
				throw new Exception("[backupBaseDatos] GlobalesDefinicionDatos.DB_PATH y/o GlobalesDefinicionDatos.DB_NAME son NULLL");
		}
		catch (Exception e)
		{
			throw new Exception("[backupBaseDatos] Error haciendo backup de la BD (Exception): " + e.getMessage());
		}
	}

	public SQLiteDatabase getMyDataBase()
	{
		return myDataBase;
	}

	public Context getMyContext()
	{
		return myContext;
	}

 

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0,
			int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

}
