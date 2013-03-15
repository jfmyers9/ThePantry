package cs169.project.thepantry.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.LinearLayout;
import android.widget.TextView;
import cs169.project.thepantry.RecipeActivity;

public class RecipeActivityTest extends ActivityInstrumentationTestCase2<RecipeActivity> {
	
	private RecipeActivity testActivity;
	Recipe dummyRecipe;
	
	class Recipe implements Serializable {
		public String id;
		public String name;
		public List<String> ingreds;
		public int numIngreds;
		public String imageUrl;
		public String sourceRecipeUrl;
		public String sourceDisplayName;
		
		public Recipe(String id, List<String> ings, String image, String recipeUrl, String disp) {
			this.id = id;
			this.ingreds = ings;
			this.numIngreds = ings.size();
			this.imageUrl = image;
			this.sourceRecipeUrl = recipeUrl;
			this.sourceDisplayName = disp;
		}
	}
	
	public RecipeActivityTest(Class<RecipeActivity> activityClass) {
		super(activityClass);
	}
	
	public RecipeActivityTest() {
		super("cs169.project.thepantry", RecipeActivity.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		setActivityInitialTouchMode(false);
		
		testActivity = getActivity();
		List<String> ingreds = new ArrayList<String>();
		ingreds.add("1/2 lbs bacon");
		ingreds.add("1 tbsp oil");
		ingreds.add("3 tsp salt");
		dummyRecipe = new Recipe("Bacon Bacon Bacon", ingreds, "http://gadgets.boingboing.net/filesroot/bacon.jpg", "http://www.yummly.com/", "I love Yummly");
	}
	
	public void testPreConditions() {
		TextView name = (TextView)testActivity.findViewById(cs169.project.thepantry.R.id.recipeName);
		LinearLayout listIngred = (LinearLayout) testActivity.findViewById(cs169.project.thepantry.R.id.list);
		assertEquals(testActivity.getActTitle(), testActivity.getString(cs169.project.thepantry.R.string.app_name));
		assertTrue(name.getText().toString() == null);
		assertEquals(listIngred.getChildCount(), 1);
	}

	protected void tearDown() throws Exception {
		testActivity = null;
		dummyRecipe = null;
	}

	

}
