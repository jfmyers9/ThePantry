package cs169.project.thepantry;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;

import cs169.project.thepantry.ThePantryContract.Ingredients;

public class InventoryAddActivity extends InventoryActivity {
	String table = Ingredients.TABLE_NAME;
	private DatabaseModel dm;
	private static final String DATABASE_NAME = "thepantry";
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inventory_add);
		setTitle(getString(R.string.InventoryAddTitle));
		SlidingMenu sm = getSlidingMenu();
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		eView = (ExpandableListView)findViewById(R.id.exp_inv_add_list);
		table = Ingredients.TABLE_NAME;
		
		//Makes ArrayList of types and items
		ArrayList<String> groupItem = getTypes(table);
		ArrayList<ArrayList<String>> childItem = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < groupItem.size(); i ++) {
			ArrayList<String> child = getItems(groupItem.get(i));
			childItem.add(child);
		}
		makeList(groupItem, childItem);
		
		
		// Only should show a back button on action bar?	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.inventory_add, menu);
		return true;
	}
	
	@Override
	public void makeList(ArrayList<String> groupItem, ArrayList<ArrayList<String>> childItem) {

		eView.setDividerHeight(2);
		eView.setGroupIndicator(null);
		eView.setClickable(true);

		NewAdapter mNewAdapter = new NewAdapter(groupItem, childItem);
		mNewAdapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE),this);
		eView.setAdapter(mNewAdapter);
		eView.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				//I think we can get rid of all of this
				
				//CheckBox checkBox = (CheckBox) v.findViewById(R.id.textView1);
				//checkBox.toggle();
				//dm.checked(table, ((TextView)checkBox).getText().toString(), checkBox.isChecked());
				return true;
			}
		});
	}
	
	@Override
	/** Same functionality as InventoryActivity search
	 *  added ability to put it in ingredient table*/
	public void search(View view) {
		//eventually display items returned as buttons as each letters are added to query -- need to create a more detailed search method
		//If item doesn't exist CustomItem button appears
		dm = new DatabaseModel(this, DATABASE_NAME);
		
		/*
		boolean found = dm.findItem(table, item);
		if (found) {
			//display button with given item
		} else {
			//display custom button
			String type = customItem(); // retrieves the type user wants selected item to be
			addEntry(item, type); // figure out a way to allow user to undo this if they screw up
		}*/
	}
	
	/** Display all types in a dropdown menu for user to select */
	public void customItem() {
		Cursor types = dm.findAllTypes(table);

		// displays types in spinner?
		// include custom button
		
		// figure out how to retrieve what button user clicked
		//return null;
		
	}
	
	/** Adds an entry to Ingredient database */
	public void addEntry(String item, String type) {
		dm = new DatabaseModel(this, DATABASE_NAME);
		dm.add(table, item, type, "0"); //adds entry to ingredient database
		
		//mark item "checked"
	}
	
	@Override
	/** Checks an item in the database */
	public void check(View view) {
		dm = new DatabaseModel(this, DATABASE_NAME);
		CheckBox checkBox = (CheckBox) view.findViewById(R.id.textView1);
		dm.checked(table, ((TextView)checkBox).getText().toString(), ThePantryContract.CHECKED, checkBox.isChecked());
	}
	
	/** Adds all items to inventory database that have been checked */
	public void updateInventory(View view) {
		//TODO: iterate through all checked items add inventory database, mark as unchecked
		dm = new DatabaseModel(this, DATABASE_NAME);

		System.out.println("DEFINITELY HERE");
		//I don't think checkedItems is working -- could be checked function though
		Cursor checked = dm.checkedItems(table, ThePantryContract.CHECKED);

		if (checked.moveToFirst()){
			System.out.println("****************");
			while(!checked.isAfterLast()){
				String data = checked.getString(0);
				System.out.println(checked.getString(0));
				System.out.println(checked.getString(1));
				System.out.println(checked.getString(2));
				System.out.println(checked.getString(3));
				//result.add(data);
				checked.moveToNext();
			}
		}
		checked.close();
		
		boolean remSuccess = true; //set to true for display testing
		
		// TODO - parse cursor and fill this list 
		/*List<String> items;
		for (String item : items) {
			boolean addSuccess = dm.add(Inventory.TABLE_NAME, item, type, amount);
		}*/
		if (remSuccess) {
			// remove the item from the shopping list display, add it to inventory display
		} else {
			// do something else
		}

		//Maybe pop up window with items added -- maybe store what didn't get added
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}	
		return super.onOptionsItemSelected(item);
	}

}
