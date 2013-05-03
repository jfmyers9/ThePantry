package cs169.project.thepantry;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import cs169.project.thepantry.R;

public class StoredResultFragment extends Fragment {

	public static final String ARG_SECTION_NUMBER = "section_number";
	ArrayList<SearchMatch> matches;
	ListView matchlist;
	SearchResultAdapter srAdapter;
	SearchModel sm = new SearchModel();
	DatabaseModel dm;
	FrameLayout mFrameOverlay;
	int type;

	public StoredResultFragment() {
	}

	public void setType(int type) {
		this.type = type;
	}

	public ArrayList<SearchMatch> storageToSearchMatch(ArrayList<Storage> storage) {
		ArrayList<SearchMatch >searchmatches = new ArrayList<SearchMatch>();
		for (Storage item : storage) {
			searchmatches.add((SearchMatch)item);
		}
		return searchmatches;
	}
	
	@Override
	public void onResume()
	{  // After a pause OR at startup
	    super.onResume();
	    //Refresh data sets when you return
		srAdapter.notifyDataSetChanged();

	}

	public ArrayList<Storage> getMatchesByType(int type) {
		dm = new DatabaseModel(getActivity(), ThePantryContract.DATABASE_NAME);
		String table = ThePantryContract.SearchMatch.TABLE_NAME;
		ArrayList<Storage> result;
		switch (type) {
		case ThePantryContract.RECENT:
			result = dm.getAllStorage(table);
			break;
		case ThePantryContract.FAVORITED:
			result = dm.getCookOrFav(table, ThePantryContract.Storage.FAVORITE);
			break;
		case ThePantryContract.COOKED:
			result = dm.getCookOrFav(table, ThePantryContract.Storage.COOKED);
			break;
		default:
			result = new ArrayList<Storage>();
		}
		dm.close();
		return result;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.pager_home_page,
				container, false);
		matches = storageToSearchMatch(getMatchesByType(type));
		matchlist = (ListView) rootView.findViewById(R.id.matchList);
		mFrameOverlay = (FrameLayout) rootView.findViewById(R.id.overlay);
		srAdapter = new SearchResultAdapter(getActivity(), matches);   
		matchlist.setAdapter(srAdapter);
		srAdapter.notifyDataSetChanged();

		//when a search result item (recipe) is clicked
		matchlist.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// online check?
				String recipeID = (String)view.getTag();
				dm = new DatabaseModel(getActivity(), ThePantryContract.DATABASE_NAME);
				Recipe recipe = (Recipe) dm.getStorage(ThePantryContract.Recipe.TABLE_NAME, recipeID);
				if (recipe != null) {
					openRecipe(recipe);
				} else {
					// TODO should throw a pantry exception - recipes should be cached here
				}
				dm.close();
			}
		});
		return rootView;
	}
	
	public void openRecipe(Recipe recipe) {
		Intent intent = new Intent(getActivity(), RecipeActivity.class);
		intent.putExtra("result", recipe);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	public SearchMatch findSearchMatch(String recipeID) {
		for (SearchMatch searchmatch : matches) {
			if (searchmatch.id.equals(recipeID)) {
				return searchmatch;
			}
		}
		return null;
	}
}
