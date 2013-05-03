package cs169.project.thepantry;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;

public class HomePageActivity extends BasicMenuActivity implements TabListener, OnQueryTextListener {
	
	// adapter and viewpager for switching between sections - Recommendations, Recent, Favorited
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	
	// fragments for the pages
	HomePageRecommendationsFragment recs;
	StoredResultFragment recents;
	StoredResultFragment favs;
	
	// other stuff
	SearchModel sm = new SearchModel();
	DatabaseModel dm;
	private static final String DATABASE_NAME = "thepantry";
	final int NUM_RECOMMENDATIONS = 5;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homepage_pager);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
		// set up fragments
		ArrayList<Fragment> frags = new ArrayList<Fragment>();
		recs = new HomePageRecommendationsFragment();
		recents = new StoredResultFragment();
		recents.setType(ThePantryContract.RECENT);
		favs = new StoredResultFragment();
		favs.setType(ThePantryContract.FAVORITED);
		frags.add(recs);
		frags.add(recents);
		frags.add(favs);
		
		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		// Create the adapter that will return a fragment for each of the 3 sections
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), frags);
		mSectionsPagerAdapter.context = this;
		
		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOffscreenPageLimit(2);
		
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
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the options menu from XML
	    MenuInflater inflater = getSupportMenuInflater();
	    inflater.inflate(R.menu.home_page, menu);

	    // Get the SearchView and set it up
	    SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
	    searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default - doesnt work TODO
	    searchView.setSubmitButtonEnabled(true);
	    searchView.setMaxWidth(1000);
	    searchView.setOnQueryTextListener(this);
	    searchView.setQueryHint("Search by recipe, ingredient...");

	    return true;
	}
	
	// set up refresh button
	public boolean refreshclick(MenuItem item) {
		recs.refreshRecs();
		return true;
	}
	
	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}
	
	@Override
	public boolean onQueryTextSubmit(String query) {
		search(query);
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
	
	// create a search criteria and send it to a search task for searching Yummly
	public void search(String query) {
    	if (isOnline()) {
    		Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("query", query);
			startActivity(intent);
    	}
	}
}
