package cs169.project.thepantry;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
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
	private ArrayList<ArrayList<String>> childItems;
	
	private static final String DATABASE_NAME = "thepantry";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.ShoppingListTitle));
		setContentView(R.layout.activity_shopping_list);
		eView = (ExpandableListView) findViewById(R.id.exp_shop_list);

		eView.setFocusable(true);
		
		groupItems = new ArrayList<String>();
		childItems = new ArrayList<ArrayList<String>>();
		
		fillArrays(groupItems, childItems);
		
		eView.setDividerHeight(2);
		eView.setGroupIndicator(null);
		eView.setClickable(true);;
		
		NewAdapter eAdapter = new NewAdapter(groupItems, childItems);
		eAdapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE),this);
		eView.setAdapter(eAdapter);
		// Only use action bar if we want to specify certain items on it
		//ActionBar actionBar = getActionBar();
		
		// Creates and populates the ingredient type drop-down menu
		spinner = (Spinner) findViewById(R.id.add_sl_types);
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
		getSupportMenuInflater().inflate(R.menu.shopping_list, menu);
		return true;
	}
	
	public void addShopItem(View view) {
		EditText eText = (EditText) findViewById(R.id.shopping_list_text);
		addItem(eText.getText().toString(), spinner.getSelectedItem().toString(), "1");
		((BaseExpandableListAdapter)eView.getExpandableListAdapter()).notifyDataSetChanged();
	}
	
	/* Fills the arrays with database data. */
	public void fillArrays(ArrayList<String> groups, ArrayList<ArrayList<String>> children) {
		groups = getTypes(ThePantryContract.ShoppingList.TABLE_NAME);
		for (int i = 0; i < groups.size(); i ++) {
			ArrayList<String> child = getItems(groups.get(i));
			children.add(child);
		}
	}
	
	/* Fills the group arraylist with data from database. */
	public ArrayList<String> getTypes(String table) {
		dm = new DatabaseModel(this, DATABASE_NAME);
		Cursor types = dm.findAllTypes(table);
		ArrayList<String> result = new ArrayList<String>();
		if (types.moveToFirst()){
			while(!types.isAfterLast()){
				String data = types.getString(0);
				System.out.println(data);
				result.add(data);
				types.moveToNext();
			}
		}
		types.close();
		return result;
	}
	
	/* Fills the children with items from database. */
	public ArrayList<String> getItems(String type) {
		dm = new DatabaseModel(this, DATABASE_NAME);
		Cursor items = dm.findTypeItems(ThePantryContract.ShoppingList.TABLE_NAME, type);
		
		ArrayList<String> result = new ArrayList<String>();
		if (items.moveToFirst()){
			while(!items.isAfterLast()){
				String data = items.getString(0);
				result.add(data);
				items.moveToNext();
			}
		}
		items.close();
		return result;
	}
	
	/** Adds the given item to the shopping list */
	public void addItem(String item, String type, String amount) {
		DatabaseModel dm = new DatabaseModel(this, DATABASE_NAME);
		// for testing purposes of the display, success is set to true
		boolean success = dm.add(ThePantryContract.ShoppingList.TABLE_NAME, item, type, amount);
		if (success) {
			int groupPos = groupItems.size();
			if (groupItems.contains(type)) {
				groupPos = groupItems.indexOf(type);
				childItems.get(groupPos).add(item);
			} else {
				groupItems.add(type);
				ArrayList<String> temp = new ArrayList<String>();
				temp.add(item);
				childItems.add(temp);
			}
		} else {
		}
	}
	
	/** Removes the given item from the shopping list */
	public void removeItem(String item) {
		// TODO - get the item/View by finding it from layout
		dm = new DatabaseModel(this, DATABASE_NAME);
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
		dm = new DatabaseModel(this, DATABASE_NAME);
		Cursor c = dm.checkedItems(ThePantryContract.ShoppingList.TABLE_NAME, ThePantryContract.CHECKED);

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
		if (remSuccess) {
			// remove the item from the shopping list display, add it to inventory display
		} else {
			// do something else
		}
	}
	
	/** Marks a shopping list item as checked */
	public void check(View view) {
		dm = new DatabaseModel(this, DATABASE_NAME);
		CheckBox checkBox = (CheckBox) view.findViewById(R.id.textView1);
		dm.checked(ThePantryContract.ShoppingList.TABLE_NAME, ((TextView)checkBox).getText().toString(), ThePantryContract.CHECKED,  checkBox.isChecked());
	}
	
	// TODO - use CursorLoader and an Adapter to populate the ListView

}
