package cs169.project.thepantry.test;

import java.util.ArrayList;

import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;
import cs169.project.thepantry.DatabaseModel;
import cs169.project.thepantry.HomePageActivity;
import cs169.project.thepantry.ThePantryContract;

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
		if (items.moveToFirst()){
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
		fail("Not yet implemented");
	}

	public void testFindItem() {
		table = "ingredients";
		String item1 = "Cookies";
		String item2 = "Milk";
		boolean success1 = testdm.findItem(table, item1);
		boolean success2 = testdm.findItem(table, item2);
		assertFalse("Error: We don't have any cookies", success1);
		assertTrue("Error: We do have milk", success2);
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
		assertEquals("Error: Produce is not ", types.get(0), "Produce");
		assertEquals("Error: Produce is not ", types.get(1), "Dairy");
		assertEquals("Error: Produce is not ", types.get(2), "Poultry");
		assertEquals("Error: Produce is not ", types.get(3), "Grain");
	}

	public void testChecked() {
		table = "recipe";
		String recipe = "Spinach";
		assertFalse("Error: Spinach hasn't been cooked", isChecked(table, recipe, "Cooked"));
		boolean success = testdm.checked(table, recipe, "Cooked", true);
		assertTrue("DatabaseModel.checked() returned false", success);
		assertTrue("Error: Spinach has been cooked", isChecked(table, recipe, "Cooked"));
	}

	public void testCheckedItems() {
		fail("Not yet implemented");
	}

	public void testIsItemChecked(String item) {
		fail("Not yet implemented");
	}

}
