package be.n4utiluss.wysiwyd;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ResultsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);

		if (savedInstanceState == null) {
			BottlesListFragment bottlesListFragment = new BottlesListFragment();
			bottlesListFragment.setArguments(getIntent().getExtras());
			
			if (findViewById(R.id.results_main_container) != null) {
				getFragmentManager().beginTransaction().add(R.id.results_main_container, bottlesListFragment).commit();
			} else {
				getFragmentManager().beginTransaction().add(R.id.results_list_container, bottlesListFragment).commit();
				
			}
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.results, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	

}
