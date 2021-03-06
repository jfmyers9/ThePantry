package cs169.project.thepantry;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cs169.project.thepantry.ThePantryContract.Inventory;

public class HomePageRecommendationsFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	public static final String ARG_SECTION_NUMBER = "section_number";
	private static final String DATABASE_NAME = "thepantry";
	final int NUM_RECOMMENDATIONS = 5;
	ArrayList<SearchMatch> recommendations;
	ListView listView;
	SearchResultAdapter srAdapter;
	SearchModel sm = new SearchModel();
	DatabaseModel dm;
	FrameLayout mFrameOverlay;
	FrameLayout mErrorOverlay;
	RelativeLayout root;

	public HomePageRecommendationsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.pager_home_page,
				container, false);
		recommendations = new ArrayList<SearchMatch>();
		listView = (ListView) rootView.findViewById(R.id.matchList);
		mFrameOverlay = (FrameLayout) rootView.findViewById(R.id.overlay);
		mErrorOverlay = (FrameLayout) rootView.findViewById(R.id.erroroverlay);
		root = (RelativeLayout) rootView.findViewById(R.id.recommendations);
		srAdapter = new SearchResultAdapter(getActivity(), recommendations);   
		listView.setAdapter(srAdapter);
		srAdapter.notifyDataSetChanged();
		
		//when a search result item (recipe) is clicked
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			    // online check?
				String recipeID = (String)view.getTag();
		    	SearchCriteria searchcriteria = new SearchCriteria("recipe", recipeID);
		    	dm = new DatabaseModel(getActivity(), ThePantryContract.DATABASE_NAME);
		    	Recipe recipe = (Recipe) dm.getStorage(ThePantryContract.Recipe.TABLE_NAME, recipeID);
		    	
		    	if (recipe != null) {
		    		openRecipe(recipe);
		    	} else {
		    		SearchMatch sm = findSearchMatch(recipeID);
		    		if (sm != null) {
		    			dm.addStorage(ThePantryContract.SearchMatch.TABLE_NAME, findSearchMatch(recipeID));
		    		}
		    		new GetRecommendationsTask(getActivity(), "recipe").execute(searchcriteria);
		    	}
		    	dm.close();
			}
		});
		
		if (((HomePageActivity)getActivity()).isOnline()) {
			mErrorOverlay.setVisibility(View.GONE);
			getRecommendations();
		} else {
			mErrorOverlay.setVisibility(View.VISIBLE);
		}
		
		return rootView;
	}
	
	public void refreshRecs() {
		if (((HomePageActivity)getActivity()).isOnline()) {
			mErrorOverlay.setVisibility(View.GONE);
			getRecommendations();
		} else {
			mErrorOverlay.setVisibility(View.VISIBLE);
		}
	}
	
	public void openRecipe(Recipe recipe) {
		Intent intent = new Intent(getActivity(), RecipeActivity.class);
		intent.putExtra("result", recipe);
		intent.putExtra("type", "normal");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	public SearchMatch findSearchMatch(String recipeID) {
		for (SearchMatch searchmatch : recommendations) {
			if (searchmatch.id.equals(recipeID)) {
				return searchmatch;
			}
		}
		return null;
	}
	
	// create a search criteria for recommendations and search with it
		public void getRecommendations() {		
			dm = new DatabaseModel(getActivity(), DATABASE_NAME);
			ArrayList<String> youHave = dm.findItemNames(Inventory.TABLE_NAME);
			String query = "";
			SearchCriteria searchcriteria;
			int numToPick;
			if (youHave.size() > 0) {
				int numItems = youHave.size();
				// pick a random number between 1-5 or 1-#items to try a combination of items in your inventory to recommend recipes based on
				if (numItems < 5) {
					numToPick = (int)(Math.random() * numItems) + 1;
				} else {
					numToPick = (int)(Math.random() * 5) + 1;
				}
				//pick numToPick inventory items at random and recommend recipes based on them
				// TODO redo search for < 4 results
				for (int i = 0; i < numToPick; i++) {
					int loc = (int)(Math.random() * (numItems)); // numitems-1?
					query += ", " + youHave.get(loc); 
				}
				System.out.println(query);
				searchcriteria = new SearchCriteria("home", query, NUM_RECOMMENDATIONS);
			}
			else {
				//default
				searchcriteria = new SearchCriteria("home", "", NUM_RECOMMENDATIONS);
			}
			new GetRecommendationsTask(getActivity(), "home").execute(searchcriteria);
		}
	
	/** AsyncTask for getting recommendations
	 *
	 */
	public class GetRecommendationsTask extends AsyncTask<SearchCriteria, String, Storage> {
		
		String q;
		Context context;
		String type = "";
		ProgressDialog progressDialog;
		
		public GetRecommendationsTask(Context context) {
	    	this.context = context;
		}
		
		public GetRecommendationsTask(Context context, String type) {
	    	this.context = context;
	    	this.type = type;
		}
		
		//show progress wheel
		@Override
	    protected void onPreExecute()
	    {
			if (type == "home") {
				// show the overlay with the progress bar
				mFrameOverlay.setVisibility(View.VISIBLE);
			} else if (type == "recipe") {
				progressDialog = new ProgressDialog(getActivity());
				progressDialog.setMessage("Loading " + this.type + "...");
				progressDialog.show();
			}
	    };
		
		@Override
		protected Storage doInBackground(SearchCriteria... sc) {
			this.q = sc[0].q;
			return sm.search(sc[0]);
		}
		
		//update list of recommendations
		@Override
		protected void onPostExecute(Storage result) {
			if (type == "home") {
				//remove the overlay
				mFrameOverlay.setVisibility(View.GONE);
			} else if (type == "recipe") {
				progressDialog.dismiss();
			}
	        //display recommendation results
			if (result != null) {
				if (this.type == "home") {
					if (srAdapter.values.size() == 0) {
						recommendations = ((SearchResult)result).matches;
						if (recommendations.size() > 0) {
							srAdapter = new SearchResultAdapter(context, recommendations);   
							listView.setAdapter(srAdapter);
						} else {
							mErrorOverlay.setVisibility(View.VISIBLE);
						}
					} else {
						srAdapter.values = ((SearchResult)result).matches; 
						srAdapter.notifyDataSetChanged();
					}
				} else if (this.type == "recipe") {
					openRecipe((Recipe)result);
				}
			} else {
				mErrorOverlay.setVisibility(View.VISIBLE);
			}
		}
	}
}