package be.n4utiluss.wysiwyd.DAO;

import be.n4utiluss.wysiwyd.DAO.requests.AbstractRequest;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public abstract class DAO<T> {
	
	protected SQLiteDatabase db;
	
	DAO(SQLiteDatabase db){
		this.db = db;
	}

	/**
	 * Deletes an object whose id is the parameter.
	 * @param id
	 * 		Id of object to delete.
	 * @return
	 * 		True is successful.
	 */
	public abstract boolean delete(long id);
	
	/**
	 * Updates an object given as a parameter.
	 * @param o
	 * 		Object to update.
	 * @return
	 * 		True is successful.
	 */
	public abstract boolean update(T o);
	
	/**
	 * Creates an an entry based on the object
	 * given as a parameter.
	 * @param o
	 * 		Object holding the information for the creation.
	 * @return
	 * 		Created object containing its ID if successful.
	 * 		null if failure.
	 */
	public abstract T create(T o);
	
	/**
	 * Finds entries in the database based on the request, and returns them as a {@link Cursor}.
	 * The cursor could then be used to list all the results with a cursor adaptor.
	 * 
	 * The method will invoke the executeRequest method on the
	 * given request.
	 * @param req
	 * 		Request for the db.
	 * @return
	 * 		Cursor yielded by the request.
	 */
	public abstract Cursor findCursor(AbstractRequest req);
}
