package cs169.project.thepantry;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
	LinearLayout ings;
	static ArrayList<CheckBox> ingChecks;
	
	boolean faved;
	boolean cooked;
	
	static DatabaseModel dm;
	
	private static final String DATABASE_NAME = "thepantry";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
		//Get bundle with recipe information.
		//Intent i = this.getIntent();
		info = (Recipe)getIntent().getExtras().getSerializable("result");
		
		//Display recipe picture if there is one.
		picture = (SmartImageView)findViewById(R.id.recipePic);
		if (info.images != null && info.images.hostedLargeUrl != null && isOnline()) {
			picture.setImageUrl(info.images.hostedLargeUrl);
			picture.setScaleType(ImageView.ScaleType.CENTER_CROP);
		}
		
		//Display recipe name.
		name = (TextView)findViewById(R.id.recipeName);
		name.setText(info.name);
		
		//Render the ingredients list to view.
		ingChecks = new ArrayList<CheckBox>();
		ings = (LinearLayout)findViewById(R.id.ingList);
		displayIngreds(info.ingredientLines);
		
		//Render directions to view.
		//fetch and parse directions aynchronously
		new ParseDirectionsTask().execute(info.source.sourceRecipeUrl);
		
		//display the source and link to the web page source, open in a webview inside the app if clicked
		Button source = (Button)findViewById(R.id.source);
		source.setText("Source: " + info.source.sourceDisplayName);
		source.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                displayWebpage(info.source.sourceRecipeUrl);
            }
        });
		
		dm = new DatabaseModel(this, DATABASE_NAME);
		
		//set favorite button to grayscale or colored image based on state in db
		//check if recipe in database or if not favorited
		star = (ImageButton)findViewById(R.id.favorite);
		faved = dm.isItemChecked(ThePantryContract.Recipe.TABLE_NAME, info.name, ThePantryContract.Recipe.FAVORITE);
		setStarButton(faved);
		
		//set cooked button to grayscale or colored image based on state in db
		//check if recipe is in db or not cooked
		check = (ImageButton)findViewById(R.id.cooked);
		cooked = dm.isItemChecked(ThePantryContract.Recipe.TABLE_NAME, info.name, ThePantryContract.Recipe.COOKED);
		dm.close();
		setCheckButton(cooked);
		
	}
	
	// show all the ingredients in the ings layout
	public void displayIngreds(List<String> ingreds) {
		for (String ingred : ingreds) {
			CheckBox tv = new CheckBox(this);
			tv.setText(ingred);
			ings.addView(tv);
			ingChecks.add(tv);
		}
	}
	
	// return a string of all checked items
	public static String getCheckedIngredientsString() {
		String message = "";
		for (CheckBox cb : ingChecks) {
			String ingred = (String) cb.getText();
			if (cb.isChecked()) {
				// TODO: parse amount and item, check for ingredient and previous amount
				String[] parsed = IngredientParser.parse(ingred);
				String amt = parsed[0] + " " + parsed[1];
				String ingred_name = parsed[3];
				message += "Ingredient: " + ingred_name + ", Amount: " + amt + "\n";
			}
		}
		return message;
	}
	
	// open the ingredient adding dialog
	public void openIngDialog(View v) {
		AddIngredientsDialogFragment dialog = new AddIngredientsDialogFragment();
		dialog.context = this;
		dialog.show(getFragmentManager(), "dialog");
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
			dm.check(ThePantryContract.Recipe.TABLE_NAME, info.name, ThePantryContract.Recipe.FAVORITE, false);
			dm.close();
		} else {
			faved = true;
			dm.check(ThePantryContract.Recipe.TABLE_NAME, info.name, ThePantryContract.Recipe.FAVORITE, true);
			dm.close();
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
			dm.check(ThePantryContract.Recipe.TABLE_NAME, info.name, ThePantryContract.Recipe.COOKED, false);
			dm.close();
		} else {
			cooked = true;
			dm.check(ThePantryContract.Recipe.TABLE_NAME, info.name, ThePantryContract.Recipe.COOKED, true);
			dm.close();
		}
		setCheckButton(cooked);
	}
	
	/**
	 * checks inventory for ingredients and adds missing ingredients to
	 * shopping list
	 */
	public static void addToShopping(Context context) {
		// for each ingredient in list
		for (CheckBox cb : ingChecks) {
			String ingred = (String) cb.getText();
			if (cb.isChecked()) {
				// TODO: parse amount and item, check for ingredient and previous amount
				String[] parsed = IngredientParser.parse(ingred);
				String amt = parsed[0] + " " + parsed[1];
				String ingred_name = parsed[3];
				dm = new DatabaseModel(context, DATABASE_NAME);	
				dm.add(ShoppingList.TABLE_NAME, ingred_name, "Other", amt);
				dm.close();
			}
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
	
	// display webpage activity with the url
	public void displayWebpage(String url) {
		Intent intent = new Intent(getApplicationContext(), DisplayWebpageActivity.class);
		intent.putExtra("url", url);
		startActivity(intent);
	}
	
	/* Class for asynchronously retrieving directions
	 * calls DirectionParser on the recipe url
	 */
	public class ParseDirectionsTask extends AsyncTask<String, Void, ArrayList<String>> {
		
		FrameLayout mFrameOverlay;
		
		@Override
	    protected void onPreExecute() {
			mFrameOverlay = (FrameLayout)findViewById(R.id.overlay);
			mFrameOverlay.setVisibility(View.VISIBLE);
	    };
		
		@Override
		protected ArrayList<String> doInBackground(String... url) {
			return DirectionParser.getDirections(url[0]);
		}
		
		@Override
		protected void onPostExecute(ArrayList<String> directionsList) {
			
			mFrameOverlay.setVisibility(View.GONE);
			
			LinearLayout directionsll = (LinearLayout)findViewById(R.id.dirList);
			
			for (int i = 0; i < directionsList.size(); i++) {
				LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View thisDirection = inflater.inflate(R.layout.direction, null);
				
				TextView number = (TextView)thisDirection.findViewById(R.id.number);
				number.setText(i+1+"");				
				
				TextView directions = (TextView)thisDirection.findViewById(R.id.direction);
				directions.setText(directionsList.get(i));
				
				directionsll.addView(thisDirection);
			}
		}
		
	}
	
	/* Class for displaying popup dialog for adding ingredients
	 * 
	 */
	public static class AddIngredientsDialogFragment extends DialogFragment {
		
		Context context;
		
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setTitle(R.string.dialog_add_ingredients_to_shopping_list)
	        	   .setMessage(getCheckedIngredientsString()) // TODO: change to editable view
	               .setPositiveButton(R.string.add_item, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       addToShopping(context);
	                   }
	               })
	               .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       // User cancelled the dialog
	                   }
	               });
	        // Create the AlertDialog object and return it
	        return builder.create();
	    }
	}
}
