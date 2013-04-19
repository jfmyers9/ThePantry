package cs169.project.thepantry;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;

import cs169.project.thepantry.SearchResultsFragment.SearchTask;

public class SearchResultsActivity extends BasicMenuActivity implements TabListener, OnQueryTextListener {

	// adapter and viewpager for switching between sections - Search Results, User Results
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	
	ArrayList<SearchMatch> matches;
	//public for testing
	public SearchResultAdapter srAdapter;
	public SearchModel sm = new SearchModel();
	public ListView listView;
	String query = "";
	
	// fragments for the 2 results - Yummly and User
	SearchResultsFragment searchresults;
	SearchResultsFragment userresults;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results_pager);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
		// set up fragments
		ArrayList<Fragment> frags = new ArrayList<Fragment>();
		searchresults = new SearchResultsFragment();
		userresults = new SearchResultsFragment();
		frags.add(searchresults);
		frags.add(userresults);
		
		// Set up the action bar 
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		// Create the adapter that will return a fragment for each of the 3 sections
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), frags);
		mSectionsPagerAdapter.context = this;
		
		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});
				
		// For each of the sections, add a tab to the action bar
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i+3)) //search results are case 3 and 4
					.setTabListener(this));
		}
		
		//get search query from intent
		query = getIntent().getExtras().getString("query");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the options menu from XML
	    MenuInflater inflater = getSupportMenuInflater();
	    inflater.inflate(R.menu.search_results, menu);

	    // Get the SearchView and set it up
	    // submit the original query to search
	    SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
	    searchView.setSubmitButtonEnabled(true);
	    searchView.setMaxWidth(1000);
	    searchView.setOnQueryTextListener(this);
	    searchView.setQuery(query,true);

	    return true;
	}
	
	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}
	
	@Override
	public boolean onQueryTextSubmit(String query) {
		SearchCriteria searchcriteria = new SearchCriteria("search", query);
		searchresults.new SearchTask(getBaseContext(), "search").execute(searchcriteria);
		//userresults.new SearchTask(getBaseContext(), "search").execute(searchcriteria);
		return true;
	}
	
	@Override
	public void onTabSelected(Tab tab,
			android.support.v4.app.FragmentTransaction ft) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab,
		android.support.v4.app.FragmentTransaction ft) {
	}

	@Override
	public void onTabReselected(Tab tab,
			android.support.v4.app.FragmentTransaction ft) {
	}
	
/*public class SearchTask extends AsyncTask<SearchCriteria, String, Storage> {
		
		String type = "";
		String q;
		Context context;
		FrameLayout mFrameOverlay;
		ProgressDialog progressDialog;
		
		public SearchTask(Context context) {
		    	this.context = context;
		}
		
		public SearchTask(Context context, String type) {
	    	this.context = context;
	    	this.type = type;
		}
		
		//show progress wheel
		@Override
	    protected void onPreExecute() {
			progressDialog = new ProgressDialog(SearchResultsActivity.this);
			progressDialog.setMessage("Loading " + this.type + "...");
			progressDialog.show();
	    };
		
		@Override
		protected Storage doInBackground(SearchCriteria... sc) {
			this.type = sc[0].type;
			this.q = sc[0].q;
			return sm.search(sc[0]);
		}
		
		@Override
		protected void onPostExecute(Storage result) {
			
		progressDialog.dismiss();
			
		//TODO: bug if results list goes from small to smaller? index out of bounds
		if (result != null) {
			if (this.type == "search") {
				if (srAdapter.values.size() == 0) {
					matches = ((SearchResult)result).matches;
					srAdapter = new SearchResultAdapter(SearchResultsActivity.this, matches);   
					listView.setAdapter(srAdapter);
					listView.bringToFront();
				}
				else {
					srAdapter.values = ((SearchResult)result).matches;
					srAdapter.notifyDataSetChanged();
					listView.smoothScrollToPosition(0);
				}
			}
			else if (this.type == "recipe") {
				Intent intent = new Intent(context, RecipeActivity.class);
				intent.putExtra("result", result);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		}
		}
	}*/
}
