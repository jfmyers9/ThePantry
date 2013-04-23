package cs169.project.thepantry;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;

import cs169.project.thepantry.ThePantryContract.Ingredients;
import cs169.project.thepantry.ThePantryContract.Inventory;


public class InventoryAddGrid extends BaseListActivity {

	public ArrayList<IngredientChild> displayChildren;
	public ImageAdapter imgAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inventory_add_grid);
		setTitle(getString(R.string.InventoryAddTitle));
		table = Ingredients.TABLE_NAME;
		groupNames = new ArrayList<String>();
		children = new ArrayList<IngredientChild>();
		imgAdapter = new ImageAdapter(this);
		// Fills children, groupItems, and groupNames
		// arrays from the database
		fillArrays();
		
		final ActionBar actionBar = getActionBar();

		// Specify that tabs should be displayed in the action bar.
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create a tab listener that is called when the user changes tabs.
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			public void onTabSelected(ActionBar.Tab tab,
					FragmentTransaction ft) { 
				// When the tab is selected, get the name
                // of the tab
				String type = (String) tab.getText(); 
				//groupItems.contains(type); // This doesn't work for some reason, would be nice to get it working
				if (groupItems.contains(new IngredientGroup(type, children))) {
					IngredientGroup group = find(groupItems, type);
					displayChildren = getCommonChildren(group);
					imgAdapter.ingredients = displayChildren;
					imgAdapter.notifyDataSetChanged();
				} else {
					//throw PantryException();
				}
			}

			public void onTabUnselected(ActionBar.Tab tab,
					FragmentTransaction ft) { }

			public void onTabReselected(ActionBar.Tab tab,
					FragmentTransaction ft) { }
		};
		
		// Sets each tab to be a type stored in the database
		for (IngredientGroup group : groupItems) {
			actionBar.addTab(
					actionBar.newTab()
					.setText(group.getGroup())
					.setTabListener(tabListener));
		}
		
		

		SlidingMenu sm = getSlidingMenu();
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);

		GridView gridview = (GridView) findViewById(R.id.inventoryGrid);
		gridview.setAdapter(imgAdapter);

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				// Decide if this is the best way to determine if its been selected previously
				IngredientChild child = displayChildren.get(position);
				child.setSelected(!child.isSelected());
				if (child.isSelected()) {
					dm = new DatabaseModel(InventoryAddGrid.this, DATABASE_NAME);
					dm.check(table, child.getName(), ThePantryContract.CHECKED, true);
					boolean success = dm.addIngredient(Inventory.TABLE_NAME, child.getName(), child.getGroup(),"1");
				} else {
					dm.check(table, child.getName(), ThePantryContract.CHECKED, false);
				}
				dm.close();
				
				//Toast.makeText(InventoryAddGrid.this, "" + displayChildren.get(position).getName(), Toast.LENGTH_SHORT).show();
			}
		});
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
	
	private IngredientGroup find(ArrayList<IngredientGroup> groups, String groupName) {
		for (IngredientGroup group : groups) {
			if (group.equals(groupName)) {
				return group;
			}
		}
		return null;
	}
	
	private IngredientChild find(ArrayList<IngredientChild> children, IngredientChild childFind) {
		for (IngredientChild child : children) {
			if (child.equals(childFind)) {
				return child;
			}
		}
		return null;
	}
	
	public ArrayList<IngredientChild> getCommonChildren(IngredientGroup group) {
		ArrayList<IngredientChild> common = new ArrayList<IngredientChild>();
		dm = new DatabaseModel(this, ThePantryContract.DATABASE_NAME);
		for (IngredientChild child : group.getChildren()) {
			boolean comm = dm.isItemChecked(table, child.getName(), ThePantryContract.Ingredients.COMMON);
			if(comm) {
				common.add(child);
			}
		}
		dm.close();
		return common;
	}
	
}
