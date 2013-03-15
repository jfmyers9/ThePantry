package cs169.project.thepantry;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;

import cs169.project.thepantry.ThePantryContract.Ingredients;
import cs169.project.thepantry.ThePantryContract.Inventory;

public class InventoryAddActivity extends InventoryActivity {
	String table = Ingredients.TABLE_NAME;
	private DatabaseModel dm;
	
	private ArrayList<IngredientGroup> groupItem;
	

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
		table = Inventory.TABLE_NAME;
		
		//Makes ArrayList of types and items
		groupItem = getTypes(table);
		for (IngredientGroup g : groupItem) {
			g.setChildren(getItems(g.getGroup()));
		}
		makeList();
		
		
		// Only should show a back button on action bar?	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.inventory_add, menu);
		return true;
	}
	
	@Override
	public void makeList() {

		eView.setDividerHeight(2);
		eView.setGroupIndicator(null);
		eView.setClickable(true);

		NewAdapter mNewAdapter = new NewAdapter(getApplicationContext(), groupItem);
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
		dm = new DatabaseModel(this);
		
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
	
	@Override
	/** Checks an item in the database */
	public void check(View view) {
		dm = new DatabaseModel(this);
		CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox1);
		dm.checked(table, ((TextView)checkBox).getText().toString(), ThePantryContract.CHECKED, checkBox.isChecked());
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
		Context context = getApplicationContext();
		Intent intent = new Intent(context, InventoryAddActivity.class);
		startActivity(intent);

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
