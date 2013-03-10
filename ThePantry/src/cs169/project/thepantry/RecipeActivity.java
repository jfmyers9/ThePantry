package cs169.project.thepantry;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public class RecipeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recipe, menu);
		return true;
	}
	
	public void addToFavorites(View v) {
		//TODO
	}
	
	public void markCooked(View v) {
		// TODO
	}
	
	public void addToShopping(View v) {
		//TODO
	}

}
