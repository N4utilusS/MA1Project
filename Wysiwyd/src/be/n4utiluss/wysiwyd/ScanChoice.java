package be.n4utiluss.wysiwyd;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import be.n4utiluss.wysiwyd.DAO.Bottle;
import be.n4utiluss.wysiwyd.DAO.DAO;
import be.n4utiluss.wysiwyd.DAO.DAOFactory;
import be.n4utiluss.wysiwyd.DAO.requests.RequestBottlesByCode;
import be.n4utiluss.wysiwyd.database.DatabaseManager;
import be.n4utiluss.wysiwyd.zxing.integration.android.IntentIntegrator;
import be.n4utiluss.wysiwyd.zxing.integration.android.IntentResult;

public class ScanChoice extends Activity {
	public final static String BOTTLE_CODE = "be.n4utiluss.wysiwyd.BOTTLE_CODE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan_choice);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scan_choice, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void QRCode(View view) {
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.initiateScan();
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		  IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		  if (scanResult != null) {
		    TextView tv = (TextView) this.findViewById(R.id.testText);
		    tv.setText(scanResult.toString());
		    
		    
		    
		    // Check if the cursor is empty, which means the user is adding a bottle.
		    if (cursor.isAfterLast()){
		    	
		    } else {	// If not empty, list the results.
		    	Intent listIntent = new Intent(this, ResultsActivity.class);
		    	listIntent.putExtra(BOTTLE_CODE, 1l);
		    }
		  }
		  // else continue with any other code you need in the method
		}
}
