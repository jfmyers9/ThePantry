package cs169.project.thepantry.test;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;

import android.database.Cursor;
import android.test.AndroidTestCase;
import cs169.project.thepantry.DatabaseModel;

public class DatabaseModelTest extends AndroidTestCase {

	DatabaseModel testdm;
	String table;
	//private static final String DATABASE_NAME = "testdatabase";
	
	protected void setUp() throws Exception {
		testdm = new DatabaseModel(getContext(), "testdatabase");
	}

	protected void tearDown() throws Exception {
		testdm = null;
	}
	
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
	
	protected boolean findItem(String item) {
		table = "ingredients";
		boolean success = testdm.findItem(table, item);
		return success;
	}
	
	protected boolean isChecked(String table, String item, String col) {
		boolean success = testdm.isItemChecked(table, item, col);
		return success;
	}

	public void testAdd() {
		table = "ingredients";
		String item = "Eggs";
		String type = "Dairy";
		String amount = "12";
		boolean success = testdm.add(table, item, type, amount);
		assertTrue("DatabaseModel.add() returned false", success);
		assertTrue("Error: Eggs not added to database", findItem(item));	
	}

	public void testRemove() {
		table = "ingredients";
		String item = "Strawberries";
		assertTrue("Strawberries should still be in the database", findItem(item));
		boolean success = testdm.remove(table, item);
		assertTrue("DatabaseModel.remove() returned false", success);
		assertFalse("Error: Bread is still in database", findItem(item));
	}

	public void testFindAllItems() {
		table = "ingredients";
		Cursor citems = testdm.findAllItems(table);
		ArrayList<String> items = parseCursor(citems);
		String[] match = {"Strawberries", "Milk", "Chicken", "Bread", "Lettuce", "Eggs"};
		for (String item : items) {
			boolean contains = Arrays.asList(match).contains(item);
			assertTrue("Error: Incorrect type " + item, contains);
		}
	}

	public void testFindItem1() {
		table = "ingredients";
		String item = "Cookies";
		boolean success = testdm.findItem(table, item);
		assertFalse("Error: We don't have any cookies", success);
	}
	
	public void testFindItem2() {
		table = "ingredients";
		String item = "Milk";
		boolean success = testdm.findItem(table, item);
		assertTrue("Error: We do have milk", success);
	}

	public void testFindTypeItems() {
		table = "ingredients";
		String type = "Produce";
		Cursor citems = testdm.findTypeItems(table, type);
		ArrayList<String> items = parseCursor(citems);
		assertEquals("Error: Strawberries is not ", items.get(0), "Strawberries");
		assertEquals("Error: Lettuce is not ", items.get(1), "Lettuce");
	}

	public void testFindType() {
		table = "ingredients";
		String item = "Strawberries";
		Cursor ctype = testdm.findType(table, item);
		ArrayList<String> types = parseCursor(ctype);
		String type = types.get(0);
		assertEquals("Error: Strawberries are not " + type, type, "Produce");
	}

	public void testFindAmount() {
		table = "ingredients";
		String item = "Chicken";
		Cursor camount = testdm.findAmount(table, item);
		ArrayList<String> amounts = parseCursor(camount);
		String amount = amounts.get(0);
		assertEquals("Error: Chicken amount is not " + amount, amount, "4");
	}

	public void testFindAllTypes() {
		table = "ingredients";
		Cursor ctypes = testdm.findAllTypes(table);
		ArrayList<String> types = parseCursor(ctypes);
		String[] match = {"Produce", "Dairy", "Poultry", "Grain"};
		for (String type : types) {
			boolean contains = Arrays.asList(match).contains(type);
			assertTrue("Error: Incorrect type " + type, contains);
		}
	}

	public void testChecked() {
		table = "recipe";
		String recipe = "Spinach";
		assertFalse("Error: Spinach hasn't been cooked", isChecked(table, recipe, "cooked"));
		boolean success = testdm.checked(table, recipe, "cooked", true);
		assertTrue("DatabaseModel.checked() returned false", success);
		assertTrue("Error: Spinach has been cooked", isChecked(table, recipe, "cooked"));
	}

	public void testCheckedItems1() {
		table = "ingredients";
		Cursor c = testdm.checkedItems(table, "checked");
		assertNull("Error: No items were checked", c);
	}
	
	public void testCheckedItems2() {
		table = "recipe";
		Cursor c = testdm.checkedItems(table, "favorite");
		ArrayList<String> checked = parseCursor(c);
		String item = checked.get(0);
		assertEquals("Error: Bacon is not " + item, item, "Bacon");
	}

	public void testIsItemChecked() {
		table = "recipe";
		String recipe = "Fried Rice";
		boolean checked1 = testdm.isItemChecked(table, recipe, "favorite");
		boolean checked2 = testdm.isItemChecked(table, recipe, "cooked");
		assertFalse("Error: Fried Rice is not a favorite", checked1);
		assertTrue("Error: Fried Rice has been cooked", checked2);
	}

}
