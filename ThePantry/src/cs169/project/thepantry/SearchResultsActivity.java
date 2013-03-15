package cs169.project.thepantry;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class SearchResultsActivity extends BasicMenuActivity {

	ArrayList<SearchMatch> matches;
	SearchResultAdapter srAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);
		
		EditText searchText = (EditText) findViewById(R.id.search_text);
		searchText.setText((String)getIntent().getStringExtra("currentSearch"));
		
		SearchResult result = (SearchResult)getIntent().getExtras().getSerializable("result");
		matches = result.matches;
		
		ListView listView = (ListView) findViewById(R.id.resultList);
		srAdapter = new SearchResultAdapter(this, matches);          
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
	
	public void search(View view) throws Exception {
		EditText searchText = (EditText) findViewById(R.id.search_text);
    	String search = searchText.getText().toString();
    	if (isOnline()) {
    		new SearchTask(getApplication()).execute("search", search);
    	}
	}

}
