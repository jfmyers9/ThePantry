package cs169.project.thepantry;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.lang3.text.WordUtils;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;

import cs169.project.thepantry.BaseListActivity.UpdateIngredientsDialogFragment;
import cs169.project.thepantry.ThePantryContract.Ingredients;
import cs169.project.thepantry.ThePantryContract.Inventory;


@SuppressLint("ResourceAsColor")
public class InventoryAddGrid extends BaseListActivity {

	public ArrayList<IngredientChild> commonChildren;
	public ArrayList<IngredientChild> alphaChildren;
	public ArrayList<IngredientChild> recentAdds;
	public final ImageAdapter imgAdapterComm = new ImageAdapter(this);
	public final ImageAdapter imgAdapterAlpha = new ImageAdapter(this);
	public ImageListener commonListener = new ImageListener(this);
	public ImageListener alphaListener = new ImageListener(this);
	Toast addRemoveToast;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inventory_add_grid);
		setTitle(getString(R.string.InventoryAddTitle));
		table = Ingredients.TABLE_NAME;
		groupNames = new ArrayList<String>();
		children = new ArrayList<IngredientChild>();
		recentAdds = new ArrayList<IngredientChild>();
		
		// Fills children, groupItems, and groupNames
		// arrays from the database
		fillArrays();
		
		
		
		final ActionBar actionBar = getActionBar();

		// Specify that tabs should be displayed in the action bar.
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create a tab listener that is called when the user changes tabs.
		IngredientsTab tabListener = new IngredientsTab();
		
		
		// Sets each tab to be a type stored in the database
		for (IngredientGroup group : groupItems) {
			actionBar.addTab(
					actionBar.newTab()
					.setText(group.getGroup())
					.setTabListener(tabListener));
		}
		
		

		SlidingMenu sm = getSlidingMenu();
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);

		// Set common title
		TextView commonTitle = (TextView) findViewById(R.id.common);
		commonTitle.setText(R.string.common);
		commonTitle.setTextColor(R.color.grey);
		
		// Set gridview of common ingredients
		ExpandableHeightGridView gridViewCommon = (ExpandableHeightGridView) findViewById(R.id.inventoryGrid);
		commonListener = new ImageListener(this, imgAdapterComm, commonChildren);
		setupGridView(gridViewCommon, imgAdapterComm, commonListener);
		
		// Set divider
		View view = (View) findViewById(R.id.divider);
		
		// Set alphabetical title
		TextView alphaTitle = (TextView) findViewById(R.id.alpha);
		alphaTitle.setText(R.string.alpha);
		alphaTitle.setTextColor(R.color.grey);
		
		// Set gridview of alphabetical order ingredients
		ExpandableHeightGridView gridViewAlpha = (ExpandableHeightGridView) findViewById(R.id.inventoryGridAlpha);
		alphaListener = new ImageListener(this, imgAdapterAlpha, alphaChildren);
		setupGridView(gridViewAlpha, imgAdapterAlpha, alphaListener);
	}
	
	public void setupGridView(ExpandableHeightGridView gridView, ImageAdapter imageAdapter, ImageListener listener) {
		gridView.setAdapter(imageAdapter);
		gridView.setExpanded(true);
		gridView.setOnItemClickListener(listener);
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
	
	private IngredientChild find(ArrayList<IngredientChild> children, IngredientChild childFind) {
		for (IngredientChild child : children) {
			if (child.equals(childFind)) {
				return child;
			}
		}
		return null;
	}
	
	public ArrayList<IngredientChild> getCommonChildren(IngredientGroup group, boolean common) {
		ArrayList<IngredientChild> children = new ArrayList<IngredientChild>();
		ArrayList<IngredientChild> groupChildren = group.getChildren();
		dm = new DatabaseModel(this, ThePantryContract.DATABASE_NAME);
		for (IngredientChild child : groupChildren) {
			boolean comm = dm.isItemChecked(table, child.getName(), ThePantryContract.Ingredients.COMMON);
			if(comm) {
				if (common) {
					children.add(child);
				}
			} else {
				if (!common) {
					children.add(child);
				}
			}
		}
		dm.close();
		Collections.sort(children, ALPHABETICAL_ORDER);
		return children;
	}
	
	class ImageListener implements OnItemClickListener {
		ImageAdapter imageAdapter;
		ArrayList<IngredientChild> children;
		Context context;
		ImageListener(Context context){}
		ImageListener(Context context, ImageAdapter imageAdapter, ArrayList<IngredientChild> children) {
			this.imageAdapter = imageAdapter;
			this.children = children;
			this.context = context;
		}
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			// Decide if this is the best way to determine if its been selected previously
			IngredientChild child = children.get(position);
			String childName = child.getName();
			if (!child.isSelected()) {
				child.setSelected(!child.isSelected());
				DatabaseModel dm = new DatabaseModel(InventoryAddGrid.this, DATABASE_NAME);
				
				dm.check(table, childName, ThePantryContract.CHECKED, true);
				if (addRemoveToast != null) {
					addRemoveToast.cancel();
				}
				addRemoveToast.makeText(InventoryAddGrid.this, WordUtils.capitalizeFully(children.get(position).getName()) + " added to your pantry", Toast.LENGTH_SHORT).show();
				boolean success = dm.addIngredient(Inventory.TABLE_NAME, childName, child.getGroup(),"1");
				dm.check(Inventory.TABLE_NAME, childName, ThePantryContract.ADDFLAG, true);
				if (!recentAdds.contains(child)) {
					recentAdds.add(child);
				}
			} else {
				dm.check(table, childName, ThePantryContract.CHECKED, false);
				// TODO: Add remove non-recent with display box
				if (!recentAdds.contains(child)) {
					RemoveDialogFragment dialog = new RemoveDialogFragment();
					dialog.context = context;
					String message = "Are you sure you want to remove " + childName + " from your pantry?";
					dialog.message = message;
					dialog.child = child;
					dialog.imageAdapter = imageAdapter;
					dialog.show(getFragmentManager(), "dialog");
					//child.setSelected(!child.isSelected()); Change this only if they confirm delete
				} else {
					child.setSelected(!child.isSelected());
					dm.check(Inventory.TABLE_NAME, childName, ThePantryContract.ADDFLAG, false);
					if (addRemoveToast != null) {
						addRemoveToast.cancel();
					}
					addRemoveToast.makeText(InventoryAddGrid.this, WordUtils.capitalizeFully(children.get(position).getName()) + " removed from your pantry", Toast.LENGTH_SHORT).show();
				}
			}
			imageAdapter.notifyDataSetChanged();
			dm.close();
			
		}
	}
	
	class IngredientsTab implements ActionBar.TabListener {
		
		public void onTabSelected(ActionBar.Tab tab,
				FragmentTransaction ft) { 
			// When the tab is selected, get the name
            // of the tab
			String type = (String) tab.getText(); 
			//groupItems.contains(type); // This doesn't work for some reason, would be nice to get it working
			if (groupItems.contains(new IngredientGroup(type, children))) {
				IngredientGroup group = find(groupItems, type);
				commonChildren = getCommonChildren(group, true);
				imgAdapterComm.ingredients = commonChildren;
				commonListener.children = commonChildren;
				imgAdapterComm.notifyDataSetChanged();
				alphaChildren = getCommonChildren(group, false);
				imgAdapterAlpha.ingredients = alphaChildren;
				alphaListener.children = alphaChildren;
				imgAdapterAlpha.notifyDataSetChanged();
			} else {
				//throw PantryException();
			}
		}

		public void onTabUnselected(ActionBar.Tab tab,
				FragmentTransaction ft) { }

		public void onTabReselected(ActionBar.Tab tab,
				FragmentTransaction ft) { }
		
		
	}
	
public static class RemoveDialogFragment extends DialogFragment {

	Context context;
	String message;
	String[] types;
	String item;
	String selectedType;
	ListView content;
	IngredientChild child;
	ImageAdapter imageAdapter;
	DatabaseModel dm;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setTitle(message)
	        	   .setPositiveButton(R.string.remove_item, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   dm = new DatabaseModel(context, ThePantryContract.DATABASE_NAME);
	                	   child.setSelected(!child.isSelected());
	                	   dm.check(Inventory.TABLE_NAME, child.getName(), ThePantryContract.REMOVEFLAG, true);
	                	   imageAdapter.notifyDataSetChanged();
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
