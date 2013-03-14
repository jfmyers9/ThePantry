package cs169.project.thepantry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class SearchResultsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);
		SearchResult results = (SearchResult)getIntent().getExtras().getSerializable("result");
		TextView textview = (TextView) findViewById(R.id.results);
		if (results != null) {
			textview.setText((String)((SearchMatch)results.matches.get(0)).name);
		}
		else {
			textview.setText("null results");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search_results, menu);
		return true;
	}

}
