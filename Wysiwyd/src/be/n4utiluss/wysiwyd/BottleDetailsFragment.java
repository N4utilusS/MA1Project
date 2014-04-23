package be.n4utiluss.wysiwyd;

import java.io.File;

import be.n4utiluss.wysiwyd.database.DatabaseHelper;
import be.n4utiluss.wysiwyd.database.DatabaseContract.*;
import be.n4utiluss.wysiwyd.fonts.Fonts;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class BottleDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

	private static final int MAIN_INFO_LOADER = 0;
	private static final int BOTTLE_VARIETIES_LOADER = 1;
	private BottleDetailsFragmentCallbacks linkedActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);	// So the onCreateOptionsMenu method is called, and the actions are set.
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_bottle_details, container, false);

		// (Re)start the loaders here, since this method is the first one called after we get back from the new bottle fragment, 
		// after we popped the previous state from the stack.

		if (this.getArguments().containsKey(BottleTable._ID)) {
			getLoaderManager().restartLoader(MAIN_INFO_LOADER, null, this);
			getLoaderManager().restartLoader(BOTTLE_VARIETIES_LOADER, null, this);
		}
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Fonts
		TextView name = (TextView) getView().findViewById(R.id.details_name);
		name.setTypeface(Fonts.getFonts(getActivity()).chopinScript);
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
		default:
			return super.onOptionsItemSelected(item);
		}
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
			vintage.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex(BottleTable.COLUMN_NAME_VINTAGE))));
			region.setText(cursor.getString(cursor.getColumnIndex(BottleTable.COLUMN_NAME_REGION)));
			quantity.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex(BottleTable.COLUMN_NAME_QUANTITY))) + " bottles");
			price.setText("Price: " + Float.toString(cursor.getFloat(cursor.getColumnIndex(BottleTable.COLUMN_NAME_PRICE))));
			ratingBar.setRating(cursor.getInt(cursor.getColumnIndex(BottleTable.COLUMN_NAME_MARK)));

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
			location.setText(cursor.getString(cursor.getColumnIndex(BottleTable.COLUMN_NAME_LOCATION)));
			note.setText(cursor.getString(cursor.getColumnIndex(BottleTable.COLUMN_NAME_NOTE)));
			code.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex(BottleTable.COLUMN_NAME_CODE))));
			
			int pictureColumnIndex = cursor.getColumnIndex(BottleTable.COLUMN_NAME_IMAGE);
			if (!cursor.isNull(pictureColumnIndex)) {
				this.setPicture(cursor.getString(pictureColumnIndex));
			} else {
				setBackground();
			}
		}
	}
	
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
	
	protected void addVarietyToLayout(String text, LinearLayout ll) {
		TextView tv = new TextView(getActivity());
		tv.setText(text);
		ll.addView(tv);
	}
	
	private void setBackground() {
		ScrollView scrollView = (ScrollView) getView().findViewById(R.id.scrollView_details);
		scrollView.setBackgroundColor(getResources().getColor(R.color.Lavender));
	}
	
	public void setPicture(String photoPath) {
		
		File picture = new File(photoPath);
		if (!picture.exists() || !picture.isFile() || !picture.canRead()) {
			setBackground();
			return;
		}
		
		// Get the dimension of the view
		ImageView imageView = (ImageView) getView().findViewById(R.id.details_background_picture);
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
	    
	 // Put padding to see the image:
	    LinearLayout ll = (LinearLayout) getView().findViewById(R.id.details_linear_layout);
	    int top = (int) ((float) targetW/ (float) photoW * bmOptions.outHeight);
	    ll.setPadding(0, top, 0, 0);
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
	}
}
