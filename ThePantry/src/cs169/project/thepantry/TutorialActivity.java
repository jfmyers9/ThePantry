package cs169.project.thepantry;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

public class TutorialActivity extends Activity {

	private static final String OPENED = "opened";
	private SharedPreferences opened;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		opened = PreferenceManager.getDefaultSharedPreferences(this);
		if (opened.getBoolean(OPENED, false) == false) {
			setContentView(R.layout.activity_tutorial);
			Gallery g = (Gallery) findViewById(R.id.gallery);
	        g.setAdapter(new ImageAdapter(this));
	        
	        // Set a item click listener, and just Toast the clicked position
	        g.setOnItemClickListener(new OnItemClickListener() {
	            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	                Toast.makeText(getApplicationContext(), "" + getCaption(position), Toast.LENGTH_SHORT).show();
	            }
	        });
		} else {
			Context context = getApplicationContext();
			HomePageActivity nextAct = new HomePageActivity();
			Intent intent = new Intent(context, nextAct.getClass());
			startActivity(intent);
			finish(); //this shuts down the app for a second?
		}
	}
	
	public String getCaption(int position) {
		switch(position) {
		case 0:
			return "Use the HomePage to view Recommendations/Search Recipes";
		case 1:
			return "After clicking on a recipe, you will be brought to a recipe view.";
		case 2:
			return "Feel free to add ingredients from recipes to your shopping list.";
		case 3:
			return "In your shopping list keep track of items that you need to buy. After buying them add them to your Pantry.";
		case 4:
			return "Your Pantry keeps track of items currently in your inventory. Reccomendations will be tailored to your Pantry.";
		case 5:
			return "Feel free to add ingredients to your Pantry.";
		case 6:
			return "Just click on an ingredient to add it to your Pantry.";
		case 7:
			return "Even more functionality can be unlocked by registering an account!";
		case 8:
			return "For Example you can add recipes to your own cookbook which will be added to our database.";
		case 9:
			return "Visiting your cookbook shows you the recipes that you have added.";
		}
		return "";
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tutorial, menu);
		return true;
	}
	
	public void setupInventory(View view) {
		stopTutorial();
		Context context = getApplicationContext();
		InventoryAddGrid invAddAct = new InventoryAddGrid();
		Intent intent = new Intent(context, invAddAct.getClass());
		startActivity(intent);
	}
	
	public void register(View view) {
		stopTutorial();
		Context context = getApplicationContext();
		RegistrationActivity regAct = new RegistrationActivity();
		Intent intent = new Intent(context, regAct.getClass());
		startActivity(intent);
	}
	
	public void skipTutorial(View view) {
		stopTutorial();
		Context context = getApplicationContext();
		Intent intent = new Intent(context, HomePageActivity.class);
		startActivity(intent);
		finish();
	}
	
	public void stopTutorial() {
		SharedPreferences.Editor editor = opened.edit();
		editor.putBoolean(OPENED, true);
		editor.commit();
	}
	
	public boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnected()) {
	        return true;
	    }
	    return false;
	}
	
	public class ImageAdapter extends BaseAdapter {
        private static final int ITEM_WIDTH = 300;
        private static final int ITEM_HEIGHT = 550;

        private final int mGalleryItemBackground;
        private final Context mContext;

        private final Integer[] mImageIds = {
                R.drawable.homepage_demo,
                R.drawable.recipe_demo,
                R.drawable.rec_sl_add_demo,
                R.drawable.sl_demo,
                R.drawable.inv_demo,
                R.drawable.inv_add_demo,
                R.drawable.inv_add_demo2,
                R.drawable.add_recipe_demo,
                R.drawable.cookbook_demo
        };

        private final float mDensity;

        public ImageAdapter(Context c) {
            mContext = c;
            // See res/values/attrs.xml for the <declare-styleable> that defines
            // Gallery1.
            TypedArray a = obtainStyledAttributes(R.styleable.Gallery1);
            mGalleryItemBackground = a.getResourceId(
                    R.styleable.Gallery1_android_galleryItemBackground, 0);
            a.recycle();

            mDensity = c.getResources().getDisplayMetrics().density;
        }

        public int getCount() {
            return mImageIds.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        @SuppressWarnings("deprecation")
		public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                convertView = new ImageView(mContext);

                imageView = (ImageView) convertView;
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setLayoutParams(new Gallery.LayoutParams(
                        (int) (ITEM_WIDTH * mDensity + 0.5f),
                        (int) (ITEM_HEIGHT * mDensity + 0.5f)));
            
                // The preferred Gallery item background
                imageView.setBackgroundResource(mGalleryItemBackground);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageResource(mImageIds[position]);

            return imageView;
        }
    }

}
