package be.n4utiluss.wysiwyd;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ResultsActivity extends Activity implements BottlesListFragment.BottlesListFragmentCallbacks,
														NewBottleFragment.NewBottleFragmentCallbacks{

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
			
			if (findViewById(R.id.results_main_container) != null) {
				arguments.putBoolean(BottlesListFragment.ACTIVATE_ON_ITEM_CLICK, false);
				bottlesListFragment.setArguments(arguments);

				getFragmentManager().beginTransaction().add(R.id.results_main_container, bottlesListFragment).commit();
			} else {
				arguments.putBoolean(BottlesListFragment.ACTIVATE_ON_ITEM_CLICK, true);
				bottlesListFragment.setArguments(arguments);

				getFragmentManager().beginTransaction().add(R.id.results_list_container, bottlesListFragment).commit();
				this.twoPane = true;
			}
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.results, menu);
		return true;
	}

	private void showNewBottleFragment() {
		NewBottleFragment fragment = new NewBottleFragment();
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
		arguments.putLong(BottleDetailsFragment.BOTTLE_ID, id);
		BottleDetailsFragment fragment = new BottleDetailsFragment();
		fragment.setArguments(arguments);
		
		
		if (this.twoPane) {
			getFragmentManager().beginTransaction()
			.replace(R.id.results_details_container, fragment).commit();
		} else {
			getFragmentManager().beginTransaction()
			.replace(R.id.results_main_container, fragment).commit();
		}
	}

	@Override
	public void onNewBottleAdded() {
		this.bottlesListFragment.refreshList();
	}

	@Override
	public void onNewBottleButtonPushed() {
		this.showNewBottleFragment();
	}

	

}
