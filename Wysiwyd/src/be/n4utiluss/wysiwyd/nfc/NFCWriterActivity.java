package be.n4utiluss.wysiwyd.nfc;

import java.io.UnsupportedEncodingException;

import org.ndeftools.Message;
import org.ndeftools.MimeRecord;
import org.ndeftools.externaltype.AndroidApplicationRecord;
import org.ndeftools.util.activity.NfcTagWriterActivity;

import android.nfc.NdefMessage;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import be.n4utiluss.wysiwyd.R;
import be.n4utiluss.wysiwyd.ResultsActivity;

public class NFCWriterActivity extends NfcTagWriterActivity {
	
	private long code = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nfcwriter);
		
		// Save the code to get it later if no saved state, since the intent is going to be replaced (see onNewIntent()):
		if (savedInstanceState == null)
			this.code = getIntent().getExtras().getLong(ResultsActivity.BOTTLE_CODE);
		else
			this.code = savedInstanceState.getLong(ResultsActivity.BOTTLE_CODE);
		
		// Enable detecting mode:
		setDetecting(true);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		savedInstanceState.putLong(ResultsActivity.BOTTLE_CODE, this.code);
	}

	@Override
	protected NdefMessage createNdefMessage() {
		
		try {
			AndroidApplicationRecord aar = new AndroidApplicationRecord();
			aar.setPackageName("be.n4utiluss.wysiwyd");
			
			String codeString = Long.toString(this.code);
			
			MimeRecord mimeRecord = new MimeRecord();
			mimeRecord.setMimeType("text/plain");
			mimeRecord.setData(codeString.getBytes("UTF-8"));
			
			Message message = new Message();
			message.add(mimeRecord);
			message.add(aar);
			
			return message.getNdefMessage();
		} catch (UnsupportedEncodingException e) {
			Log.e("ENCODING", "Encoding not supported!");
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void writeNdefCannotWriteTech() {
		Toast.makeText(this, "Cannot write to the tag!", Toast.LENGTH_LONG).show();
	}

	@Override
	protected void writeNdefFailed(Exception arg0) {
		Toast.makeText(this, "Failed to write!", Toast.LENGTH_LONG).show();		
	}

	@Override
	protected void writeNdefNotWritable() {
		Toast.makeText(this, "NFC tag not writable!", Toast.LENGTH_LONG).show();
	}

	@Override
	protected void writeNdefSuccess() {
		Toast.makeText(this, "NFC tag written!", Toast.LENGTH_LONG).show();
	}

	@Override
	protected void writeNdefTooSmall(int required, int capacity) {
		Toast.makeText(this, "NFC tag too small: " + required + " required (" + capacity + " available).", Toast.LENGTH_LONG).show();		
	}

	@Override
	protected void onNfcFeatureNotFound() {
		
	}

	@Override
	protected void onNfcStateChange(boolean arg0) {
		
	}

	@Override
	protected void onNfcStateDisabled() {
		startNfcSettingsActivity();
	}

	@Override
	protected void onNfcStateEnabled() {
		
	}

}
