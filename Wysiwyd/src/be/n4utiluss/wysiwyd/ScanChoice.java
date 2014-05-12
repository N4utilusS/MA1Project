package be.n4utiluss.wysiwyd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import be.n4utiluss.wysiwyd.nfc.NFCScanActivity;
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

	public void QRorBarCode(View view) {
		if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			IntentIntegrator integrator = new IntentIntegrator(this);
			integrator.initiateScan();

		}
	}

	public void NFCCode(View view) {
		if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC)) {
			Intent nfcIntent = new Intent(this, NFCScanActivity.class);
			startActivity(nfcIntent);
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == IntentIntegrator.REQUEST_CODE) {
			IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
			
			if (resultCode == RESULT_OK && scanResult != null) {
				Intent listIntent = new Intent(this, ResultsActivity.class);
				
				long code;
				try {
					code = Long.parseLong(scanResult.getContents());
					listIntent.putExtra(BOTTLE_CODE, code);
					startActivity(listIntent);
				} catch (NumberFormatException e) {
					Context context = getApplicationContext();
					CharSequence text = "Not a valid number!";
					int duration = Toast.LENGTH_SHORT;

					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
				}
			}
		}
	}
}
