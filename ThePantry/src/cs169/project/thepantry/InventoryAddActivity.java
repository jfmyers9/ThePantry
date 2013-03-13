package cs169.project.thepantry;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class InventoryAddActivity extends InventoryActivity {
	//TODO: MAKE A ABSTRACT INVENTORY CLASS THAT BOTH INVENTORY ACTIVITIES EXTEND
	//TODO: Decide on the best way to call the database model
	//TODO: Make methods for buttons
	private String TABLE_NAME;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inventory_add);
		TABLE_NAME = getString(R.string.ingredientsTable);
		// Only should show a back button on action bar?

		//TODO: decide how to display categories -- spinner or some other way (can inherit from inventory)
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.inventory_add, menu);
		return true;
	}
	
	/** Same functionality as InventoryActivity search
	 *  added ability to put it in ingredient table*/
	public void search(String item) {
		//eventually display items returned as buttons as each letters are added to query -- need to create a more detailed search method
		//If item doesn�t exist CustomItem button appears
		//String foundItem = DatabaseModel.findItem(table, item);
		if (item!=null) {
			//display button with given item
		} else {
			//display custom button
			String type = customItem(); // retrieves the type user wants selected item to be
			addEntry(item, type); // figure out a way to allow user to undo this if they screw up
		}
	}
	
	/** Display all types in a dropdown menu for user to select */
	public String customItem() {
		//List<String> types = DatabaseModel.findAllTypes(table);
		// displays types in spinner?
		// include custom button
		// figure out how to retrieve what button user clicked
		return null;
	}
	
	/** Adds an entry to Ingredient database */
	public void addEntry(String item, String type) {
		//DatabaseModel.add(table, item, type, 0); //adds entry to ingredient database
		//mark item "checked"
	}
	
	/** Adds all items to inventory database that have been checked */
	public void updateInventory() {
		//TODO: iterate through all checked items add inventory database, mark as unchecked
		
		//List<String> entries = DatabaseModel.checkedItems(); //should return entries
		String item = null, type = null; // These will get changed once I decide the best way to retrieve entries
		//DatabaseModel.add(getString(R.string.inventoryTable), item, type, 0);
		//Maybe pop up window with items added
	}

}
