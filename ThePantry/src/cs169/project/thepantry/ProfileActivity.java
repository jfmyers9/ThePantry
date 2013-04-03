package cs169.project.thepantry;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import cs169.project.thepantry.HomePageActivity.HomeSearchTask;

public class ProfileActivity extends Activity {
	
	// assumes that when profile clicked, then extra holds username
	String username;
	
	TextView likes;
	TextView allergic;
	
	// get tables to be filled in
	ListView favorites;
	ListView history;
	
	// database access
	DatabaseModel dm;
	private static final String DATABASE_NAME = "thepantry";
	
	// set up listview for favorites and history
	ArrayList<SearchMatch> faves = new ArrayList<SearchMatch>();
	ArrayList<SearchMatch> cooked = new ArrayList<SearchMatch>();
	SearchResultAdapter favesAdapter;
	SearchResultAdapter cookAdapter;
	
	SearchModel sm = new SearchModel();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// login status of the user
		SharedPreferences shared_pref = PreferenceManager.getDefaultSharedPreferences(this);
		Boolean login_status = shared_pref.getBoolean("loggedin", false);
		
		// check if the user is logged in, if not take user to login screen
		if (login_status) {
			setContentView(R.layout.activity_profile);
			
			// set text for textviews
			TextView favorite_text = (TextView)findViewById(R.id.user_faves_text);
			favorite_text.setText("Favorites");
			TextView cooked_text = (TextView)findViewById(R.id.user_cooked_text);
			cooked_text.setText("Cooking History");
			
			// get user information
			username = shared_pref.getString("username", "");
			TextView name = (TextView)findViewById(R.id.username);
			name.setText(username);
			
			likes = (TextView)findViewById(R.id.user_likes);
			allergic = (TextView)findViewById(R.id.user_allergic);
			likes.setText("Likes: " + shared_pref.getString("likes", ""));
			allergic.setText("Allergies: " + shared_pref.getString("allergies", ""));
			
			favorites = (ListView)findViewById(R.id.user_favorites);
			favesAdapter = new SearchResultAdapter(this, faves);
			favorites.setAdapter(favesAdapter);
			
			setFavorites();
			
			history = (ListView)findViewById(R.id.user_cook_history);
			cookAdapter = new SearchResultAdapter(this, cooked);
			history.setAdapter(cookAdapter);
			
			setHistory();
			
			// fill in lists
			favesAdapter.notifyDataSetChanged();
			cookAdapter.notifyDataSetChanged();
			
			// when recipes are clicked
			favorites.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
				    // When clicked
					if (isOnline()){
			    		SearchCriteria searchcriteria = new SearchCriteria("recipe", (String)view.getTag());
			    		HomePageActivity hpActivity = new HomePageActivity();
			    		HomeSearchTask hstask = hpActivity.new HomeSearchTask(getApplicationContext(), "recipe");
			    		hstask.execute(searchcriteria);
					}
				}
			});
			
			history.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
				    // When clicked
					if (isOnline()){
			    		SearchCriteria searchcriteria = new SearchCriteria("recipe", (String)view.getTag());
			    		HomePageActivity hpActivity = new HomePageActivity();
			    		HomeSearchTask hstask = hpActivity.new HomeSearchTask(getApplicationContext(), "recipe");
			    		hstask.execute(searchcriteria);
					}
				}
			});
		} else {
			Intent i = new Intent(this, LoginActivity.class);
			startActivity(i);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}
	
	/**
	 * Allows users to edit their profile information.
	 * Should start a new activity
	 */
	public void editProfile() {
		// TODO: add button or link to layout
	}
	
	public boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnected()) {
	        return true;
	    }
	    return false;
	}
	
	/**
	 * Gets the user's favorite recipes and adds each to the table layout favorites
	 */
	public void setFavorites() {
		// TODO
		dm = new DatabaseModel(this, DATABASE_NAME);
		Cursor faves = dm.checkedItems(ThePantryContract.Recipe.TABLE_NAME, ThePantryContract.Recipe.FAVORITE);
		
		//LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//View recipe = inflater.inflate(R.layout.list_result, favorites);
		
		if (faves != null) {
			while (!faves.isAfterLast()) {
				String recipe_name = faves.getString(0);
				System.out.println(recipe_name);
				SearchCriteria sc = new SearchCriteria("recipe", recipe_name);
				new ProfileSearchTask(this, "fave").execute(sc);
				faves.moveToNext();
			}
			faves.close();
		}
		
	}
	
	/**
	 * Gets the user's cooking history and adds each to the table layout history
	 */
	public void setHistory() {
		// TODO
		dm = new DatabaseModel(this, DATABASE_NAME);
		Cursor cooked = dm.checkedItems(ThePantryContract.Recipe.TABLE_NAME, ThePantryContract.Recipe.COOKED);
		
		if (cooked != null) {
			while (!cooked.isAfterLast()) {
				String recipe_name = cooked.getString(0);
				System.out.println(recipe_name);
				SearchCriteria sc = new SearchCriteria("recipe", recipe_name);
				new ProfileSearchTask(this, "cook").execute(sc);
				cooked.moveToNext();
			}
			cooked.close();
		}
	}
	
	
	public class ProfileSearchTask extends AsyncTask<SearchCriteria, String, Storage> {
		
		String type = "";
		String q;
		Context context;
		
		public ProfileSearchTask(Context context) {
	    	this.context = context;
		}
		
		public ProfileSearchTask(Context context, String type) {
		    	this.context = context;
		    	this.type = type;
		}
		
		
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

	        
			if (result != null) {
				if (this.type == "fave") {
					if (favesAdapter.values.size() == 0) {
						faves = ((SearchResult)result).matches;
						favesAdapter = new SearchResultAdapter(ProfileActivity.this, faves);   
						favorites.setAdapter(favesAdapter);
					} else {
						favesAdapter.values = ((SearchResult)result).matches; 
						favesAdapter.notifyDataSetChanged();
					}
				}
				else if (this.type == "cook") {
					if (cookAdapter.values.size() == 0) {
						cooked = ((SearchResult)result).matches;
						cookAdapter = new SearchResultAdapter(ProfileActivity.this, cooked);   
						favorites.setAdapter(cookAdapter);
					} else {
						cookAdapter.values = ((SearchResult)result).matches; 
						cookAdapter.notifyDataSetChanged();
					}
				}
			}
			
		}
		
	}
	
}
