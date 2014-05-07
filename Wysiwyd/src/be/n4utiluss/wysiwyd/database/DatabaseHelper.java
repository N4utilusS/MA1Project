package be.n4utiluss.wysiwyd.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages connections with the database with a singleton
 * @author anthonydebruyn
 *
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	private final static Object o = new Object();
	private static DatabaseHelper instance = null;

	private DatabaseHelper(Context context) {
		super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
	}
	
	/**
	 * Returns the only instance of the class, to ensure no leaks are created.
	 * @param context The context of the call.
	 * @return The only instance.
	 */
	public static DatabaseHelper getInstance(Context context) {
		synchronized(o) {
			if (instance == null)
				instance = new DatabaseHelper(context.getApplicationContext());
		}
		
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DatabaseContract.BottleTable.CREATE_TABLE);
		db.execSQL(DatabaseContract.VarietyTable.CREATE_TABLE);
		db.execSQL(DatabaseContract.BottleVarietyTable.CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DatabaseContract.BottleTable.DELETE_TABLE);
		db.execSQL(DatabaseContract.VarietyTable.DELETE_TABLE);
		db.execSQL(DatabaseContract.BottleVarietyTable.DELETE_TABLE);
		onCreate(db);
	}

}
