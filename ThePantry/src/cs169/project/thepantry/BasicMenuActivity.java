package cs169.project.thepantry;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class BasicMenuActivity extends SlidingFragmentActivity {
	
	private String title;
	private ActionBar actionBar;
	
	public BasicMenuActivity() {
		title = null;
	}
	
	public BasicMenuActivity(String name) {
		title = name;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (title == null) {
			title = getString(R.string.app_name);
		}	
		setTitle(title);
		
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
		getSupportMenuInflater().inflate(R.menu.basic_menu, menu);
		return true;
	}
	
	// check if there is a network connection. each activity will inherit this
	public boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnected()) {
	        return true;
	    }
	    return false;
	}

}
