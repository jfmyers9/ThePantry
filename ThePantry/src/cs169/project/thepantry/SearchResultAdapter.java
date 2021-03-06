package cs169.project.thepantry;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;

import cs169.project.thepantry.ThePantryContract.Inventory;

public class SearchResultAdapter extends ArrayAdapter<SearchMatch> {
	
	  private final Context context;
	  public List<SearchMatch> values;
	  public int type;
	  DatabaseModel dm = new DatabaseModel(this.getContext(), ThePantryContract.DATABASE_NAME);
	  ArrayList<IngredientChild> invItems;
	  
	  public SearchResultAdapter(Context context, List<SearchMatch> values) {
		  
	  //call the super class constructor and provide the ID of the resource to use instead of the default list view item
	    super(context, R.layout.list_result, values);
	    this.context = context;
	    this.values = values;
	    invItems = dm.findAllItems(Inventory.TABLE_NAME);
	    dm.close();
	  }
	  
	  //this method is called once for each item in the list
	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
		  
		String youHave = "";
		int havenum = 0;
		String source = "Unknown";
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View listItem = inflater.inflate(R.layout.list_result, parent, false);
	
	    if (values.size() > 0) {
			Boolean found;
			System.out.println("type: " + type);
		  	System.out.println(values.size());
	    	for (int i=0; i < values.get(position).ingredients.size(); i++) {
	    		found = false;
	    		if (invItems != null) {
	    			for (IngredientChild child : invItems) {
	    				if (child.getName().equals(values.get(position).ingredients.get(i).toLowerCase())) {
	    					found = true;
	    					break;
	    				}
	    			}
	    		}
	    		if (found) {
	    			havenum++;
	    		}
	    	}
	    	
	    	listItem.setTag(values.get(position).id);
	    	System.out.println(values.get(position).name);
	    
		    // set the title of the result item
		    TextView titleView = (TextView) listItem.findViewById(R.id.title);
		    titleView.setText(values.get(position).name);
		    
		    //set you have and you need
		    TextView youHaveView = (TextView) listItem.findViewById(R.id.you_have);
		    youHaveView.setText(Html.fromHtml("You have <b>" + havenum + "</b> of <b>" + values.get(position).ingredients.size() + "</b> ingredients."));
		    
		    // display the source
		    TextView timeView = (TextView) listItem.findViewById(R.id.source);
		    timeView.setText("Source: " + values.get(position).sourceDisplayName);
		    
		    //load the image
		    SmartImageView imageView = (SmartImageView) listItem.findViewById(R.id.image);
		    //if (values.get(position).smallImageUrl != null) { //might need an online check
		    imageView.setImageUrl(values.get(position).smallImageUrl, R.drawable.default_image);
	    }   
	    return listItem;
	  }
	  
	  @Override
		public int getCount() {
			return values.size();
		}
}
