package be.n4utiluss.wysiwyd;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import be.n4utiluss.wysiwyd.database.DatabaseContract.BottleTable;
import be.n4utiluss.wysiwyd.database.DatabaseContract.BottleVarietyTable;
import be.n4utiluss.wysiwyd.database.DatabaseContract.VarietyTable;
import be.n4utiluss.wysiwyd.database.DatabaseContract;
import be.n4utiluss.wysiwyd.database.DatabaseHelper;
import be.n4utiluss.wysiwyd.fonts.Fonts;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;

/**
 * Fragment displaying the details about a bottle.
 * @author anthonydebruyn
 *
 */
public class BottleDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnLongClickListener {

	private static final int MAIN_INFO_LOADER = 0;
	private static final int BOTTLE_VARIETIES_LOADER = 1;
	private static final int AMOUNT_OF_VIEWS_IN_VARIETIES_LINEAR_LAYOUT_TO_PASS = 1;
	private static final String VARIETIES_KEY = "be.n4utiluss.wysiwyd.varieties";
	private BottleDetailsFragmentCallbacks linkedActivity;
	private String photoPath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);	// So the onCreateOptionsMenu method is called, and the actions are set.
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_bottle_details, container, false);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// (Re)start the loaders:

		if (savedInstanceState == null && this.getArguments().containsKey(BottleTable._ID)) {
			getLoaderManager().restartLoader(MAIN_INFO_LOADER, null, this);
			getLoaderManager().restartLoader(BOTTLE_VARIETIES_LOADER, null, this);
		} else {
			resetInfo(savedInstanceState);
		}

		// Fonts
		TextView name = (TextView) getView().findViewById(R.id.details_name);
		name.setTypeface(Fonts.getFonts(getActivity()).chopinScript);
		
		// Set long click listener on code view:
		GridLayout gl = (GridLayout) getView().findViewById(R.id.details_code_layout);
		gl.setOnLongClickListener(this);
	}
	
	/**
	 * Takes a bundle with all the saved information, and restores it.
	 * Used when a rotation of the screen occurs and other occasions.
	 * @param savedInstanceState The saved information.
	 */
	private void resetInfo(Bundle savedInstanceState) {
		
		if (savedInstanceState.containsKey(DatabaseContract.BottleTable.COLUMN_NAME_IMAGE)) {
			setPicture(savedInstanceState.getString(DatabaseContract.BottleTable.COLUMN_NAME_IMAGE));
			//this.photoPath = savedInstanceState.getString(DatabaseContract.BottleTable.COLUMN_NAME_IMAGE);
		}
		
		// The varieties:
		LinearLayout ll = (LinearLayout) getView().findViewById(R.id.details_varieties_layout);
		ArrayList<String> varieties = savedInstanceState.getStringArrayList(VARIETIES_KEY);
		for (String variety: varieties)
			addVarietyToLayout(variety, ll);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof BottleDetailsFragmentCallbacks)) {
			throw new IllegalStateException("Activity must implement fragment callbacks.");
		}

		this.linkedActivity = (BottleDetailsFragmentCallbacks) activity;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.details, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		switch (id) {
		case R.id.action_edit:
			this.linkedActivity.onEditEvent(getArguments().getLong(BottleTable._ID));
			return true;
			
		case R.id.action_details_image:
			toggleDetails();
			return true;
			
		case R.id.details_action_delete:
			delete();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Called after the user has pushed the delete button to delete a bottle.
	 * Displays a dialog asking confirmation, and deletes the current bottle if confirmed.
	 * Deletes the picture file if this is the only bottle using it.
	 */
	private void delete() {
		AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
		
		adb.setMessage(R.string.details_delete_popup_message);
		
		adb.setNegativeButton("Cancel", null);
		
		adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	DatabaseHelper dbHelper = DatabaseHelper.getInstance(getActivity());
	    		SQLiteDatabase db = dbHelper.getWritableDatabase();
	    		
	    		// First delete the bottle information in the main bottle table:
	    		String whereClause = BottleTable._ID + " = ?";
	    		String[] whereArgs = {Long.toString(getArguments().getLong(BottleTable._ID))};
	    		
	    		db.delete(BottleTable.TABLE_NAME, whereClause, whereArgs);

	    		// Then remove entries in the Bottle Varieties table:
	    		whereClause = BottleVarietyTable.COLUMN_NAME_BOTTLE_ID + " = ?";
	    		
	    		db.delete(BottleVarietyTable.TABLE_NAME, whereClause, whereArgs);
	    		
	    		// Suppress the picture file if no bottle uses it anymore:
	    		whereArgs[0] = photoPath;
	    		Cursor cursor = db.query(BottleTable.TABLE_NAME, null, BottleTable.COLUMN_NAME_IMAGE + " = ?", whereArgs, null, null, null, "1");
	    		
	    		if (cursor.isAfterLast()) {
	    			suppressPicture();
	    		}
	    		cursor.close();
	    		
	    		linkedActivity.onDeleteEvent();
	      } });
		
		adb.show();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int idLoader, Bundle bundle) {
		long id = this.getArguments().getLong(BottleTable._ID);
		String idString = Long.toString(id);
		SQLiteCursorLoader cursorLoader = null;

		switch (idLoader){
		case MAIN_INFO_LOADER:
			cursorLoader = new SQLiteCursorLoader(this.getActivity(),
					DatabaseHelper.getInstance(getActivity()), 
					"SELECT * " +
							" FROM " + BottleTable.TABLE_NAME +
							" WHERE " + BottleTable._ID + " = ?", 
							new String[] { idString });

			break;
		case BOTTLE_VARIETIES_LOADER:
			cursorLoader = new SQLiteCursorLoader(this.getActivity(),
					DatabaseHelper.getInstance(getActivity()), 
					"SELECT v." + VarietyTable.COLUMN_NAME_NAME + ", v." + VarietyTable._ID + " AS " + VarietyTable._ID +
					" FROM " + BottleVarietyTable.TABLE_NAME + " bv, " + VarietyTable.TABLE_NAME + " v " +
					" WHERE bv." + BottleVarietyTable.COLUMN_NAME_BOTTLE_ID + " = ?" +
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
		default:
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {

	}
	
	/**
	 * Puts the information received in the cursor into the text fields.
	 * @param cursor The information about the bottle.
	 */
	private void setInfo(Cursor cursor) {
		if (cursor.moveToFirst()) {

			TextView appellation = (TextView) getView().findViewById(R.id.details_appellation);
			TextView name = (TextView) getView().findViewById(R.id.details_name);
			TextView vintage = (TextView) getView().findViewById(R.id.details_vintage);
			TextView region = (TextView) getView().findViewById(R.id.details_region);
			TextView quantity = (TextView) getView().findViewById(R.id.details_quantity);
			TextView price = (TextView) getView().findViewById(R.id.details_price);
			RatingBar ratingBar = (RatingBar) getView().findViewById(R.id.details_mark);
			TextView colour = (TextView) getView().findViewById(R.id.details_colour);
			TextView sugar = (TextView) getView().findViewById(R.id.details_sugar);
			TextView effervescence = (TextView) getView().findViewById(R.id.details_effervescence);
			TextView addDate = (TextView) getView().findViewById(R.id.details_addDate);
			TextView apogee = (TextView) getView().findViewById(R.id.details_apogee);
			TextView location = (TextView) getView().findViewById(R.id.details_location);
			TextView note = (TextView) getView().findViewById(R.id.details_note);
			TextView code = (TextView) getView().findViewById(R.id.details_code);

			appellation.setText(cursor.getString(cursor.getColumnIndex(BottleTable.COLUMN_NAME_APPELLATION)));
			name.setText(cursor.getString(cursor.getColumnIndex(BottleTable.COLUMN_NAME_NAME)));
			
			int vintageColumnIndex = cursor.getColumnIndex(BottleTable.COLUMN_NAME_VINTAGE);
			if (!cursor.isNull(vintageColumnIndex))
				vintage.setText(Integer.toString(cursor.getInt(vintageColumnIndex)));
			
			int regionColumnIndex = cursor.getColumnIndex(BottleTable.COLUMN_NAME_REGION);
			if (!cursor.isNull(regionColumnIndex))
				region.setText(cursor.getString(regionColumnIndex));
			
			quantity.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex(BottleTable.COLUMN_NAME_QUANTITY))) + " bottle(s)");
			
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
			String[] colourArray = getResources().getStringArray(R.array.colour_array);
			colour.setText(colourArray[colourValue]);

			int sugarValue = cursor.getInt(cursor.getColumnIndex(BottleTable.COLUMN_NAME_SUGAR));
			String[] sugarArray = getResources().getStringArray(R.array.sugar_array);
			sugar.setText(sugarArray[sugarValue]);

			int effervescenceValue = cursor.getInt(cursor.getColumnIndex(BottleTable.COLUMN_NAME_EFFERVESCENCE));
			String[] effervescenceArray = getResources().getStringArray(R.array.effervescence_array);
			effervescence.setText(effervescenceArray[effervescenceValue]);
			
			addDate.setText(cursor.getString(cursor.getColumnIndex(BottleTable.COLUMN_NAME_ADD_DATE)));
			apogee.setText(cursor.getString(cursor.getColumnIndex(BottleTable.COLUMN_NAME_APOGEE)));
			
			int locationColumnIndex = cursor.getColumnIndex(BottleTable.COLUMN_NAME_LOCATION);
			if (!cursor.isNull(locationColumnIndex))
				location.setText(cursor.getString(locationColumnIndex));
			
			int noteColumnIndex = cursor.getColumnIndex(BottleTable.COLUMN_NAME_NOTE);
			if (!cursor.isNull(noteColumnIndex))
				note.setText(cursor.getString(noteColumnIndex));
			
			code.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex(BottleTable.COLUMN_NAME_CODE))));
			
			int pictureColumnIndex = cursor.getColumnIndex(BottleTable.COLUMN_NAME_IMAGE);
			if (!cursor.isNull(pictureColumnIndex)) {
				setPicture(cursor.getString(pictureColumnIndex));
			}
		}
	}
	
	/**
	 * Puts the varieties information into the view.
	 * Calls addVarietyToLayout() to add each variety.
	 * @param cursor The varieties information coming from the SQLite DB.
	 */
	private void setVarieties(Cursor cursor) {
		if (this.getView() == null)
			Log.e("Details", "Null POINTER!!!!");
		LinearLayout ll = (LinearLayout) getView().findViewById(R.id.details_varieties_layout);
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
		ll.addView(tv);
	}
	
	/**
	 * Sets the background picture to a given picture found at the location given by the passed path.
	 * @param photoPath The photo path on the device.
	 */
	public void setPicture(String photoPath) {
		
		File picture = new File(photoPath);
		if (!picture.exists() || !picture.isFile() || !picture.canRead()) {
			return;
		}
		
		ImageView imageView = (ImageView) getView().findViewById(R.id.details_background_picture);
		
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inPurgeable = true;
	    
	    Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
	    imageView.setImageBitmap(bitmap);
	    
	    this.photoPath = photoPath;
	}
	
	/**
	 * Toggles the scrollview visibility to hide or show the details, to see the background image.
	 */
	public void toggleDetails() {
		ScrollView scrollView = (ScrollView) getView().findViewById(R.id.scrollView_details);
		if (scrollView.getVisibility() == View.VISIBLE)
			scrollView.setVisibility(View.INVISIBLE);
		else
			scrollView.setVisibility(View.VISIBLE);
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
	public boolean onLongClick(View view) {
		TextView code = (TextView) getView().findViewById(R.id.details_code);
		String codeStringValue = code.getText().toString();
		if (!TextUtils.isEmpty(codeStringValue)) {
			try {
				long codeValue = Long.parseLong(codeStringValue);
				this.linkedActivity.onWriteCodeEvent(codeValue);
			}
			catch (NumberFormatException e) {
				Toast.makeText(this.getActivity(), "Code not recognized.", Toast.LENGTH_LONG).show();
			}
		}
		return true;
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		if (this.photoPath != null)
			savedInstanceState.putString(DatabaseContract.BottleTable.COLUMN_NAME_IMAGE, this.photoPath);

		// Save the varieties:
		LinearLayout ll = (LinearLayout) getView().findViewById(R.id.details_varieties_layout);
		int count = ll.getChildCount();	// There is one title view and the auto complete (so put -2).
		ArrayList<String> varieties = new ArrayList<String>();

		for (int i = AMOUNT_OF_VIEWS_IN_VARIETIES_LINEAR_LAYOUT_TO_PASS; i < count; ++i) {
			TextView tv = (TextView) ll.getChildAt(i);
			varieties.add(tv.getText().toString());
		}

		savedInstanceState.putStringArrayList(VARIETIES_KEY, varieties);
	}

	/**
	 * The interface that must be implemented by the connected activity, 
	 * to allow this fragment to communicate with it.
	 * @author anthonydebruyn
	 *
	 */
	public interface BottleDetailsFragmentCallbacks {
		/**
		 * Called after the edit action button has been pushed.
		 */
		public void onEditEvent(long id);
		
		/**
		 * Called after a bottle has been deleted through the details fragment.
		 */
		public void onDeleteEvent();
		
		/**
		 * Called after the user long clicked the code layout in the details fragment, to launch the code generation (writer).
		 */
		public void onWriteCodeEvent(long code);
	}
}
