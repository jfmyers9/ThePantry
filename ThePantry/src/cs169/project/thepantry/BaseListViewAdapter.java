package cs169.project.thepantry;

import java.util.ArrayList;

import org.apache.commons.lang3.text.WordUtils;

import cs169.project.thepantry.BaseListAdapter.ViewHolder;
import cs169.project.thepantry.ThePantryContract.Ingredients;
import cs169.project.thepantry.ThePantryContract.Inventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class BaseListViewAdapter extends BaseAdapter {

	private final Context context;
	private ArrayList<IngredientChild> items;
	public BaseListAdapter eAdapter;
	private DatabaseModel dm;
	private static final String DATABASE_NAME = "thepantry";
	private String table;
	
	 public BaseListViewAdapter(Context context, ArrayList<IngredientChild> items, String table) {
		    this.context = context;
		    this.items = items;
		    this.table = table;
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
		dm = new DatabaseModel(context, DATABASE_NAME);
        ViewHolder holder = null;
        if (convertView == null) {
        	LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        	convertView = infalInflater.inflate(R.layout.child_row_inventory, null);
        }
        IngredientChild item = getItem(position);
        final ViewHolder childHolder;
        	childHolder = new ViewHolder((TextView)convertView.findViewById(R.id.textView));
        	childHolder.cb.setText(WordUtils.capitalizeFully(item.getName()));
        	convertView.setOnClickListener(new OnClickListener (){
    			@Override
    			public void onClick(View v) {
    				IngredientChild item = (IngredientChild) childHolder.cb
    						.getTag();
					item.setSelected(false);
					ArrayList<IngredientChild> tmpChildren = new ArrayList<IngredientChild>(); 
					IngredientGroup tmpGroup = new IngredientGroup(item.getGroup(), tmpChildren);
					if (dm.findItem(table, item.getName()) && !dm.isItemChecked(table, item.getName(), ThePantryContract.REMOVEFLAG)) {
						Toast toast = Toast.makeText(context, "You already have " + WordUtils.capitalizeFully(item.getName()) + " in your pantry", Toast.LENGTH_SHORT);
						toast.show();
					} else {
						dm.addIngredient(table, item.getName(), item.getGroup(), "1");
						dm.check(Ingredients.TABLE_NAME, item.getName(), ThePantryContract.CHECKED, true);
						dm.check(table, item.getName(), ThePantryContract.CHECKED, false); // Probably not the cleanest way to do this but fuck it
						eAdapter.addChild(item, tmpGroup);
						eAdapter.notifyDataSetChanged();
					}
    			}
    		});
        childHolder.cb.setTag(item);
        
        return convertView;
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
