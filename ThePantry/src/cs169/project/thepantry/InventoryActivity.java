package cs169.project.thepantry;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
	ProgressDialog progressDialog;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
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
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Loading Ingredients...");
		progressDialog.show();
		Context context = getApplicationContext();
		Intent intent = new Intent(context, InventoryAddGrid.class);
		startActivity(intent);
	}
	
	
}
