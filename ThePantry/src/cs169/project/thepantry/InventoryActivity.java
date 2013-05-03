package cs169.project.thepantry;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;

import cs169.project.thepantry.BaseListActivity.AddIngredientsDialogFragment;
import cs169.project.thepantry.ThePantryContract.Ingredients;
import cs169.project.thepantry.ThePantryContract.Inventory;


public class InventoryActivity extends BaseListActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.InventoryTitle));
		setContentView(R.layout.activity_inventory);
		table = Inventory.TABLE_NAME;
		lView = (ListView) findViewById(R.id.inv_list);
		lView.setVisibility(View.INVISIBLE);
		
		setupAdapter();
	    setupSearchView();
	}
	
	public void setupAdapter() {
		eView = (ExpandableListView) findViewById(R.id.exp_view);
		eView.setFocusable(true);
		eView.setDividerHeight(2);
		eView.setClickable(true);

		groupItems = new ArrayList<IngredientGroup>();
		groupNames = new ArrayList<String>();
		children = new ArrayList<IngredientChild>();
		fillArrays();
		
		mSearchView = (SearchView) findViewById(R.id.search);
		eAdapter = new BaseListAdapter(getApplicationContext(), groupItems, table);
		eView.setAdapter(eAdapter);
	}
	
	public boolean onQueryTextChange(String newText) {
		if (TextUtils.isEmpty(newText)) {
			lView.setVisibility(View.INVISIBLE);
			eView.setVisibility(View.VISIBLE);
		} else {
			ArrayList<IngredientChild> tmpItems = search(newText);
			ArrayList<IngredientChild> items = new ArrayList<IngredientChild>();
			for (IngredientChild c : children) {
				if (tmpItems.contains(c)){
					items.add(c);
				}
			}
			BaseListViewAdapter lAdapter = new BaseListViewAdapter(this,  items, table);
			lView.setAdapter(lAdapter);
			eView.setVisibility(View.INVISIBLE);
			lView.setVisibility(View.VISIBLE);
		}
		return true;
	}
	
	public boolean onQueryTextSubmit(String query) {
		String message;
		if(dm.findItem(table, query)) {
			// TODO: Make this popup window to increment amount
			message = "You already have this item";
			Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
			toast.show();
			return true;
		} else {
			AddIngredientsDialogFragment dialog = new AddIngredientsDialogFragment();
		    ListView lv = new ListView(this);
		    
		    ArrayList<IngredientGroup> gTypes = getTypes(Ingredients.TABLE_NAME);
		    ArrayList<String> types = new ArrayList<String>();
		    for (IngredientGroup g : gTypes) {
		    	types.add(g.getGroup());
			}
		    types.add("Other");
		    
		    ListAdapter listTypes = new ArrayAdapter<String>(this,
		            android.R.layout.simple_list_item_checked, types);
		    lv.setAdapter(listTypes);
			
			dialog.context = this;
			dialog.message = "Select a category for " + query + ".";
			dialog.types = types.toArray(new String[0]);
			dialog.item = query;
			dialog.content = lv;
			dialog.show(getFragmentManager(), "dialog");
			return true;
		}
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.inventory, menu);
		return true;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		table = Inventory.TABLE_NAME;
		setupAdapter();
	}

	/** Takes you to InventoryAdd Activity */
	public void edit(View view) {
		Context context = getApplicationContext();
		Intent intent = new Intent(context, InventoryAddActivity.class);
		startActivity(intent);
	}
	
	/** Takes you to InventoryAdd Activity */
	public void editTwo(View view) {
		Context context = getApplicationContext();
		Intent intent = new Intent(context, InventoryAddGrid.class);
		startActivity(intent);
	}
	
}
