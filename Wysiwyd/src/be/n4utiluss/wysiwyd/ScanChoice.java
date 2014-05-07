package be.n4utiluss.wysiwyd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import be.n4utiluss.wysiwyd.zxing.integration.android.IntentIntegrator;
import be.n4utiluss.wysiwyd.zxing.integration.android.IntentResult;

/**
 * Activity managing the choice between all the scanning options, such as barcode/QR scanning or NFC.
 * @author anthonydebruyn
 *
 */
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
		
		return super.onOptionsItemSelected(item);
	}

	public void QRCode(View view) {
		//IntentIntegrator integrator = new IntentIntegrator(this);
		//integrator.initiateScan();
		
		// TODO Remove:
		Intent listIntent = new Intent(this, ResultsActivity.class);
		listIntent.putExtra(BOTTLE_CODE, 1l);
		startActivity(listIntent);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == IntentIntegrator.REQUEST_CODE) {
			IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
			
			if (scanResult != null) {
				TextView tv = (TextView) this.findViewById(R.id.testText);
				tv.setText(scanResult.toString());

				Intent listIntent = new Intent(this, ResultsActivity.class);
				listIntent.putExtra(BOTTLE_CODE, 1l);
				startActivity(listIntent);
			}
		}
	}
}
