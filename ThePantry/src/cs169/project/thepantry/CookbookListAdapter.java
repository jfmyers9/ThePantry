package cs169.project.thepantry;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CookbookListAdapter extends ArrayAdapter<Storage> {
	
	private Context context;
	private List<Storage> values;
	
	public CookbookListAdapter (Context context, List<Storage> values) {
	    super(context, R.layout.cook_book_list, values);
	    this.context = context;
	    this.values = values;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View listItem = inflater.inflate(R.layout.cook_book_list, parent, false);
	    
	    if (values.size() > 0) {
    		TextView tv = (TextView) listItem.findViewById(R.id.recipe_name);
    		Recipe rec = (Recipe) values.get(position);
    		String title = "" + position + ". " + rec.name;
    		System.out.println(title);
    		listItem.setTag(rec);
    		tv.setText(title);
	    }
		
		return listItem;		
	}

}
