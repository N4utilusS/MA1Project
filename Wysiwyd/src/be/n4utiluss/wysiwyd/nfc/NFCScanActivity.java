package be.n4utiluss.wysiwyd.nfc;

import java.nio.charset.Charset;

import org.ndeftools.Message;
import org.ndeftools.MimeRecord;
import org.ndeftools.util.activity.NfcReaderActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import be.n4utiluss.wysiwyd.R;
import be.n4utiluss.wysiwyd.ResultsActivity;


public class NFCScanActivity extends NfcReaderActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nfcscan);

		// Enable detecting mode:
		setDetecting(true);
	}

	@Override
	protected void readEmptyNdefMessage() {
		Log.i("READ","empty");
		
	}

	@Override
	protected void readNdefMessage(Message message) {
		Log.i("READ","read");
		MimeRecord record = (MimeRecord) message.get(0);
		
		byte[] data = record.getData();
		String str = new String(data, Charset.forName("UTF-8"));

		try {
			long code = Long.parseLong(str);
			Intent listIntent = new Intent(this, ResultsActivity.class);
			listIntent.putExtra(ResultsActivity.BOTTLE_CODE, code);
			startActivity(listIntent);
		} catch (NumberFormatException e) {
			Context context = getApplicationContext();
			CharSequence text = "Not a valid number!";
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
		
	}

	@Override
	protected void readNonNdefMessage() {
		Log.i("READ","non");		
	}

	@Override
	protected void onNfcFeatureNotFound() {
		Log.i("READ","not");
	}

	@Override
	protected void onNfcStateChange(boolean arg0) {
		Log.i("READ","state");		
	}

	@Override
	protected void onNfcStateDisabled() {
		Log.i("READ","dis");		
	}

	@Override
	protected void onNfcStateEnabled() {
		Log.i("READ","en");
	}

}
