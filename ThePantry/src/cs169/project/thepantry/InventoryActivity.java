package cs169.project.thepantry;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class InventoryActivity extends Activity {
	final String table = getString(R.string.inventoryTable);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inventory);
		
		//TODO: decide how to display categories -- spinner or some other way
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.inventory, menu);
		return true;
	}
	
	/** Takes you to InventoryAdd Activity */
	public void edit() {
		//TODO -- make intent to Inventory Add
	}

	/** Display buttons with items of specified type */
	public void showItems(String type) {
		//List<String> items = DatabaseModel.findTypeItems(table, type);
		// Call function to display buttons of 
	}
	
	/** Marks items as checked in table of current activity */
	public void checkItem(String item) {
		//TODO -- think about checked item in database model (may need extra methods)
	}
	
	/** Display item searched for if it is in the table 
	 * Eventually display items dynamically as a letter is added to query*/
	public void search(String item) {
		//String foundItem = DatabaseModel.findItem(table, item);
		if (item!=null) {
			//displays item
		} else {
			//display message -- item not in inventory
		}
	}

	/** Display remove button on specified item/button */
	public void swipeRemove() {
		//TODO -- display remove button
	}
	
	/** Removes specified item from database */
	public void remove(String item) {
		//DatabaseModel.remove(table, item);
	}
	
}
