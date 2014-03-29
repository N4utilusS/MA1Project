package be.n4utiluss.wysiwyd.DAO;

import android.database.sqlite.SQLiteDatabase;

/**
 * Factory for all the DAOs.
 * @author anthonydebruyn
 *
 */
public class DAOFactory {
	
	private SQLiteDatabase db;

	public DAOFactory(SQLiteDatabase db){
		this.db = db;
	}

	public DAO<Bottle> getDAOBottle(){
		return new DAOBottle(db);
	}
}
