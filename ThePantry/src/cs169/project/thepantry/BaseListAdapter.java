package cs169.project.thepantry;

import java.util.ArrayList;

import org.apache.commons.lang3.text.WordUtils;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import cs169.project.thepantry.ThePantryContract.Ingredients;
import cs169.project.thepantry.ThePantryContract.Inventory;

public class BaseListAdapter extends BaseExpandableListAdapter {
	
	private ArrayList<IngredientGroup> groups;
	private Context context;
	private String table;
	private DatabaseModel dm;
	private BaseListAdapter currAdapt;
	private static final String DATABASE_NAME = "thepantry";
	private static final String TAG = "BaseListAdapter";
	
	public BaseListAdapter(Context context, ArrayList<IngredientGroup> groups, String table) {
		this.context = context;
		this.groups = groups;
		this.table = table;
		this.currAdapt = this;
	}
	
	public void addChild(IngredientChild child, IngredientGroup group) {
		if (!groups.contains(group)) {
			groups.add(group);
		}
		int index = groups.indexOf(group);
		ArrayList<IngredientChild> children = groups.get(index).getChildren();
		children.add(child);
		groups.get(index).setChildren(children);
	}
	
	public void removeChild(IngredientChild child, IngredientGroup group) {
		// Pop up a window to ask if user wants to remove item
		
		int index = groups.indexOf(group);
		ArrayList<IngredientChild> children = groups.get(index).getChildren();
		children.remove(child);
		if (children.size() <= 0) {
			groups.remove(group);
		}
		notifyDataSetChanged();
		dm = new DatabaseModel(context, DATABASE_NAME);
		if (table == Inventory.TABLE_NAME) {
			dm.check(Ingredients.TABLE_NAME, child.getName(), ThePantryContract.CHECKED, false);
		}
		if (table != Ingredients.TABLE_NAME) {
			dm.check(table, child.getName(), ThePantryContract.REMOVEFLAG, true);
		} else {
			dm.remove(table, child.getName());
		}
	}

	@Override
	public IngredientChild getChild(int groupPosition, int childPosition) {
		return groups.get(groupPosition).getChildren().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}
	

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null) {
			dm = new DatabaseModel(context, DATABASE_NAME);
			LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
			if (table == Inventory.TABLE_NAME) {
				convertView = infalInflater.inflate(R.layout.child_row_inventory, null);
			} else {
				convertView = infalInflater.inflate(R.layout.child_row, null);
			}
		}
		IngredientChild child = getChild(groupPosition, childPosition);
		final IngredientGroup group = getGroup(groupPosition);
		final ViewHolder childHolder;
		if (table != Inventory.TABLE_NAME) {
			childHolder = new ViewHolder((CheckBox)convertView.findViewById(R.id.checkBox1), child.isSelected());
		} else {
			childHolder = new ViewHolder((TextView)convertView.findViewById(R.id.textView));
		}
		childHolder.cb.setText(WordUtils.capitalizeFully(child.getName()));
		// Detects if a given item was swiped
		final SwipeDetector swipeDetector = new SwipeDetector();
		convertView.setOnTouchListener(swipeDetector);
		
		convertView.setOnClickListener(new OnClickListener (){
			@Override
			public void onClick(View v) {
				IngredientChild child = (IngredientChild) childHolder.cb
						.getTag();
				if (swipeDetector.swipeDetected()) {
					//showDialog(); // currently not working will display a box to ask user if they want to delete
					//System.out.println(dm.isItemChecked(table, child.getName(), ThePantryContract.REMOVEFLAG));
					removeChild(child, group);
				} else if (table != Inventory.TABLE_NAME) {
					((CheckBox)childHolder.cb).toggle();
					child.setSelected(((CheckBox)childHolder.cb).isChecked());
					dm.check(table, child.getName(), ThePantryContract.CHECKED, ((CheckBox)childHolder.cb).isChecked());
				}
			}
		});
		childHolder.cb.setTag(child);
		return convertView;
	}
	
	public void showDialog(IngredientChild child, IngredientGroup group) {
		AddIngredientsDialogFragment dialog = new AddIngredientsDialogFragment();
		dialog.context = context;
		dialog.message = child.getName();
		dialog.child = child;
		dialog.group = group;
		dialog.adapter = currAdapt;
		
		// Figure out how to do FragmentManager in a class
		//FragmentManager tmp = new FragmentManager();
		//dialog.show(getFragmentManager(), "dialog");
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return groups.get(groupPosition).getChildren().size();
	}

	@Override
	public IngredientGroup getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}
	
	@Override
	public int getGroupCount() {
		return groups.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		IngredientGroup group = groups.get(groupPosition);
		if (convertView == null) {
			LayoutInflater inf = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
			convertView = inf.inflate(R.layout.group_row, null);
		}
		TextView tv = (TextView)convertView.findViewById(R.id.checkedText1);
		tv.setText(WordUtils.capitalizeFully(group.getGroup()));
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	public static class ViewHolder {
	    protected TextView cb;
	    ViewHolder(TextView text) {
	    	cb = text;
	    }
	    
	    ViewHolder(TextView checkBox, boolean selected) {
	    	cb = checkBox;
	    	((CheckBox)cb).setChecked(selected);
	    }
	}
	
	/* Class for displaying popup dialog for adding ingredients
	 * 
	 */
	public static class AddIngredientsDialogFragment extends DialogFragment {
		
		Context context;
		String message;
		BaseListAdapter adapter;
		IngredientChild child;
		IngredientGroup group;
		
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setTitle(R.string.delete_check + message + "?")
	        	   .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   adapter.removeChild(child, group); // pass in instance of baselist?
	                   }
	               })
	               .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   //Do Nothing
	                   }
	               });
	        // Create the AlertDialog object and return it
	        return builder.create();
	    }
	}
	
}