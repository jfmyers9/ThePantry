package cs169.project.thepantry;

import java.util.ArrayList;

import org.apache.commons.lang3.text.WordUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter {
	private Context mContext;
	protected ArrayList<IngredientChild> ingredients;
	
    public ImageAdapter(Context c, ArrayList<IngredientChild> ingredients) {
        mContext = c;
        this.ingredients = ingredients;
    }
    
    public ImageAdapter(Context c) {
        mContext = c;
    }
    
	@Override
	public int getCount() {
		return ingredients.size();
	}

	@Override
	public Object getItem(int position) {
        return null;
    }

	@Override
    public long getItemId(int position) {
        return 0;
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        final ImageView check;
       
        imageView = new ImageView(mContext);
        
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        View row=inflater.inflate(R.layout.row, parent, false);
        
        TextView label=(TextView)row.findViewById(R.id.image_name);
        label.setText(WordUtils.capitalizeFully(ingredients.get(position).getName()));
        
        imageView=(ImageView)row.findViewById(R.id.image);
        String image = ingredients.get(position).getImage();
        if (image.equals("default")) {
        	imageView.setImageResource(R.drawable.default_image);
        } else {
        	System.out.println(image);
        	int img = mContext.getResources().getIdentifier(image,"drawable", mContext.getPackageName());
        	imageView.setImageResource(img);
        }
        check = (ImageView)row.findViewById(R.id.check);
        if(ingredients.get(position).isSelected()) {
        	check.setImageResource(R.drawable.check_on);
        }
        return row;
    }

}
