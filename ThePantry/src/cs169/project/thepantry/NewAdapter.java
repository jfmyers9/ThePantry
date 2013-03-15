package cs169.project.thepantry;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class NewAdapter extends BaseExpandableListAdapter {
	
	private ArrayList<IngredientGroup> groups;
	private Context context;
	private IngredientChild tmpChild;
	
	public NewAdapter(Context context, ArrayList<IngredientGroup> groups) {
		this.context = context;
		this.groups = groups;
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

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return groups.get(groupPosition).getChildren().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		IngredientChild child = groups.get(groupPosition).getChildren().get(childPosition);
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.child_row, null);
		}
		final ViewHolder childHolder = new ViewHolder();
		childHolder.cb = (CheckBox)convertView.findViewById(R.id.checkBox1);
		childHolder.cb.setText(child.getName());
        childHolder.cb
        .setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button,
                    boolean isChecked) {
                IngredientChild item = (IngredientChild) childHolder.cb
                        .getTag();
                item.setSelected(button.isChecked());
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
	public Object getGroup(int groupPosition) {
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
		CheckedTextView ctv = (CheckedTextView)convertView.findViewById(R.id.checkedText1);
		ctv.setText(group.getGroup());		
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
	}
	
}