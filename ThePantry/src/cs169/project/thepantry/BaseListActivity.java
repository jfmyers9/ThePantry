package cs169.project.thepantry;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SearchView;


import com.actionbarsherlock.view.Menu;

import cs169.project.thepantry.ThePantryContract.Inventory;
import cs169.project.thepantry.ThePantryContract.ShoppingList;

public abstract class BaseListActivity extends BasicMenuActivity implements SearchView.OnQueryTextListener {

	public DatabaseModel dm;
	public ExpandableListView eView;
	public ListView lView;
	BaseListAdapter eAdapter;
	public String table;
	public static final String DATABASE_NAME = "thepantry";
	public ArrayList<IngredientGroup> groupItems;
	public ArrayList<String> groupNames;
	public ArrayList<IngredientChild> children;
	public SearchView mSearchView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.base_list, menu);
		return true;
	}

	public void setupSearchView() {
		mSearchView.setIconifiedByDefault(false);
		mSearchView.setOnQueryTextListener(this);
		mSearchView.setSubmitButtonEnabled(false);
		mSearchView.setQueryHint(getString(R.string.ingredient_search));
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
			BaseListViewAdapter lAdapter = new BaseListViewAdapter(this,  items);
			lView.setAdapter(lAdapter);
			eView.setVisibility(View.INVISIBLE);
			lView.setVisibility(View.VISIBLE);
		}
		return true;
	}

	public boolean onQueryTextSubmit(String query) {
		return false;
	}
	
	/** Fills the arrays with database data. */
	public void fillArrays() {
		groupItems = getTypes(table);
		for (IngredientGroup g : groupItems) {
			groupNames.add(g.getGroup());
			g.setChildren(getItems(table, g.getGroup()));
		}
	}
	
	/** Retrieves ingredient types from the database and
	 *  returns an ArrayList with said types to be used for display */
	public ArrayList<IngredientGroup> getTypes(String table) {
		dm = new DatabaseModel(this, DATABASE_NAME);
		System.out.println("getTypes table is " + table);
		Cursor types = dm.findAllTypes(table);
		ArrayList<IngredientGroup> result = new ArrayList<IngredientGroup>();
		if (types!=null){
			while(!types.isAfterLast()){
				String data = types.getString(0);
				System.out.println(data);
				result.add(new IngredientGroup(data, new ArrayList<IngredientChild>()));
				types.moveToNext();
			}
		types.close();
		}
		return result;
	}
	
	/** Retrieves ingredients from the database and
	 *  returns an ArrayList with said ingredients to be used for display */
	public ArrayList<IngredientChild> getItems(String table, String type) {
		dm = new DatabaseModel(this, DATABASE_NAME);
		Cursor items = dm.findTypeItems(table, type);
		
		ArrayList<IngredientChild> result = new ArrayList<IngredientChild>();
		if (items != null){
			while(!items.isAfterLast()){
				String data = items.getString(0);
				IngredientChild temp = new IngredientChild(data,type);
				children.add(temp);
				result.add(temp);
				items.moveToNext();
			}
			items.close();
		}
		return result;
	}
	
	/** Adds the given item to the list and the database
	 * @throws IOException */
	public void addItem(String table, String item, String type, String amount) throws IOException {
		if (item.matches("[\\s]*")) {
			throw new IOException("Ingredient cannot be empty, please try again");
		}
		DatabaseModel dm = new DatabaseModel(this, DATABASE_NAME);
		boolean success = dm.add(table, item, type, amount);
		int groupPos = 0;
		IngredientGroup temp = new IngredientGroup(type,new ArrayList<IngredientChild>());
		if (success) {
			if (groupItems.contains(temp)) {
				groupPos = groupItems.indexOf(temp);
				eAdapter.addChild(new IngredientChild(item, type), groupItems.get(groupPos));
			} else {
				eAdapter.addChild(new IngredientChild(item, type), temp);
			}
		}
	}
	
	/** Search database with a given string */
	public ArrayList<IngredientChild> search(String query) {
		// might return ingredient child if I make a custom adapter for listView
		dm = new DatabaseModel(this, DATABASE_NAME);
		Cursor items = dm.search(table, query);
		
		ArrayList<IngredientChild> result = new ArrayList<IngredientChild>();
		if (items != null){
			while(!items.isAfterLast()){
				String data = items.getString(0);
				IngredientChild item = new IngredientChild(data,"tmp"); // I'm lazy change this to the right type
				result.add(item);
				items.moveToNext();
			}
			items.close();
		}
		return result;
	}
	
	/** Removes the given item from the database and list 
	 * @throws ThePantryException */
	public void removeItem(String table, String item) throws ThePantryException {
		dm = new DatabaseModel(this, DATABASE_NAME);
		boolean success = dm.remove(table, item);
		if (!success) {
			//throw new ThePantryException(item + " could not be removed from database");
		}
	}
	
	/** Adds all items to inventory database that have been checked 
	 * Need to check if item is in inventory table -- can increment amount if is 
	 * @throws ThePantryException */
	public void updateInventory(View view) throws ThePantryException {
		dm = new DatabaseModel(this, DATABASE_NAME);
		for (IngredientChild c : children) {
			if (c.isSelected()) {
				boolean success = dm.add(Inventory.TABLE_NAME, c.getName(),c.getGroup(),"1");
				
				// If the shopping list "updates" the ingredients are removed from list
				if (table == ShoppingList.TABLE_NAME) {
					dm.remove(table, c.getName());
				}
				if (!success) {
					System.err.println("You Fucked Up");
				}
			}
		}
		
		//For now it takes you to inventory -- next iteration it will pop toast?
		Context context = getApplicationContext();
		Intent intent = new Intent(context, InventoryActivity.class);
		startActivity(intent);
	}

}
