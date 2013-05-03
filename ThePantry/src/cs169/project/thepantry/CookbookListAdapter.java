package cs169.project.thepantry;

import java.util.ArrayList;
import java.util.List;

import com.loopj.android.image.SmartImageView;

import cs169.project.thepantry.ThePantryContract.Inventory;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CookbookListAdapter extends ArrayAdapter<Storage> {
	
	private Context context;
	List<Storage> values;
	DatabaseModel dm = new DatabaseModel(this.getContext(), ThePantryContract.DATABASE_NAME);
	ArrayList<IngredientChild> invItems;
	
	public CookbookListAdapter (Context context, List<Storage> values) {
	    super(context, R.layout.cook_book_list, values);
	    this.context = context;
	    this.values = values;
	    invItems = dm.findAllItems(Inventory.TABLE_NAME);
	    dm.close();
	}
	
	@Override
	public int getCount() {
		return values.size();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String youHave = "";
		int havenum = 0;
		String source = "Unknown";
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View listItem = inflater.inflate(R.layout.cook_book_list, parent, false);
	    
	    if (values.size() > 0) {
	    	Recipe rec = (Recipe) values.get(position);
			System.out.println(rec);
	    	Boolean found;
	    	for (int i=0; i < rec.ingredientLines.size(); i++) {
	    		found = false;
	    		if (invItems != null) {
	    			for (IngredientChild child : invItems) {
	    				if (child.getName().equals(rec.ingredientLines.get(i).toLowerCase())) {
	    					found = true;
	    					break;
	    				}
	    			}
	    		}
	    		if (found) {
	    			havenum++;
	    		}
	    	}
	    	
	    	listItem.setTag(rec.id);
	    
		    // set the title of the result item
		    TextView titleView = (TextView) listItem.findViewById(R.id.title);
		    titleView.setText(rec.name);
		    
		    //set you have and you need
		    TextView youHaveView = (TextView) listItem.findViewById(R.id.you_have);
		    youHaveView.setText(Html.fromHtml("You have <b>" + havenum + "</b> of <b>" + rec.ingredientLines.size() + "</b> ingredients."));
		    
		    // display the source
		    TextView timeView = (TextView) listItem.findViewById(R.id.source);
		    timeView.setText("Source: The Pantry");
		    
		    //load the image
		    SmartImageView imageView = (SmartImageView) listItem.findViewById(R.id.image);
		    //if (values.get(position).smallImageUrl != null) { //might need an online check
		    imageView.setImageUrl(rec.images.hostedLargeUrl, R.drawable.default_image);
    		listItem.setTag(rec);
	    }
		
		return listItem;		
	}

}
