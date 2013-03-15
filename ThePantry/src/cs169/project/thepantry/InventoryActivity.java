package cs169.project.thepantry;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;

import cs169.project.thepantry.ThePantryContract.Ingredients;
import cs169.project.thepantry.ThePantryContract.Inventory;

public class InventoryActivity extends BasicMenuActivity {
	String table;
	private DatabaseModel dm;
	
	ExpandableListView eView;
	ExpandableListAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.InventoryTitle));
		setContentView(R.layout.activity_inventory);
		eView = (ExpandableListView)findViewById(R.id.exp_view);
		eView.setFocusable(true);
		

		//Doesn't make list if onCreate is being called from InventoryAdd
		if (this instanceof InventoryActivity) {
			table = Inventory.TABLE_NAME;
			
			//Makes ArrayList of types and items
			ArrayList<String> groupItem = getTypes(table);
			ArrayList<Object> childItem = new ArrayList<Object>();
			for (int i = 0; i < groupItem.size(); i ++) {
				ArrayList<String> child = getItems(groupItem.get(i));
				childItem.add(child);
			}
			makeList(groupItem, childItem);
		}
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.inventory, menu);
		return true;
	}
	
	public void makeList(ArrayList<String> groupItem, ArrayList<Object> childItem) {

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
	
	public void check(View view) {
		dm = new DatabaseModel(this);
		CheckBox checkBox = (CheckBox) view.findViewById(R.id.textView1);
		dm.checked(table, ((TextView)checkBox).getText().toString(), checkBox.isChecked());
	}
	
	/** Takes you to InventoryAdd Activity */
	public void edit() { // change back to void after testing
		//TODO -- make intent to Inventory Add
		Context context = getApplicationContext();
		Intent intent = new Intent(context, InventoryAddActivity.class); //Change back to HomePageActivity.class 
		startActivity(intent);
	}

	/** Display buttons with items of specified type */
	public ArrayList<String> getTypes(String table) {
		dm = new DatabaseModel(this);
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
	
	/** Display buttons with items of specified type */
	public ArrayList<String> getItems(String type) {
		dm = new DatabaseModel(this);
		Cursor items = dm.findTypeItems(Ingredients.TABLE_NAME, type);
		
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
	
	
	/** Display item searched for if it is in the table 
	 * Eventually display items dynamically as a letter is added to query*/
	public void search(View view) {
		//for now just have it return true if item is in table, false otherwise
		dm = new DatabaseModel(this);
		
		/*
		boolean found = dm.findItem(table, item);
		if (found) {
			//displays item
		} else {
			//display message -- item not in inventory
		}*/
	}

	/** Display remove button on specified item/button */
	public void swipeRemove() {
		//TODO -- display remove button
	}
	
	/** Removes specified item from database */
	public void remove(String item) {
		dm = new DatabaseModel(this);
		// for testing purposes of the display, success is set to true
		boolean success = true;
		dm.remove(table, item);
		if (success) {
			// TODO - remove item and its checkbox from display/layout
		} else {
			// do something else
		}
	}
	
}
