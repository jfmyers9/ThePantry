package cs169.project.thepantry;

import java.util.ArrayList;

import cs169.project.thepantry.ThePantryContract.Ingredients;
import cs169.project.thepantry.ThePantryContract.Inventory;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class InventoryActivity extends ExpandableListActivity implements OnChildClickListener {
	String table;
	private DatabaseModel dm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inventory);
		
		table = Inventory.TABLE_NAME;
		
		dm = new DatabaseModel(this);
		Cursor types = dm.findAllTypes(table);
		//TODO: decide how to display categories -- spinner or some other way

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.inventory, menu);
		return true;
	}
	
	/** Takes you to InventoryAdd Activity */
	public void edit() { // change back to void after testing
		//TODO -- make intent to Inventory Add
		Context context = getApplicationContext();
		Intent intent = new Intent(context, InventoryAddActivity.class); //Change back to HomePageActivity.class 
		startActivity(intent);
	}

	/** Display buttons with items of specified type */
	public void showItems(String type) {
		dm = new DatabaseModel(this);
		Cursor items = dm.findTypeItems(Ingredients.TABLE_NAME, type);
		
		System.out.println(items.getString(0));
		//System.out.println(items.getString(1));
		//System.out.println(items.getString(2));
		//System.out.println(items.getString(3));
		//System.out.println(items.getString(4));
		// Call function to display buttons of 
	}
	
	/** Marks items as checked in table of current activity */
	public void checkItem(String item) {
		dm = new DatabaseModel(this);
		boolean success = dm.checked(table, item);
		if (success) {
			// do something?
		} else {
			// do something else?
		}
	}
	
	/** Display item searched for if it is in the table 
	 * Eventually display items dynamically as a letter is added to query*/
	public void search(String item) {
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
