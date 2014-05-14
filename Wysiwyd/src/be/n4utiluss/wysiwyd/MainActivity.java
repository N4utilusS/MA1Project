package be.n4utiluss.wysiwyd;

import java.io.File;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;

import be.n4utiluss.wysiwyd.database.DatabaseContract.BottleTable;
import be.n4utiluss.wysiwyd.database.DatabaseHelper;
import be.n4utiluss.wysiwyd.database.DatabaseContract;
import be.n4utiluss.wysiwyd.fonts.Fonts;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * The main activity class, displayed when the app is launched.
 * @author anthonydebruyn
 *
 */
public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getLoaderManager().initLoader(0, null, this);

		// Fonts
		TextView wysiwyd = (TextView) findViewById(R.id.main_wysiwyd);
		TextView scan = (TextView) findViewById(R.id.main_scan_text);
		TextView search = (TextView) findViewById(R.id.main_search_text);
		
		wysiwyd.setTypeface(Fonts.getFonts(this).robotoThin);
		scan.setTypeface(Fonts.getFonts(this).robotoThin);
		search.setTypeface(Fonts.getFonts(this).robotoThin);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		//int id = item.getItemId();
		
		return super.onOptionsItemSelected(item);
	}

	public void scan(View view){
    	Intent intent = new Intent(this, ScanChoice.class);
    	startActivity(intent);
    }
	
	public void search(View view) {
		Intent intent = new Intent(this, ResultsActivity.class);
    	startActivity(intent);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int idLoader, Bundle bundle) {
		
		SQLiteCursorLoader cursorLoader = new SQLiteCursorLoader(this,
				DatabaseHelper.getInstance(this), 
				DatabaseContract.SELECT + BottleTable._ID + DatabaseContract.COMMA_SEP + BottleTable.COLUMN_NAME_IMAGE +
				DatabaseContract.FROM + BottleTable.TABLE_NAME +
				DatabaseContract.WHERE + BottleTable.COLUMN_NAME_IMAGE + DatabaseContract.NOT + DatabaseContract.NULL +
				DatabaseContract.ORDER_BY + DatabaseContract.RANDOM +
				DatabaseContract.LIMIT + "1", 
				null);

		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		String photoPath = null;

		if (cursor.moveToFirst()) {
			photoPath = cursor.getString(cursor.getColumnIndex(BottleTable.COLUMN_NAME_IMAGE));
		} else {
			return;
		}

		// Check if file exists and is readable:
		File picture = new File(photoPath);
		if (!picture.exists() || !picture.isFile() || !picture.canRead()) {
			return;
		}

		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inPurgeable = true;
		Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
		
		ImageView imageView = (ImageView) findViewById(R.id.main_image_background);
		Bitmap finalBitmap = BlurBuilder.blur(this, bitmap);
		imageView.setImageBitmap(finalBitmap);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {}

}
