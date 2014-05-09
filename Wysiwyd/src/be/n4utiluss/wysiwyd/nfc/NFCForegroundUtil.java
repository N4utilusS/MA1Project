package be.n4utiluss.wysiwyd.nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcA;
import android.util.Log;

public class NFCForegroundUtil {
	private NfcAdapter nfcAdapter;

	private Activity activity;
	private IntentFilter intentFiltersArray[];
	private PendingIntent pendingIntent;
	private String techListsArray[][];

	public NFCForegroundUtil(Activity activity) {
		super();
		this.activity = activity; 
		nfcAdapter = NfcAdapter.getDefaultAdapter(activity.getApplicationContext());

		pendingIntent = PendingIntent.getActivity(activity, 
				0, 
				new Intent(activity, activity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 
				0);

		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

		try {
			ndef.addDataType("*/*");
		} catch (MalformedMimeTypeException e) {
			throw new RuntimeException("Unable to speciy */* Mime Type", e);
		}
		intentFiltersArray = new IntentFilter[] { ndef };

		techListsArray = new String[][] { new String[] { NfcA.class.getName() } };

	}

	public void enableForeground()
	{
		Log.d("demo", "Foreground NFC dispatch enabled");
		nfcAdapter.enableForegroundDispatch(
				activity, pendingIntent, intentFiltersArray, techListsArray);     
	}

	public void disableForeground()
	{
		Log.d("demo", "Foreground NFC dispatch disabled");
		nfcAdapter.disableForegroundDispatch(activity);
	}

	public NfcAdapter getNfcAdapter() {
		return nfcAdapter;
	}
}
