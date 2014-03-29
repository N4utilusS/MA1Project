package be.n4utiluss.wysiwyd.DAO.requests;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import static be.n4utiluss.wysiwyd.database.DatabaseContract.*;

public class RequestBottlesByCode extends AbstractRequest {
	private long code;

	public RequestBottlesByCode(long code){
		super();
		this.code = code;
	}

	@Override
	public Cursor executeRequest(SQLiteDatabase db) { 
		
		return db.rawQuery("SELECT * " +
				"FROM " + BottleTable.TABLE_NAME + " b, " +
						BottleVarietyTable.TABLE_NAME + " bv, " +
						VarietyTable.TABLE_NAME + " v " +
				"WHERE b." + BottleTable._ID + " = bv." + BottleVarietyTable.COLUMN_NAME_BOTTLE_ID +
				" AND v." + VarietyTable._ID + " = bv." + BottleVarietyTable.COLUMN_NAME_VARIETY_ID +
				" AND b." + BottleTable.COLUMN_NAME_CODE + " = ?", 
				new String[]{ Long.toString(this.code) });
	}

}
