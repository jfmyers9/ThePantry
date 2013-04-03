package cs169.project.thepantry;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

public class BaseListViewAdapter extends BaseAdapter {

	private final Context context;
	private ArrayList<IngredientChild> items;
	private DatabaseModel dm;
	private static final String DATABASE_NAME = "thepantry";
	
	 public BaseListViewAdapter(Context context, ArrayList<IngredientChild> items) {
		    this.context = context;
		    this.items = items;
	 }

	 public void addItem(IngredientChild item) {
         items.add(item);
         notifyDataSetChanged();
     }
	 
	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public IngredientChild getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		System.out.println("getView " + position + " " + convertView);
        ViewHolder holder = null;
        if (convertView == null) {
        	LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.child_row, null);
        }
        IngredientChild item = getItem(position);
		final ViewHolder childHolder = new ViewHolder((CheckBox)convertView.findViewById(R.id.checkBox1), item.isSelected());
		childHolder.cb.setText(item.getName());


		
		convertView.setOnClickListener(new OnClickListener (){
			@Override
			public void onClick(View v) {
				IngredientChild item = (IngredientChild) childHolder.cb
						.getTag();
					childHolder.cb.toggle();
					item.setSelected(childHolder.cb.isChecked());
			}
		});
		
        childHolder.cb.setTag(item);
        
        return convertView;
    }
		

	
	
	public static class ViewHolder {
	    protected CheckBox cb;
	    ViewHolder(CheckBox checkBox, boolean selected) {
	    	cb = checkBox;
	    	cb.setChecked(selected);
	    }
	}
	
	
}
