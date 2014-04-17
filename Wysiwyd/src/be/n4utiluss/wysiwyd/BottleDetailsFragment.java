package be.n4utiluss.wysiwyd;

import be.n4utiluss.wysiwyd.database.DatabaseHelper;
import be.n4utiluss.wysiwyd.database.DatabaseContract.*;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

public class BottleDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
	public static final String BOTTLE_ID = "be.n4utiluss.wysiwyd.Bottle_Id";

	private static final int MAIN_INFO_LOADER = 0;
	private static final int VARIETY_LOADER = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);	// So the onCreateOptionsMenu method is called, and the actions are set.

		this.getLoaderManager().initLoader(MAIN_INFO_LOADER, null, this);
		this.getLoaderManager().initLoader(VARIETY_LOADER, null, this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_bottle_details, container, false);

		return rootView;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.details, menu);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int idLoader, Bundle bundle) {
		long id = this.getArguments().getLong(BOTTLE_ID);
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
			if (cursor.moveToNext()) {
				if (this.getView() == null)
					Log.e("Details", "Null POINTER!!!!");
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
				quantity.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex(BottleTable.COLUMN_NAME_QUANTITY))));
				price.setText(Float.toString(cursor.getFloat(cursor.getColumnIndex(BottleTable.COLUMN_NAME_PRICE))));
				ratingBar.setRating(cursor.getInt(cursor.getColumnIndex(BottleTable.COLUMN_NAME_MARK)));
				colour.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex(BottleTable.COLUMN_NAME_COLOUR))));
				sugar.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex(BottleTable.COLUMN_NAME_SUGAR))));				
				effervescence.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex(BottleTable.COLUMN_NAME_EFFERVESCENCE))));
				addDate.setText(cursor.getString(cursor.getColumnIndex(BottleTable.COLUMN_NAME_ADD_DATE)));
				apogee.setText(cursor.getString(cursor.getColumnIndex(BottleTable.COLUMN_NAME_APOGEE)));
				location.setText(cursor.getString(cursor.getColumnIndex(BottleTable.COLUMN_NAME_LOCATION)));
				note.setText(cursor.getString(cursor.getColumnIndex(BottleTable.COLUMN_NAME_NOTE)));
				code.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex(BottleTable.COLUMN_NAME_CODE))));
			}
			break;

		case VARIETY_LOADER:
			if (this.getView() == null)
				Log.e("Details", "Null POINTER!!!!");
			LinearLayout ll = (LinearLayout) getView().findViewById(R.id.details_varieties_layout);
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
	public void onLoaderReset(Loader<Cursor> loader) {

	}


}
