package cs169.project.thepantry;

import java.util.ArrayList;

import android.os.Bundle;

import android.app.Activity;
//import android.app.ActionBar;

import android.database.Cursor;

import android.view.Menu;

//import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;


public class ShoppingListActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.action_shopping_list);
		setContentView(R.layout.activity_shopping_list);
		// Only use action bar if we want to specify certain items on it
		//ActionBar actionBar = getActionBar();
		
		// Creates and populates the ingredient type drop-down menu
		Spinner spinner = (Spinner) findViewById(R.id.add_sl_item_types);
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
	public void addItem(String item, String type, float amount) {
		DatabaseModel dm = new DatabaseModel(this);
		boolean success = dm.add(ThePantryContract.ShoppingList.TABLE_NAME, item, type, amount);
		if (success) {
			// TODO - display item on shopping list (add it to layout)
		} else {
			// do something else
		}
	}
	
	/** Extracts the item text and type from the user input. */
	public void addExtract() {
		float amount = 1;
		EditText itemText = (EditText) findViewById(R.id.add_sl_item_text);
		Spinner typeSpinner = (Spinner) findViewById(R.id.add_sl_item_types);
		String item = itemText.getText().toString();
		String type = typeSpinner.getSelectedItem().toString();
		addItem(item, type, amount);
	}
	
	/** Removes the given item from the shopping list */
	public void removeItem(String item) {
		// TODO - get the item/View by finding it from layout
		DatabaseModel dm = new DatabaseModel(this);
		boolean success = dm.remove(ThePantryContract.ShoppingList.TABLE_NAME, item);
		if (success) {
			// TODO - remove item and its checkbox from display/layout
		} else {
			// do something else
		}
	}
	
	/** Swipes to bring up the delete button for an item on the shopping list. */
	public void swipeToRemove(String item) {
		// TODO - implement this, brings up a button whose onClick=removeItem
	}
	
	/** Updates the inventory with items checked on the shopping list */
	public void updateInventory() {
		boolean remSuccess = false;
		DatabaseModel dm = new DatabaseModel(this);
		Cursor c = dm.checkedItems(ThePantryContract.ShoppingList.TABLE_NAME);

		// Parses the cursor into a list of Strings, still needs work, have to extract type and amount
		ArrayList<String> items = new ArrayList<String>();
		if (c.moveToFirst()){
			while(!c.isAfterLast()){
				String data = c.getString(0);
				items.add(data);
				c.moveToNext();
			}
		}
		c.close();
		
		// Add to inventory, removing from shopping list
		for (String item : items) {
			String type = "Other";
			float amount = 1;
			boolean addSuccess = dm.add(ThePantryContract.Inventory.TABLE_NAME, item, type, amount);
			if (addSuccess) {
				remSuccess = dm.remove(ThePantryContract.ShoppingList.TABLE_NAME, item);
			} else {
				// do something else
			}
		}
		if (remSuccess) {
			// remove the item from the shopping list display, add it to inventory display
		} else {
			// do something else
		}
	}
	
	/** Marks a shopping list item as checked */
	public void checkItem(String item, boolean checked) {
		DatabaseModel dm = new DatabaseModel(this);
		boolean success = dm.checked(ThePantryContract.ShoppingList.TABLE_NAME, item, checked);
		if (success) {
			// do something?
		} else {
			// do something else?
		}
	}
	
	// TODO - use CursorLoader and an Adapter to populate the ListView

}

