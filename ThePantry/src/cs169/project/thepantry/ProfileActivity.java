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
	private static final String DATABASE_NAME = ThePantryContract.DATABASE_NAME;
	private final String LOGGED_IN = "log_in";
	
	// set up listview for history
	ArrayList<SearchMatch> cooked = new ArrayList<SearchMatch>();
	SearchResultAdapter cookAdapter;
	
	SearchModel sm = new SearchModel();
	
	@Override
	public void onResume()
	{  // After a pause OR at startup
	    super.onResume();
	    //Refresh data sets when you return
		SharedPreferences shared_pref = PreferenceManager.getDefaultSharedPreferences(this);
		String login_status = shared_pref.getString(LOGGED_IN, null);
	    if (login_status != null) {
		    cooked.clear();
		    setHistory();
			cookAdapter = new SearchResultAdapter(this, cooked);
			history = (ListView)findViewById(R.id.user_cook_history);
			history.setAdapter(cookAdapter);
			cookAdapter.notifyDataSetChanged();
	    } else {
	    	super.finish();
	    }
	}
	
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
			IngredientSyncTask invSync = new IngredientSyncTask(this);
			invSync.execute(ThePantryContract.Inventory.TABLE_NAME, login_status);
			UserRecipeSyncTask urSync = new UserRecipeSyncTask(this);
			urSync.execute(ThePantryContract.CookBook.TABLE_NAME, login_status);
			DatabaseModel dm = new DatabaseModel(this, DATABASE_NAME);
			
			// set text for textviews
			TextView cooked_text = (TextView)findViewById(R.id.user_cooked_text);
			cooked_text.setText("Cooking History");
			
			// get user information
			username = shared_pref.getString("username", "Username");
			TextView name = (TextView)findViewById(R.id.username);
			name.setText(username);
			
			likes = (TextView)findViewById(R.id.user_likes);
			allergic = (TextView)findViewById(R.id.user_allergic);
			likes.setText("Likes: " + shared_pref.getString("likes", "You have not specified any ingredients you like."));
			allergic.setText("Allergies: " + shared_pref.getString("allergies", "You have not specified any allergies."));
			
			history = (ListView)findViewById(R.id.user_cook_history);
			cookAdapter = new SearchResultAdapter(this, cooked);
			history.setAdapter(cookAdapter);
			
			// fill in history list
			setHistory();
			cookAdapter.notifyDataSetChanged();
			
			// when recipes are clicked

			history.setOnItemClickListener(new OnItemClickListener() {
				/*public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
				    // When clicked
					if (isOnline()){
			    		SearchCriteria searchcriteria = new SearchCriteria("recipe", (String)view.getTag());
			    		new HomePageRecommendationsFragment().new GetRecommendationsTask(getApplicationContext(), "recipe").execute(searchcriteria);
					}
				}*/
				
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
				    // online check?
					String recipeID = (String)view.getTag();
			    	//SearchCriteria searchcriteria = new SearchCriteria("recipe", recipeID);
			    	DatabaseModel dm = makeDM();
			    	Recipe recipe = (Recipe) dm.getStorage(ThePantryContract.Recipe.TABLE_NAME, recipeID);
			    	
			    	if (recipe != null) {
			    		openRecipe(recipe);
			    	}
			    	dm.close();
				}
			});
			
		} else {
			Intent i = new Intent(this, LoginActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}
	
	public DatabaseModel makeDM() {
		return new DatabaseModel(this, DATABASE_NAME);
	}
	
	public void openRecipe(Recipe recipe) {
		Intent intent = new Intent(this, RecipeActivity.class);
		intent.putExtra("result", recipe);
		intent.putExtra("type", "normal");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
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
	
}
