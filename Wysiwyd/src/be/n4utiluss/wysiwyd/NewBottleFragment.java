package be.n4utiluss.wysiwyd;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import be.n4utiluss.wysiwyd.database.DatabaseContract;
import be.n4utiluss.wysiwyd.database.DatabaseHelper;
import be.n4utiluss.wysiwyd.database.DatabaseContract.BottleTable;
import be.n4utiluss.wysiwyd.database.DatabaseContract.BottleVarietyTable;
import be.n4utiluss.wysiwyd.database.DatabaseContract.VarietyTable;

public class NewBottleFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final int MAIN_INFO_LOADER = 0;
	private static final int VARIETY_LOADER = 1;

	private NewBottleFragmentCallbacks linkedActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);	// So the onCreateOptionsMenu method is called, and the actions are set.
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_new_bottle, container, false);

		// (Re)start the loaders here, since this method is the first one called after we get back from the new bottle fragment, 
		// after we popped the previous state from the stack.

		Bundle arguments = getArguments();
		if (arguments != null && arguments.containsKey(BottleTable._ID)) {
			getLoaderManager().initLoader(MAIN_INFO_LOADER, null, this);
			getLoaderManager().initLoader(VARIETY_LOADER, null, this);
		}

		return rootView;
	}



	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof NewBottleFragmentCallbacks)) {
			throw new IllegalStateException("Activity must implement fragment's callbacks.");
		}

		this.linkedActivity = (NewBottleFragmentCallbacks) activity;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.new_bottle, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		switch (id) {
		case R.id.action_add_new_bottle:
			addBottle();
			dismissFragment();
			this.linkedActivity.onNewBottleAdded();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void dismissFragment() {
		this.getFragmentManager().popBackStack();
	}

	@Override
	public void onStop(){
		super.onStop();

		// We notify the activity that this fragment is being stopped with a callback method.
		this.linkedActivity.onNewBottleFragmentDismissed();
	}

	private void addBottle() {
		// TODO Add checkings on the data added.

		TextView appellation = (TextView) getView().findViewById(R.id.new_bottle_appellation);
		TextView name = (TextView) getView().findViewById(R.id.new_bottle_name);
		TextView vintage = (TextView) getView().findViewById(R.id.new_bottle_vintage);
		TextView region = (TextView) getView().findViewById(R.id.new_bottle_region);
		TextView quantity = (TextView) getView().findViewById(R.id.new_bottle_quantity);
		TextView price = (TextView) getView().findViewById(R.id.new_bottle_price);
		RatingBar ratingBar = (RatingBar) getView().findViewById(R.id.new_bottle_mark);
		Spinner colour = (Spinner) getView().findViewById(R.id.new_bottle_colour);
		Spinner sugar = (Spinner) getView().findViewById(R.id.new_bottle_sugar);
		Spinner effervescence = (Spinner) getView().findViewById(R.id.new_bottle_effervescence);
		DatePicker addDate = (DatePicker) getView().findViewById(R.id.new_bottle_addDate);
		DatePicker apogee = (DatePicker) getView().findViewById(R.id.new_bottle_apogee);
		TextView location = (TextView) getView().findViewById(R.id.new_bottle_location);
		TextView note = (TextView) getView().findViewById(R.id.new_bottle_note);
		TextView code = (TextView) getView().findViewById(R.id.new_bottle_code);

		ContentValues values = new ContentValues();

		String appellationValue = appellation.getText().toString();
		if (!TextUtils.isEmpty(appellationValue))
			values.put(DatabaseContract.BottleTable.COLUMN_NAME_APPELLATION, appellationValue);

		String nameValue = name.getText().toString();
		if (!TextUtils.isEmpty(nameValue))
			values.put(DatabaseContract.BottleTable.COLUMN_NAME_NAME, nameValue);

		String vintageStringValue = vintage.getText().toString();
		if (!TextUtils.isEmpty(vintageStringValue)) {
			try {
				values.put(DatabaseContract.BottleTable.COLUMN_NAME_VINTAGE, Integer.valueOf(vintageStringValue));
			}
			catch (NumberFormatException e) {

			}
		} else {
			// TODO Tell user error.
		}

		String regionValue = region.getText().toString();
		if (!TextUtils.isEmpty(regionValue))
			values.put(DatabaseContract.BottleTable.COLUMN_NAME_REGION, regionValue);

		String quantityStringValue = quantity.getText().toString();
		if (!TextUtils.isEmpty(quantityStringValue)) {
			try {
				values.put(DatabaseContract.BottleTable.COLUMN_NAME_QUANTITY, Integer.valueOf(quantityStringValue));
			}
			catch (NumberFormatException e) {

			}
		} else {
			// TODO Tell user error.
		}

		String priceStringValue = price.getText().toString();
		if (!TextUtils.isEmpty(priceStringValue)) {
			try {
				values.put(DatabaseContract.BottleTable.COLUMN_NAME_PRICE, Float.valueOf(priceStringValue));
			}
			catch (NumberFormatException e) {

			}
		} else {
			// TODO Tell user error.
		}

		if (ratingBar.getRating() > 0)	// The minimum rating value is 1 star. If 0 is the value, we consider that no mark is given.
			values.put(DatabaseContract.BottleTable.COLUMN_NAME_MARK, (int) ratingBar.getRating());

		int colourValue = colour.getSelectedItemPosition();
		values.put(DatabaseContract.BottleTable.COLUMN_NAME_COLOUR, colourValue);
		
		int sugarValue = sugar.getSelectedItemPosition();
		values.put(DatabaseContract.BottleTable.COLUMN_NAME_SUGAR, sugarValue);
		
		int effervescenceValue = effervescence.getSelectedItemPosition();
		values.put(DatabaseContract.BottleTable.COLUMN_NAME_EFFERVESCENCE, effervescenceValue);

		// TODO Add the varieties.

		String addDateValue = addDate.getYear() + "-" + addDate.getMonth() + "-" + addDate.getDayOfMonth();
		values.put(DatabaseContract.BottleTable.COLUMN_NAME_ADD_DATE, addDateValue);

		String apogeeValue = apogee.getYear() + "-" + apogee.getMonth() + "-" + apogee.getDayOfMonth();
		values.put(DatabaseContract.BottleTable.COLUMN_NAME_APOGEE, apogeeValue);

		String locationValue = location.getText().toString();
		if (!TextUtils.isEmpty(locationValue))
			values.put(DatabaseContract.BottleTable.COLUMN_NAME_LOCATION, locationValue);

		String noteValue = note.getText().toString();
		if (!TextUtils.isEmpty(noteValue))
			values.put(DatabaseContract.BottleTable.COLUMN_NAME_NOTE, noteValue);

		String codeStringValue = code.getText().toString();
		if (!TextUtils.isEmpty(codeStringValue)) {
			try {
				values.put(DatabaseContract.BottleTable.COLUMN_NAME_CODE, Integer.valueOf(codeStringValue));
			}
			catch (NumberFormatException e) {

			}
		} else {
			// TODO Tell user error.
		}

		// Write to DB.
		DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		db.insert(DatabaseContract.BottleTable.TABLE_NAME, null, values);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int idLoader, Bundle bundle) {
		
		long id = getArguments().getLong(BottleTable._ID);
		String idString = Long.toString(id);
		SQLiteCursorLoader cursorLoader = null;

		switch (idLoader){
		case MAIN_INFO_LOADER:
			cursorLoader = new SQLiteCursorLoader(this.getActivity(),
					new DatabaseHelper(this.getActivity()), 
					"SELECT * " +
							" FROM " + BottleTable.TABLE_NAME +
							" WHERE " + BottleTable._ID + " = ?", 
							new String[] { idString });

			break;
		case VARIETY_LOADER:
			cursorLoader = new SQLiteCursorLoader(this.getActivity(),
					new DatabaseHelper(this.getActivity()), 
					"SELECT DISTINCT v." + VarietyTable.COLUMN_NAME_NAME +
					" FROM " + BottleVarietyTable.TABLE_NAME + " bv, " + VarietyTable.TABLE_NAME + " v " +
					" WHERE bv." + BottleVarietyTable._ID + " = ?" +
					" AND bv." + BottleVarietyTable.COLUMN_NAME_VARIETY_ID + " = v." + VarietyTable._ID, 
					new String[] { idString });

			break;
		default:
		}


		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		switch (loader.getId()){
		case MAIN_INFO_LOADER:
			if (cursor.moveToFirst()) {

				EditText appellation = (EditText) getView().findViewById(R.id.new_bottle_appellation);
				EditText name = (EditText) getView().findViewById(R.id.new_bottle_name);
				EditText vintage = (EditText) getView().findViewById(R.id.new_bottle_vintage);
				EditText region = (EditText) getView().findViewById(R.id.new_bottle_region);
				EditText quantity = (EditText) getView().findViewById(R.id.new_bottle_quantity);
				EditText price = (EditText) getView().findViewById(R.id.new_bottle_price);
				RatingBar ratingBar = (RatingBar) getView().findViewById(R.id.new_bottle_mark);
				Spinner colour = (Spinner) getView().findViewById(R.id.new_bottle_colour);
				Spinner sugar = (Spinner) getView().findViewById(R.id.new_bottle_sugar);
				Spinner effervescence = (Spinner) getView().findViewById(R.id.new_bottle_effervescence);
				DatePicker addDate = (DatePicker) getView().findViewById(R.id.new_bottle_addDate);
				DatePicker apogee = (DatePicker) getView().findViewById(R.id.new_bottle_apogee);
				EditText location = (EditText) getView().findViewById(R.id.new_bottle_location);
				EditText note = (EditText) getView().findViewById(R.id.new_bottle_note);
				EditText code = (EditText) getView().findViewById(R.id.new_bottle_code);

				appellation.setText(cursor.getString(cursor.getColumnIndex(BottleTable.COLUMN_NAME_APPELLATION)));
				name.setText(cursor.getString(cursor.getColumnIndex(BottleTable.COLUMN_NAME_NAME)));
				vintage.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex(BottleTable.COLUMN_NAME_VINTAGE))));
				region.setText(cursor.getString(cursor.getColumnIndex(BottleTable.COLUMN_NAME_REGION)));
				quantity.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex(BottleTable.COLUMN_NAME_QUANTITY))));
				price.setText(Float.toString(cursor.getFloat(cursor.getColumnIndex(BottleTable.COLUMN_NAME_PRICE))));
				ratingBar.setRating(cursor.getInt(cursor.getColumnIndex(BottleTable.COLUMN_NAME_MARK)));

				int colourValue = cursor.getInt(cursor.getColumnIndex(BottleTable.COLUMN_NAME_COLOUR));
				colour.setSelection(colourValue);

				int sugarValue = cursor.getInt(cursor.getColumnIndex(BottleTable.COLUMN_NAME_SUGAR));
				sugar.setSelection(sugarValue);

				int effervescenceValue = cursor.getInt(cursor.getColumnIndex(BottleTable.COLUMN_NAME_EFFERVESCENCE));
				effervescence.setSelection(effervescenceValue);
				
				// Add date setting:
				String addDateString = cursor.getString(cursor.getColumnIndex(BottleTable.COLUMN_NAME_ADD_DATE));
				String[] addDateArray = addDateString.split("-");
				
				try {
					int year = Integer.parseInt(addDateArray[0]);
					int month = Integer.parseInt(addDateArray[1]);
					int day = Integer.parseInt(addDateArray[2]);
					
					addDate.init(year, month, day, null);
				} catch (NumberFormatException e) {
					e.printStackTrace();
					Log.e("NumberFormatException", "Number provided is not correctly formatted (add date)");
				}
				
				// Apogee setting;
				String apogeeString = cursor.getString(cursor.getColumnIndex(BottleTable.COLUMN_NAME_APOGEE));
				String[] apogeeArray = apogeeString.split("-");
				
				try {
					int year = Integer.parseInt(apogeeArray[0]);
					int month = Integer.parseInt(apogeeArray[1]);
					int day = Integer.parseInt(apogeeArray[2]);
					
					apogee.init(year, month, day, null);
				} catch (NumberFormatException e) {
					e.printStackTrace();
					Log.e("NumberFormatException", "Number provided is not correctly formatted");
				}
								
				location.setText(cursor.getString(cursor.getColumnIndex(BottleTable.COLUMN_NAME_LOCATION)));
				note.setText(cursor.getString(cursor.getColumnIndex(BottleTable.COLUMN_NAME_NOTE)));
				code.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex(BottleTable.COLUMN_NAME_CODE))));
			}
			break;

		case VARIETY_LOADER:
			if (this.getView() == null)
				Log.e("Details", "Null POINTER!!!!");
			LinearLayout ll = (LinearLayout) getView().findViewById(R.id.details_varieties_layout);
			cursor.moveToPosition(-1);
			while (cursor.moveToNext()) {
				TextView tv = new TextView(getActivity());
				tv.setText(cursor.getString(cursor.getColumnIndex(VarietyTable.COLUMN_NAME_NAME)));

				ll.addView(tv);
			}
			break;
		default:
		}

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {

	}

	/**
	 * Interface that must be implemented by the linked activity, to be able to communicate with this fragment.
	 * @author anthonydebruyn
	 *
	 */
	public interface NewBottleFragmentCallbacks {

		/**
		 * Called when the new bottle has been added in the db.
		 */
		public void onNewBottleAdded();
		/**
		 * Called when the new bottle fragment is being stopped.
		 * The call occurs during the onStop() method of this fragment.
		 */
		public void onNewBottleFragmentDismissed();
	}
}
