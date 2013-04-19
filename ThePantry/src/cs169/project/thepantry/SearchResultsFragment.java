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
import android.widget.FrameLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class SearchResultsFragment extends Fragment {

	public static final String ARG_SECTION_NUMBER = "section_number";
	ArrayList<SearchMatch> matches;
	ListView matchlist;
	SearchResultAdapter srAdapter;
	SearchModel sm = new SearchModel();
	DatabaseModel dm;
	FrameLayout mFrameOverlay;

	public SearchResultsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.pager_home_page,
				container, false);
		matches = new ArrayList<SearchMatch>();
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
		    	SearchCriteria searchcriteria = new SearchCriteria("recipe", (String)view.getTag());
		    	new SearchTask(getActivity(), "recipe").execute(searchcriteria);
			}
		});
		
		return rootView;
	}
	
	/** AsyncTask for getting Yummly search results
	 *
	 */
	public class SearchTask extends AsyncTask<SearchCriteria, String, Storage> {
		String q;
		String type = "";
		Context context;
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
			progressDialog = new ProgressDialog(getActivity());
			progressDialog.setMessage("Loading " + this.type + "...");
			progressDialog.show();
	    };
		
		@Override
		protected Storage doInBackground(SearchCriteria... sc) {
			this.q = sc[0].q;
			return sm.search(sc[0]);
		}
		
		//update list of matches
		@Override
		protected void onPostExecute(Storage result) {
			progressDialog.dismiss();
	        //display recommendation results
			if (result != null) {
				if (this.type == "search") {
					if (srAdapter.values.size() == 0) {
						matches = ((SearchResult)result).matches;
						srAdapter = new SearchResultAdapter(context, matches);   
						matchlist.setAdapter(srAdapter);
						matchlist.bringToFront();
					}
					else {
						srAdapter.values = ((SearchResult)result).matches;
						srAdapter.notifyDataSetChanged();
						matchlist.smoothScrollToPosition(0);
					}
				} else if (this.type == "recipe") {
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