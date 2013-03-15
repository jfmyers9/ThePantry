package cs169.project.thepantry;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;

public class TutorialActivity extends Activity {

	private static final String OPENED = "opened";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPreferences opened = PreferenceManager.getDefaultSharedPreferences(this);
		if (opened.getBoolean(OPENED, false) == false) {
			SharedPreferences.Editor editor = opened.edit();
			editor.putBoolean(OPENED, true);
			editor.commit();
			setContentView(R.layout.activity_tutorial);
		} else {
			Context context = getApplicationContext();
			HomePageActivity nextAct = new HomePageActivity();
			Intent intent = new Intent(context, nextAct.getClass());
			startActivity(intent);
			finish(); //this shuts down the app for a second?
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tutorial, menu);
		return true;
	}
	
	public void setupInventory(View view) {
		Context context = getApplicationContext();
		InventoryAddActivity invAddAct = new InventoryAddActivity();
		Intent intent = new Intent(context, invAddAct.getClass());
		startActivity(intent);
	}
	
	public void skipTutorial(View view) {
		Context context = getApplicationContext();
		Intent intent = new Intent(context, HomePageActivity.class);
		startActivity(intent);
		finish();
	}
	
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
