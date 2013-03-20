package cs169.project.thepantry.test;

import cs169.project.thepantry.DirectionParser;
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
		dp.getDirections("http://breakfast.food.com/recipe/asparagus-omelette-wraps-232083");
	}

}
