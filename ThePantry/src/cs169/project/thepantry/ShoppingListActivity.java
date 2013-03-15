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
	private NewAdapter eAdapter;
	private Spinner spinner;
	
	private ArrayList<IngredientGroup> groupItems;
	private ArrayList<String> groupNames;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.ShoppingListTitle));
		setContentView(R.layout.activity_shopping_list);
		eView = (ExpandableListView) findViewById(R.id.exp_shop_list);
		eView.setFocusable(true);
		
		groupItems = new ArrayList<IngredientGroup>();
		groupNames = new ArrayList<String>();
		
		fillArrays();
		
		eView.setDividerHeight(2);
		eView.setClickable(true);;
		
		eAdapter = new NewAdapter(getApplicationContext(), groupItems);
		eView.setAdapter(eAdapter);
		
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
		eText.setText("");
		((BaseExpandableListAdapter)eView.getExpandableListAdapter()).notifyDataSetChanged();
	}
	
	/* Fills the arrays with database data. */
	public void fillArrays() {
		groupItems = getTypes(ThePantryContract.ShoppingList.TABLE_NAME);
		for (IngredientGroup g : groupItems) {
			groupNames.add(g.getGroup());
			g.setChildren(getItems(g.getGroup()));
		}
	}
	
	/* Fills the group arraylist with data from database. */
	public ArrayList<IngredientGroup> getTypes(String table) {
		dm = new DatabaseModel(this);
		Cursor types = dm.findAllTypes(table);
		ArrayList<IngredientGroup> result = new ArrayList<IngredientGroup>();
		if (types.moveToFirst()){
			while(!types.isAfterLast()){
				String data = types.getString(0);
				System.out.println(data);
				result.add(new IngredientGroup(data, new ArrayList<IngredientChild>()));
				types.moveToNext();
			}
		}
		types.close();
		return result;
	}
	
	/* Fills the children with items from database. */
	public ArrayList<IngredientChild> getItems(String type) {
		dm = new DatabaseModel(this);
		Cursor items = dm.findTypeItems(ThePantryContract.ShoppingList.TABLE_NAME, type);
		
		ArrayList<IngredientChild> result = new ArrayList<IngredientChild>();
		if (items.moveToFirst()){
			while(!items.isAfterLast()){
				String data = items.getString(0);
				result.add(new IngredientChild(data,type));
				items.moveToNext();
			}
		}
		items.close();
		return result;
	}
	
	/** Adds the given item to the shopping list */
	public void addItem(String item, String type, String amount) {
		DatabaseModel dm = new DatabaseModel(this);
		// for testing purposes of the display, success is set to true
		boolean success = dm.add(ThePantryContract.ShoppingList.TABLE_NAME, item, type, amount);
		int groupPos = 0;
		IngredientGroup temp = new IngredientGroup(type,null);
		if (success) {
			if (groupItems.contains(temp)) {
				groupPos = groupItems.indexOf(temp);
				eAdapter.addChild(new IngredientChild(item, type), groupItems.get(groupPos));
			} else {
				groupNames.add(type);
				groupPos = groupItems.indexOf(temp);
				IngredientGroup group = new IngredientGroup(type, new ArrayList<IngredientChild>());
				eAdapter.addChild(new IngredientChild(item, type), groupItems.get(groupPos));
			}
		} else {
		}
	}
	
	/** Extracts the item text and type from the user input. */
	public void addExtract() {
		String amount = "1";
		EditText itemText = (EditText) findViewById(R.id.shopping_list_text);
		Spinner typeSpinner = (Spinner) findViewById(R.id.add_sl_types);
		String item = itemText.getText().toString();
		String type = typeSpinner.getSelectedItem().toString();
		addItem(item, type, amount);
	}
	
	/** Removes the given item from the shopping list */
	public void removeItem(String item) {
		// TODO - get the item/View by finding it from layout
		dm = new DatabaseModel(this);
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
		dm = new DatabaseModel(this);
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
		dm = new DatabaseModel(this);
		CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox1);
		dm.checked(ThePantryContract.ShoppingList.TABLE_NAME, ((TextView)checkBox).getText().toString(), ThePantryContract.CHECKED,  checkBox.isChecked());
	}
	
	// TODO - use CursorLoader and an Adapter to populate the ListView

}
