package be.n4utiluss.wysiwyd;

import be.n4utiluss.wysiwyd.database.DatabaseContract;
import be.n4utiluss.wysiwyd.database.DatabaseHelper;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class ModifyBottleFragment extends AbstractBottleInfoFragment {

	@Override
	protected void writeToDB(ContentValues values) {
		DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		String selection = DatabaseContract.BottleTable._ID + " = ?";
		String[] selectionArgs = { Long.toString(getArguments().getLong(DatabaseContract.BottleTable._ID)) };

		db.update(DatabaseContract.BottleTable.TABLE_NAME, values, selection, selectionArgs);
		db.close();
		dismissFragment();
		getLinkedActivity().onNewBottleAdded();
	}

	@Override
	public void onDestroy(){
		super.onDestroy();

		// We notify the activity that this fragment is being stopped with a callback method.
		getLinkedActivity().onNewBottleFragmentDismissed();
	}

	/**
	 * Interface that must be implemented by the linked activity, to be able to communicate with this fragment.
	 * @author anthonydebruyn
	 *
	 */
	public interface ModifyBottleFragmentCallbacks {

		/**
		 * Called when the bottle has been modified in the db.
		 */
		public void onBottleModified();
		/**
		 * Called when the modify bottle fragment is being destroyed.
		 * The call occurs during the onDestroy() method of this fragment.
		 */
		public void onModifyBottleFragmentDismissed();
	}
}
