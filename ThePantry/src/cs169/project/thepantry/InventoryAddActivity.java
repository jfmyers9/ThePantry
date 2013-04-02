package cs169.project.thepantry;

import java.util.ArrayList;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SearchView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;

import cs169.project.thepantry.ThePantryContract.Ingredients;

public class InventoryAddActivity extends BaseListActivity {


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inventory_add);
		setTitle(getString(R.string.InventoryAddTitle));
		table = Ingredients.TABLE_NAME;
		
		SlidingMenu sm = getSlidingMenu();
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		
		lView = (ListView) findViewById(R.id.inv_add_list);
		lView.setVisibility(View.INVISIBLE);
		
		eView = (ExpandableListView)findViewById(R.id.exp_inv_add_list);
		eView.setFocusable(false);
		eView.setDividerHeight(2);
		eView.setClickable(false);

		groupItems = new ArrayList<IngredientGroup>();
		groupNames = new ArrayList<String>();
		children = new ArrayList<IngredientChild>();
		
		fillArrays();
		
		mSearchView = (SearchView) findViewById(R.id.search);
		eAdapter = new BaseListAdapter(getApplicationContext(), groupItems, table);
		eView.setAdapter(eAdapter);	
		
	    setupSearchView();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.inventory_add, menu);
		return true;
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
	
	/** Display all types in a drop-down menu for user to select
	 * Called from search if item doesn't exist */
	public void customItem() {
		Cursor types = dm.findAllTypes(table);
		// displays types in spinner -- include custom button
		// figure out how to retrieve what button user clicked
		//return null;
	}

}
