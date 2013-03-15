package cs169.project.thepantry.test;

import java.util.ArrayList;

import junit.framework.TestCase;
import android.database.Cursor;
import android.test.mock.MockContext;
import cs169.project.thepantry.DatabaseModel;
import cs169.project.thepantry.ThePantryContract;

import org.junit.*;

public class DatabaseModelTest extends TestCase {

	DatabaseModel testdm;
	String table;
	private static final String DATABASE_NAME = "testdatabase";
	
	protected void setUp() throws Exception {
		MockContext dummy = new MockContext();
		testdm = new DatabaseModel(dummy, DATABASE_NAME);
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

	@Test
	public void testAdd() {
		table = "Ingredients";
		String item = "Eggs";
		String type = "Dairy";
		String amount = "12";
		boolean success = testdm.add(table, item, type, amount);
		assertTrue("Error: Ice Cream not added to database", success);
		testFindItem(item);	
	}

	public void testRemove() {
		table = "Ingredients";
		String item = "Milk";
		boolean success = testdm.remove(table, item);
		assertTrue("Error: Milk not removed from database", success);
		testFindItem(item);
	}

	public void testFindAllItems() {
		fail("Not yet implemented");
	}

	public void testFindItem(String item) {
		fail("Not yet implemented");
	}

	public void testFindTypeItems() {
		fail("Not yet implemented");
	}

	public void testFindType() {
		table = "Ingredients";
		String item = "Strawberries";
		Cursor ctype = testdm.findType(table, item);
		ArrayList<String> types = parseCursor(ctype);
		String type = types.get(0);
		assertEquals("Error: Strawberries are not " + type, type, "Produce");
	}

	public void testFindAmount() {
		table = "Ingredients";
		String item = "Chicken";
		Cursor camount = testdm.findAmount(table, item);
		ArrayList<String> amounts = parseCursor(camount);
		String amount = amounts.get(0);
		assertEquals("Error: Chicken amount is not " + amount, amount, "4");
	}

	public void testFindAllTypes() {
		fail("Not yet implemented");
	}

	public void testChecked() {
		String item1 = "Bread";
		//String item2 = "Milk";
		boolean success = testdm.checked(table, item1, ThePantryContract.CHECKED, true);
		assertTrue("Error: Bread was not checked", success);
		//boolean item1check = testIsItemChecked(item1);
		//boolean item2check = testIsItemChecked(item2);
		
	}

	public void testCheckedItems() {
		fail("Not yet implemented");
	}

	public void testIsItemChecked(String item) {
		fail("Not yet implemented");
	}

}
