package cs169.project.thepantry;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SearchView;

import com.actionbarsherlock.view.Menu;

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
		
	    setupSearchView();
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.inventory, menu);
		return true;
	}
	

	/** Takes you to InventoryAdd Activity */
	public void edit(View view) {
		Context context = getApplicationContext();
		Intent intent = new Intent(context, InventoryAddActivity.class);
		startActivity(intent);
	}
	

	
}
