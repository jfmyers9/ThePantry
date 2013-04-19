package cs169.project.thepantry.test;

import java.util.ArrayList;
import java.util.Arrays;

import cs169.project.thepantry.DatabaseModel;
import cs169.project.thepantry.IngredientChild;
import cs169.project.thepantry.IngredientGroup;
import cs169.project.thepantry.ThePantryContract;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

public class DatabaseModelTest extends AndroidTestCase {
	
	DatabaseModel testdm;

	protected void setUp() throws Exception {
		super.setUp();
		testdm = new DatabaseModel(getContext(), "testdatabase");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		testdm.close();
		testdm = null;
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

	/** Functional test for adding an ingredient.*/
	public void testAddIngredient() {
		String table = ThePantryContract.Inventory.TABLE_NAME;
		String item = "eggs";
		String type = "dairy";
		String amount = "12";
		boolean success = testdm.addIngredient(table, item, type, amount);
		assertTrue("DatabaseModel.addIngredient() returned false", success);
		assertTrue("Error: Eggs not added to database", findItem(table, item));
	}

	public void testAddStorage() {
		fail("Not yet implemented");
	}

	/** Unit test for addToDatabase, add method that interacts directly with the database */
	public void testAddToDatabase() {
		ContentValues values = new ContentValues();
		String table = ThePantryContract.Inventory.TABLE_NAME;
		String item = "bananas";
		String type = "produce";
		String amount = "5";
		values.put(ThePantryContract.ITEM, item);
		values.put(ThePantryContract.TYPE, type);
		values.put(ThePantryContract.AMOUNT, amount);
		values.put(ThePantryContract.ADDFLAG, "true");
		values.put(ThePantryContract.REMOVEFLAG, "false");
		boolean success = testdm.addToDatabase(table, item, values);
		assertTrue("DatabaseModel.addToDatabase() returned false", success);
		assertTrue("Error: bananas not added to database", testdm.findItem(table, item));
	}

	public void testCheck() {
		String table = ThePantryContract.Ingredients.TABLE_NAME;
		String item = "strawberries";
		assertFalse("Error: Spinach hasn't been cooked", isChecked(table, item, "checked"));
		boolean success = testdm.check(table, item, "checked", true);
		assertTrue("DatabaseModel.checked() returned false", success);
		assertTrue("Error: Strawberries have been checked", isChecked(table, item, "checked"));
	}

	public void testCheckedItems() {
		String table = ThePantryContract.Ingredients.TABLE_NAME;
		String select = "common";
		ArrayList<IngredientChild> result = testdm.checkedItems(table, select);
		String milk = result.get(0).getName();
		String chicken = result.get(1).getName();
		assertEquals(milk, "milk");
		assertEquals(chicken, "chicken");
	}

	/** Unit test for clearing a table */
	public void testClear() {
		String table = ThePantryContract.Inventory.TABLE_NAME;
		boolean success = testdm.clear(table);
		assertTrue("Error: Database not cleared", success);
		assertFalse("Error: shouldn't have anything in table", testdm.findItem(table, "milk"));
	}

	/** Unit test for cursorToAmount */
	public void testCursorToAmount() {
		String[] colNames = {"amount"};
		String[] values = {"40"};
		MatrixCursor cursor = new MatrixCursor(colNames);
		cursor.addRow(values);
		assertEquals(testdm.cursorToAmount(cursor), "40");
	}
	
	/** Unit test for cursorToAmount default amount */
	public void testCursorToAmountDefault() {
		assertEquals(testdm.cursorToAmount(null), ThePantryContract.DEFAULTAMOUNT);
	}

	/** Functional test for cursorToObject looking for amountval */
	public void testCursorToObjectAmount() {
		String[] colNames = {"amount"};
		String[] values = {"40"};
		MatrixCursor cursor = new MatrixCursor(colNames);
		cursor.addRow(values);
		String result = (String) testdm.cursorToObject(cursor, ThePantryContract.Ingredients.TABLE_NAME, ThePantryContract.AMOUNTVAL);
		assertEquals(result, "40");
	}
	
	/** Functional test for cursorToObject looking for childlist */
	public void testCursorToObjectChildList() {
		String[] colNames = {"item", "type", "amount", "checked"};
		String[] values = {"marshmallows", "dessert", "40", "false"};
		MatrixCursor cursor = new MatrixCursor(colNames);
		cursor.addRow(values);
		String table = ThePantryContract.Ingredients.TABLE_NAME;
		ArrayList<IngredientChild> result = 
				(ArrayList<IngredientChild>) testdm.cursorToObject(cursor, table, ThePantryContract.CHILDLIST);
		assertEquals(result.get(0).getName(), "marshmallows");
	}
	
	/** Functional test for cursorToObject looking for grouplist */
	public void testCursorToObjectGroupList() {
		String[] colNames = {"type"};
		String[] values = {"dessert"};
		String[] values2 = {"sweets"};
		String table = ThePantryContract.Ingredients.TABLE_NAME;
		MatrixCursor cursor = new MatrixCursor(colNames);
		cursor.addRow(values);
		cursor.addRow(values2);
		ArrayList<IngredientGroup> groups =
				(ArrayList<IngredientGroup>) testdm.cursorToObject(cursor, table, ThePantryContract.GROUPLIST);
		assertEquals(groups.get(0).getGroup(), "dessert");
		assertEquals(groups.get(1).getGroup(), "sweets");
		
	}
	
	/** Functional test for cursorToObject looking for group */
	public void testCursorToObjectGroup() {
		String[] colNames = {"type"};
		String[] values = {"dessert"};
		String table = ThePantryContract.Ingredients.TABLE_NAME;
		MatrixCursor cursor = new MatrixCursor(colNames);
		cursor.addRow(values);
		IngredientGroup group =
				(IngredientGroup) testdm.cursorToObject(cursor, table, ThePantryContract.GROUPLIST);
		assertEquals(group.getGroup(), "dessert");
	}
	
	/** Functional test for cursorToObject looking for stringlist */
	public void testCursorToObjectStringList() {
		String[] colNames = {"name"};
		String[] value1 = {"alice"};
		String[] value2 = {"bob"};
		String[] value3 = {"charles"};
		MatrixCursor cursor = new MatrixCursor(colNames);
		cursor.addRow(value1);
		cursor.addRow(value2);
		cursor.addRow(value3);
		ArrayList<String> names =
				(ArrayList<String>)testdm.cursorToObject(cursor, ThePantryContract.Ingredients.TABLE_NAME, ThePantryContract.STRINGLIST);
		assertEquals(names.get(0), "alice");
		assertEquals(names.get(1), "bob");
		assertEquals(names.get(2), "charles");
	}
		
	/** Functional test for cursorToObject looking for storage */
	public void testCursorToObjectStorage() {
		String[] colNames = {"item", "type", "amount", "checked"};
		String[] values = {"marshmallows", "dessert", "40", "false"};
		MatrixCursor cursor = new MatrixCursor(colNames);
		cursor.addRow(values);
	}

	/** Unit test for cursorToStringList */
	public void testCursorToStringList() {
		ArrayList<String> result = testdm.cursorToStringList(null);
		assertEquals(result.size(), 0);
	}

	/** Functional test */
	public void testFindAllItems() {
		String table = ThePantryContract.SearchMatch.TABLE_NAME;
		ArrayList<IngredientChild> items = testdm.findAllItems(table);
		String[] match = {"candied bacon", "spinach salad", "german chocolate cake"};
		for (IngredientChild item : items) {
			boolean contains = Arrays.asList(match).contains(item.getName());
			assertTrue("Error: Incorrect type " + item.getName(), contains);
		}
	}

	/** Functional test */
	public void testFindAllTypes() {
		String table = "ingredients";
		ArrayList<IngredientGroup> types = testdm.findAllTypes(table);
		String[] match = {"Produce", "Dairy", "Poultry", "Grain"};
		for (IngredientGroup type : types) {
			boolean contains = Arrays.asList(match).contains(type.getGroup());
		assertTrue("Error: Incorrect type " + type.getGroup(), contains);
		}
	}

	/** Functional test */
	public void testFindAmount() {
		String table = ThePantryContract.Ingredients.TABLE_NAME;
		String item = "chicken";
		String amount = testdm.findAmount(table, item);
		assertEquals("Error: Chicken amount is not " + amount, amount, "4");
	}

	/** Unit test for finding if an item exists in the table */
	public void testFindItem() {
		String table = ThePantryContract.Ingredients.TABLE_NAME;
		String item = "milk";
		boolean success = testdm.findItem(table, item);
		assertTrue("Error: We do have milk", success);
	}
	
	public void testFindItemFail() {
		String table = ThePantryContract.Ingredients.TABLE_NAME;
		String item = "cookies";
		boolean success = testdm.findItem(table, item);
		assertFalse("Error: We don't have any cookies", success);
	}

	/** Functional test */
	public void testFindItemNames() {
		String table = "ingredients";
		ArrayList<String> names = testdm.findItemNames(table);
		String[] match = {"candied bacon", "spinach salad", "german chocolate cake"};
		for (String item : names) {
			boolean contains = Arrays.asList(match).contains(item);
			assertTrue("Error: Incorrect item " + item, contains);
		}
	}

	/** Functional test */
	public void testFindType() {
		String table = ThePantryContract.Ingredients.TABLE_NAME;
		String item = "milk";
		IngredientGroup type = testdm.findType(table, item);
		assertEquals("Error: Milk is not " + type.getGroup(), type.getGroup(), "Dairy");
	}

	/** Functional test */
	public void testFindTypeItems() {
		String table = ThePantryContract.Ingredients.TABLE_NAME;
		String type = "dairy";
		ArrayList<IngredientChild> items = testdm.findTypeItems(table, type);
		assertEquals("Error: Milk is not ", items.get(0).getName(), "Milk");
	}

	public void testGetAllRecipes() {
		//fail("Not yet implemented");
	}

	public void testGetCookOrFav() {
		//fail("Not yet implemented");
	}

	public void testGetStorage() {
		//fail("Not yet implemented");
	}

	/** Unit test for checking whether an item is checked or not */
	public void testIsItemChecked() {
		String item = "chicken";
		String col = "common";
		String table = ThePantryContract.Ingredients.TABLE_NAME;
		boolean checked = testdm.isItemChecked(table, item, col);
		assertTrue("Chicken should be common", checked);
	}

	public void testMakeIngredientChildren() {
		String[] colNames = {"item", "type", "amount", "checked"};
		String[] values = {"marshmallows", "dessert", "40", "false"};
		Integer[] indices = {0, 1};
		String table = "helloworld";
		MatrixCursor cursor = new MatrixCursor(colNames);
		cursor.addRow(values);
		ArrayList<IngredientChild> result = testdm.makeIngredientChildren(cursor, indices, table);
		assertEquals("marshamllows", result.get(0).getName());
	}
	
	public void testMakeIngredientChildrenNull() {
		ArrayList<IngredientChild> result = testdm.makeIngredientChildren(null, null, null);
		assertEquals(result.size(), 0);
	}

	public void testMakeIngredientGroups() {
		String[] colNames = {"type"};
		String[] values = {"dessert"};
		Integer[] indices = {0, 1};
		MatrixCursor cursor = new MatrixCursor(colNames);
		cursor.addRow(values);
		ArrayList<IngredientGroup> result = testdm.makeIngredientGroups(cursor, indices);
		assertEquals("dessert", result.get(0).getGroup());
	}
	
	public void testMakeIngredientGroupNull() {
		ArrayList<IngredientGroup> result = testdm.makeIngredientGroups(null, null);
		assertEquals(result.size(), 0);
	}

	public void testMakeRecipe() {
		//fail("Not yet implemented");
	}

	public void testMakeSearchMatch() {
		//fail("Not yet implemented");
	}

	public void testMakeStorageValue() {
		//fail("Not yet implemented");
	}

	public void testQueryToChecked() {
		SQLiteDatabase db = testdm.getReadableDatabase();
		boolean checked = testdm.queryToChecked(db, false, ThePantryContract.Ingredients.TABLE_NAME,
												new String[]{"item"}, "common", new String[]{"chicken"});
		assertTrue("Chicken should be common", checked);
	}
	
	public void testQueryToCheckedFalse() {
		SQLiteDatabase db = testdm.getReadableDatabase();
		boolean checked = testdm.queryToChecked(db, false, ThePantryContract.Ingredients.TABLE_NAME,
												new String[]{"item"}, "checked", new String[]{"chicken"});
		assertTrue("Chicken should not be checked", checked);
	}

	public void testQueryToCursor() {
		SQLiteDatabase db = testdm.getReadableDatabase();
		Cursor checked = testdm.queryToCursor(db, false, ThePantryContract.Ingredients.TABLE_NAME,
				new String[]{"item"}, "checked", new String[]{"sandwiches"});
		assertNull(checked);
	}

	/** Unit test for removing items from the database */
	public void testRemove() {
		String table = ThePantryContract.Recipe.TABLE_NAME;
		String item = "spinach salad";
		assertTrue("Spinach salad should still be in the database", findItem(table, item));
		boolean success = testdm.remove(table, item);
		assertTrue("DatabaseModel.remove() returned false", success);
		assertFalse("Error: Spinch salad is still in database", findItem(table, item));
	}

	public void testSearch() {
		fail("Not yet implemented");
	}

	/** Unit test for setting indices in the ingredients table */
	public void testSetIndicesIngredients() {
		int[] answer = {0, 1};
		Integer[] indices = testdm.setIndices(ThePantryContract.Ingredients.TABLE_NAME);
		assertEquals(answer, indices);
	}
	
	/** Unit test for setting indices in the inventory table */
	public void testSetIndicesInventory() {
		int[] answer = {2, 3};
		Integer[] indices = testdm.setIndices(ThePantryContract.Inventory.TABLE_NAME);
		assertEquals(answer, indices);
	}
	
	/** Unit test for setting indices in the shopping list table */
	public void testSetIndicesShoppingList() {
		int[] answer = {1, 2};
		Integer[] indices = testdm.setIndices(ThePantryContract.ShoppingList.TABLE_NAME);
		assertEquals(answer, indices);
	}

}
