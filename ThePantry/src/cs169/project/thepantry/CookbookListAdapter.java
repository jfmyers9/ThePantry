package cs169.project.thepantry;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;

import cs169.project.thepantry.ThePantryContract.Inventory;

public class CookbookListAdapter extends ArrayAdapter<Storage> {
	
	private Context context;
	List<Storage> values;
	DatabaseModel dm = new DatabaseModel(this.getContext(), ThePantryContract.DATABASE_NAME);
	ArrayList<IngredientChild> invItems;
	ArrayList<Bitmap> images;
	
	public CookbookListAdapter (Context context, List<Storage> values) {
	    super(context, R.layout.cook_book_list, values);
	    this.context = context;
	    this.values = values;
	    images = new ArrayList<Bitmap>();
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
	    	images.add(position, null);
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
	    
		    // set the title of the result item
		    TextView titleView = (TextView) listItem.findViewById(R.id.title);
		    titleView.setText(rec.name);
		    
		    //set you have and you need
		    TextView youHaveView = (TextView) listItem.findViewById(R.id.you_have);
		    youHaveView.setText(Html.fromHtml("You have <b>" + havenum + "</b> of <b>" + rec.ingredientLines.size() + "</b> ingredients."));
		    
		    // display the source
		    TextView timeView = (TextView) listItem.findViewById(R.id.source);
		    timeView.setText("Source: The Pantry");
		    SmartImageView imageView = (SmartImageView) listItem.findViewById(R.id.image);
//		    LoadImageTask task = new LoadImageTask(position, rec);
		    //task.execute();
//		    imageView.setImageBitmap(images.get(position));	 
		    imageView.setImageUrl(rec.images.hostedSmallUrl);
		    
		    //load the image
    		listItem.setTag(rec);
	    }
		
		return listItem;		
	}
	
	public static Bitmap decodeSampledBitmapFromResource(String res, int resId,
	        int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
		try {
		    final BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inJustDecodeBounds = true;
		    BitmapFactory.decodeStream((InputStream) new URL(res).getContent());
	
		    // Calculate inSampleSize
		    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	
		    // Decode bitmap with inSampleSize set
		    options.inJustDecodeBounds = false;
		    return BitmapFactory.decodeStream((InputStream) new URL(res).getContent());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static int calculateInSampleSize(
        BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
    	final int width = options.outWidth;
    	int inSampleSize = 1;

    	if (height > reqHeight || width > reqWidth) {

        	// Calculate ratios of height and width to requested height and width
        	final int heightRatio = Math.round((float) height / (float) reqHeight);
        	final int widthRatio = Math.round((float) width / (float) reqWidth);

        	// Choose the smallest ratio as inSampleSize value, this will guarantee
        	// a final image with both dimensions larger than or equal to the
        	// requested height and width.
        	inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
    	}

    	return inSampleSize;
	}
	
//	private class LoadImageTask extends AsyncTask<Void, Void, Void>{
//		
//		private int pos;
//		private Recipe rec;
//		
//		public LoadImageTask(int pos, Recipe rec) {
//			this.pos = pos;
//			this.rec = rec;
//		}
//
//		@Override
//		protected Void doInBackground(Void... params) {
//		    Bitmap bm = decodeSampledBitmapFromResource(rec.images.hostedSmallUrl, R.id.image, 100, 100);
//		    images.add(pos, bm);
//			return null;
//		}
//		
//	}

}
