package cs169.project.thepantry.test;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.LinearLayout;
import android.widget.TextView;
import cs169.project.thepantry.RecipeActivity;

public class RecipeActivityTest extends ActivityInstrumentationTestCase2<RecipeActivity> {
	
	private RecipeActivity testActivity;
	
	public RecipeActivityTest(Class<RecipeActivity> activityClass) {
		super(activityClass);
	}
	
	public RecipeActivityTest() {
		super(RecipeActivity.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		setActivityIntent(new Intent());
		setActivityInitialTouchMode(false);
		
		testActivity = new RecipeActivity();
	}

	protected void tearDown() throws Exception {
		testActivity = null;
	}

	public void testDisplayIngreds() {
		final List<String> ingreds = new ArrayList<String>();
		ingreds.add("1/2 lbs bacon");
		ingreds.add("1 tbsp oil");
		ingreds.add("3 tsp salt");
		int numIngreds = ingreds.size();
		this.sendKeys("work");
		testActivity.runOnUiThread(new Runnable() {
			public void run() {
				testActivity.displayIngreds(ingreds);
			}
		});
		assertEquals("Error: Doesn't display correct number of ingredients.", numIngreds, 3);
	}

}
