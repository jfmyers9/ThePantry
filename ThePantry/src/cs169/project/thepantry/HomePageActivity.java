package cs169.project.thepantry;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class HomePageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_page);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_page, menu);
		return true;
	}
	
	public void search(View view) throws Exception {
		EditText searchText = (EditText) findViewById(R.id.search_text);
    	String search = searchText.getText().toString();
		new SearchTask(getApplication()).execute("search", search);
	}

}
