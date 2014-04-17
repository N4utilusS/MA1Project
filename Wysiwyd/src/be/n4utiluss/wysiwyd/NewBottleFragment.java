package be.n4utiluss.wysiwyd;

import be.n4utiluss.wysiwyd.database.DatabaseContract;
import be.n4utiluss.wysiwyd.database.DatabaseHelper;
import android.app.Fragment;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RatingBar;
import android.widget.TextView;

public class NewBottleFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);	// So the onCreateOptionsMenu method is called, and the actions are set.
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_new_bottle, container, false);

		return rootView;
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
			showBottlesListFragment();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void showBottlesListFragment() {
		this.getFragmentManager().popBackStack();
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
		TextView colour = (TextView) getView().findViewById(R.id.new_bottle_colour);
		TextView sugar = (TextView) getView().findViewById(R.id.new_bottle_sugar);
		TextView effervescence = (TextView) getView().findViewById(R.id.new_bottle_effervescence);
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
		
		String colourStringValue = colour.getText().toString();
		if (!TextUtils.isEmpty(colourStringValue)) {
			try {
				values.put(DatabaseContract.BottleTable.COLUMN_NAME_COLOUR, Integer.valueOf(colourStringValue));
			}
			catch (NumberFormatException e) {
				
			}
		} else {
			// TODO Tell user error.
		}
		
		String sugarStringValue = sugar.getText().toString();
		if (!TextUtils.isEmpty(sugarStringValue)) {
			try {
				values.put(DatabaseContract.BottleTable.COLUMN_NAME_SUGAR, Integer.valueOf(sugarStringValue));
			}
			catch (NumberFormatException e) {
				
			}
		} else {
			// TODO Tell user error.
		}
		
		String effervescenceStringValue = effervescence.getText().toString();
		if (!TextUtils.isEmpty(effervescenceStringValue)) {
			try {
				values.put(DatabaseContract.BottleTable.COLUMN_NAME_EFFERVESCENCE, Integer.valueOf(effervescenceStringValue));
			}
			catch (NumberFormatException e) {
				
			}
		} else {
			// TODO Tell user error.
		}
		
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
}
