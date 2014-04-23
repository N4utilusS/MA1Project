package be.n4utiluss.wysiwyd;

import java.io.File;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import be.n4utiluss.wysiwyd.database.DatabaseContract;
import be.n4utiluss.wysiwyd.database.DatabaseContract.BottleTable;
import be.n4utiluss.wysiwyd.database.DatabaseContract.BottleVarietyTable;
import be.n4utiluss.wysiwyd.database.DatabaseContract.VarietyTable;
import be.n4utiluss.wysiwyd.database.DatabaseHelper;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;

public abstract class AbstractBottleInfoFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnLongClickListener, OnClickListener {


	private static final int MAIN_INFO_LOADER = 0;
	private static final int BOTTLE_VARIETIES_LOADER = 1;
	private static final int ALL_VARIETIES_LOADER = 2;
	private String photoPath = null;
	public static final int AMOUNT_OF_VIEWS_IN_VARIETIES_LINEAR_LAYOUT_TO_PASS = 2;
	
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_abstract_bottle_info, container, false);

		// (Re)start the loaders here, since this method is the first one called after we get back from the new bottle fragment, 
		// after we popped the previous state from the stack.

		Bundle arguments = getArguments();
		if (arguments != null && arguments.containsKey(BottleTable._ID) && (arguments.getLong(BottleTable._ID) > 0)) {
			getLoaderManager().initLoader(MAIN_INFO_LOADER, null, this);
			getLoaderManager().initLoader(BOTTLE_VARIETIES_LOADER, null, this);
			getLoaderManager().initLoader(ALL_VARIETIES_LOADER, null, this);
		}
		
		Button addVarietyButton = (Button) rootView.findViewById(R.id.new_bottle_add_variety_button);
		addVarietyButton.setOnClickListener(this);
		
		return rootView;
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
			ContentValues values = getValues();
			writeToDB(values);
			writeVarietiesToDB();
			return true;
		
		case R.id.action_picture:
			getLinkedActivity().onTakePicture();
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	abstract protected void writeToDB(ContentValues values);
	
	abstract protected void writeVarietiesToDB();

	protected void dismissFragment() {
		this.getFragmentManager().popBackStack();
	}

	private ContentValues getValues() {
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
		
		// The picture path:
		if (this.photoPath != null)
			values.put(DatabaseContract.BottleTable.COLUMN_NAME_IMAGE, this.photoPath);
		
		return values;
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
			
		case BOTTLE_VARIETIES_LOADER:
			cursorLoader = new SQLiteCursorLoader(this.getActivity(),
					new DatabaseHelper(this.getActivity()), 
					
					"SELECT DISTINCT v." + VarietyTable.COLUMN_NAME_NAME + ", v." + VarietyTable._ID + 
					" FROM " + BottleVarietyTable.TABLE_NAME + " bv, " + VarietyTable.TABLE_NAME + " v " +
					" WHERE bv." + BottleVarietyTable._ID + " = ?" +
					" AND bv." + BottleVarietyTable.COLUMN_NAME_VARIETY_ID + " = v." + VarietyTable._ID, 
					
					new String[] { idString });

			break;
			
		case ALL_VARIETIES_LOADER:
			cursorLoader = new SQLiteCursorLoader(this.getActivity(),
					new DatabaseHelper(this.getActivity()), 
					
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
				
				
				int pictureColumnIndex = cursor.getColumnIndex(BottleTable.COLUMN_NAME_IMAGE);
				if (!cursor.isNull(pictureColumnIndex)) {
					setPicture(cursor.getString(pictureColumnIndex));
				} else {
					setBackground();
				}
			}
			break;

		case BOTTLE_VARIETIES_LOADER:
			setVarieties(cursor);
			break;
		
		case ALL_VARIETIES_LOADER:
			
			setAllVarieties(cursor);
		}

	}
	
	private void setVarieties(Cursor cursor) {
		if (this.getView() == null)
			Log.e("Details", "Null POINTER!!!!");
		LinearLayout ll = (LinearLayout) getView().findViewById(R.id.new_bottle_varieties_layout);
		cursor.moveToPosition(-1);
		while (cursor.moveToNext()) {
			String text = cursor.getString(cursor.getColumnIndex(VarietyTable.COLUMN_NAME_NAME));
			addVarietyToLayout(text, ll);
		}
	}
	
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
	public void onLoaderReset(Loader<Cursor> arg0) {

	}
	
	private void setBackground() {
		ScrollView scrollView = (ScrollView) getView().findViewById(R.id.scrollView_abstract_bottle_info);
		scrollView.setBackgroundColor(getResources().getColor(R.color.Lavender));
	}

	public void setPicture(String photoPath) {
		
		File picture = new File(photoPath);
		if (!picture.exists() || !picture.isFile() || !picture.canRead()) {
			setBackground();
			return;
		}
		
		// Get the dimension of the view
		ImageView imageView = (ImageView) getView().findViewById(R.id.background_picture);
		int targetW = imageView.getWidth();
		
		// Get the dimensions of the bitmap
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(photoPath, bmOptions);
	    int photoW = bmOptions.outWidth;

	    // Determine how much to scale down the image
	    int scaleFactor = photoW/targetW;
	    
	    // Decode the image file into a Bitmap sized to fill the View
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;
	    
	    Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
	    imageView.setImageBitmap(bitmap);
	    ScrollView scrollView = (ScrollView) getView().findViewById(R.id.scrollView_abstract_bottle_info);
		scrollView.setBackgroundColor(getResources().getColor(R.color.Transparent));
	    
	 // Put padding to see the image:
	    LinearLayout ll = (LinearLayout) getView().findViewById(R.id.abstract_bottle_info_linear_layout);
	    int top = (int) ((float) targetW/ (float) photoW * bmOptions.outHeight);
	    ll.setPadding(0, top, 0, 0);
	    
	    // Suppress the old picture to gain space:
	    if (this.photoPath != null)
	    	suppressPicture();
	    this.photoPath  = photoPath;
	}

	private void suppressPicture() {
		File picture = new File(this.photoPath);
		if (!picture.delete())
			Log.e("FILE_DELETE", "Previous picture not deleted.");

		this.photoPath = null;
	}
	
	public interface AbstractBottleInfoFragmentCallbacks {
		public void onTakePicture();
	}
}
