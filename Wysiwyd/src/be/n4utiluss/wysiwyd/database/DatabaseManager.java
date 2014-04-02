package be.n4utiluss.wysiwyd.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseManager {
	private static DatabaseManager dbManager;
	private SQLiteDatabase db;
	private DatabaseHelper dbHelper;
	
	private DatabaseManager(Context c){
		this.dbHelper = new DatabaseHelper(c);
	}

	public static synchronized DatabaseManager getDatabaseManager(Context c){
		if (dbManager == null)
			dbManager = new DatabaseManager(c);
		return dbManager;
	}
	
	public SQLiteDatabase getDatabase(){
		if (this.db == null)
			this.db = this.dbHelper.getWritableDatabase();
		return this.db;
	}
}
