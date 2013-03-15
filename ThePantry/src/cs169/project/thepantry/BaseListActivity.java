package cs169.project.thepantry;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;

import cs169.project.thepantry.ThePantryContract.Inventory;

public abstract class BaseListActivity extends BasicMenuActivity {

	public DatabaseModel dm;
	public ExpandableListView eView;
	NewAdapter eAdapter;
	public String table;
	
	public ArrayList<IngredientGroup> groupItems;
	public ArrayList<String> groupNames;
	public ArrayList<ArrayList<String>> childItems;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.base_list, menu);
		return true;
	}
	
	/** Fills the arrays with database data. */
	public void fillArrays() {
		groupItems = getTypes();
		for (IngredientGroup g : groupItems) {
			groupNames.add(g.getGroup());
			g.setChildren(getItems(g.getGroup()));
		}
	}
	
	/** Display buttons with items of specified type */
	public ArrayList<IngredientGroup> getTypes() {
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
	
	/** Display buttons with items of specified type */
	public ArrayList<IngredientChild> getItems(String type) {
		dm = new DatabaseModel(this);
		Cursor items = dm.findTypeItems(table, type);
		
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
	
	
	/** Marks an item as checked */
	public void check(View view) {
		dm = new DatabaseModel(this);
		
		// Finds  the view that called check
		CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox1); //This is a little unclean
		String item =  ((TextView)checkBox).getText().toString();
		
		//Updates the check field of the item that called check
		dm.checked(table, item, ThePantryContract.CHECKED,  checkBox.isChecked());
	}
	
	/** Adds the given item to the list */
	public void addItem(String item, String type, String amount) {
		DatabaseModel dm = new DatabaseModel(this);	
		boolean success = dm.add(table, item, type, amount);
		int groupPos = 0;
		IngredientGroup temp = new IngredientGroup(type,new ArrayList<IngredientChild>());
		if (success) {
			if (groupItems.contains(temp)) {
				groupPos = groupItems.indexOf(temp);
				eAdapter.addChild(new IngredientChild(item, type), groupItems.get(groupPos));
			} else {
				eAdapter.addChild(new IngredientChild(item, type), temp);
			}
		} 
	}
	
	/** Swipes to bring up the delete button for an item on the shopping list. */
	public void swipeToRemove(View view) {
		// TODO - implement this, brings up a button whose onClick=removeItem
	}
	
	/** Removes the given item from the shopping list */
	public void removeItem(String item) {
		// TODO - get the item/View by finding it from layout
		dm = new DatabaseModel(this);
		boolean success = dm.remove(table, item);
		if (success) {
			// TODO - remove item and its checkbox from display/layout
		} else {
			// do something else
		}
	}
	
	/** Adds all items to inventory database that have been checked */
	public void updateInventory(View view) {
		dm = new DatabaseModel(this);
		Cursor checked = dm.checkedItems(table, ThePantryContract.CHECKED);

		if (checked.moveToFirst()){
			while(!checked.isAfterLast()){
				dm.add(Inventory.TABLE_NAME, checked.getString(0), checked.getString(1), checked.getString(3));
				checked.moveToNext();
			}
		}
		checked.close();
		
		//For now it takes you to inventory -- next iteration it will pop toast?
		Context context = getApplicationContext();
		Intent intent = new Intent(context, InventoryActivity.class);
		startActivity(intent);
	}
	

}
