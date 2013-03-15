package cs169.project.thepantry;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class HomePageActivity extends BasicMenuActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_page);
	}
	
	public void search(View view) throws Exception {
		EditText searchText = (EditText) findViewById(R.id.search_text);
    	String search = searchText.getText().toString();
    	if (isOnline()) {
    		new SearchTask(getApplication()).execute("search", search);
    	}
	}

}
