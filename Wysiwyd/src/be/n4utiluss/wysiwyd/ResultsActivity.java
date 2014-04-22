package be.n4utiluss.wysiwyd;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import be.n4utiluss.wysiwyd.database.DatabaseContract;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

public class ResultsActivity extends Activity implements BottlesListFragment.BottlesListFragmentCallbacks,
														AbstractBottleInfoFragment.AbstractBottleInfoFragmentCallbacks,
														NewBottleFragment.NewBottleFragmentCallbacks,
														ModifyBottleFragment.ModifyBottleFragmentCallbacks,
														BottleDetailsFragment.BottleDetailsFragmentCallbacks {

	private static final int REQUEST_TAKE_PHOTO = 1;
	private static final String ALBUM_NAME = "Wysiwyd Bottles";
	
	private boolean twoPane = false;
	private BottlesListFragment bottlesListFragment;
	private String currentPhotoPath = null;
	private AbstractBottleInfoFragment abstractBottleInfoFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);

		if (savedInstanceState == null) {
			bottlesListFragment = new BottlesListFragment();
			bottlesListFragment.setArguments(getIntent().getExtras());
			Bundle arguments = getIntent().getExtras();
			
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			
			if (findViewById(R.id.results_main_container) != null) {
				arguments.putBoolean(BottlesListFragment.ACTIVATE_ON_ITEM_CLICK, false);
				bottlesListFragment.setArguments(arguments);

				transaction.add(R.id.results_main_container, bottlesListFragment);
			} else {
				arguments.putBoolean(BottlesListFragment.ACTIVATE_ON_ITEM_CLICK, true);
				bottlesListFragment.setArguments(arguments);

				transaction.add(R.id.results_list_container, bottlesListFragment);
				this.twoPane = true;
			}
			
			transaction.commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.results, menu);
		return true;
	}

	private void showNewBottleFragment(long id) {
		NewBottleFragment fragment = new NewBottleFragment();
		if (id > 0) {
			Bundle arguments = new Bundle();
			arguments.putLong(DatabaseContract.BottleTable._ID, id);
			fragment.setArguments(arguments);
		}
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		
		if (this.twoPane) {
			transaction.replace(R.id.results_details_container, fragment);
		} else {
			transaction.replace(R.id.results_main_container, fragment);
		}
		
		this.abstractBottleInfoFragment = fragment;
		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	public void onBottleSelected(long id) {
		Bundle arguments = new Bundle();
		arguments.putLong(DatabaseContract.BottleTable._ID, id);
		BottleDetailsFragment fragment = new BottleDetailsFragment();
		fragment.setArguments(arguments);
		
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		
		if (this.twoPane) {
			transaction.replace(R.id.results_details_container, fragment);
		} else {
			transaction.replace(R.id.results_main_container, fragment);
		}
		
		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	public void onNewBottleAdded() {
		this.bottlesListFragment.refreshList();
	}

	@Override
	public void onNewBottleEvent(long id) {
		this.showNewBottleFragment(id);
		if (twoPane)
			this.bottlesListFragment.setNewBottleButtonActivated(false);
	}

	@Override
	public void onNewBottleFragmentDismissed() {
		// Only in two pane mode, since for the single pane the fragment is already hidden, and so is the button.
		if (twoPane)
			this.bottlesListFragment.setNewBottleButtonActivated(true);
		this.abstractBottleInfoFragment = null;
	}

	@Override
	public void onBottleModified() {
		this.bottlesListFragment.refreshList();
	}

	@Override
	public void onModifyBottleFragmentDismissed() {
		// Only in two pane mode, since for the single pane the fragment is already hidden, and so is the button.
		if (twoPane)
			this.bottlesListFragment.setNewBottleButtonActivated(true);
		this.abstractBottleInfoFragment = null;
	}

	@Override
	public void onEditEvent(long id) {
		Bundle arguments = new Bundle();
		arguments.putLong(DatabaseContract.BottleTable._ID, id);
		ModifyBottleFragment fragment = new ModifyBottleFragment();
		fragment.setArguments(arguments);
		
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		
		if (this.twoPane) {
			transaction.replace(R.id.results_details_container, fragment);
		} else {
			transaction.replace(R.id.results_main_container, fragment);
		}
		
		this.abstractBottleInfoFragment = fragment;
		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	public void onTakePicture() {
		dispatchTakePictureIntent();
	}
	
	private void dispatchTakePictureIntent() {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    // Ensure that there's a camera activity to handle the intent
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null && this.isExternalStorageWritable()) {
	        // Create the File where the photo should go
	        File photoFile = null;
	        try {
	            photoFile = createImageFile();
	        } catch (IOException ex) {
	            // Error occurred while creating the File
	            Log.e("FILE CREATION", "Photo file not created");
	        }
	        // Continue only if the File was successfully created
	        if (photoFile != null) {
	        	
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
	                    Uri.fromFile(photoFile));
	            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
	        }
	    }
	}
	
	private File createImageFile() throws IOException {
	    // Create an image file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "JPEG_" + timeStamp + "_";
	    File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), ALBUM_NAME);
	    if (!storageDir.exists() || !storageDir.isDirectory()) {
	    	if (!storageDir.mkdirs()) {
	            Log.e("Picture folder creation", "Directory not created");
	            throw new IOException("Album Directory not created.");
	        }
	    }
	    File image = File.createTempFile(
	        imageFileName,  /* prefix */
	        ".jpg",         /* suffix */
	        storageDir      /* directory */
	    );

	    this.currentPhotoPath = image.getAbsolutePath();
	    return image;
	}
	
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
		
		switch (requestCode) {
		case REQUEST_TAKE_PHOTO:
			if (this.abstractBottleInfoFragment != null)
				this.abstractBottleInfoFragment.setPicture(this.currentPhotoPath);
			break;
		}
	}
}
