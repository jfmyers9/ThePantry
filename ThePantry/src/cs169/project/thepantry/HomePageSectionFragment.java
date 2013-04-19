package cs169.project.thepantry;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class HomePageSectionFragment extends Fragment {

	public static final String ARG_SECTION_NUMBER = "section_number";
	ListView matchlist;
	ArrayList<SearchMatch> matches;
	SearchResultAdapter srAdapter;

	public HomePageSectionFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.pager_home_page,
				container, false);
		matches = new ArrayList<SearchMatch>();
		matchlist = (ListView) rootView.findViewById(R.id.matchList);
		srAdapter = new SearchResultAdapter(getActivity(), matches);   
		matchlist.setAdapter(srAdapter);
		srAdapter.notifyDataSetChanged();
		
		//when a search result item (recipe) is clicked
		matchlist.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			    // online check?
		    	SearchCriteria searchcriteria = new SearchCriteria("recipe", (String)view.getTag());
		    	((HomePageActivity)getActivity()).new HomeSearchTask(getActivity(), "recipe").execute(searchcriteria);
			}
		});
		
		return rootView;
	}
}