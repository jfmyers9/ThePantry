package cs169.project.thepantry;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.provider.Contacts.Groups;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.TextView;

public class NewAdapter extends BaseExpandableListAdapter {

	 public ArrayList<String> groupItem, tempChild;
	 public ArrayList<Object> childItem = new ArrayList<Object>();
	 public LayoutInflater minflater;
	 public Activity activity;
	 public String table;
	 public DatabaseModel dm;
	 public Context cont;
	 
	 public NewAdapter(ArrayList<String> grList, ArrayList<Object> childItem) {
		 groupItem = grList;
		 this.childItem = childItem;
	 }
	 
	 public void setInflater(LayoutInflater mInflater, Activity act) {
		 this.minflater = mInflater;
		 activity = act;
	 }
	 
	@Override
	public Object getChild(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getChildId(int arg0, int arg1) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		
		tempChild = (ArrayList<String>) childItem.get(groupPosition);
		TextView text = null;
		if (convertView == null) {
			convertView = minflater.inflate(R.layout.child_row, null); 
		}
		text = (TextView) convertView.findViewById(R.id.textView1); 
		text.setText(tempChild.get(childPosition));
		convertView.setOnClickListener(new OnClickListener() {
			   @Override
			   public void onClick(View v) {
				  //This responds to anywhere you click except for text, may be good to use
			   }
			  });
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return ((ArrayList<String>) childItem.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getGroupCount() {
		return groupItem.size();
	}
	
	 @Override
	 public void onGroupCollapsed(int groupPosition) {
	  super.onGroupCollapsed(groupPosition);
	 }

	 @Override
	 public void onGroupExpanded(int groupPosition) {
	  super.onGroupExpanded(groupPosition);
	 }


	@Override
	public long getGroupId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = minflater.inflate(R.layout.group_row, null);
		}
		((CheckedTextView) convertView).setText(groupItem.get(groupPosition)); //child_row amy will make in layout
		((CheckedTextView) convertView).setChecked(isExpanded);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void add(String g, String c) {
		groupItem.add(g);
		childItem.add(c);
		notifyDataSetChanged();
	}

}
