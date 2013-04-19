package cs169.project.thepantry;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
public class ProfileActivity extends BasicMenuActivity {
	
	// assumes that when profile clicked, then extra holds username
	String username;
	
	TextView likes;
	TextView allergic;
	
	// get tables to be filled in
	ListView history;
	
	// database access
	DatabaseModel dm;
	private static final String DATABASE_NAME = "thepantry";
	private final String LOGGED_IN = "log_in";
	
	// set up listview for history
	ArrayList<SearchMatch> cooked = new ArrayList<SearchMatch>();
	SearchResultAdapter cookAdapter;
	
	SearchModel sm = new SearchModel();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		// login status of the user
		SharedPreferences shared_pref = PreferenceManager.getDefaultSharedPreferences(this);
		String login_status = shared_pref.getString(LOGGED_IN, null);
		
		// check if the user is logged in, if not take user to login screen
		if (login_status != null) {
			setContentView(R.layout.activity_profile);
			
			IngredientSyncTask slSync = new IngredientSyncTask(this);
			slSync.execute(ThePantryContract.ShoppingList.TABLE_NAME, login_status);
			DatabaseModel dm = new DatabaseModel(this, "thepantry");
			IngredientSyncTask invSync = new IngredientSyncTask(this);
			invSync.execute(ThePantryContract.Inventory.TABLE_NAME, login_status);
			
			// set text for textviews
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
			
			history = (ListView)findViewById(R.id.user_cook_history);
			cookAdapter = new SearchResultAdapter(this, cooked);
			history.setAdapter(cookAdapter);
			
			// fill in history list
			setHistory();
			cookAdapter.notifyDataSetChanged();
			
			// when recipes are clicked

			history.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
				    // When clicked
					if (isOnline()){
			    		SearchCriteria searchcriteria = new SearchCriteria("recipe", (String)view.getTag());
			    		new HomePageRecommendationsFragment().new GetRecommendationsTask(getApplicationContext(), "recipe").execute(searchcriteria);
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
		getSupportMenuInflater().inflate(R.menu.profile, menu);
		return true;
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
	 * Gets the user's cooking history and adds each to the table layout history
	 */
	public void setHistory() {
		// TODO
		dm = new DatabaseModel(this, DATABASE_NAME);
		/*
		Cursor cooked = dm.checkedItems(ThePantryContract.Recipe.TABLE_NAME, ThePantryContract.Recipe.COOKED);
		
		if (cooked != null) {
			while (!cooked.isAfterLast()) {
				String recipe_name = cooked.getString(0);
				System.out.println(recipe_name);
				SearchCriteria sc = new SearchCriteria("recipe", recipe_name);
				//new ProfileSearchTask(this, "cook").execute(sc);
				cooked.moveToNext();
			}
			cooked.close();
		}*/
		ArrayList<Storage> cookedRecipes = dm.getCookOrFav(ThePantryContract.SearchMatch.TABLE_NAME, ThePantryContract.SearchMatch.COOKED);
		dm.close();
		for (Storage recipe : cookedRecipes) {
			cooked.add((SearchMatch)recipe);
		}
		System.out.println("Can I cast Recipe to SearchMatch object");
	}
	
	
//	public class ProfileSearchTask extends AsyncTask<SearchCriteria, String, Storage> {
//		
//		String type = "";
//		String q;
//		Context context;
//		
//		public ProfileSearchTask(Context context) {
//	    	this.context = context;
//		}
//		
//		public ProfileSearchTask(Context context, String type) {
//		    	this.context = context;
//		    	this.type = type;
//		}
//		
//		
//		@Override
//		protected Storage doInBackground(SearchCriteria... sc) {
//			this.type = sc[0].type;
//			this.q = sc[0].q;
//			return sm.search(sc[0]);
//		}
//		
//		//update list of recommendations using a SearchResultsAdapter
//		//or open a SearchResultsActivity if a search was made
//		//or open a recipe page if a recipe was selected
//		@Override
//		protected void onPostExecute(Storage result) {
//
//	        
//			if (result != null) {
//				if (this.type == "fave") {
//					if (favesAdapter.values.size() == 0) {
//						faves = ((SearchResult)result).matches;
//						favesAdapter = new SearchResultAdapter(ProfileActivity.this, faves);   
//						favorites.setAdapter(favesAdapter);
//					} else {
//						favesAdapter.values = ((SearchResult)result).matches; 
//						favesAdapter.notifyDataSetChanged();
//					}
//				}
//				else if (this.type == "cook") {
//					if (cookAdapter.values.size() == 0) {
//						cooked = ((SearchResult)result).matches;
//						cookAdapter = new SearchResultAdapter(ProfileActivity.this, cooked);   
//						favorites.setAdapter(cookAdapter);
//					} else {
//						cookAdapter.values = ((SearchResult)result).matches; 
//						cookAdapter.notifyDataSetChanged();
//					}
//				}
//			}
//			
//		}
//		
//	}
	
}
