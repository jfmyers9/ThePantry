package cs169.project.thepantry;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.WindowManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import cs169.project.thepantry.HomePageActivity.HomeSearchTask;

public class ProfileActivity extends Activity {
	
	// login status of the user
	SharedPreferences shared_pref = PreferenceManager.getDefaultSharedPreferences(this);
	Boolean login_status = shared_pref.getBoolean("loggedin", false);
	
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
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
			/*favorites.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
				    // When clicked
					if (isOnline()){
			    		SearchCriteria searchcriteria = new SearchCriteria("recipe", (String)view.getTag());
			    		new HomeSearchTask(getApplicationContext(), "recipe").execute(searchcriteria);
					}
				}
			});
			
			history.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
				    // When clicked
					if (isOnline()){
			    		SearchCriteria searchcriteria = new SearchCriteria("recipe", (String)view.getTag());
			    		new HomeSearchTask(getApplicationContext(), "recipe").execute(searchcriteria);
					}
				}
			});*/
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
				//SearchCriteria sc = new SearchCriteria("recipe", recipe_name);
				//new HomeSearchTask(this, "search").execute(sc);
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
	}
	
}
