package be.n4utiluss.wysiwyd.search;

import java.util.ArrayList;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Spinner;
import android.widget.Toast;
import be.n4utiluss.wysiwyd.R;
import be.n4utiluss.wysiwyd.database.DatabaseHelper;
import be.n4utiluss.wysiwyd.database.DatabaseContract.VarietyTable;

public class SearchFragment extends Fragment implements OnLongClickListener, 
														LoaderManager.LoaderCallbacks<Cursor>, 
														OnClickListener {
	public static final String APPELLATION = "be.n4utiluss.wysiwyd.appellation";
	public static final String NAME = "be.n4utiluss.wysiwyd.name";
	public static final String VINTAGE_FROM = "be.n4utiluss.wysiwyd.vintage_from";
	public static final String VINTAGE_TO = "be.n4utiluss.wysiwyd.vintage_to";
	public static final String REGION = "be.n4utiluss.wysiwyd.region";
	public static final String QUANTITY_MIN = "be.n4utiluss.wysiwyd.quantity_min";
	public static final String QUANTITY_MAX = "be.n4utiluss.wysiwyd.quantity_max";
	public static final String PRICE_MIN = "be.n4utiluss.wysiwyd.price_min";
	public static final String PRICE_MAX = "be.n4utiluss.wysiwyd.price_max";
	public static final String RATING_MIN = "be.n4utiluss.wysiwyd.rating_min";
	public static final String RATING_MAX = "be.n4utiluss.wysiwyd.rating_max";
	public static final String COLOUR = "be.n4utiluss.wysiwyd.colour";
	public static final String SUGAR = "be.n4utiluss.wysiwyd.sugar";
	public static final String EFFERVESCENCE = "be.n4utiluss.wysiwyd.effervescence";
	public static final String VARIETIES = "be.n4utiluss.wysiwyd.varieties";
	public static final String ADD_DATE_FROM = "be.n4utiluss.wysiwyd.add_date_from";
	public static final String ADD_DATE_TO = "be.n4utiluss.wysiwyd.add_date_to";
	public static final String APOGEE_FROM = "be.n4utiluss.wysiwyd.apogee_from";
	public static final String APOGEE_TO = "be.n4utiluss.wysiwyd.apogee_to";
	public static final String CODE = "be.n4utiluss.wysiwyd.code";
	public static final int AMOUNT_OF_VIEWS_IN_VARIETIES_LINEAR_LAYOUT_TO_PASS = 2;
	private static final int ALL_VARIETIES_LOADER = 0;

	private SearchFragmentCallbacks linkedActivity;
	private String[] varieties;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof SearchFragmentCallbacks)) {
			throw new IllegalStateException("Activity must implement fragment callbacks.");
		}

		this.linkedActivity = (SearchFragmentCallbacks) activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);	// So the onCreateOptionsMenu method is called, and the actions are set.
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_search, container, false);
		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		// (Re)start the loaders here.
		getLoaderManager().restartLoader(ALL_VARIETIES_LOADER, null, this);
		
		final RatingBar min = (RatingBar) getView().findViewById(R.id.search_mark_min);
		final RatingBar max = (RatingBar) getView().findViewById(R.id.search_mark_max);
		
		// Reset the values:
		if (savedInstanceState != null) {
			resetValues(savedInstanceState);
		} else {
			// Set the values to all for the properties:
			Spinner colour = (Spinner) getView().findViewById(R.id.search_colour);
			Spinner sugar = (Spinner) getView().findViewById(R.id.search_sugar);
			Spinner effervescence = (Spinner) getView().findViewById(R.id.search_effervescence);
			
			colour.setSelection(3);
			sugar.setSelection(4);
			effervescence.setSelection(4);
		}
		
		// Listeners to prevent the min rating bar to go above the max one, and conversely:
		min.setOnRatingBarChangeListener(new OnRatingBarChangeListener(){

			@Override
			public void onRatingChanged(RatingBar arg0, float rating, boolean fromUser) {
				if (fromUser && max.getRating() < rating)
					max.setRating(rating);
			}
			
		});
		
		max.setOnRatingBarChangeListener(new OnRatingBarChangeListener(){

			@Override
			public void onRatingChanged(RatingBar arg0, float rating, boolean fromUser) {
				if (fromUser && min.getRating() > rating)
					min.setRating(rating);
			}
			
		});
		
		Button addVarietyButton = (Button) getView().findViewById(R.id.search_add_variety_button);
		addVarietyButton.setOnClickListener(this);
		
		this.linkedActivity.onSearchFragmentStarted();
	}
	
	private void resetValues(Bundle savedInstanceState) {
		
		// The varieties:
		LinearLayout ll = (LinearLayout) getView().findViewById(R.id.search_varieties_layout);
		ArrayList<String> varieties = savedInstanceState.getStringArrayList(VARIETIES);
		for (String variety: varieties)
			addVarietyToLayout(variety, ll);
		Log.i("RESET", "Reset");
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.search, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		switch (id) {
		case R.id.search_action_find:
			try {
				Bundle info = getInfo();
				this.linkedActivity.find(info);
			} catch (Exception e) {
				Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
			}
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private Bundle getInfo() throws Exception {
		Bundle info = new Bundle();
		
		EditText appellation = (EditText) getView().findViewById(R.id.search_appellation);
		EditText name = (EditText) getView().findViewById(R.id.search_name);
		EditText vintageFrom = (EditText) getView().findViewById(R.id.search_vintage_from);
		CheckBox vintageFromCheckBox = (CheckBox) getView().findViewById(R.id.search_checkbox_vintage_from);
		EditText vintageTo = (EditText) getView().findViewById(R.id.search_vintage_to);
		CheckBox vintageToCheckBox = (CheckBox) getView().findViewById(R.id.search_checkbox_vintage_to);
		EditText region = (EditText) getView().findViewById(R.id.search_region);
		EditText quantityMin = (EditText) getView().findViewById(R.id.search_quantity_min);
		CheckBox quantityMinCheckBox = (CheckBox) getView().findViewById(R.id.search_checkbox_quantity_min);
		EditText quantityMax = (EditText) getView().findViewById(R.id.search_quantity_max);
		CheckBox quantityMaxCheckBox = (CheckBox) getView().findViewById(R.id.search_checkbox_quantity_max);
		EditText priceMin = (EditText) getView().findViewById(R.id.search_price_min);
		CheckBox priceMinCheckBox = (CheckBox) getView().findViewById(R.id.search_checkbox_price_min);
		EditText priceMax = (EditText) getView().findViewById(R.id.search_price_max);
		CheckBox priceMaxCheckBox = (CheckBox) getView().findViewById(R.id.search_checkbox_price_max);
		RatingBar ratingBarMin = (RatingBar) getView().findViewById(R.id.search_mark_min);
		CheckBox ratingBarMinCheckBox = (CheckBox) getView().findViewById(R.id.search_checkbox_mark_min);
		RatingBar ratingBarMax = (RatingBar) getView().findViewById(R.id.search_mark_max);
		CheckBox ratingBarMaxCheckBox = (CheckBox) getView().findViewById(R.id.search_checkbox_mark_max);
		Spinner colour = (Spinner) getView().findViewById(R.id.search_colour);
		Spinner sugar = (Spinner) getView().findViewById(R.id.search_sugar);
		Spinner effervescence = (Spinner) getView().findViewById(R.id.search_effervescence);
		DatePicker addDateFrom = (DatePicker) getView().findViewById(R.id.search_addDate_from);
		CheckBox addDateFromCheckBox = (CheckBox) getView().findViewById(R.id.search_checkbox_addDate_from);
		DatePicker addDateTo = (DatePicker) getView().findViewById(R.id.search_addDate_to);
		CheckBox addDateToCheckBox = (CheckBox) getView().findViewById(R.id.search_checkbox_addDate_to);
		DatePicker apogeeFrom = (DatePicker) getView().findViewById(R.id.search_apogee_from);
		CheckBox apogeeFromCheckBox = (CheckBox) getView().findViewById(R.id.search_checkbox_apogee_from);
		DatePicker apogeeTo = (DatePicker) getView().findViewById(R.id.search_apogee_to);
		CheckBox apogeeToCheckBox = (CheckBox) getView().findViewById(R.id.search_checkbox_apogee_to);
		EditText code = (EditText) getView().findViewById(R.id.search_code);
		
		if(!TextUtils.isEmpty(appellation.getText().toString()))
			info.putString(APPELLATION, appellation.getText().toString());
		if(!TextUtils.isEmpty(name.getText().toString()))
			info.putString(NAME, name.getText().toString());
		if(vintageFromCheckBox.isChecked() && !TextUtils.isEmpty(vintageFrom.getText().toString())) {
			try {
				info.putInt(VINTAGE_FROM, Math.abs(Integer.parseInt(vintageFrom.getText().toString())));
			}
			catch (NumberFormatException e) {
				throw new Exception("Vintage from not recognized!");
			}
		}
		if(vintageToCheckBox.isChecked() && !TextUtils.isEmpty(vintageTo.getText().toString())) {
			try {
				info.putInt(VINTAGE_TO, Math.abs(Integer.parseInt(vintageTo.getText().toString())));
			}
			catch (NumberFormatException e) {
				throw new Exception("Vintage to not recognized!");
			}
		}
		if(!TextUtils.isEmpty(region.getText().toString()))
			info.putString(REGION, region.getText().toString());
		if(quantityMinCheckBox.isChecked() && !TextUtils.isEmpty(quantityMin.getText().toString())) {
			try {
				info.putInt(QUANTITY_MIN, Integer.parseInt(quantityMin.getText().toString()));
			}
			catch (NumberFormatException e) {
				throw new Exception("Quantity min not recognized!");
			}
		}
		if(quantityMaxCheckBox.isChecked() && !TextUtils.isEmpty(quantityMax.getText().toString())) {
			try {
				info.putInt(QUANTITY_MAX, Integer.parseInt(quantityMax.getText().toString()));
			}
			catch (NumberFormatException e) {
				throw new Exception("Quantity max not recognized!");
			}
		}
			
		if(priceMinCheckBox.isChecked() && !TextUtils.isEmpty(priceMin.getText().toString())) {
			try {
				info.putFloat(PRICE_MIN, Math.abs(Float.parseFloat(priceMin.getText().toString())));
			}
			catch (NumberFormatException e) {
				throw new Exception("Price min not recognized!");
			}
		}
			
		if(priceMaxCheckBox.isChecked() && !TextUtils.isEmpty(priceMax.getText().toString())) {
			try {
				info.putFloat(PRICE_MAX, Math.abs(Float.parseFloat(priceMax.getText().toString())));
			}
			catch (NumberFormatException e) {
				throw new Exception("Price max not recognized!");
			}
		}
		if(ratingBarMinCheckBox.isChecked())
			info.putInt(RATING_MIN, (int) ratingBarMin.getRating());
		if(ratingBarMaxCheckBox.isChecked())
			info.putInt(RATING_MAX, (int) ratingBarMax.getRating());
		if(colour.getSelectedItemPosition() < 3)
			info.putInt(COLOUR, colour.getSelectedItemPosition());
		if(sugar.getSelectedItemPosition() < 4)
			info.putInt(SUGAR, sugar.getSelectedItemPosition());
		if(effervescence.getSelectedItemPosition() < 4)
			info.putInt(EFFERVESCENCE, effervescence.getSelectedItemPosition());
		
		if(addDateFromCheckBox.isChecked()) {
			String month = ((addDateFrom.getMonth()+1) < 10) ? ("0"+(addDateFrom.getMonth()+1)) : Integer.toString((addDateFrom.getMonth()+1));
			String day = (addDateFrom.getDayOfMonth() < 10) ? ("0"+addDateFrom.getDayOfMonth()) : Integer.toString(addDateFrom.getDayOfMonth());
			
			String addDateFromValue = addDateFrom.getYear() + "-" + month + "-" + day;
			info.putString(ADD_DATE_FROM, addDateFromValue);
		}
		if(addDateToCheckBox.isChecked()) {
			String month = ((addDateTo.getMonth()+1) < 10) ? ("0"+(addDateTo.getMonth()+1)) : Integer.toString((addDateTo.getMonth()+1));
			String day = (addDateTo.getDayOfMonth() < 10) ? ("0"+addDateTo.getDayOfMonth()) : Integer.toString(addDateTo.getDayOfMonth());
			
			String addDateToValue = addDateTo.getYear() + "-" + month + "-" + day;
			info.putString(ADD_DATE_TO, addDateToValue);
		}
		if(apogeeFromCheckBox.isChecked()) {
			String month = ((apogeeFrom.getMonth()+1) < 10) ? ("0"+(apogeeFrom.getMonth()+1)) : Integer.toString((apogeeFrom.getMonth()+1));
			String day = (apogeeFrom.getDayOfMonth() < 10) ? ("0"+apogeeFrom.getDayOfMonth()) : Integer.toString(apogeeFrom.getDayOfMonth());
			
			String apogeeFromValue = apogeeFrom.getYear() + "-" + month + "-" + day;
			info.putString(APOGEE_FROM, apogeeFromValue);
		}
		if(apogeeToCheckBox.isChecked()) {
			String month = ((apogeeFrom.getMonth()+1) < 10) ? ("0"+(apogeeFrom.getMonth()+1)) : Integer.toString((apogeeFrom.getMonth()+1));
			String day = (apogeeFrom.getDayOfMonth() < 10) ? ("0"+apogeeFrom.getDayOfMonth()) : Integer.toString(apogeeFrom.getDayOfMonth());
			
			String apogeeToValue = apogeeTo.getYear() + "-" + month + "-" + day;
			info.putString(APOGEE_TO, apogeeToValue);
		}
		
		if(!TextUtils.isEmpty(code.getText().toString())) {
			try {
				info.putLong(CODE, Long.parseLong(code.getText().toString()));
			}
			catch (NumberFormatException e) {
				throw new Exception("Code not recognized!");
			}
		}
		
		// Save the varieties:
		LinearLayout ll = (LinearLayout) getView().findViewById(R.id.search_varieties_layout);
		int count = ll.getChildCount();	// There is one title view and the auto complete (so put -2).
		ArrayList<String> varieties = new ArrayList<String>();

		for (int i = AMOUNT_OF_VIEWS_IN_VARIETIES_LINEAR_LAYOUT_TO_PASS; i < count; ++i) {
			TextView tv = (TextView) ll.getChildAt(i);
			varieties.add(tv.getText().toString());
		}
		
		if(!varieties.isEmpty())
			info.putStringArrayList(VARIETIES, varieties);
		
		return info;
	}
	
	@Override
	public void onStop() {
		super.onStop();
		this.linkedActivity.onSearchFragmentDismissed();
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		
		// Save the varieties:
		LinearLayout ll = (LinearLayout) getView().findViewById(R.id.search_varieties_layout);
		int count = ll.getChildCount();	// There is one title view and the auto complete (so put -2).
		ArrayList<String> varieties = new ArrayList<String>();

		for (int i = AMOUNT_OF_VIEWS_IN_VARIETIES_LINEAR_LAYOUT_TO_PASS; i < count; ++i) {
			TextView tv = (TextView) ll.getChildAt(i);
			varieties.add(tv.getText().toString());
		}

		savedInstanceState.putStringArrayList(VARIETIES, varieties);
	}
	
	@Override
	public boolean onLongClick(View v) {
		LinearLayout ll = (LinearLayout) getView().findViewById(R.id.search_varieties_layout);
		ll.removeView(v);
		return true;
	}
	

	@Override
	public Loader<Cursor> onCreateLoader(int idLoader, Bundle bundle) {
		SQLiteCursorLoader cursorLoader = null;

		switch (idLoader) {
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
		switch (loader.getId()) {
		case ALL_VARIETIES_LOADER:
			try{
				setAllVarieties(cursor);
			} finally {
				cursor.close();
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> c) {}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.search_add_variety_button)
			addVariety();
	}
	
	/**
	 * Called when the user wants to add a variety to a bottle.
	 * Adds the typed variety to the layout.
	 */
	public void addVariety() {
		AutoCompleteTextView auto = (AutoCompleteTextView) getView().findViewById(R.id.search_varieties_autocomplete);
		String text = auto.getText().toString();
		
		// Check if length of text is > 0:
		if (text.length() == 0)
			return;
		
		// Check if variety in DB:
		boolean inDB = false;
		for (int i = 0; i < this.varieties.length && !inDB; ++i) {
			if (this.varieties[i].equals(text))
				inDB = true;
		}
		if (!inDB)
			return;
		
		// Check if not in the list already:
		LinearLayout ll = (LinearLayout) getView().findViewById(R.id.search_varieties_layout);
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
	
	/**
	 * Called by the loader after having searched for all the varieties in the SQLite DB.
	 * Creates an {@link ArrayAdapter} for the auto complete variety text view.
	 * The adapter takes all the recorded varieties.
	 * @param cursor The cursor containing all the varieties names.
	 */
	private void setAllVarieties(Cursor cursor) {
		this.varieties = new String[cursor.getCount()];
		int index = cursor.getColumnIndex(VarietyTable.COLUMN_NAME_NAME);
		int i = 0;
		cursor.moveToPosition(-1);
		while (cursor.moveToNext()) {
			if (!cursor.isNull(index))
				varieties[i++] = cursor.getString(index);
		}
		
		final AutoCompleteTextView autocomplete = (AutoCompleteTextView) getView().findViewById(R.id.search_varieties_autocomplete);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_dropdown_item_1line, varieties);
		autocomplete.setAdapter(adapter);
	}
	
	public interface SearchFragmentCallbacks {
		
		public void find(Bundle values);
		
		public void onSearchFragmentStarted();
		
		public void onSearchFragmentDismissed();
	}

}
