package cs169.project.thepantry;

import java.io.IOException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;

import cs169.project.thepantry.ThePantryContract.Ingredients;
import cs169.project.thepantry.ThePantryContract.Inventory;
import cs169.project.thepantry.ThePantryContract.ShoppingList;

public abstract class BaseListActivity extends BasicMenuActivity implements SearchView.OnQueryTextListener {

	public DatabaseModel dm;
	public ExpandableListView eView;
	public ListView lView;
	BaseListAdapter eAdapter;
	public static String table;
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
		mSearchView.setSubmitButtonEnabled(true);
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
			dialog.message = "Select a category to add " + query + " to your pantry?";
			dialog.types = types.toArray(new String[0]);
			dialog.item = query;
			dialog.content = lv;
			dialog.show(getFragmentManager(), "dialog");
			return true;
		}
	}
	
	/** Fills the arrays with database data. */
	public void fillArrays() {
		groupItems = getTypes(table);
		for (IngredientGroup g : groupItems) {
			groupNames.add(g.getGroup());
			System.out.println(g.getGroup());
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
				result.add(new IngredientGroup(data, new ArrayList<IngredientChild>()));
				types.moveToNext();
			}
		types.close();
		}
		return result;
	}
	
	/** Retrieves ingredients from the database and
	 *  returns an ArrayList with the ingredients to be used for display */
	public ArrayList<IngredientChild> getItems(String table, String type) {
		dm = new DatabaseModel(this, DATABASE_NAME);
		Cursor items = dm.findTypeItems(table, type);
		
		ArrayList<IngredientChild> result = new ArrayList<IngredientChild>();
		if (items != null){
			while(!items.isAfterLast()){
				String data = items.getString(0);
				IngredientChild temp = new IngredientChild(data,type);
				System.out.println(temp.getName());
				if (table != ThePantryContract.Inventory.TABLE_NAME) {
					boolean checked = dm.isItemChecked(table, data, ThePantryContract.CHECKED);
					temp.setSelected(checked);
				}
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
		boolean success = dm.addIngredient(table, item, type, amount);
		int groupPos = 0;
		IngredientGroup temp = new IngredientGroup(type,new ArrayList<IngredientChild>());
		if (success) {
			IngredientChild child = new IngredientChild(item, type);
			children.add(child);
			if (groupItems.contains(temp)) {
				groupPos = groupItems.indexOf(temp);
				eAdapter.addChild(child, groupItems.get(groupPos));
			} else {
				eAdapter.addChild(child, temp);
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
				IngredientChild item = new IngredientChild(data);
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
		String message = "";
		for (IngredientChild c : children) {
			if (c.isSelected()) {
				System.out.println(c.getName());
				boolean success = dm.addIngredient(Inventory.TABLE_NAME, c.getName(),c.getGroup(),"1");
				message += c.getName() + "\n";
				// If the shopping list "updates" the ingredients are removed from list
				dm.check(table, c.getName(), ThePantryContract.CHECKED, false);
				if (table == ShoppingList.TABLE_NAME) {
					dm.check(table, c.getName(), ThePantryContract.REMOVEFLAG, true);
				} 
				if (!success) {
					System.err.println("You Fucked Up");
				}
			}
		}
		
		// Opens pop up window with items being added to the pantry
		UpdateIngredientsDialogFragment dialog = new UpdateIngredientsDialogFragment();
		dialog.context = this;
		dialog.message = message;
		dialog.show(getFragmentManager(), "dialog");
	}
	
	/* Class for displaying popup dialog for adding ingredients
	 * 
	 */
	public static class UpdateIngredientsDialogFragment extends DialogFragment {
		
		Context context;
		String message;
		
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setTitle(R.string.dialog_update_inventory)
	        	   .setMessage(message) 
	        	   .setPositiveButton(R.string.inventory_go, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   //go to inventory
	                  		Intent intent = new Intent(context, InventoryActivity.class);
	                  		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	                  		startActivity(intent);
	                   }
	               })
	               .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	               		
	                   }
	               });
	        // Create the AlertDialog object and return it
	        return builder.create();
	    }
	}
	
	/* Class for displaying popup dialog for adding new ingredients
	 * 
	 */
	public static class AddIngredientsDialogFragment extends DialogFragment {
		
		Context context;
		String message;
		String[] types;
		String item;
		String selectedType;
		ListView content;
		
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setTitle(message)
	        	   .setSingleChoiceItems(types, 0,
	        			   new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == -1) {
							selectedType = "Other";
						} else {
							selectedType = types[which];
						}
					}
	        	   })
	        	   .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   // call context function
	                	   // adds selected category to the database
	                	   
	                	   //DatabaseModel dm = new DatabaseModel(context, ThePantryContract.DATABASE_NAME);
	                	   //dm.add(table, item, selectedType, "1");
	                   }
	               })
	               .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	               		
	                   }
	               });
	        // Create the AlertDialog object and return it
	        return builder.create();
	    }
	}

}
