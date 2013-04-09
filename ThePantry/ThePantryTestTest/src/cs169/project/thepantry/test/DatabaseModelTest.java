package cs169.project.thepantry.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.test.AndroidTestCase;
import cs169.project.thepantry.DatabaseModel;
import cs169.project.thepantry.Storage;
import cs169.project.thepantry.ThePantryContract;

public class DatabaseModelTest extends AndroidTestCase {

	DatabaseModel testdm;
	//private static final String DATABASE_NAME = "testdatabase";
	
	/** Set up method for DatabaseModelTest module */
	protected void setUp() throws Exception {
		super.setUp();
		testdm = new DatabaseModel(getContext(), "testdatabase");
	}

	/** Tear down method for DatabaseModelTest module */
	protected void tearDown() throws Exception {
		super.tearDown();
		testdm.close();
		testdm = null;
	}
	
	/** Helper method to parse through the items in the Cursor
	 *  Returns an ArrayList of strings containing the items from
	 *  the cursor. */
	protected ArrayList<String> parseCursor(Cursor items) {
		ArrayList<String> result = new ArrayList<String>();
		if (items != null){
			while(!items.isAfterLast()){
				String data = items.getString(0);
				result.add(data);
				items.moveToNext();
			}
		}
		items.close();
		return result;
	}
	
	/** Helper method to find an item in the specified table */
	protected boolean findItem(String table, String item) {
		boolean success = testdm.findItem(table, item);
		return success;
	}
	
	/** Helper method to check if an item is in the specified table */
	protected boolean isChecked(String table, String item, String col) {
		boolean success = testdm.isItemChecked(table, item, col);
		return success;
	}
	
	/** Tests the add method in the DatabaseModel by adding eggs to the
	 *  ingredients table. Asserts that the method returns true and eggs
	 *  can now be looked up in the database.  */
	public void testAdd() {
		String table = "ingredients";
		String item = "Eggs";
		String type = "Dairy";
		String amount = "12";
		boolean success = testdm.add(table, item, type, amount);
		assertTrue("DatabaseModel.add() returned false", success);
		assertTrue("Error: Eggs not added to database", findItem(table, item));
	}
	
	
	public void testAddFail() {
		String table = "helloworld";
		String item = "Eggs";
		String type = "Dairy";
		String amount = "12";
		boolean success = testdm.add(table, item, type, amount);
		assertFalse("DatabaseModel.add() returned false, table doesn't exist", success);
		assertFalse("Error: Eggs should not be added to database", findItem(table, item));	
	}
	
	/** Tests the remove method for the DatabaseModel. Asserts that strawberries
	 *  can be find in the database before the removal attempt, removes strawberries,
	 *  and asserts that the remove method returned true and strawberries can no
	 *  longer be found in the database.
	 */
	public void testRemove() {
		String table = "ingredients";
		String item = "Eggs";
		assertTrue("Eggs should still be in the database", findItem(table, item));
		boolean success = testdm.remove(table, item);
		assertTrue("DatabaseModel.remove() returned false", success);
		assertFalse("Error: Eggs are still in database", findItem(table, item));
	}
	
	public void testRemoveFail() {
		String table = "ingredients";
		String item = "Canteloupe";
		boolean success = testdm.remove(table, item);
		assertFalse("DatabaseModel.remove() returned true, Cantelope not in database", success);
	}
	
	public void testRemoveException() {
		String table = "helloworld";
		String item = "Canteloupe";
		boolean success = testdm.remove(table, item);
		assertFalse("Expected exception and return false, helloworld is not a table", success);
	}

	/** Tests the find all items method in the DatabaseModel.
	 *  For each item in the returned Cursor, asserts that it is in known items.
	 */
	public void testFindAllItems() {
		String table = "ingredients";
		Cursor citems = testdm.findAllItems(table);
		ArrayList<String> items = parseCursor(citems);
		String[] match = {"Strawberries", "Milk", "Chicken", "Bread", "Lettuce", "Eggs"};
		for (String item : items) {
			boolean contains = Arrays.asList(match).contains(item);
			assertTrue("Error: Incorrect type " + item, contains);
		}
	}
	
	public void testFindAllItemsFail() {
		String table = "helloworld";
		Cursor citems = testdm.findAllItems(table);
		assertNull("Table helloworld shouldn't exist", citems);
	}
	
	/** Tests the findItem method in the DatabaseModel
	 *  Tries to look for cookies, which do not exist.
	 *  Asserts the method returns false.
	 */
	public void testFindItem1() {
		String table = "ingredients";
		String item = "Cookies";
		boolean success = testdm.findItem(table, item);
		assertFalse("Error: We don't have any cookies", success);
	}
	
	/** Tests the findItem method in the DatabaseModel
	 *  Tries to look for milk, which does exist.
	 *  Asserts the method returns true.
	 */
	public void testFindItem2() {
		String table = "ingredients";
		String item = "Milk";
		boolean success = testdm.findItem(table, item);
		assertTrue("Error: We do have milk", success);
	}
	
