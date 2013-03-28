package cs169.project.thepantry.test;

import cs169.project.thepantry.DirectionParser;
import cs169.project.thepantry.IngredientParser;
import junit.framework.TestCase;

public class DirectionParserTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetDirections() {
		DirectionParser dp = new DirectionParser();
		//dp.getDirections("http://breakfast.food.com/recipe/asparagus-omelette-wraps-232083");
	}

	public void testParseIngredients() {
		//String ingredientLine = "1 1/2 pounds (jeg) unsalted beets, with bacon strips";
		String ingredientLine = "2 rounded teaspoons prepared dijon style mustard";
		String[] result = IngredientParser.parse(ingredientLine);
		System.out.println("Results:");
		if (result[0] != null) {
			System.out.println(result[0]);
		}
		if (result[1] != null) {
			System.out.println(result[1]);
		}
		if (result[2] != null) {
			System.out.println(result[2]);
		}
		if (result[3] != null) {
			System.out.println(result[3]);
		}
	}
	
}