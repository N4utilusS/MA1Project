package be.n4utiluss.wysiwyd.DAO.requests;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * The abstract class for the requests to the database.
 * @author anthonydebruyn
 *
 */
public abstract class AbstractRequest {

	/**
	 * Executes a SQLite query and returns the results.
	 * @param db The database object.
	 * @return A Cursor containing the results of the executed SQLite query
	 */
	public abstract Cursor executeRequest(SQLiteDatabase db);
}
