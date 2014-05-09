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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;
import be.n4utiluss.wysiwyd.database.DatabaseContract.BottleTable;
import be.n4utiluss.wysiwyd.database.DatabaseHelper;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;

/**
 * Fragment displaying a list of bottles matching certain properties.
 * @author anthonydebruyn
 *
 */
public class BottlesListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>,
																OnItemLongClickListener {

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
	private BottlesListFragmentCallbacks linkedActivity;
	private Menu menu;
	private boolean actionNewActivated = true;
	private boolean actionSearchActivated = true;
	
	
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.list, menu);
		
		// Save the menu object to use it later.
		this.menu = menu;
		
		// Set the new bottle button state.
		setNewBottleButtonActivated(this.actionNewActivated);
		
		// Set the search button state.
		setSearchButtonActivated(this.actionSearchActivated);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		switch (id) {
		case R.id.action_new:
			this.linkedActivity.onNewBottleEvent(-1);
			return true;
			
		case R.id.list_action_search:
			this.linkedActivity.onSearchEvent();
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
				DatabaseHelper.getInstance(getActivity()), 
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
        this.bottleCursorAdapter.changeCursor(cursor);

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
		this.bottleCursorAdapter.changeCursor(null);
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
		
		// Set the long click listener for the new bottle event:
	    getListView().setOnItemLongClickListener(this);
	}
	
	/**
	 * Called when the view is recreated to reselect the bottle that was selected.
	 * Selects the bottle with the passed position.
	 * @param position The saved position, the index of the previously selected bottle.
	 */
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
			throw new IllegalStateException("Activity must implement fragment callbacks.");
		}

		this.linkedActivity = (BottlesListFragmentCallbacks) activity;
	}
	
	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		
		this.linkedActivity.onBottleSelected(id);
	}
	
	@Override
    public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {
        this.linkedActivity.onNewBottleEvent(id);
        return true;
    }
	
	/**
	 * Relaunches the cursor loader to refresh the data.
	 */
	public void refreshList(){
		this.getLoaderManager().restartLoader(0, null, this);
	}

	/**
	 * Sets the state of the "New Bottle" button.
	 * @param status Hide it and disable it when set to false.
	 */
	public void setNewBottleButtonActivated(boolean status) {
		if (this.menu != null){
			this.menu.findItem(R.id.action_new).setVisible(status);
			this.menu.findItem(R.id.action_new).setEnabled(status);
		}
		this.actionNewActivated = status;
	}
	
	/**
	 * Sets the state of the "Search" button.
	 * @param status Hide it and disable it when set to false.
	 */
	public void setSearchButtonActivated(boolean status) {
		if (this.menu != null){
			this.menu.findItem(R.id.list_action_search).setVisible(status);
			this.menu.findItem(R.id.list_action_search).setEnabled(status);
		}
		this.actionSearchActivated = status;
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
		 * Called after the new bottle button is pushed.
		 * @param id The id of the bottle we want the info from to put into the text fields, -1 if none.
		 */
		public void onNewBottleEvent(long id);
		
		public void onSearchEvent();
	}
}
