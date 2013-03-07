package cs169.project.thepantry;

import android.os.Bundle;

import android.app.Activity;
//import android.app.ActionBar;

import android.view.Menu;

import android.widget.Spinner;
import android.widget.ArrayAdapter;

public class ShoppingListActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopping_list);
		// Only use action bar if we want to specify certain items on it
		//ActionBar actionBar = getActionBar();
		
		// Creates and populates the ingredient type drop-down menu
		Spinner spinner = (Spinner) findViewById(R.id.ingredient_types);
		ArrayAdapter<CharSequence> adapter = 
				ArrayAdapter.createFromResource(this,
												R.array.ingredient_type_array,
												android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shopping_list, menu);
		return true;
	}
	
	/** Adds the given item to the shopping list */
	public void addItem() {
		// calls DatabaseModel
	}
	
	/** Removes the given item from the shopping list */
	public void removeItem() {
		// calls DatabaseModel
	}
	
	/** Updates the inventory with items checked on the shopping list */
	public void updateInventory() {
		// calls DatabaseModel
	}
	
	/** Marks a shopping list item as checked */
	public void checkItem() {
		// calls DatabaseModel
	}

}
