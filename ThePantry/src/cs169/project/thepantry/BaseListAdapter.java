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
	
	public ArrayList<IngredientGroup> groups;
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
		for (IngredientGroup g : groups) {
			System.out.println(g.getGroup());
			System.out.println(group.getGroup());
			System.out.println(g.equals(group));
		}
		System.out.println("looking for " + group.getGroup());
		ArrayList<IngredientChild> children = groups.get(index).getChildren();
		children.remove(child);
		if (children.size() <= 0) {
			groups.remove(group);
		}
		notifyDataSetChanged();
		dm = new DatabaseModel(context, DATABASE_NAME);
		dm.check(Ingredients.TABLE_NAME, child.getName(), ThePantryContract.CHECKED, false);
		if (table != Ingredients.TABLE_NAME) {
			dm.check(table, child.getName(), ThePantryContract.REMOVEFLAG, true);
		} else {
			dm.remove(table, child.getName());
		}
		dm.close();
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
			convertView = infalInflater.inflate(R.layout.child_row, null);
		}
		IngredientChild child = getChild(groupPosition, childPosition);
		final IngredientGroup group = getGroup(groupPosition);
		final ViewHolder childHolder;
		childHolder = new ViewHolder((CheckBox)convertView.findViewById(R.id.checkBox1), child.isSelected());
		childHolder.cb.setText(WordUtils.capitalizeFully(child.getName()));
		
		convertView.setOnClickListener(new OnClickListener (){
			@Override
			public void onClick(View v) {
				IngredientChild child = (IngredientChild) childHolder.cb
						.getTag();
				((CheckBox)childHolder.cb).toggle();
				child.setSelected(((CheckBox)childHolder.cb).isChecked());
				dm.check(table, child.getName(), ThePantryContract.CHECKED, ((CheckBox)childHolder.cb).isChecked());
			}
		});
		childHolder.cb.setTag(child);
		return convertView;
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
	
	
}