	public void testFindItemFail() {
		String table = "helloworld";
		String item = "Milk";
		boolean success = testdm.findItem(table, item);
		assertFalse("Error: Helloworld table should not exist", success);
	}

	/** Tests the findTypeItems method in the DatabaseModel.
	 *  Searches for all ingredients of type produce and
	 *  asserts the items returned are Strawberries and Lettuce.
	 */
	public void testFindTypeItems() {
		String table = "ingredients";
		String type = "Dairy";
		Cursor citems = testdm.findTypeItems(table, type);
		ArrayList<String> items = parseCursor(citems);
		assertEquals("Error: Milk is not ", items.get(0), "Milk");
	}
	
	public void testFindTypeItemsFail() {
		String table = "ingredients";
		String type = "Cardboard";
		Cursor citems = testdm.findTypeItems(table, type);
		assertNull("Column cardboard should not exist", citems);
	}

	/** Tests the findType method in DatabaseModel.
	 *  Asserts that Strawberries are of type produce.
	 */
	public void testFindType() {
		String table = "ingredients";
		String item = "Milk";
		Cursor ctype = testdm.findType(table, item);
		ArrayList<String> types = parseCursor(ctype);
		String type = types.get(0);
		assertEquals("Error: Milk is not " + type, type, "Dairy");
	}
	
	/** Tests both the findType method in DatabaseModel
	 *  Tries to find bananas in the database, which do not exist.
	 *  Asserts the returned Cursor is null.
	 */
	public void testFindTypeFail() {
		String table = "ingredients";
		String item = "Bananas";
		Cursor ctype = testdm.findType(table, item);
		assertNull("Bananas should not be in the database", ctype);
	}

	/** Tests the findAmount method in DatabaseModel.
	 *  Asserts that Chicken has amount 4.
	 */
	public void testFindAmount() {
		String table = "ingredients";
		String item = "Chicken";
		Cursor camount = testdm.findAmount(table, item);
		ArrayList<String> amounts = parseCursor(camount);
		String amount = amounts.get(0);
		assertEquals("Error: Chicken amount is not " + amount, amount, "4");
	}
	
	public void testFindAmountFail() {
		String table = "ingredients";
		String item = "Duck";
		Cursor camount = testdm.findAmount(table, item);
		assertNull("Duck should not be in the database", camount);
	}

	/** Tests the findAllTypes method in DatabaseModel.
	 *  Asserts that each type returned by the Cursor parser is a member
	 *  of the known types.
	 */
	public void testFindAllTypes() {
		String table = "ingredients";
		Cursor ctypes = testdm.findAllTypes(table);
		ArrayList<String> types = parseCursor(ctypes);
		String[] match = {"Produce", "Dairy", "Poultry", "Grain"};
		for (String type : types) {
			boolean contains = Arrays.asList(match).contains(type);
			assertTrue("Error: Incorrect type " + type, contains);
		}
	}
	
	public void testFindAllTypesFail() {
		String table = "helloworld";
		Cursor ctypes = testdm.findAllTypes(table);
		assertNull("Helloworld table should not exist", ctypes);
	}

	public void testSearch() {
		String table = "ingredients";
		testdm.check(table, "Lettuce", "removeFlag", true);
		Cursor cItems = testdm.search(table, "c");
		ArrayList<String> items = parseCursor(cItems);
		String[] match = {"Chicken", "Carrots", "Lettuce"};
		for (String item : items) {
			boolean contains = Arrays.asList(match).contains(item);
			assertTrue("Error: Incorrect item " + item, contains);
		}
	}

	public void testChecked() {
		String table = "recipes";
		String recipe = "Spinach";
		//assertFalse("Error: Spinach hasn't been cooked", isChecked(table, recipe, "cooked"));
		boolean success = testdm.check(table, recipe, "cooked", true);
		assertTrue("DatabaseModel.checked() returned false", success);
		assertTrue("Error: Spinach has been cooked", isChecked(table, recipe, "cooked"));
	}

	public void testCheckedFail() {
		String table = "ingredients";
		String recipe = "Spinach";
		boolean success = testdm.check(table, recipe, "cooked", true);
		assertFalse("Spinach should not be in the ingredients table", success);
	}
		
	public void testCheckedItems1() {
		String table = "ingredients";
		Cursor c = testdm.checkedItems(table, "checked");
		assertNull("Error: No items were checked", c);
	}
	
	public void testCheckedItems2() {
		String table = "recipes";
		Cursor c = testdm.checkedItems(table, "favorite");
		ArrayList<String> checked = parseCursor(c);
		String item = checked.get(0);
		assertEquals("Error: Bacon is not " + item, item, "Bacon");
	}

	public void testIsItemChecked() {
		String table = "recipes";
		String recipe = "Fried Rice";
		boolean checked1 = testdm.isItemChecked(table, recipe, "favorite");
		assertFalse("Error: Fried Rice is not a favorite", checked1);
	}
	
