package cs169.project.thepantry;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;

public class RecipeActivity extends Activity {
	
	Recipe info;
	
	SmartImageView picture;
	TextView name;
	ListView ingredients;
	TextView directions;
	ImageButton star;
	ImageButton check;
	
	ArrayList<String> recipe_ingreds;
	IngredientAdapter ingredAdapter;
	
	boolean faved;
	boolean cooked;
	
	DatabaseModel dm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe);
		
		//Get bundle with recipe information.
		//Intent i = this.getIntent();
		info = (Recipe)getIntent().getExtras().getSerializable("result");
		
		//Display recipe picture if there is one.
		picture = (SmartImageView)findViewById(R.id.recipePic);
		if (info.images != null && info.images.hostedLargeUrl != null) { //might need online check
			picture.setImageUrl(info.images.hostedLargeUrl);
			picture.setScaleType(ImageView.ScaleType.CENTER_CROP);
		}
		
		//Display recipe name.
		name = (TextView)findViewById(R.id.recipeName);
		name.setText(info.name);
		
		//Render the ingredients list to view.
		ingredients = (ListView)findViewById(R.id.ingredList);
		recipe_ingreds = info.ingredientLines;
		
		ingredAdapter = new IngredientAdapter(this, recipe_ingreds);
		ingredients.setAdapter(ingredAdapter);
		
		//Render directions to view.
		directions = (TextView)findViewById(R.id.directions);
		directions.setText(Html.fromHtml("<a href='"+info.source.sourceRecipeUrl + "'>"+info.source.sourceDisplayName+"</a>"));
		
		dm = new DatabaseModel(this);
		
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
	
	public class IngredientAdapter extends ArrayAdapter<String> {
		
		private final Context context;
		private final ArrayList<String> values;
		
		public IngredientAdapter(Context context, ArrayList<String> values) {
			//call the super class constructor and provide resource layout id
			//instead of the default list view item
			super(context, R.layout.list_ingredients, values);
			this.context = context;
			this.values = values;
		}
		
		//for every ingredient in the array set the list item
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View listIngred = inflater.inflate(R.layout.list_ingredients, parent, false);
			
			TextView ingred = (TextView)listIngred.findViewById(R.id.ingred);
			ingred.setText(values.get(position));
			
			return listIngred;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recipe, menu);
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
		dm = new DatabaseModel(this);
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
		dm = new DatabaseModel(this);
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
			String amt;
			String ingred_name;
			// parse string for quantity and ingredient name
			// check ingredients table for ingredient
			// check shopping table for ingredient
			// add ingredient to shopping table if not previously in either table
		}
		
	}

}
