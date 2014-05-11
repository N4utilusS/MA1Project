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
import be.n4utiluss.wysiwyd.database.DatabaseContract;
import be.n4utiluss.wysiwyd.database.DatabaseContract.BottleTable;
import be.n4utiluss.wysiwyd.database.DatabaseContract.BottleVarietyTable;
import be.n4utiluss.wysiwyd.database.DatabaseContract.VarietyTable;
import be.n4utiluss.wysiwyd.database.DatabaseHelper;
import be.n4utiluss.wysiwyd.search.SearchFragment;

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
	private final static String SORT_TYPE = "be.n4utiluss.wysiwyd.sort_type";
	
	// Sort types:
	public final static int NONE = 0;
	public final static int MARK_DESC = 1;
	public final static int MARK_ASC = 2;
	public final static int VINTAGE_DESC = 3;
	public final static int VINTAGE_ASC = 4;
	public final static int QUANTITY_DESC = 5;
	public final static int QUANTITY_ASC = 6;
	public final static int NAME_DESC = 7;
	public final static int NAME_ASC = 8;
		
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
	private Bundle searchInfo;
	private int sortType = NONE;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);	// So the onCreateOptionsMenu method is called, and the actions are set.
		
		// Find the bottles:
		this.bottleCursorAdapter = new BottleCursorAdapter(this.getActivity(), null, 0);
	    this.setListAdapter(bottleCursorAdapter);
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
			
		case R.id.list_action_sort_mark_desc:
			this.sortType = MARK_DESC;
			this.refreshList();
			return true;
			
		case R.id.list_action_sort_mark_asc:
			this.sortType = MARK_ASC;
			this.refreshList();
			return true;
			
		case R.id.list_action_sort_vintage_desc:
			this.sortType = VINTAGE_DESC;
			this.refreshList();
			return true;
			
		case R.id.list_action_sort_vintage_asc:
			this.sortType = VINTAGE_ASC;
			this.refreshList();
			return true;
			
		case R.id.list_action_sort_quantity_desc:
			this.sortType = QUANTITY_DESC;
			this.refreshList();
			return true;
			
		case R.id.list_action_sort_quantity_asc:
			this.sortType = QUANTITY_ASC;
			this.refreshList();
			return true;
			
		case R.id.list_action_sort_name_desc:
			this.sortType = NAME_DESC;
			this.refreshList();
			return true;
			
		case R.id.list_action_sort_name_asc:
			this.sortType = NAME_ASC;
			this.refreshList();
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}

	@Override
	public Loader<Cursor> onCreateLoader(int idLoader, Bundle bundle) {

		// Construction of the query, based on the provided search information:
		StringBuilder query = new StringBuilder(100);
		query.append(DatabaseContract.SELECT).append(BottleTable._ID).append(DatabaseContract.COMMA_SEP)
		.append(BottleTable.COLUMN_NAME_NAME).append(DatabaseContract.COMMA_SEP)
		.append(BottleTable.COLUMN_NAME_VINTAGE).append(DatabaseContract.COMMA_SEP)
		.append(BottleTable.COLUMN_NAME_MARK).append(DatabaseContract.COMMA_SEP)
		.append(BottleTable.COLUMN_NAME_QUANTITY)
		.append(DatabaseContract.FROM).append(BottleTable.TABLE_NAME)
		.append(DatabaseContract.WHERE).append("1=1");

		if (this.searchInfo != null) {

			if (this.searchInfo.containsKey(SearchFragment.APPELLATION))
				query.append(DatabaseContract.AND).append(BottleTable.COLUMN_NAME_APPELLATION).append(DatabaseContract.LIKE)
				.append(DatabaseContract.STRING_DELIMITER).append(DatabaseContract.ANY_STRING_WILDCARD)
				.append(this.searchInfo.getString(SearchFragment.APPELLATION))
				.append(DatabaseContract.ANY_STRING_WILDCARD).append(DatabaseContract.STRING_DELIMITER);

			if (this.searchInfo.containsKey(SearchFragment.NAME))
				query.append(DatabaseContract.AND).append(BottleTable.COLUMN_NAME_NAME).append(DatabaseContract.LIKE)
				.append(DatabaseContract.STRING_DELIMITER).append(DatabaseContract.ANY_STRING_WILDCARD)
				.append(this.searchInfo.getString(SearchFragment.NAME))
				.append(DatabaseContract.ANY_STRING_WILDCARD).append(DatabaseContract.STRING_DELIMITER);

			if (this.searchInfo.containsKey(SearchFragment.REGION))
				query.append(DatabaseContract.AND).append(BottleTable.COLUMN_NAME_REGION).append(DatabaseContract.LIKE)
				.append(DatabaseContract.STRING_DELIMITER).append(DatabaseContract.ANY_STRING_WILDCARD)
				.append(this.searchInfo.getString(SearchFragment.REGION))
				.append(DatabaseContract.ANY_STRING_WILDCARD).append(DatabaseContract.STRING_DELIMITER);

			if (this.searchInfo.containsKey(SearchFragment.VINTAGE_FROM))
				query.append(DatabaseContract.AND).append(BottleTable.COLUMN_NAME_VINTAGE).append(DatabaseContract.BEQ)
				.append(this.searchInfo.getInt(SearchFragment.VINTAGE_FROM));

			if (this.searchInfo.containsKey(SearchFragment.VINTAGE_TO))
				query.append(DatabaseContract.AND).append(BottleTable.COLUMN_NAME_VINTAGE).append(DatabaseContract.LEQ)
				.append(this.searchInfo.getInt(SearchFragment.VINTAGE_TO));

			if (this.searchInfo.containsKey(SearchFragment.QUANTITY_MIN))
				query.append(DatabaseContract.AND).append(BottleTable.COLUMN_NAME_QUANTITY).append(DatabaseContract.BEQ)
				.append(this.searchInfo.getInt(SearchFragment.QUANTITY_MIN));

			if (this.searchInfo.containsKey(SearchFragment.QUANTITY_MAX))
				query.append(DatabaseContract.AND).append(BottleTable.COLUMN_NAME_QUANTITY).append(DatabaseContract.LEQ)
				.append(this.searchInfo.getInt(SearchFragment.QUANTITY_MAX));

			if (this.searchInfo.containsKey(SearchFragment.PRICE_MIN))
				query.append(DatabaseContract.AND).append(BottleTable.COLUMN_NAME_PRICE).append(DatabaseContract.BEQ)
				.append(this.searchInfo.getFloat(SearchFragment.PRICE_MIN));

			if (this.searchInfo.containsKey(SearchFragment.PRICE_MAX))
				query.append(DatabaseContract.AND).append(BottleTable.COLUMN_NAME_PRICE).append(DatabaseContract.LEQ)
				.append(this.searchInfo.getFloat(SearchFragment.PRICE_MAX));

			if (this.searchInfo.containsKey(SearchFragment.RATING_MIN))
				query.append(DatabaseContract.AND).append(BottleTable.COLUMN_NAME_MARK).append(DatabaseContract.BEQ)
				.append(this.searchInfo.getInt(SearchFragment.RATING_MIN));

			if (this.searchInfo.containsKey(SearchFragment.RATING_MAX))
				query.append(DatabaseContract.AND).append(BottleTable.COLUMN_NAME_MARK).append(DatabaseContract.LEQ)
				.append(this.searchInfo.getInt(SearchFragment.RATING_MAX));

			if (this.searchInfo.containsKey(SearchFragment.ADD_DATE_FROM))
				query.append(DatabaseContract.AND).append(BottleTable.COLUMN_NAME_ADD_DATE).append(DatabaseContract.BEQ)
				.append(DatabaseContract.STRING_DELIMITER).append(this.searchInfo.getString(SearchFragment.ADD_DATE_FROM)).append(DatabaseContract.STRING_DELIMITER);

			if (this.searchInfo.containsKey(SearchFragment.ADD_DATE_TO))
				query.append(DatabaseContract.AND).append(BottleTable.COLUMN_NAME_ADD_DATE).append(DatabaseContract.LEQ)
				.append(DatabaseContract.STRING_DELIMITER).append(this.searchInfo.getString(SearchFragment.ADD_DATE_TO)).append(DatabaseContract.STRING_DELIMITER);

			if (this.searchInfo.containsKey(SearchFragment.APOGEE_FROM))
				query.append(DatabaseContract.AND).append(BottleTable.COLUMN_NAME_APOGEE).append(DatabaseContract.BEQ)
				.append(DatabaseContract.STRING_DELIMITER).append(this.searchInfo.getString(SearchFragment.APOGEE_FROM)).append(DatabaseContract.STRING_DELIMITER);

			if (this.searchInfo.containsKey(SearchFragment.APOGEE_TO))
				query.append(DatabaseContract.AND).append(BottleTable.COLUMN_NAME_APOGEE).append(DatabaseContract.LEQ)
				.append(DatabaseContract.STRING_DELIMITER).append(this.searchInfo.getString(SearchFragment.APOGEE_TO)).append(DatabaseContract.STRING_DELIMITER);

			if (this.searchInfo.containsKey(SearchFragment.COLOUR))
				query.append(DatabaseContract.AND).append(BottleTable.COLUMN_NAME_COLOUR).append(DatabaseContract.EQ)
				.append(this.searchInfo.getInt(SearchFragment.COLOUR));

			if (this.searchInfo.containsKey(SearchFragment.SUGAR))
				query.append(DatabaseContract.AND).append(BottleTable.COLUMN_NAME_SUGAR).append(DatabaseContract.EQ)
				.append(this.searchInfo.getInt(SearchFragment.SUGAR));

			if (this.searchInfo.containsKey(SearchFragment.EFFERVESCENCE))
				query.append(DatabaseContract.AND).append(BottleTable.COLUMN_NAME_EFFERVESCENCE).append(DatabaseContract.EQ)
				.append(this.searchInfo.getInt(SearchFragment.EFFERVESCENCE));
			
			if (this.searchInfo.containsKey(SearchFragment.CODE))
				query.append(DatabaseContract.AND).append(BottleTable.COLUMN_NAME_CODE).append(DatabaseContract.EQ)
				.append(this.searchInfo.getLong(SearchFragment.CODE));

			if (this.searchInfo.containsKey(SearchFragment.VARIETIES)) {
				query.append(DatabaseContract.AND).append(BottleTable._ID).append(DatabaseContract.IN)
				.append(DatabaseContract.OPENING_PAR)
				.append(DatabaseContract.SELECT).append("bv.").append(BottleVarietyTable.COLUMN_NAME_BOTTLE_ID)
				.append(DatabaseContract.FROM).append(BottleVarietyTable.TABLE_NAME).append(" bv").append(DatabaseContract.COMMA_SEP)
				.append(VarietyTable.TABLE_NAME).append(" v")
				.append(DatabaseContract.WHERE).append("bv.").append(BottleVarietyTable.COLUMN_NAME_VARIETY_ID)
				.append(DatabaseContract.EQ).append("v.").append(VarietyTable._ID)
				.append(DatabaseContract.AND).append("v.").append(VarietyTable.COLUMN_NAME_NAME).append(DatabaseContract.IN).append(DatabaseContract.OPENING_PAR);

				for (String variety : this.searchInfo.getStringArrayList(SearchFragment.VARIETIES))
					query.append(DatabaseContract.STRING_DELIMITER).append(variety).append(DatabaseContract.STRING_DELIMITER).append(DatabaseContract.COMMA_SEP);

				query.delete(query.length()-DatabaseContract.COMMA_SEP.length(), query.length());

				query.append(DatabaseContract.CLOSING_PAR).append(DatabaseContract.CLOSING_PAR);
			}

		}
		
		if (this.sortType != NONE)
			query.append(DatabaseContract.ORDER_BY);
		
		switch (this.sortType) {
		case MARK_DESC:
			query.append(BottleTable.COLUMN_NAME_MARK).append(DatabaseContract.DESC);
			break;
			
		case MARK_ASC:
			query.append(BottleTable.COLUMN_NAME_MARK).append(DatabaseContract.ASC);
			break;
			
		case VINTAGE_DESC:
			query.append(BottleTable.COLUMN_NAME_VINTAGE).append(DatabaseContract.DESC);
			break;
			
		case VINTAGE_ASC:
			query.append(BottleTable.COLUMN_NAME_VINTAGE).append(DatabaseContract.ASC);
			break;
			
		case QUANTITY_DESC:
			query.append(BottleTable.COLUMN_NAME_QUANTITY).append(DatabaseContract.DESC);
			break;
			
		case QUANTITY_ASC:
			query.append(BottleTable.COLUMN_NAME_QUANTITY).append(DatabaseContract.ASC);
			break;
			
		case NAME_DESC:
			query.append(BottleTable.COLUMN_NAME_NAME).append(DatabaseContract.DESC);
			break;
			
		case NAME_ASC:
			query.append(BottleTable.COLUMN_NAME_NAME).append(DatabaseContract.ASC);
			break;
		}

		SQLiteCursorLoader cursorLoader = new SQLiteCursorLoader(this.getActivity(),
				DatabaseHelper.getInstance(getActivity()), 
				query.toString(), 
				null);
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

		if (savedInstanceState != null && savedInstanceState.containsKey(SORT_TYPE))
			this.sortType = savedInstanceState.getInt(SORT_TYPE);
		
		getLoaderManager().initLoader(0, null, this);
		
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
		
		outState.putInt(SORT_TYPE, this.sortType);
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
	
	public void setSearchBundle(Bundle info) {
		this.searchInfo = info;
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
