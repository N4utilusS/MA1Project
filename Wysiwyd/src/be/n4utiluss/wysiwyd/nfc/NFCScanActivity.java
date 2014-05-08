package be.n4utiluss.wysiwyd.nfc;

import java.io.IOException;
import java.nio.charset.Charset;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;
import be.n4utiluss.wysiwyd.R;

public class NFCScanActivity extends Activity {

	NFCForegroundUtil nfcForegroundUtil = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nfcscan);

		// Creates the foreground NFC dispatch system:
		nfcForegroundUtil = new NFCForegroundUtil(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nfcscan, menu);
		return true;
	}

	public void onPause() {
	    super.onPause();
	    nfcForegroundUtil.disableForeground();
	}   

	public void onResume() {
	    super.onResume();
	    nfcForegroundUtil.enableForeground();

	    if (!nfcForegroundUtil.getNfcAdapter().isEnabled())
	    {
	        Toast.makeText(getApplicationContext(), 
                    "Please activate NFC and press Back to return to the application!", 
                    Toast.LENGTH_LONG).show();
	        startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
	    }

	}
	
	public void onNewIntent(Intent intent) {
	    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
	    //do something with tagFromIntent
	    MifareUltralight mifare = MifareUltralight.get(tag);
        try {
            mifare.connect();
            byte[] payload = mifare.readPages(4);
            //return new String(payload, Charset.forName("US-ASCII"));
        } catch (IOException e) {
            Log.e("", "IOException while writing MifareUltralight message...", e);
        } finally {
            if (mifare != null) {
               try {
                   mifare.close();
               }
               catch (IOException e) {
                   Log.e("", "Error closing tag...", e);
               }
            }
        }
	}

}
