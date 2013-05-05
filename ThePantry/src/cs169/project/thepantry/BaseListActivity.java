package cs169.project.thepantry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import org.apache.commons.lang3.text.WordUtils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
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

import cs169.project.thepantry.ThePantryContract.Ingredients;
import cs169.project.thepantry.ThePantryContract.Inventory;
import cs169.project.thepantry.ThePantryContract.ShoppingList;

public abstract class BaseListActivity extends BasicMenuActivity implements SearchView.OnQueryTextListener {

	public static DatabaseModel dm;
	public ExpandableListView eView;
	public ListView lView;
	BaseListAdapter eAdapter;
	BaseListViewAdapter lAdapter;
	public static String table;
	public static final String DATABASE_NAME = "thepantry";
	public static ArrayList<IngredientGroup> groupItems;
	public static ArrayList<String> groupNames;
	public static ArrayList<IngredientChild> children;
	public SearchView mSearchView;
	public static Context context;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		context = getApplicationContext();
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.base_list, menu);
		return true;
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
	
	// get the search view and then call this, and then set the hint in the activity
	public void setupSearchView() {
		mSearchView.setIconifiedByDefault(false);
		mSearchView.setOnQueryTextListener(this);
		mSearchView.setSubmitButtonEnabled(true);
	}
	
	public static IngredientGroup find(ArrayList<IngredientGroup> groups, String groupName) {
		for (IngredientGroup group : groups) {
			if (group.equals(groupName)) {
				return group;
			}
		}
		return null;
	}
	
	public boolean onQueryTextChange(String newText) {
		if (TextUtils.isEmpty(newText)) {
			lView.setVisibility(View.INVISIBLE);
			lView.setAdapter(null);
			eView.setVisibility(View.VISIBLE);
		} else {
			ArrayList<IngredientChild> items = search(newText);
			lAdapter = new BaseListViewAdapter(this,  items, table);
			lAdapter.eAdapter = eAdapter;
			lView.setAdapter(lAdapter);
			eView.setVisibility(View.INVISIBLE);
			lView.setVisibility(View.VISIBLE);
		}
		return true;
	}

	public boolean onQueryTextSubmit(String query) {
		String message;
		if(dm.findItem(table, query) && !dm.isItemChecked(table, query, ThePantryContract.REMOVEFLAG)) {
			// TODO: Make this popup window to increment amount
			message = "You have already added this item.";
			Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
			toast.show();
			return true;
		} else {
			AddIngredientsDialogFragment dialog = new AddIngredientsDialogFragment();		    
		    ArrayList<IngredientGroup> gTypes = getTypes(Ingredients.TABLE_NAME);
		    ArrayList<String> types = new ArrayList<String>();
		    for (IngredientGroup g : gTypes) {
		    	types.add(WordUtils.capitalizeFully(g.getGroup()));
			}
		    
			dialog.context = this;
			dialog.message = "Select a category for " + query + ".";
			dialog.types = types.toArray(new String[0]);
			dialog.item = query;
			dialog.eAdapter = eAdapter;
			dialog.table = table;
			if (table.equals(Inventory.TABLE_NAME)) {
				System.out.println("lAdapter " + lAdapter);
				dialog.lAdapter = lAdapter;
			}
			dialog.show(getFragmentManager(), "dialog");
			mSearchView.setQuery("", false);
			return true;
		}
	}
	
	public static void resetChildren() {
		DatabaseModel dm = new DatabaseModel(context, ThePantryContract.DATABASE_NAME);
		children = dm.findAllItems(table);
	}
	
	/** Fills the arrays with database data. */
	public static void fillArrays() {
		children = new ArrayList<IngredientChild>();
		groupItems = getTypes(table);
		for (IngredientGroup g : groupItems) {
			groupNames.add(g.getGroup());
			g.setChildren(getItems(table, g.getGroup()));
		}
		if (table == ThePantryContract.Ingredients.TABLE_NAME) {
			setCommImg(); // combine this with setChecked to clean up later
		} 
		if (table != ThePantryContract.Inventory.TABLE_NAME) {
			setChecked();
		}
		//Collections.sort(children, ALPHABETICAL_ORDER);
	}
	
	public static Comparator<IngredientChild> ALPHABETICAL_ORDER = new Comparator<IngredientChild>() {
	    public int compare(IngredientChild child1, IngredientChild child2) {
	    	String str1 = child1.getName();
	    	String str2 = child2.getName();
	        int res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
	        if (res == 0) {
	            res = str1.compareTo(str2);
	        }
	        return res;
	    }
	};
	
	// sets if a child is marked as checked
	public static void setChecked() {
		dm = new DatabaseModel(context, DATABASE_NAME);
		for (IngredientChild child : children) {
			boolean checked = dm.isItemChecked(table, child.getName(), ThePantryContract.CHECKED);
			child.setSelected(checked);
		}
		dm.close();
	}
	
	// Sets the common and image field for each child 
	public static void setCommImg() {
		dm = new DatabaseModel(context, DATABASE_NAME);
		for (IngredientChild child : children) {
			String childName = child.getName();
			boolean checked = dm.isItemChecked(table, childName, ThePantryContract.Ingredients.COMMON);
			child.setCommon(checked);
			child.setImage(dm.findIngImg(childName));
		}
		dm.close();
	}
	
	/** Retrieves ingredient types from the database and
	 *  returns an ArrayList with said types to be used for display */
	public static ArrayList<IngredientGroup> getTypes(String table) {
		dm = new DatabaseModel(context, DATABASE_NAME);
		ArrayList<IngredientGroup> types = dm.findAllTypes(table);
		dm.close();
		return types;
	}
	
	/** Retrieves ingredients from the database and
	 *  returns an ArrayList with the ingredients to be used for display */
	public static ArrayList<IngredientChild> getItems(String table, String type) {
		dm = new DatabaseModel(context, DATABASE_NAME);
		ArrayList<IngredientChild> items = dm.findTypeItems(table, type);
		dm.close();
		children.addAll(items);
		return items;
	}
	
	/** Adds the given item to the list and the database
	 * @throws IOException */
	public void addItem(String table, String query, Context context) throws IOException {
		if (query.matches("[\\s]*")) {
			throw new IOException("Ingredient cannot be empty, please try again");
		}
	    ListView lv = new ListView(this);
		ArrayList<IngredientGroup> gTypes = getTypes(Ingredients.TABLE_NAME);
	    ArrayList<String> types = new ArrayList<String>();
	    for (IngredientGroup g : gTypes) {
	    	types.add(g.getGroup());
		}
	    ListAdapter listTypes = new ArrayAdapter<String>(this,
	            android.R.layout.simple_list_item_checked, types);
	    lv.setAdapter(listTypes);
	    
		AddIngredientsDialogFragment dialog = new AddIngredientsDialogFragment();
		dialog.context = context;
		dialog.message = "Select a category for " + query + ".";
		dialog.types = types.toArray(new String[0]);
		dialog.item = query;
		dialog.show(getFragmentManager(), "dialog");
	}
	
	/** Search database with a given string */
	public ArrayList<IngredientChild> search(String query) {
		// might return ingredient child if I make a custom adapter for listView
		dm = new DatabaseModel(this, DATABASE_NAME);
		ArrayList<IngredientChild> items = dm.search(Ingredients.TABLE_NAME, query);
		dm.close();
		return items;
	}
	
	/** Removes the given item from the database and list 
	 * @throws ThePantryException */
	public void removeChecked(View v) throws ThePantryException {
		String message = "";
		for (IngredientChild c : children) {
			if (c.isSelected()) {
				message += c.getName() + "\n";
			}
		}
		// Opens pop up window with items being removed
		if (message.equals("")) {
			message = "You have not selected any items";
			Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
			toast.show();
		} else {
			RemoveIngredientsDialogFragment dialog = new RemoveIngredientsDialogFragment();
			dialog.context = this;
			dialog.message = message;
			dialog.table = table;
			dialog.eAdapter = eAdapter;
			dialog.lAdapter = lAdapter;
			dialog.show(getFragmentManager(), "dialog");
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
				boolean success = dm.addIngredient(Inventory.TABLE_NAME, c.getName(),c.getGroup(),"1");
				message += c.getName() + "\n";
				// If the shopping list "updates" the ingredients are removed from list
				dm.check(table, c.getName(), ThePantryContract.CHECKED, false);
				if (table.equals(ShoppingList.TABLE_NAME)) {
					dm.check(table, c.getName(), ThePantryContract.REMOVEFLAG, true);
				}
				if (!success) {
					// Pantry Exception instead
					System.err.println("You Fucked Up");
				}
			}
			dm.close();
		}
		
		// Opens pop up window with items being added to the pantry
		if (message.equals("")) {
			message = "You have not selected any items";
			Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
			toast.show();
		} else {
			UpdateIngredientsDialogFragment dialog = new UpdateIngredientsDialogFragment();
			dialog.context = this;
			dialog.message = message;
			dialog.show(getFragmentManager(), "dialog");
		}
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
		String table;
		String selectedType;
		BaseListViewAdapter lAdapter;
		BaseListAdapter eAdapter;
		
		private void addItem() {
			DatabaseModel dm = new DatabaseModel(context, ThePantryContract.DATABASE_NAME);
			if (selectedType == null) {
				selectedType = types[0];
			}
			boolean success = dm.addIngredient(table, item, selectedType, "1");
			IngredientGroup temp = new IngredientGroup(selectedType,new ArrayList<IngredientChild>());
			if (success) {
				IngredientChild child = new IngredientChild(item, selectedType);
				children.add(child);
				if (groupItems.contains(temp)) {
					int groupPos = groupItems.indexOf(temp);
					eAdapter.addChild(child, groupItems.get(groupPos));
				} else {
					eAdapter.addChild(child, temp);
				}
				eAdapter.notifyDataSetChanged();
				if (table.equals(Inventory.TABLE_NAME)) {
					lAdapter.addItem(child);
				}
			}
		}		
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setTitle(message)
	        	   .setSingleChoiceItems(types, 0,
	        			   new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						selectedType = types[which];
					}
	        	   })
	        	   .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   // call context function
	                	   // adds selected category to the database
	                	   addItem();
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
	
	/* Class for displaying popup dialog for removing ingredients
	 * 
	 */
	public static class RemoveIngredientsDialogFragment extends DialogFragment {
		
		Context context;
		String message;
		String[] types;
		String item;
		String table;
		String selectedType;
		BaseListViewAdapter lAdapter;
		BaseListAdapter eAdapter;
		DatabaseModel dm;
		
		private void removeItems() {
			for (IngredientChild child : children) {
				if (child.isSelected()) {
					// If the shopping list "updates" the ingredients are removed from list
					dm = new DatabaseModel(context, DATABASE_NAME);
					dm.check(Ingredients.TABLE_NAME, child.getName(), ThePantryContract.CHECKED, false);
					if (table != Ingredients.TABLE_NAME) {
						dm.check(table, child.getName(), ThePantryContract.REMOVEFLAG, true);
					} else {
						dm.remove(table, child.getName());
					}
				}
			}
			fillArrays();
			eAdapter.groups = groupItems;
			//resetChildren();
			eAdapter.notifyDataSetChanged();
		}	
		
		@Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setTitle(R.string.dialog_remove_items)
	        	   .setMessage(message) 
	        	   .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   removeItems();
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
