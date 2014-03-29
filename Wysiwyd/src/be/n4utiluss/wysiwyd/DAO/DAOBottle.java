package be.n4utiluss.wysiwyd.DAO;

import be.n4utiluss.wysiwyd.DAO.requests.AbstractRequest;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DAOBottle extends DAO<Bottle> {

	DAOBottle(SQLiteDatabase db) {
		super(db);
	}

	@Override
	public boolean delete(long id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Bottle o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Bottle create(Bottle o) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cursor findCursor(AbstractRequest req) {
		return req.executeRequest(this.db);
	}

}
