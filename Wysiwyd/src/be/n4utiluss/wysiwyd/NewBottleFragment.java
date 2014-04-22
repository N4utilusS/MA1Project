package be.n4utiluss.wysiwyd;

import be.n4utiluss.wysiwyd.AbstractBottleInfoFragment.AbstractBottleInfoFragmentCallbacks;
import be.n4utiluss.wysiwyd.database.DatabaseContract;
import be.n4utiluss.wysiwyd.database.DatabaseHelper;
import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class NewBottleFragment extends AbstractBottleInfoFragment {

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
		DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		db.insert(DatabaseContract.BottleTable.TABLE_NAME, null, values);
		db.close();
		dismissFragment();
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
