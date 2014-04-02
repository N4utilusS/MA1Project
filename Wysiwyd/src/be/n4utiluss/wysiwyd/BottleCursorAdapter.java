package be.n4utiluss.wysiwyd;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import be.n4utiluss.wysiwyd.database.DatabaseContract;

/**
 * This class is used to replace the simpleCursorAdapter, to allow the use of rating bars in the list.
 * It inheritates from {@link CursorAdapter}.
 * @author anthonydebruyn
 *
 */
public class BottleCursorAdapter extends CursorAdapter {

	public BottleCursorAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		
		// Set the name of the bottle.
		TextView name = (TextView) view.findViewById(R.id.list_element_bottle_name);
		name.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.BottleTable.COLUMN_NAME_NAME)));
		
		// Set the vintage of the bottle.
		TextView vintage = (TextView) view.findViewById(R.id.list_element_bottle_vintage);
		vintage.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.BottleTable.COLUMN_NAME_VINTAGE)));
		
		// Set the mark of the bottle.
		RatingBar rating = (RatingBar) view.findViewById(R.id.list_element_bottle_mark);
		rating.setRating(cursor.getInt(cursor.getColumnIndex(DatabaseContract.BottleTable.COLUMN_NAME_MARK)));
		
		// Set the quantity of the bottle.
		TextView quantity = (TextView) view.findViewById(R.id.list_element_bottle_quantity);
		quantity.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex(DatabaseContract.BottleTable.COLUMN_NAME_QUANTITY))));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		final View view = LayoutInflater.from(context).inflate(R.layout.list_element, viewGroup, false);
		return view;
	}

}
