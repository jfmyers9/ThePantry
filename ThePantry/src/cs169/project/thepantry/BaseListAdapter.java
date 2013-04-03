package cs169.project.thepantry;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import cs169.project.thepantry.ThePantryContract.Inventory;

public class BaseListAdapter extends BaseExpandableListAdapter {
	
	private ArrayList<IngredientGroup> groups;
	private Context context;
	private String table;
	private DatabaseModel dm;
	private static final String DATABASE_NAME = "thepantry";
	private static final String TAG = "BaseListAdapter";
	
	public BaseListAdapter(Context context, ArrayList<IngredientGroup> groups, String table) {
		this.context = context;
		this.groups = groups;
		this.table = table;
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
		dm.check(table, child.getName(), ThePantryContract.REMOVEFLAG, true);
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
			LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
			if (table == Inventory.TABLE_NAME) {
				// TODO: make it use child_row_inventory
				//convertView = infalInflater.inflate(R.layout.child_row_inventory, null);
				convertView = infalInflater.inflate(R.layout.child_row, null);
			} else {
				convertView = infalInflater.inflate(R.layout.child_row, null);
			}
		}
		IngredientChild child = getChild(groupPosition, childPosition);
		final ViewHolder childHolder = new ViewHolder((CheckBox)convertView.findViewById(R.id.checkBox1), child.isSelected());
		final IngredientGroup group = getGroup(groupPosition);
		childHolder.cb.setText(child.getName());

		
		// Detects if a given item was swiped
		final SwipeDetector swipeDetector = new SwipeDetector();
		convertView.setOnTouchListener(swipeDetector);
		
		convertView.setOnClickListener(new OnClickListener (){
			@Override
			public void onClick(View v) {
				IngredientChild child = (IngredientChild) childHolder.cb
						.getTag();
				if (swipeDetector.swipeDetected()) {
					removeChild(child, group);
				} else if (table != Inventory.TABLE_NAME) {
					childHolder.cb.toggle();
					child.setSelected(childHolder.cb.isChecked());
				}
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
		tv.setText(group.getGroup());		
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
	    protected CheckBox cb;
	    ViewHolder(CheckBox checkBox, boolean selected) {
	    	cb = checkBox;
	    	cb.setChecked(selected);
	    }
	}
	
}