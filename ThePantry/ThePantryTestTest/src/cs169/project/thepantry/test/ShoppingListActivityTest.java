package cs169.project.thepantry.test;

import java.io.IOException;
import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import cs169.project.thepantry.IngredientChild;
import cs169.project.thepantry.IngredientGroup;
import cs169.project.thepantry.ShoppingListActivity;

public class ShoppingListActivityTest extends ActivityInstrumentationTestCase2<ShoppingListActivity> {

	private ShoppingListActivity act;
	private String table = "ingredients";
	private String database = "testdatabase";
	private String amount = "1"; 
	
	public ShoppingListActivityTest() {
		super(ShoppingListActivity.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
	    act = getActivity();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/** Helper method to convert IngredientGroup arrays into their String representations */
	protected ArrayList<String> groupToName(ArrayList<IngredientGroup> group) {
		act = getActivity();
		ArrayList<String> result = new ArrayList<String>();
		for (IngredientGroup g : group) {
			result.add(g.getGroup());
		}
		return result;
	}
	
	public void testFillArrays() {
		act.table = "ingredients";
		act.fillArrays();
		int itemCount = act.groupItems.size();
		int childrenCount = act.children.size();
		int nameCount = act.groupNames.size();
		assertFalse("Item array should not be empty", itemCount == 0);
		assertFalse("Children array should not be empty", childrenCount == 0);
		assertFalse("Name array should not be empty", nameCount == 0);
	}
	
	public void testSetChecked() {
	}
	
	public void testGetTypes() {
		ArrayList<IngredientGroup> types = act.getTypes(table);
		String[] names = (String[]) groupToName(types).toArray();
		String[] known = {"produce", "dairy", "poultry", "grain"};
		assertEquals(known, names);
	}

	public void testGetItems() {
		String type = "dairy";
		ArrayList<IngredientChild> types = act.getItems(table, type);
		String item = types.get(0).getName();
		assertEquals("milk", item);
	}

	/** Functional test for adding an item from the shopping list to the Database */
	public void testAddItem() {
		String item = "pasta";
		String type = "grain";
		boolean thrown = false;
		try {
			act.addItem(table, item, type, amount);
		} catch (IOException e) {
			thrown = true;
		}
		assertFalse("Pasta should not cause IOException", thrown);
	}
	
	public void testAddItemEmptyInput() {
		String item = "";
		String type = "other";
		boolean thrown = false;
		try {
			act.addItem(table, item, type, amount);
		} catch (IOException e) {
			thrown = true;
		}
		assertTrue("Expecting IOException, empty input not allowed", thrown);
	}
	
	public void testAddItemSpacesInput() {
		String item = "   ";
		String type = "other";
		boolean thrown = false;
		try {
			act.addItem(table, item, type, amount);
		} catch (IOException e) {
			thrown = true;
		}
		assertTrue("Expecting IOException, spaces input not allowed", thrown);
	}
	
	public void testAddShopItem() {
		//implement later
	}
	
	public void testRetrieveType() {
		//implement later
	}

}

