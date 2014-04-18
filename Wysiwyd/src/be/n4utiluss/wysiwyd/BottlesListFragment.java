package be.n4utiluss.wysiwyd;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import be.n4utiluss.wysiwyd.database.DatabaseContract.BottleTable;
import be.n4utiluss.wysiwyd.database.DatabaseHelper;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;

public class BottlesListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "be.n4utiluss.wysiwyd.Activated_Position";
	public final static String ACTIVATE_ON_ITEM_CLICK = "be.n4utiluss.wysiwyd.Activate_On_Item_Clicked";

	private BottleCursorAdapter bottleCursorAdapter;
	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int activatedPosition = ListView.INVALID_POSITION;
	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private BottlesListFragmentCallbacks bottleListener;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);	// So the onCreateOptionsMenu method is called, and the actions are set.
		
		// Find the bottles:
		this.bottleCursorAdapter = new BottleCursorAdapter(this.getActivity(), null, 0);
	    this.setListAdapter(bottleCursorAdapter);
	    this.getLoaderManager().initLoader(0, null, this);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.list, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		switch (id) {
		case R.id.action_new:
			this.bottleListener.onNewBottleButtonPushed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		
		long code = this.getArguments().getLong(ScanChoice.BOTTLE_CODE);
		String codeString = Long.toString(code);

		SQLiteCursorLoader cursorLoader = new SQLiteCursorLoader(this.getActivity(),
				new DatabaseHelper(this.getActivity()), 
				"SELECT " + BottleTable._ID + ", " + BottleTable.COLUMN_NAME_NAME + ", " + BottleTable.COLUMN_NAME_VINTAGE + ", " + BottleTable.COLUMN_NAME_MARK + ", " + BottleTable.COLUMN_NAME_QUANTITY +
				" FROM " + BottleTable.TABLE_NAME +
				" WHERE " + BottleTable.COLUMN_NAME_CODE + " = ?", 
				new String[] { codeString });
		return cursorLoader;
	}


	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        this.bottleCursorAdapter.swapCursor(cursor);

        // The list should now be shown.
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
		
	}


	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
		this.bottleCursorAdapter.swapCursor(null);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Set the activation mode/choice mode.
		Bundle arguments = this.getArguments();
		boolean activate = false;
		if (arguments.containsKey(ACTIVATE_ON_ITEM_CLICK))
			activate = arguments.getBoolean(ACTIVATE_ON_ITEM_CLICK);
		this.setActivateOnItemClick(activate);
		
		// Restore the previously serialized activated item position.
		if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
		}
	}
	
	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(activatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		activatedPosition = position;
	}
	
	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick  ? ListView.CHOICE_MODE_SINGLE
									: ListView.CHOICE_MODE_NONE);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (activatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, activatedPosition);
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof BottlesListFragmentCallbacks)) {
			throw new IllegalStateException("Activity must implement fragment's callbacks.");
		}

		this.bottleListener = (BottlesListFragmentCallbacks) activity;
	}
	
	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		
		this.bottleListener.onBottleSelected(id);
	}
	
	/**
	 * Relaunches the cursor loader to refresh the data.
	 */
	public void refreshList(){
		this.getLoaderManager().restartLoader(0, null, this);
	}
	
	
	
	/**
	 * The interface that must be implemented by the connected activity, 
	 * to allow this fragment to communicate with it.
	 * @author anthonydebruyn
	 *
	 */
	public interface BottlesListFragmentCallbacks {
		/**
		 * Called on the listener when a bottle is selected,
		 * passing the id of the bottle.
		 * @param id The id of the bottle.
		 */
		public void onBottleSelected(long id);
		/**
		 * Called when the new bottle button is pushed.
		 */
		public void onNewBottleButtonPushed();
	}
}
