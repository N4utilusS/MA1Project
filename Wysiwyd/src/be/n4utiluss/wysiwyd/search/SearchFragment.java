package be.n4utiluss.wysiwyd.search;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Spinner;
import be.n4utiluss.wysiwyd.R;

public class SearchFragment extends Fragment {

	private SearchFragmentCallbacks linkedActivity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof SearchFragmentCallbacks)) {
			throw new IllegalStateException("Activity must implement fragment callbacks.");
		}

		this.linkedActivity = (SearchFragmentCallbacks) activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);	// So the onCreateOptionsMenu method is called, and the actions are set.
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_search, container, false);
		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		final RatingBar min = (RatingBar) getView().findViewById(R.id.search_mark_min);
		final RatingBar max = (RatingBar) getView().findViewById(R.id.search_mark_max);
		
		// Reset the values:
		
		// Listeners to prevent the min rating bar to go above the max one, and conversely:
		min.setOnRatingBarChangeListener(new OnRatingBarChangeListener(){

			@Override
			public void onRatingChanged(RatingBar arg0, float rating, boolean fromUser) {
				if (fromUser && max.getRating() < rating)
					max.setRating(rating);
			}
			
		});
		
		max.setOnRatingBarChangeListener(new OnRatingBarChangeListener(){

			@Override
			public void onRatingChanged(RatingBar arg0, float rating, boolean fromUser) {
				if (fromUser && min.getRating() > rating)
					min.setRating(rating);
			}
			
		});
		
		// Set the values to all for the properties:
		Spinner colour = (Spinner) getView().findViewById(R.id.search_colour);
		Spinner sugar = (Spinner) getView().findViewById(R.id.search_sugar);
		Spinner effervescence = (Spinner) getView().findViewById(R.id.search_effervescence);
		
		colour.setSelection(3);
		sugar.setSelection(4);
		effervescence.setSelection(4);
		
		this.linkedActivity.onSearchFragmentStarted();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.search, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		switch (id) {
		case R.id.search_action_find:
			Bundle info = getInfo();
			this.linkedActivity.find(info);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private Bundle getInfo() {
		
		return null;
	}
	
	@Override
	public void onStop() {
		super.onStop();
		this.linkedActivity.onSearchFragmentDismissed();
	}
	
	public interface SearchFragmentCallbacks {
		
		public void find(Bundle values);
		
		public void onSearchFragmentStarted();
		
		public void onSearchFragmentDismissed();
	}
}
