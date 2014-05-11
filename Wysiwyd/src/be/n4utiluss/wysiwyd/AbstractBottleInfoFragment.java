package be.n4utiluss.wysiwyd;

import java.io.File;
import java.util.ArrayList;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import be.n4utiluss.wysiwyd.database.DatabaseContract;
import be.n4utiluss.wysiwyd.database.DatabaseContract.BottleTable;
import be.n4utiluss.wysiwyd.database.DatabaseContract.BottleVarietyTable;
import be.n4utiluss.wysiwyd.database.DatabaseContract.VarietyTable;
import be.n4utiluss.wysiwyd.database.DatabaseHelper;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;

/**
 * Abstract class representing a fragment allowing to modify the properties of a bottle.
 * It contains all the attributes and methods that inheriting classes have in common, such as the text fields.
 * Two inheriting classes are the {@link ModifyBottleFragment} and the {@link NewBottleFragment}.
 * @author anthonydebruyn
 *
 */
public abstract class AbstractBottleInfoFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnLongClickListener, OnClickListener {


	private static final int MAIN_INFO_LOADER = 0;
	private static final int BOTTLE_VARIETIES_LOADER = 1;
	private static final int ALL_VARIETIES_LOADER = 2;
	private String photoPath = null;
	public static final int AMOUNT_OF_VIEWS_IN_VARIETIES_LINEAR_LAYOUT_TO_PASS = 2;
	
	private static final String VARIETIES_KEY = "be.n4utiluss.wysiwyd.varieties";
	
