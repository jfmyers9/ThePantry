package cs169.project.thepantry;

import java.util.ArrayList;
import java.util.Locale;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.view.Menu;

import cs169.project.thepantry.ThePantryContract.Inventory;

public class HomePageActivity extends BasicMenuActivity implements TabListener {
	
	// adapter and viewpager for switching between sections - Recommendations, Recent, Favorited
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	
	// fragments for the pages
	HomePageRecommendationsFragment recs;
	HomePageSectionFragment recents;
	HomePageSectionFragment favs;
	
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
		recents = new HomePageSectionFragment();
		favs = new HomePageSectionFragment();
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
		
		if (isOnline()) {
			//getRecommendations();
		} else {
			//TODO: display an "offline" message
		}
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
	public void search(View view) throws Exception {
		EditText searchText = (EditText) findViewById(R.id.search_text);
    	String search = searchText.getText().toString();
    	if (isOnline()) {
    		SearchCriteria searchcriteria = new SearchCriteria("search", search);
    		new HomeSearchTask(getApplicationContext(), "search").execute(searchcriteria);
    	}
	}
	
	// create a search criteria for recommendations and search with it
	public void getRecommendations() {		
		dm = new DatabaseModel(this, DATABASE_NAME);
		Cursor youHave = dm.findAllItems(Inventory.TABLE_NAME);

		String query = "";
		SearchCriteria searchcriteria;
		int numToPick;
		if (youHave != null && youHave.moveToFirst()) {
			int numItems = youHave.getCount();
			// pick a random number between 1-5 or 1-#items to try a combination of items in your inventory to recommend recipes based on
			if (numItems < 5) {
				numToPick = (int)(Math.random() * numItems) + 1;
			} else {
				numToPick = (int)(Math.random() * 5) + 1;
			}
			//pick numToPick inventory items at random and recommend recipes based on them
			// TODO redo search for < 4 results
			for (int i = 0; i < numToPick; i++) {
				int loc = (int)(Math.random() * (numItems));
				while (loc > 0) {
					youHave.moveToNext();
					loc--;
				}
				query += ", " + youHave.getString(Inventory.ITEMIND);
				youHave.moveToFirst();
			}
			searchcriteria = new SearchCriteria("home", query, NUM_RECOMMENDATIONS);
		}
		else {
			//default
			searchcriteria = new SearchCriteria("home", "", NUM_RECOMMENDATIONS);
		}
		new HomeSearchTask(getApplicationContext(), "home").execute(searchcriteria);
	}
	
	/** AsyncTask for performing search
	 *
	 */
	public class HomeSearchTask extends AsyncTask<SearchCriteria, String, Storage> {
		
		String type = "";
		String q;
		Context context;
		FrameLayout mFrameOverlay;
		ProgressDialog progressDialog;
		
		public HomeSearchTask(Context context) {
	    	this.context = context;
		}
		
		public HomeSearchTask(Context context, String type) {
		    	this.context = context;
		    	this.type = type;
		}
		
		//show progress wheel
		@Override
	    protected void onPreExecute()
	    {
			/*// show the overlay with the progress bar
			if (this.type == "home") {
				mFrameOverlay = (FrameLayout)findViewById(R.id.overlay);
				mFrameOverlay.setVisibility(View.VISIBLE);
			} else {
				progressDialog = new ProgressDialog(HomePageActivity.this);
				progressDialog.setMessage("Loading " + this.type + "...");
				progressDialog.show();
			}*/
	    };
		
		@Override
		protected Storage doInBackground(SearchCriteria... sc) {
			this.type = sc[0].type;
			this.q = sc[0].q;
			return sm.search(sc[0]);
		}
		
		//update list of recommendations using a SearchResultsAdapter
		//or open a SearchResultsActivity if a search was made
		//or open a recipe page if a recipe was selected
		@Override
		protected void onPostExecute(Storage result) {
			
			//remove the overlay
			/*if (this.type == "home") {
				mFrameOverlay.setVisibility(View.GONE);
				
			} else {
				progressDialog.dismiss();
			}*/
	        
			if (result != null) {
				if (this.type == "home") {
					/*if (srAdapter.values.size() == 0) {
						recommendations = ((SearchResult)result).matches;
						srAdapter = new SearchResultAdapter(HomePageActivity.this, recommendations);   
						listView.setAdapter(srAdapter);
					} else {
						srAdapter.values = ((SearchResult)result).matches; 
						srAdapter.notifyDataSetChanged();
					}*/
					((HomePageSectionFragment)mSectionsPagerAdapter.fragments.get(0)).matches = ((SearchResult)result).matches;
					((HomePageSectionFragment)mSectionsPagerAdapter.fragments.get(0)).srAdapter.notifyDataSetChanged();
					mSectionsPagerAdapter.notifyDataSetChanged();
				}
				else if (this.type == "search") {
					Intent intent = new Intent(context, SearchResultsActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("result", result);
					intent.putExtra("currentSearch", this.q);
					startActivity(intent);
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
	}

}
