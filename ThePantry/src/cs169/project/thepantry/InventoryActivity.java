package cs169.project.thepantry;

import java.util.ArrayList;

import android.app.ProgressDialog;
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
	
	ProgressDialog progressDialog;
	Thread thread;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.InventoryTitle));
		setContentView(R.layout.activity_inventory);
		table = Inventory.TABLE_NAME;
		
		setupAdapter();
		setupSearchView();
		mSearchView.setQueryHint(getString(R.string.inventory_hint));
		lView = (ListView) findViewById(R.id.inv_list);
		lView.setVisibility(View.INVISIBLE);
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
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
		setupAdapter();
	}

	/** Takes you to InventoryAdd Activity */
	public void edit(View view) {
		// TODO Thread shit doesn't do anything -- total bull shit
		thread = new Thread()
		{
		    @Override
		    public void run() {
					progressDialog = new ProgressDialog(InventoryActivity.this);
					progressDialog.setMessage("Loading Ingredients...");
					progressDialog.show();
					
		    }
		};
		thread.run();
		Context context = getApplicationContext();
		Intent intent = new Intent(context, InventoryAddGrid.class);
		startActivity(intent);
	}
	
	
}
