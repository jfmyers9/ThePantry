package cs169.project.thepantry;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class RecipeActivity extends Activity {
	
	Recipe info;
	
	WebView picture;
	TextView name;
	ListView ingredients;
	TextView directions;
	
	ArrayList<String> recipe_ingreds;
	IngredientAdapter ingredAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe);
		
		//Get bundle with recipe information.
		//Intent i = this.getIntent();
		info = (Recipe)getIntent().getExtras().getSerializable("result");
		
		//Display recipe picture if there is one.
		picture = (WebView)findViewById(R.id.recipePic);
		if (info.images != null && info.images.hostedLargeUrl != null) {
			picture.loadUrl(info.images.hostedLargeUrl);
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
	
	/* 
	 * saves this recipe to the favorites list.
	*/
	public void addToFavorites(View v) {
		// update recipe table of database so favorited column is true
	}
	
	/*
	 * marks this recipe as having been cooked before and updates inventory
	 * according to ingredients list.
	 */
	public void markCooked(View v) {
		// update recipe table of database so cooked column is true
	}
	
	/*
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
