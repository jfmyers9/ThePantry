package cs169.project.thepantry;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class HomePageActivity extends SlidingFragmentActivity {
	
	public ActionBar actionBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.app_name);
		
		actionBar = getSupportActionBar();
		
		setBehindContentView(R.layout.menu_frame);
		
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		
		sm.setFadeDegree(0.35f);
		
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.menu_frame, new NavMenuFragment())
		.commit();
		
		setContentView(R.layout.activity_home_page);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return true;
		}	
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.home_page, menu);
		return true;
	}
	
	public void search(View view) throws Exception {
		EditText searchText = (EditText) findViewById(R.id.search_text);
    	String search = searchText.getText().toString();
		new SearchTask(getApplication()).execute("search", search);
	}

}
