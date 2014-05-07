package be.n4utiluss.wysiwyd;

import be.n4utiluss.wysiwyd.database.DatabaseContract;
import be.n4utiluss.wysiwyd.database.DatabaseHelper;
import be.n4utiluss.wysiwyd.database.DatabaseContract.BottleVarietyTable;
import be.n4utiluss.wysiwyd.database.DatabaseContract.VarietyTable;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Fragment displaying all the tools needed to create a new bottle.
 * @author anthonydebruyn
 *
 */
public class NewBottleFragment extends AbstractBottleInfoFragment {
	private long insertedBottleId = -1;

	private NewBottleFragmentCallbacks getLinkedActivity() {
		// Activities containing this fragment must implement its callbacks.
		if (!(getActivity() instanceof NewBottleFragmentCallbacks)) {
			Log.e("NewBottleFragment", "Activity not implementing callbacks");
			throw new IllegalStateException("Activity must implement fragment callbacks.");
		}
		
		return (NewBottleFragmentCallbacks) getActivity();
	}

	@Override
	protected void writeToDB(ContentValues values) {
		DatabaseHelper dbHelper = DatabaseHelper.getInstance(getActivity());
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		this.insertedBottleId = db.insert(DatabaseContract.BottleTable.TABLE_NAME, null, values);
		db.close();
	}
	
	@Override
	protected void writeVarietiesToDB() {
		/*
		 * For each variety:
		 * - Search in DB for the variety.
		 * - If found, get the _id. If not found, add it and get the id.
		 * (This is done with a UNIQUE qualifier on the name column.)
		 * - Suppress all entries in BottleVariety table for the current bottle.
		 * - Put new entries.
		 */
		if (this.insertedBottleId == -1)
			return;
		
		DatabaseHelper dbHelper = DatabaseHelper.getInstance(getActivity());
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		// Insert new varieties if not present, and add relations in the BottleVariety table:
		LinearLayout ll = (LinearLayout) getView().findViewById(R.id.new_bottle_varieties_layout);
		int count = ll.getChildCount();	// There is one title view and the auto complete (so put -2).

		for (int i = AMOUNT_OF_VIEWS_IN_VARIETIES_LINEAR_LAYOUT_TO_PASS; i < count; ++i) {
			TextView tv = (TextView) ll.getChildAt(i);
			String text = tv.getText().toString();
			
			// Insert or get id if already in DB:
			String[] selectionArgs2 = {text};
			Cursor cursor = db.query(VarietyTable.TABLE_NAME, null, VarietyTable.COLUMN_NAME_NAME + " = ?", selectionArgs2, null, null, null, "1");
			long id;
			if (cursor.moveToFirst()) {
				id = cursor.getLong(cursor.getColumnIndex(VarietyTable._ID));
			} else {	// If variety not in DB, add it.
				ContentValues contentValues = new ContentValues();
				contentValues.put(VarietyTable.COLUMN_NAME_NAME, text);
				id = db.insert(VarietyTable.TABLE_NAME, null, contentValues);
			}
						
			// Insert relation in bottle variety table:
			ContentValues varietyValues = new ContentValues();
			varietyValues.put(BottleVarietyTable.COLUMN_NAME_BOTTLE_ID, this.insertedBottleId);
			varietyValues.put(BottleVarietyTable.COLUMN_NAME_VARIETY_ID, id);
			db.insert(BottleVarietyTable.TABLE_NAME, null, varietyValues);
		}

		db.close();
		getLinkedActivity().onNewBottleAdded();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof NewBottleFragmentCallbacks)) {
			Log.e("NewBottleFragment", "Activity not implementing callbacks");
			throw new IllegalStateException("Activity must implement fragment callbacks.");
		}
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
	public interface NewBottleFragmentCallbacks extends AbstractBottleInfoFragmentCallbacks {

		/**
		 * Called when the new bottle has been added in the db.
		 */
		public void onNewBottleAdded();
		/**
		 * Called when the new bottle fragment is being destroyed.
		 * The call occurs during the onDestroy() method of this fragment.
		 */
		public void onNewBottleFragmentDismissed();
	}
}
