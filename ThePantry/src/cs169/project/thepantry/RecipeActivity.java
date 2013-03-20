package cs169.project.thepantry;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.image.SmartImageView;

import cs169.project.thepantry.ThePantryContract.ShoppingList;

public class RecipeActivity extends BasicMenuActivity {
	
	Recipe info;
	
	SmartImageView picture;
	TextView name;
	ImageButton star;
	ImageButton check;
	LinearLayout ll;
	
	boolean faved;
	boolean cooked;
	
	DatabaseModel dm;
	
	private static final String DATABASE_NAME = "thepantry";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe);
		
		//Get bundle with recipe information.
		//Intent i = this.getIntent();
		info = (Recipe)getIntent().getExtras().getSerializable("result");
		
		//Display recipe picture if there is one.
		picture = (SmartImageView)findViewById(R.id.recipePic);
		if (info.images != null && info.images.hostedLargeUrl != null && isOnline()) { //might need online check
			picture.setImageUrl(info.images.hostedLargeUrl);
			picture.setScaleType(ImageView.ScaleType.CENTER_CROP);
		}
		
		//Display recipe name.
		name = (TextView)findViewById(R.id.recipeName);
		name.setText(info.name);
		
		//Render the ingredients list to view.
		ll = (LinearLayout)findViewById(R.id.ingList);
		displayIngreds(info.ingredientLines);
		
		//Render directions to view.
		ll = (LinearLayout)findViewById(R.id.dirList);
		TextView title = new TextView(this);
		title.setText("Directions:");
		title.setTextSize(TypedValue.COMPLEX_UNIT_PT, 10);
		ll.addView(title);
		//fetch and parse directions aynchronously
		new ParseDirectionsTask().execute(info.source.sourceRecipeUrl);
		
		TextView source = new TextView(this);
		source.setText(Html.fromHtml("Source: <a href='"+info.source.sourceRecipeUrl + "'>"+info.source.sourceDisplayName+"</a>"));
		source.setMovementMethod(LinkMovementMethod.getInstance());
		ll.addView(source);
		
		dm = new DatabaseModel(this, DATABASE_NAME);
		
		//set favorite button to grayscale or colored image based on state in db
		//check if recipe in database or if not favorited
		star = (ImageButton)findViewById(R.id.favorite);
		faved = dm.isItemChecked(ThePantryContract.Recipe.TABLE_NAME, info.name, ThePantryContract.FAVORITE);
		setStarButton(faved);
		
		//set cooked button to grayscale or colored image based on state in db
		//check if recipe is in db or not cooked
		check = (ImageButton)findViewById(R.id.cooked);
		cooked = dm.isItemChecked(ThePantryContract.Recipe.TABLE_NAME, info.name, ThePantryContract.COOKED);
		setCheckButton(cooked);
		
	}
	
	public void displayIngreds(List<String> ingreds) {
		for (String ingred : ingreds) {
			TextView tv = new TextView(this);
			tv.setText(ingred);
			ll.addView(tv);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.recipe, menu);
		return true;
	}
	
	/**
	 * Set favorite button to correct image either grayscale for not favorited
	 * or color for favorited
	 */
	public void setStarButton(boolean faveStatus) {
		if (faveStatus) {
			star.setBackgroundResource(R.drawable.star_on);
		} else {
			star.setBackgroundResource(R.drawable.star_off);
		}
	}
	
	/**
	 * Set cooked button to correct image either grayscale for not cooked
	 * or color for cooked
	 */
	public void setCheckButton(boolean cookStatus) {
		if (cookStatus) {
			check.setBackgroundResource(R.drawable.check_on);
		} else {
			check.setBackgroundResource(R.drawable.check_off);
		}
	}
	
	/** 
	 * saves this recipe to the favorites list.
	*/
	public void toggleFavorites(View v) {
		// update recipe table of database so favorited column is yes/no
		dm = new DatabaseModel(this, DATABASE_NAME);
		if (faved) {
			faved = false;
			dm.checked(ThePantryContract.Recipe.TABLE_NAME, info.name, ThePantryContract.FAVORITE, false);
		} else {
			faved = true;
			dm.checked(ThePantryContract.Recipe.TABLE_NAME, info.name, ThePantryContract.FAVORITE, true);
		}
		setStarButton(faved);
	}
	
	/**
	 * marks this recipe as having been cooked before and updates inventory
	 * according to ingredients list.
	 */
	public void toggleCooked(View v) {
		// update recipe table of database so cooked column is true
		dm = new DatabaseModel(this, DATABASE_NAME);
		if (cooked) {
			cooked = false;
			dm.checked(ThePantryContract.Recipe.TABLE_NAME, info.name, ThePantryContract.COOKED, false);
		} else {
			cooked = true;
			dm.checked(ThePantryContract.Recipe.TABLE_NAME, info.name, ThePantryContract.COOKED, true);
		}
		setCheckButton(cooked);
	}
	
	/**
	 * checks inventory for ingredients and adds missing ingredients to
	 * shopping list
	 */
	public void addToShopping(View v) {
		// for each ingredient in list
		for (String ingred : info.ingredientLines) {
			// TODO: parse amount and item, check for ingredient and previous amount
			String amt = "1";
			String ingred_name = ingred;
			dm = new DatabaseModel(this, DATABASE_NAME);	
			dm.add(ShoppingList.TABLE_NAME, ingred_name, "Other", amt);
			Toast toast = Toast.makeText(getApplicationContext(), "Items added!", Toast.LENGTH_SHORT);
			toast.show();
		}
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}	
		return super.onOptionsItemSelected(item);
	}
	
	/* Class for asynchronously retrieving directions
	 * 
	 */
	public class ParseDirectionsTask extends AsyncTask<String, Void, ArrayList<String>> {
		
		@Override
		protected ArrayList<String> doInBackground(String... url) {
			return DirectionParser.getDirections(url[0]);
		}
		
		@Override
		protected void onPostExecute(ArrayList<String> directionsList) {
			if (directionsList.size() > 0) {
				String directionsText = "";
				for (String dir : directionsList) {
					directionsText += dir + "\n";
				}
				TextView directions = new TextView(RecipeActivity.this);
				directions.setText(directionsText);
				ll.addView(directions);
			}
		}
		
	}

}
