package be.n4utiluss.wysiwyd.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	public DatabaseHelper(Context context) {
		super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
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
