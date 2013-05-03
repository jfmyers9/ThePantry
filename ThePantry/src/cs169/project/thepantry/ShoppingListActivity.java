package cs169.project.thepantry;

import java.util.ArrayList;

import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.SearchView;

import com.actionbarsherlock.view.Menu;

import cs169.project.thepantry.ThePantryContract.ShoppingList;

public class ShoppingListActivity extends BaseListActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.ShoppingListTitle));
		setContentView(R.layout.activity_shopping_list);
		table = ShoppingList.TABLE_NAME;
		eView = (ExpandableListView) findViewById(R.id.exp_shop_list);
		eView.setFocusable(true);
		eView.setDividerHeight(2);
		eView.setClickable(true);
		mSearchView = (SearchView) findViewById(R.id.search);
		setupSearchView();
		mSearchView.setQueryHint(getString(R.string.add_ing_to_shopping));
		
		groupItems = new ArrayList<IngredientGroup>();
		groupNames = new ArrayList<String>();
		children = new ArrayList<IngredientChild>();
		
		fillArrays();
		eAdapter = new BaseListAdapter(getApplicationContext(), groupItems, table);
		eView.setAdapter(eAdapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.shopping_list, menu);
		return true;
	}

}
