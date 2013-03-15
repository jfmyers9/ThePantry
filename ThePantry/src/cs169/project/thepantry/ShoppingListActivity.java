package cs169.project.thepantry;

import java.util.ArrayList;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;

public class ShoppingListActivity extends BasicMenuActivity {
	
	private DatabaseModel dm;
	private ExpandableListView eView;
	private Spinner spinner;
	
	private ArrayList<String> groupItems;
	private ArrayList<Object> childItems;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopping_list);
		// Only use action bar if we want to specify certain items on it
		//ActionBar actionBar = getActionBar();
		
		// Creates and populates the ingredient type drop-down menu
		spinner = (Spinner) findViewById(R.id.ingredient_types);
		ArrayAdapter<CharSequence> adapter = 
				ArrayAdapter.createFromResource(this,
												R.array.ingredient_type_array,
												android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
		groupItems = new ArrayList<String>();
		childItems = new ArrayList<Object>();
		eView = (ExpandableListView) findViewById(R.id.exp_shop_list);
		eView.setAdapter(new NewAdapter(groupItems, childItems));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.shopping_list, menu);
		return true;
	}
	
	public void addShopItem(View view) {
		EditText eText = (EditText) findViewById(R.id.shopping_list_text);
		addItem(eText.getText().toString(), spinner.getSelectedItem().toString(), 1);
	}
	
	/** Adds the given item to the shopping list */
	public void addItem(String item, String type, float amount) {
		DatabaseModel dm = new DatabaseModel(this);
		// for testing purposes of the display, success is set to true
		boolean success = dm.add(ThePantryContract.ShoppingList.TABLE_NAME, item, type, amount);
		if (success) {
			groupItems.add(type);
			childItems.add(item);
			((NewAdapter)eView.getExpandableListAdapter()).add(type, item);
		} else {
		}
	}
	
	/** Removes the given item from the shopping list */
	public void removeItem(String item) {
		DatabaseModel dm = new DatabaseModel(this);
		// for testing purposes of the display, success is set to true
		boolean success = true;
		//dm.remove(ThePantryContract.ShoppingList.TABLE_NAME, item);
		if (success) {
			// TODO - remove item and its checkbox from display/layout
		} else {
			// do something else
		}
	}
	
	/** Updates the inventory with items checked on the shopping list */
	public void updateInventory() {
		DatabaseModel dm = new DatabaseModel(this);
		Cursor c = dm.checkedItems(ThePantryContract.ShoppingList.TABLE_NAME);
		boolean remSuccess = true; //set to true for display testing
		// TODO - parse cursor and fill this list 
		/*List<String> items;
		for (String item : items) {
			boolean addSuccess = dm.add(ThePantryContract.Inventory.TABLE_NAME, item, type, amount);
			if (addSuccess) {
				boolean remSuccess = dm.remove(ThePantryContract.ShoppingList.TABLE_NAME, item);
			} else {
				// do something else
			}
		}*/
		if (remSuccess) {
			// remove the item from the shopping list display, add it to inventory display
		} else {
			// do something else
		}
	}
	
	/** Marks a shopping list item as checked */
	public void check(View view) {
		dm = new DatabaseModel(this);
		CheckBox checkBox = (CheckBox) view.findViewById(R.id.textView1);
		dm.checked(ThePantryContract.ShoppingList.TABLE_NAME, ((TextView)checkBox).getText().toString(), checkBox.isChecked());
	}
	
	// TODO - use CursorLoader and an Adapter to populate the ListView

}
