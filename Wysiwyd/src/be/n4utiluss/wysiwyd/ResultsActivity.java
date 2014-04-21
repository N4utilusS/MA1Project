package be.n4utiluss.wysiwyd;

import be.n4utiluss.wysiwyd.database.DatabaseContract;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;

public class ResultsActivity extends Activity implements BottlesListFragment.BottlesListFragmentCallbacks,
														NewBottleFragment.NewBottleFragmentCallbacks,
														ModifyBottleFragment.ModifyBottleFragmentCallbacks,
														BottleDetailsFragment.BottleDetailsFragmentCallbacks {

	private boolean twoPane = false;
	private BottlesListFragment bottlesListFragment;
	
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
		
		transaction.addToBackStack(null);
		transaction.commit();
	}

}