	public void testIsItemChecked2() {
		String table = "recipes";
		String recipe = "Fried Rice";
		boolean checked2 = testdm.isItemChecked(table, recipe, "cooked");
		assertTrue("Error: Fried Rice has been cooked", checked2);
	}
	
	public void testFindItemNames() {
		String table = "ingredients";
		Cursor cnames = testdm.findItemNames(table);
		ArrayList<String> result = parseCursor(cnames);
		String[] expected = {"Strawberries", "Milk", "Chicken", "Bread", "Lettuce", "Eggs"};
		for (String item : result) {
			boolean contains = Arrays.asList(expected).contains(item);
			assertTrue("Error: Incorrect item " + item, contains);
		}
	}
	
	public void testRemoveAllBut() {
		String table = "inventory";
		ArrayList<String> keep = new ArrayList<String>();
		keep.add("Cookies");
		keep.add("Milk");
		boolean success = testdm.removeAllBut(table, keep);
		//assertTrue("Remove operation failed", success);
		assertFalse(findItem(table, "Vegetables"));
	}

	public void testClear() {
		String table = "ingredients";
		boolean success = testdm.clear(table);
		assertTrue("Error: Database not cleared", success);
		assertFalse("Error: shouldn't have anything in table", testdm.findItem(table, "milk"));
	}
	
	// Doesn't work because privacy with recipe class, really annoying!
	public void testRecipe() {
		Recipe recipe = new Recipe();
		recipe.id = "testID";
		recipe.name = "testName";
		
		Attribution att = new Attribution("testURL", "testText", "testLogo");
		recipe.attribution = att;
		
		ArrayList<String> ingredients = new ArrayList<String>();
		ingredients.add("testIng1");
		ingredients.add("testIng2");
		recipe.ingredientLines = ingredients;
		
		RecipeImages img = new RecipeImages("testLargeUrl", "testSmallUrl");
		recipe.images = img;
		
		RecipeSource src = new RecipeSource("testRecipeUrl", "testSiteUrl", "testSourceName");
		recipe.source = src;
		
		//boolean success = testdm.addRecipe((Recipe)recipe);
		//assertTrue("Remove operation failed", success);
		
		/* Test should be like this
		 * dm = new DatabaseModel(this, ThePantryContract.DATABASE_NAME);
		ArrayList<Recipe> recipes = dm.getCookOrFav(ThePantryContract.Recipe.COOKED);
		Recipe recipe = recipes.get(0);
		System.out.println("Size should be 1: " + recipes.size());
		System.out.println("Should be bacon: " + recipe.name);
		System.out.println("Should be Bacon with tons of bacon: " + recipe.name);
		System.out.println("Should be testID: " + recipe.id);
		
		System.out.println("Should be Bacon: " + recipe.ingredientLines.get(0));
		System.out.println("Should be Beets: " + recipe.ingredientLines.get(1));
		System.out.println("Should be Carrots: " + recipe.ingredientLines.get(2));
		
		System.out.println("Should be testLargeURL: " + recipe.images.hostedLargeUrl);
		System.out.println("Should be testSmallURL: " + recipe.images.hostedSmallUrl);
		
		System.out.println("Should be testURL: " + recipe.attribution.url);
		System.out.println("Should be testText: " + recipe.attribution.text);
		System.out.println("Should be testLogo: " + recipe.attribution.logo);
	
		System.out.println("Should be testRecipeURL: " + recipe.source.sourceRecipeUrl);
		System.out.println("Should be testSiteURL: " + recipe.source.sourceSiteUrl);
		System.out.println("Should be testSourceName: " + recipe.source.sourceDisplayName);
		 */
	}
}



/** All Recipes below added from Storage in order to test Recipe Database
 */
class Recipe extends Storage implements Serializable {
	String id;
	String name;
	Boolean cooked; // can probably take out, leave until caching works properly
	Boolean favorite; // can probably take out, leave until caching works properly
	Attribution attribution;
	ArrayList<String> ingredientLines; //in order
	RecipeImages images; //not always present
	RecipeSource source; //not always present

	Recipe() {
		cooked=false;
		favorite=false;
	}
	public String getId() {
		return id;
	}
}

class Attribution extends Storage implements Serializable {
	String url;
	String text;
	String logo;
	
	Attribution (String url, String text, String logo) {
		this.url = url;
		this.text = text;
		this.logo = logo;
	}
}

class RecipeImages extends Storage implements Serializable {
	String hostedLargeUrl;
	String hostedSmallUrl;
	
	RecipeImages(String lrgUrl, String smlUrl) {
		hostedLargeUrl = lrgUrl;
		hostedSmallUrl = smlUrl;
	}
}

class RecipeSource extends Storage implements Serializable {
	String sourceRecipeUrl;
	String sourceSiteUrl;
	String sourceDisplayName;
	
	RecipeSource(String recipeUrl, String siteUrl, String name) {
		sourceRecipeUrl = recipeUrl;
		sourceSiteUrl = siteUrl;
		sourceDisplayName = name;
	}
}
