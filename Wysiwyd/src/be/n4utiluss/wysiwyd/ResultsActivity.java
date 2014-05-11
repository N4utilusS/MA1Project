package be.n4utiluss.wysiwyd;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import be.n4utiluss.wysiwyd.database.DatabaseContract;
import be.n4utiluss.wysiwyd.search.SearchFragment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.Fragment.SavedState;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;

/**
 * Activity managing the results of queries on bottles with certain properties.
 * Manages the fragments dealing with the list of bottles, the creation and editing of bottles.
 * @author anthonydebruyn
 *
 */
public class ResultsActivity extends Activity implements BottlesListFragment.BottlesListFragmentCallbacks,
														AbstractBottleInfoFragment.AbstractBottleInfoFragmentCallbacks,
														NewBottleFragment.NewBottleFragmentCallbacks,
														ModifyBottleFragment.ModifyBottleFragmentCallbacks,
														BottleDetailsFragment.BottleDetailsFragmentCallbacks,
														SearchFragment.SearchFragmentCallbacks {

	private static final int REQUEST_TAKE_PHOTO = 1;
	private static final String ALBUM_NAME = "Wysiwyd Bottles";
	private static final String LIST_FRAGMENT_TAG = "be.n4utiluss.wysiwyd.list_fragment_tag";
	private static final String ABSTRACT_BOTTLE_INFO_FRAGMENT_TAG = "be.n4utiluss.wysiwyd.abstract_bottle_info_fragment_tag";
	private static final String SEARCH_FRAGMENT_TAG = "be.n4utiluss.wysiwyd.search_fragment_tag";
	private static final String DETAILS_FRAGMENT_TAG = "be.n4utiluss.wysiwyd.details_fragment_tag";
	private static final String PICTURE_PATH = "be.n4utiluss.wysiwyd.picture_path";
	
	private BottlesListFragment bottlesListFragment;
	private String currentPhotoPath = null;
	private AbstractBottleInfoFragment abstractBottleInfoFragment;
	private SavedState searchFragmentSavedState;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);

		if (savedInstanceState == null) {
			bottlesListFragment = new BottlesListFragment();
			Bundle arguments = getIntent().getExtras();
			if (arguments == null)
				arguments = new Bundle();
			
			Bundle searchInfo = null;
			
			if (arguments.containsKey(ScanChoice.BOTTLE_CODE)) {
				searchInfo = new Bundle();
				searchInfo.putLong(SearchFragment.CODE, arguments.getLong(ScanChoice.BOTTLE_CODE));
			}
			bottlesListFragment.setSearchBundle(searchInfo);
			
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			
			if (findViewById(R.id.results_main_container) != null) {
				arguments.putBoolean(BottlesListFragment.ACTIVATE_ON_ITEM_CLICK, false);
				bottlesListFragment.setArguments(arguments);
				
				transaction.replace(R.id.results_main_container, bottlesListFragment, LIST_FRAGMENT_TAG);
			} else {
				arguments.putBoolean(BottlesListFragment.ACTIVATE_ON_ITEM_CLICK, true);
				bottlesListFragment.setArguments(arguments);

				transaction.replace(R.id.results_list_container, bottlesListFragment, LIST_FRAGMENT_TAG);
			}

			transaction.commit();
		} else {
			// Retrieve the fragment pointers after configuration change.
			this.bottlesListFragment = (BottlesListFragment) getFragmentManager().findFragmentByTag(LIST_FRAGMENT_TAG);
			this.abstractBottleInfoFragment = (AbstractBottleInfoFragment) getFragmentManager().findFragmentByTag(ABSTRACT_BOTTLE_INFO_FRAGMENT_TAG);
			
			/*// If an edit/new bottle panel is displayed, and we are in two pane mode, hide new bottle button of the list fragment.
			if (findViewById(R.id.results_list_container) != null && this.abstractBottleInfoFragment != null) {
				this.bottlesListFragment.setNewBottleButtonActivated(false);
			}*/
			
			if (savedInstanceState.containsKey(PICTURE_PATH))
				this.currentPhotoPath = savedInstanceState.getString(PICTURE_PATH);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.results, menu);
		return true;
	}

	/**
	 * Creates and displays the fragment used to create a new bottle.
	 * The id passed, if strictly positive, is used by the fragment to display the information from the associated bottle.
	 * @param id The id of the bottle we want the information from, to pre-fill the text fields.
	 */
	private void showNewBottleFragment(long id) {
		NewBottleFragment fragment = new NewBottleFragment();
		Bundle arguments = new Bundle();
		if (id > 0) {
			arguments.putLong(DatabaseContract.BottleTable._ID, id);
		}
		
		if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(ScanChoice.BOTTLE_CODE)) {
			arguments.putLong(ScanChoice.BOTTLE_CODE, getIntent().getExtras().getLong(ScanChoice.BOTTLE_CODE));
		}
		
		fragment.setArguments(arguments);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		
		if (findViewById(R.id.results_main_container) == null) {
			transaction.replace(R.id.results_details_container, fragment, ABSTRACT_BOTTLE_INFO_FRAGMENT_TAG);
		} else {
			transaction.replace(R.id.results_main_container, fragment, ABSTRACT_BOTTLE_INFO_FRAGMENT_TAG);
		}

		this.abstractBottleInfoFragment = fragment;
		// Names the current state with the details tag, since we can only arrive here by viewing details or nothing (in the 2pane mode).
		// We'll need this tag to suppress all the states above this one and this one in the backstack, to avoid the formation of chains of details fragments.
		transaction.addToBackStack(DETAILS_FRAGMENT_TAG);
		transaction.commit();
	}

	@Override
	public void onBottleSelected(long id) {
		Bundle arguments = new Bundle();
		arguments.putLong(DatabaseContract.BottleTable._ID, id);
		BottleDetailsFragment fragment = new BottleDetailsFragment();
		fragment.setArguments(arguments);
		
		getFragmentManager().popBackStack(DETAILS_FRAGMENT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		
		if (findViewById(R.id.results_main_container) == null) {
			transaction.replace(R.id.results_details_container, fragment);
			
		} else {
			transaction.replace(R.id.results_main_container, fragment);
			transaction.addToBackStack(null);
		}
		
		transaction.commit();
	}

	@Override
	public void onNewBottleAdded() {
		this.bottlesListFragment.refreshList();
	}

	@Override
	public void onNewBottleEvent(long id) {
		this.showNewBottleFragment(id);
	}

	@Override
	public void onNewBottleFragmentDismissed() {
		// Only in two pane mode, since for the single pane the fragment is already hidden, and so is the button.
		if (findViewById(R.id.results_details_container) != null) {
			this.bottlesListFragment.setNewBottleButtonActivated(true);
			this.bottlesListFragment.setSearchButtonActivated(true);
		}
		this.abstractBottleInfoFragment = null;
	}

	@Override
	public void onBottleModified() {
		this.bottlesListFragment.refreshList();
	}

	@Override
	public void onModifyBottleFragmentDismissed() {
		// Only in two pane mode, since for the single pane the fragment is already hidden, and so is the button.
		if (findViewById(R.id.results_details_container) != null) {
			this.bottlesListFragment.setNewBottleButtonActivated(true);
			this.bottlesListFragment.setSearchButtonActivated(true);
		}
		this.abstractBottleInfoFragment = null;
	}

	@Override
	public void onEditEvent(long id) {
		Bundle arguments = new Bundle();
		arguments.putLong(DatabaseContract.BottleTable._ID, id);
		ModifyBottleFragment fragment = new ModifyBottleFragment();
		fragment.setArguments(arguments);
		
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		
		if (findViewById(R.id.results_main_container) == null) {
			transaction.replace(R.id.results_details_container, fragment, ABSTRACT_BOTTLE_INFO_FRAGMENT_TAG);
		} else {
			transaction.replace(R.id.results_main_container, fragment, ABSTRACT_BOTTLE_INFO_FRAGMENT_TAG);
		}
		
		this.abstractBottleInfoFragment = fragment;
		// Names the current state with the details tag, since we can only arrive here by viewing details (in the 2pane mode).
		// We'll need this tag to suppress all the states above this one and this one in the backstack, to avoid the formation of chains of details fragments.
		transaction.addToBackStack(DETAILS_FRAGMENT_TAG);
		transaction.commit();
		
	}

	@Override
	public void onTakePicture() {
		dispatchTakePictureIntent();
	}
	
	/**
	 * Manages the picture capture by creating an intent and broadcasting it.
	 */
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
	        	
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
	            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
	        }
	    }
	}
	
	/**
	 * Creates a new image file to receive the new picture.
	 * The name will contain the date, plus a random number, thanks to the createTempFile() method.
	 * @return The new file.
	 * @throws IOException
	 */
	@SuppressLint("SimpleDateFormat")
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
	
	/**
	 * Checks if the external storage is writable.
	 * @return True if it is writable.
	 */
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
			if (resultCode == RESULT_OK && this.abstractBottleInfoFragment != null) {
				this.abstractBottleInfoFragment.setPicture(this.currentPhotoPath);
			} else {
				new File(this.currentPhotoPath).delete();
			}
			this.currentPhotoPath = null;
			break;
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		  super.onSaveInstanceState(savedInstanceState);
		  
		  if (this.currentPhotoPath != null)
			  savedInstanceState.putString(PICTURE_PATH, this.currentPhotoPath);
	}

	@Override
	public void onSearchEvent() {
		
		SearchFragment searchFragment = (SearchFragment) getFragmentManager().findFragmentByTag(SEARCH_FRAGMENT_TAG);
		
		if (this.getFragmentManager().findFragmentByTag(SEARCH_FRAGMENT_TAG) == null)
			searchFragment = new SearchFragment();
		searchFragment.setInitialSavedState(searchFragmentSavedState);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		
		if (findViewById(R.id.results_main_container) == null) {
			transaction.replace(R.id.results_details_container, searchFragment, SEARCH_FRAGMENT_TAG);
		} else {
			transaction.replace(R.id.results_main_container, searchFragment, SEARCH_FRAGMENT_TAG);
		}

		// Names the current state with the details tag, since we can only arrive here by viewing details or nothing (in the 2pane mode).
		// We'll need this tag to suppress all the states above this one and this one in the backstack, to avoid the formation of chains of details fragments.
		transaction.addToBackStack(DETAILS_FRAGMENT_TAG);
		transaction.commit();
	}

	@Override
	public void find(Bundle values) {
		this.bottlesListFragment.setSearchBundle(values);
		this.bottlesListFragment.refreshList();
		
		// Save the state of the fragment, so we don't have to re-type everything:
		SearchFragment searchFragment = (SearchFragment) getFragmentManager().findFragmentByTag(SEARCH_FRAGMENT_TAG);
		searchFragmentSavedState = getFragmentManager().saveFragmentInstanceState(searchFragment);
		
		// Remove the search fragment if we are in single pane mode:
		if (findViewById(R.id.results_main_container) != null) {
			getFragmentManager().popBackStack();
		}
	}

	@Override
	public void onSearchFragmentDismissed() {
		if (findViewById(R.id.results_details_container) != null) {	// Two pane
			this.bottlesListFragment.setSearchButtonActivated(true);
			this.bottlesListFragment.setNewBottleButtonActivated(true);
		}
	}

	@Override
	public void onSearchFragmentStarted() {
		if (findViewById(R.id.results_details_container) != null) {	// Two pane
			this.bottlesListFragment.setSearchButtonActivated(false);
			this.bottlesListFragment.setNewBottleButtonActivated(false);
		}
	}

	@Override
	public void onAbstractBottleInfoFragmentStarted() {
		if (findViewById(R.id.results_details_container) != null) {	// Two pane
			this.bottlesListFragment.setNewBottleButtonActivated(false);
			this.bottlesListFragment.setSearchButtonActivated(false);
		}
	}
}
