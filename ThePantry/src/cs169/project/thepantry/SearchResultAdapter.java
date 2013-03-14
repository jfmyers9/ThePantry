package cs169.project.thepantry;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SearchResultAdapter extends ArrayAdapter<SearchMatch> {
	
	  private final Context context;
	  private final List<SearchMatch> values;

	  public SearchResultAdapter(Context context, List<SearchMatch> values) {
		  
	  //call the super class constructor and provide the ID of the resource to use instead of the default list view item
	    super(context, R.layout.list_result, values);
	    this.context = context;
	    this.values = values;
	  }
	  
	  //this method is called once for each item in the list
	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
		String youHave = "";
		String youNeed = "";
		String time = "";
		
		for (int i=0; i < values.get(position).ingredients.size(); i++) {
			youNeed += values.get(position).ingredients.get(i) + ", ";
		}

	    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View listItem = inflater.inflate(R.layout.list_result, parent, false);
	    listItem.setTag(values.get(position).id);
	    
	    TextView titleView = (TextView) listItem.findViewById(R.id.title);
	    titleView.setText(values.get(position).name);
	    
	    TextView timeView = (TextView) listItem.findViewById(R.id.time);
	    timeView.setText(time); //(values.get(position).time);
	    
	    TextView youHaveView = (TextView) listItem.findViewById(R.id.you_have);
	    youHaveView.setText("You have: " + youHave);
	    
	    TextView youNeedView = (TextView) listItem.findViewById(R.id.you_need);
	    youNeedView.setText("You need: " + youNeed);

	    return listItem;
	  
	  }

}
