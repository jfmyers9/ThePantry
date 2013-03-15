package cs169.project.thepantry;

import java.util.ArrayList;

import android.app.Application;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

public class HomePageActivity extends BasicMenuActivity {
	
	ArrayList<SearchMatch> recommendations;
	SearchResultAdapter srAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_page);

		// intent extras are null coming from the sliding menu
		if (getIntent().getExtras() == null) {
			getRecommendations(getApplication());
		}
		
		else {
			//retrieve recommendation results
			SearchResult recs = (SearchResult)getIntent().getExtras().getSerializable("result");
			recommendations = recs.matches;
		
			ListView listView = (ListView) findViewById(R.id.recsList);
			srAdapter = new SearchResultAdapter(this, recommendations);          
			listView.setAdapter(srAdapter);
			srAdapter.notifyDataSetChanged();
		
			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// When clicked
					if (isOnline()){
						new SearchTask(getApplication()).execute("recipe", (String)view.getTag());
					}
				}
			});
		}
	}
	
	public void search(View view) throws Exception {
		EditText searchText = (EditText) findViewById(R.id.search_text);
    	String search = searchText.getText().toString();
    	if (isOnline()) {
    		new SearchTask(getApplication()).execute("search", search);
    	}
	}
	
	public void getRecommendations(Application app) {
		//if (isOnline()){
			new SearchTask(app).execute("home", "bacon");
		//}
	}

}