	/**
	 * Returns the activity attached to this fragment.
	 * The activity must implement the {@link AbstractBottleInfoFragmentCallbacks} interface.
	 * This interface is used by the fragment to communicate with the activity.
	 * @return The connected activity.
	 */
	private AbstractBottleInfoFragmentCallbacks getLinkedActivity() {
		// Activities containing this fragment must implement its callbacks.
		if (!(getActivity() instanceof AbstractBottleInfoFragmentCallbacks)) {
			Log.e("AbstractBottleInfoFragment", "Activity not implementing callbacks");
			throw new IllegalStateException("Activity must implement fragment callbacks.");
		}
		
		return (AbstractBottleInfoFragmentCallbacks) getActivity();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);	// So the onCreateOptionsMenu method is called, and the actions are set.
	}

	/**
	 * Takes a bundle with all the saved information, and restores it.
	 * Used when a rotation of the screen occurs.
	 * @param savedInstanceState The saved information.
	 */
	private void resetInfo(Bundle savedInstanceState) {
		/*
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
		
		appellation.setText(savedInstanceState.getString(DatabaseContract.BottleTable.COLUMN_NAME_APPELLATION));
		name.setText(savedInstanceState.getString(DatabaseContract.BottleTable.COLUMN_NAME_NAME));
		vintage.setText(savedInstanceState.getString(DatabaseContract.BottleTable.COLUMN_NAME_VINTAGE));
		region.setText(savedInstanceState.getString(DatabaseContract.BottleTable.COLUMN_NAME_REGION));
		quantity.setText(savedInstanceState.getString(DatabaseContract.BottleTable.COLUMN_NAME_QUANTITY));
		price.setText(savedInstanceState.getString(DatabaseContract.BottleTable.COLUMN_NAME_PRICE));
		ratingBar.setRating(savedInstanceState.getInt(DatabaseContract.BottleTable.COLUMN_NAME_MARK));
		colour.setSelection(savedInstanceState.getInt(DatabaseContract.BottleTable.COLUMN_NAME_COLOUR));
		sugar.setSelection(savedInstanceState.getInt(DatabaseContract.BottleTable.COLUMN_NAME_SUGAR));
		effervescence.setSelection(savedInstanceState.getInt(DatabaseContract.BottleTable.COLUMN_NAME_EFFERVESCENCE));
		
		String addDateString = savedInstanceState.getString(DatabaseContract.BottleTable.COLUMN_NAME_ADD_DATE);
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
		
		String apogeeString = savedInstanceState.getString(DatabaseContract.BottleTable.COLUMN_NAME_APOGEE);
		String[] apogeeArray = apogeeString.split("-");
		
		try {
			int year = Integer.parseInt(apogeeArray[0]);
			int month = Integer.parseInt(apogeeArray[1]);
			int day = Integer.parseInt(apogeeArray[2]);
			
			apogee.init(year, month, day, null);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			Log.e("NumberFormatException", "Number provided is not correctly formatted (apogee date)");
		}
		
		location.setText(savedInstanceState.getString(DatabaseContract.BottleTable.COLUMN_NAME_LOCATION));
		note.setText(savedInstanceState.getString(DatabaseContract.BottleTable.COLUMN_NAME_NOTE));
		code.setText(savedInstanceState.getString(DatabaseContract.BottleTable.COLUMN_NAME_CODE));
		*/
		if (savedInstanceState.containsKey(DatabaseContract.BottleTable.COLUMN_NAME_IMAGE)) {
			//setPicture(savedInstanceState.getString(DatabaseContract.BottleTable.COLUMN_NAME_IMAGE));
			this.photoPath = savedInstanceState.getString(DatabaseContract.BottleTable.COLUMN_NAME_IMAGE);
		} else {
			this.setBackground();
		}
		// The varieties:
		LinearLayout ll = (LinearLayout) getView().findViewById(R.id.new_bottle_varieties_layout);
		ArrayList<String> varieties = savedInstanceState.getStringArrayList(VARIETIES_KEY);
		for (String variety: varieties)
			addVarietyToLayout(variety, ll);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_abstract_bottle_info, container, false);

		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// (Re)start the loaders here.
		getLoaderManager().restartLoader(ALL_VARIETIES_LOADER, null, this);

		if (savedInstanceState == null) {
			Bundle arguments = getArguments();
			if (arguments != null && arguments.containsKey(BottleTable._ID) && (arguments.getLong(BottleTable._ID) > 0)) {
				getLoaderManager().restartLoader(MAIN_INFO_LOADER, null, this);
				getLoaderManager().restartLoader(BOTTLE_VARIETIES_LOADER, null, this);
			} else if (arguments.containsKey(ScanChoice.BOTTLE_CODE)){
				// Pre-fill with the scanned code:
				EditText code = (EditText) getView().findViewById(R.id.new_bottle_code);
				code.setText(Long.toString(arguments.getLong(ScanChoice.BOTTLE_CODE)));
			}
		} else {
			resetInfo(savedInstanceState);
		}
		
		
		Button addVarietyButton = (Button) getView().findViewById(R.id.new_bottle_add_variety_button);
		addVarietyButton.setOnClickListener(this);
		
		getLinkedActivity().onAbstractBottleInfoFragmentStarted();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.abstract_bottle_info, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		switch (id) {
		case R.id.action_execute:
			try {
				ContentValues values = getValues();
				writeToDB(values);
				writeVarietiesToDB();
				dismissFragment();
			} catch (Exception e) {
				Context context = getActivity().getApplicationContext();
				CharSequence text = e.getMessage();
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			}
			return true;
		
		case R.id.action_abstract_bottle_info_picture:
			getLinkedActivity().onTakePicture();
			return true;
			
		case R.id.action_abstract_bottle_info_image:
			toggleDetails();
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Add the received information about a bottle to the SQLite DB.
	 * The parameter passed contains all the information except the varieties.
	 * @param values The information about the bottle except the varieties.
	 */
	abstract protected void writeToDB(ContentValues values);
	
	/**
	 * Get the varieties information in the view of the fragment and add it to the SQLite DB.
	 */
	abstract protected void writeVarietiesToDB();

	/**
	 * Dismiss the fragment by popping it out of the back stack.
	 */
	protected void dismissFragment() {
		this.getFragmentManager().popBackStack();
	}

	/**
	 * Gets all the information about the bottle and puts it in a {@link ContentValues} object to give it later to the SQLite DB.
	 * @return The information.
	 * @throws Exception 
	 */
	private ContentValues getValues() throws Exception {

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
		else
			throw new Exception("Appellation is mandatory!");

		String nameValue = name.getText().toString();
		if (!TextUtils.isEmpty(nameValue))
			values.put(DatabaseContract.BottleTable.COLUMN_NAME_NAME, nameValue);
		else
			throw new Exception("Name is mandatory!");

		String vintageStringValue = vintage.getText().toString();
		if (!TextUtils.isEmpty(vintageStringValue)) {
			try {
				values.put(DatabaseContract.BottleTable.COLUMN_NAME_VINTAGE, Math.abs(Integer.valueOf(vintageStringValue)));
			}
			catch (NumberFormatException e) {
				throw new Exception("Vintage not recognized!");
			}
		} else if (effervescence.getSelectedItemPosition() > 0) {
			values.putNull(DatabaseContract.BottleTable.COLUMN_NAME_VINTAGE);
		} else {
			throw new Exception("Vintage is mandatory for not sparkling wines!");
		}

		String regionValue = region.getText().toString();
		if (!TextUtils.isEmpty(regionValue))
			values.put(DatabaseContract.BottleTable.COLUMN_NAME_REGION, regionValue);
		else
			values.putNull(DatabaseContract.BottleTable.COLUMN_NAME_REGION);

		String quantityStringValue = quantity.getText().toString();
		if (!TextUtils.isEmpty(quantityStringValue)) {
			try {
				values.put(DatabaseContract.BottleTable.COLUMN_NAME_QUANTITY, Math.abs(Integer.valueOf(quantityStringValue)));
			}
			catch (NumberFormatException e) {
				throw new Exception("Quantity not recognized!");
			}
		} else {
			throw new Exception("Quantity field empty!");
		}

		String priceStringValue = price.getText().toString();
		if (!TextUtils.isEmpty(priceStringValue)) {
			try {
				values.put(DatabaseContract.BottleTable.COLUMN_NAME_PRICE, Math.abs(Float.valueOf(priceStringValue)));
			}
			catch (NumberFormatException e) {
				
				throw new Exception("Price not recognized!");
			}
		} else {
			values.putNull(DatabaseContract.BottleTable.COLUMN_NAME_PRICE);
		}

		if (ratingBar.getRating() > 0)	// The minimum rating value is 1 star. If 0 is the value, we consider that no mark is given.
			values.put(DatabaseContract.BottleTable.COLUMN_NAME_MARK, (int) ratingBar.getRating());

		int colourValue = colour.getSelectedItemPosition();
		values.put(DatabaseContract.BottleTable.COLUMN_NAME_COLOUR, colourValue);
		
		int sugarValue = sugar.getSelectedItemPosition();
		values.put(DatabaseContract.BottleTable.COLUMN_NAME_SUGAR, sugarValue);
		
		int effervescenceValue = effervescence.getSelectedItemPosition();
		values.put(DatabaseContract.BottleTable.COLUMN_NAME_EFFERVESCENCE, effervescenceValue);

		String month = ((addDate.getMonth()+1) < 10) ? ("0"+(addDate.getMonth()+1)) : Integer.toString((addDate.getMonth()+1));
		String day = (addDate.getDayOfMonth() < 10) ? ("0"+addDate.getDayOfMonth()) : Integer.toString(addDate.getDayOfMonth());
		String addDateValue = addDate.getYear() + "-" + month + "-" + day;
		values.put(DatabaseContract.BottleTable.COLUMN_NAME_ADD_DATE, addDateValue);

		month = ((apogee.getMonth()+1) < 10) ? ("0"+(apogee.getMonth()+1)) : Integer.toString((apogee.getMonth()+1));
		day = (apogee.getDayOfMonth() < 10) ? ("0"+apogee.getDayOfMonth()) : Integer.toString(apogee.getDayOfMonth());
		String apogeeValue = apogee.getYear() + "-" + month + "-" + day;
		values.put(DatabaseContract.BottleTable.COLUMN_NAME_APOGEE, apogeeValue);

		String locationValue = location.getText().toString();
		if (!TextUtils.isEmpty(locationValue))
			values.put(DatabaseContract.BottleTable.COLUMN_NAME_LOCATION, locationValue);
		else
			values.putNull(DatabaseContract.BottleTable.COLUMN_NAME_LOCATION);

		String noteValue = note.getText().toString();
		if (!TextUtils.isEmpty(noteValue))
			values.put(DatabaseContract.BottleTable.COLUMN_NAME_NOTE, noteValue);
		else
			values.putNull(DatabaseContract.BottleTable.COLUMN_NAME_NOTE);

		String codeStringValue = code.getText().toString();
		if (!TextUtils.isEmpty(codeStringValue)) {
			try {
				values.put(DatabaseContract.BottleTable.COLUMN_NAME_CODE, Long.valueOf(codeStringValue));
			}
			catch (NumberFormatException e) {
				throw new Exception("Code not recognized!");
			}
		} else {
			// Tell user error.
			
			throw new Exception("Code field empty!");
		}
		
		// The picture path:
		if (this.photoPath != null)
			values.put(DatabaseContract.BottleTable.COLUMN_NAME_IMAGE, this.photoPath);
		else
			values.putNull(DatabaseContract.BottleTable.COLUMN_NAME_IMAGE);
		
		return values;
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int idLoader, Bundle bundle) {
		
		
		SQLiteCursorLoader cursorLoader = null;

		switch (idLoader){
		case MAIN_INFO_LOADER:
			long id = getArguments().getLong(BottleTable._ID);
			String idString = Long.toString(id);
			cursorLoader = new SQLiteCursorLoader(this.getActivity(),
					DatabaseHelper.getInstance(getActivity()), 
					
					"SELECT * " +
							" FROM " + BottleTable.TABLE_NAME +
							" WHERE " + BottleTable._ID + " = ?", 
							
							new String[] { idString });

			break;
			
		case BOTTLE_VARIETIES_LOADER:
			id = getArguments().getLong(BottleTable._ID);
			idString = Long.toString(id);
			cursorLoader = new SQLiteCursorLoader(this.getActivity(),
					DatabaseHelper.getInstance(getActivity()), 
					
					"SELECT v." + VarietyTable.COLUMN_NAME_NAME + ", v." + VarietyTable._ID + " AS " + VarietyTable._ID +
					" FROM " + BottleVarietyTable.TABLE_NAME + " bv, " + VarietyTable.TABLE_NAME + " v " +
					" WHERE bv." + BottleVarietyTable.COLUMN_NAME_BOTTLE_ID + " = ?" +
					" AND bv." + BottleVarietyTable.COLUMN_NAME_VARIETY_ID + " = v." + VarietyTable._ID, 
					
					new String[] { idString });

			break;
			
		case ALL_VARIETIES_LOADER:
			cursorLoader = new SQLiteCursorLoader(this.getActivity(),
					DatabaseHelper.getInstance(getActivity()), 
					
					"SELECT *" +
					" FROM " + VarietyTable.TABLE_NAME, 
					
					null);
		}

		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		switch (loader.getId()){
		case MAIN_INFO_LOADER:
			try{
				setInfo(cursor);
			} finally {
				cursor.close();
			}
			break;

		case BOTTLE_VARIETIES_LOADER:
			try{
				setVarieties(cursor);
			} finally {
				cursor.close();
			}
			break;
		
		case ALL_VARIETIES_LOADER:
			try{
				setAllVarieties(cursor);
			} finally {
				cursor.close();
			}
		}
	}
	
	/**
	 * Puts the information received in the cursor into the text fields for further modifications.
	 * @param cursor The information about the bottle.
	 */
	protected void setInfo(Cursor cursor) {
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
			
			int vintageColumnIndex = cursor.getColumnIndex(BottleTable.COLUMN_NAME_VINTAGE);
			if (!cursor.isNull(vintageColumnIndex))
				vintage.setText(Integer.toString(cursor.getInt(vintageColumnIndex)));
			
			int regionColumnIndex = cursor.getColumnIndex(BottleTable.COLUMN_NAME_REGION);
			if (!cursor.isNull(regionColumnIndex))
				region.setText(cursor.getString(regionColumnIndex));
			
			quantity.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex(BottleTable.COLUMN_NAME_QUANTITY))));
			
			int priceColumnIndex = cursor.getColumnIndex(BottleTable.COLUMN_NAME_PRICE);
			if (!cursor.isNull(priceColumnIndex))
				price.setText(Float.toString(cursor.getFloat(priceColumnIndex)));
			
			int markColumnIndex = cursor.getColumnIndex(BottleTable.COLUMN_NAME_MARK);
			if (!cursor.isNull(markColumnIndex)) {
				ratingBar.setRating(cursor.getInt(markColumnIndex));
			} else {
				ratingBar.setRating(0);
			}

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
				int month = Integer.parseInt(addDateArray[1])-1;
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
				int month = Integer.parseInt(apogeeArray[1])-1;
				int day = Integer.parseInt(apogeeArray[2]);
				
				apogee.init(year, month, day, null);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				Log.e("NumberFormatException", "Number provided is not correctly formatted");
			}
			
			int locationColumnIndex = cursor.getColumnIndex(BottleTable.COLUMN_NAME_LOCATION);
			if (!cursor.isNull(locationColumnIndex))
				location.setText(cursor.getString(locationColumnIndex));
			
			int noteColumnIndex = cursor.getColumnIndex(BottleTable.COLUMN_NAME_NOTE);
			if (!cursor.isNull(noteColumnIndex))
				note.setText(cursor.getString(noteColumnIndex));
			
			code.setText(Long.toString(cursor.getLong(cursor.getColumnIndex(BottleTable.COLUMN_NAME_CODE))));
			
			int pictureColumnIndex = cursor.getColumnIndex(BottleTable.COLUMN_NAME_IMAGE);
			if (!cursor.isNull(pictureColumnIndex)) {
				setPicture(cursor.getString(pictureColumnIndex));
			} else {
				setBackground();
			}
		}
		
		
	}
	
	/**
	 * Puts the varieties information into the view.
	 * Calls addVarietyToLayout() to add each variety.
	 * @param cursor The varieties information coming from the SQLite DB.
	 */
	private void setVarieties(Cursor cursor) {
		
		LinearLayout ll = (LinearLayout) getView().findViewById(R.id.new_bottle_varieties_layout);
		cursor.moveToPosition(-1);
		while (cursor.moveToNext()) {
			String text = cursor.getString(cursor.getColumnIndex(VarietyTable.COLUMN_NAME_NAME));
			addVarietyToLayout(text, ll);
		}
	}
	
	/**
	 * Adds the passed variety to the layout and adds a long click listener on it.
	 * @param text The variety name.
	 * @param ll The target layout.
	 */
	protected void addVarietyToLayout(String text, LinearLayout ll) {
		TextView tv = new TextView(getActivity());
		tv.setText(text);
		tv.setOnLongClickListener(this);
		ll.addView(tv);
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.new_bottle_add_variety_button)
			addVariety();
	}
	
	/**
	 * Called when the user wants to add a variety to a bottle.
	 * Adds the typed variety to the layout.
	 */
	public void addVariety() {
		AutoCompleteTextView auto = (AutoCompleteTextView) getView().findViewById(R.id.new_bottle_varieties_autocomplete);
		String text = auto.getText().toString();
		
		// Check if length of text is > 0:
		if (text.length() == 0)
			return;
		
		// Check if not in the list already:
		LinearLayout ll = (LinearLayout) getView().findViewById(R.id.new_bottle_varieties_layout);
		int count = ll.getChildCount();
		boolean present = false;
		
		for (int i = AMOUNT_OF_VIEWS_IN_VARIETIES_LINEAR_LAYOUT_TO_PASS; i < count && !present; ++i) {
			TextView tv = (TextView) ll.getChildAt(i);
			if (text.equals(tv.getText().toString()))
				present = true;
		}
		
		if (!present) {
			addVarietyToLayout(text, ll);
		}
	}
	
	@Override
	public boolean onLongClick(View v) {
		LinearLayout ll = (LinearLayout) getView().findViewById(R.id.new_bottle_varieties_layout);
		ll.removeView(v);
		return true;
	}
	
	/**
	 * Called by the loader after having searched for all the varieties in the SQLite DB.
	 * Creates an {@link ArrayAdapter} for the auto complete variety text view.
	 * The adapter takes all the recorded varieties.
	 * @param cursor The cursor containing all the varieties names.
	 */
	private void setAllVarieties(Cursor cursor) {
		String[] varieties = new String[cursor.getCount()];
		int index = cursor.getColumnIndex(VarietyTable.COLUMN_NAME_NAME);
		int i = 0;
		cursor.moveToPosition(-1);
		while (cursor.moveToNext()) {
			if (!cursor.isNull(index))
				varieties[i++] = cursor.getString(index);
		}
		
		final AutoCompleteTextView autocomplete = (AutoCompleteTextView) getView().findViewById(R.id.new_bottle_varieties_autocomplete);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_dropdown_item_1line, varieties);
		autocomplete.setAdapter(adapter);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {}
	
	/**
	 * Sets the background of the fragment to a defined color. Used in case no picture is available.
	 */
	private void setBackground() {
		ScrollView scrollView = (ScrollView) getView().findViewById(R.id.scrollView_abstract_bottle_info);
		scrollView.setBackgroundColor(getResources().getColor(R.color.DetailsBackground));
	}

	/**
	 * Sets the background picture to a given picture found at the location given by the passed path.
	 * @param photoPath The photo path on the device.
	 */
	public void setPicture(String photoPath) {
		
		File picture = new File(photoPath);
		if (!picture.exists() || !picture.isFile() || !picture.canRead()) {
			setBackground();
			return;
		}
		
		ImageView imageView = (ImageView) getView().findViewById(R.id.background_picture);
		
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inPurgeable = true;
	    
	    Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
	    imageView.setImageBitmap(bitmap);
	    
	    // Set the background color to transparent to see the picture.
	    ScrollView scrollView = (ScrollView) getView().findViewById(R.id.scrollView_abstract_bottle_info);
		scrollView.setBackgroundColor(getResources().getColor(R.color.Transparent));
	    
	    // Suppress the old picture to gain space:
	    if (this.photoPath != null)
	    	suppressPicture();
	    this.photoPath  = photoPath;
	}

	/**
	 * Suppress the picture file currently saved for this bottle.
	 */
	private void suppressPicture() {
		File picture = new File(this.photoPath);
		if (!picture.delete())
			Log.e("FILE_DELETE", "Previous picture not deleted.");

		this.photoPath = null;
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		/*
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
		
		savedInstanceState.putString(DatabaseContract.BottleTable.COLUMN_NAME_APPELLATION, appellation.getText().toString());
		savedInstanceState.putString(DatabaseContract.BottleTable.COLUMN_NAME_NAME, name.getText().toString());
		savedInstanceState.putString(DatabaseContract.BottleTable.COLUMN_NAME_VINTAGE, vintage.getText().toString());
		savedInstanceState.putString(DatabaseContract.BottleTable.COLUMN_NAME_REGION, region.getText().toString());
		savedInstanceState.putString(DatabaseContract.BottleTable.COLUMN_NAME_QUANTITY, quantity.getText().toString());
		savedInstanceState.putString(DatabaseContract.BottleTable.COLUMN_NAME_PRICE, price.getText().toString());
		savedInstanceState.putInt(DatabaseContract.BottleTable.COLUMN_NAME_MARK, (int) ratingBar.getRating());
		savedInstanceState.putInt(DatabaseContract.BottleTable.COLUMN_NAME_COLOUR, colour.getSelectedItemPosition());
		savedInstanceState.putInt(DatabaseContract.BottleTable.COLUMN_NAME_SUGAR, sugar.getSelectedItemPosition());
		savedInstanceState.putInt(DatabaseContract.BottleTable.COLUMN_NAME_EFFERVESCENCE, effervescence.getSelectedItemPosition());
		String addDateValue = addDate.getYear() + "-" + addDate.getMonth() + "-" + addDate.getDayOfMonth();
		savedInstanceState.putString(DatabaseContract.BottleTable.COLUMN_NAME_ADD_DATE, addDateValue);
		String apogeeValue = apogee.getYear() + "-" + apogee.getMonth() + "-" + apogee.getDayOfMonth();
		savedInstanceState.putString(DatabaseContract.BottleTable.COLUMN_NAME_APOGEE, apogeeValue);
		savedInstanceState.putString(DatabaseContract.BottleTable.COLUMN_NAME_LOCATION, location.getText().toString());
		savedInstanceState.putString(DatabaseContract.BottleTable.COLUMN_NAME_NOTE, note.getText().toString());
		savedInstanceState.putString(DatabaseContract.BottleTable.COLUMN_NAME_CODE, code.getText().toString());
		*/
		if (this.photoPath != null)
			savedInstanceState.putString(DatabaseContract.BottleTable.COLUMN_NAME_IMAGE, this.photoPath);
		
		// Save the varieties:
		LinearLayout ll = (LinearLayout) getView().findViewById(R.id.new_bottle_varieties_layout);
		int count = ll.getChildCount();	// There is one title view and the auto complete (so put -2).
		ArrayList<String> varieties = new ArrayList<String>();
		
		for (int i = AMOUNT_OF_VIEWS_IN_VARIETIES_LINEAR_LAYOUT_TO_PASS; i < count; ++i) {
			TextView tv = (TextView) ll.getChildAt(i);
			varieties.add(tv.getText().toString());
		}
		
		savedInstanceState.putStringArrayList(VARIETIES_KEY, varieties);
	}
	
	/**
	 * Toggles the scrollview visibility to hide or show the details, to see the background image.
	 */
	public void toggleDetails() {
		ScrollView scrollView = (ScrollView) getView().findViewById(R.id.scrollView_abstract_bottle_info);
		if (scrollView.getVisibility() == View.VISIBLE)
			scrollView.setVisibility(View.INVISIBLE);
		else
			scrollView.setVisibility(View.VISIBLE);
	}
	
	/**
	 * Callback interface that the connected activity must implement to allow this fragment to communicate with it.
	 * @author anthonydebruyn
	 *
	 */
	public interface AbstractBottleInfoFragmentCallbacks {
		/**
		 * Called when the user wants to take a picture, and has push the picture button.
		 * The connected activity will take care of the intent generation and reception of the captured picture.
		 */
		public void onTakePicture();
		
		public void onAbstractBottleInfoFragmentStarted();
	}
}
