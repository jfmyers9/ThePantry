package cs169.project.thepantry.test;

import java.io.IOException;
import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import cs169.project.thepantry.BaseListActivity;
import cs169.project.thepantry.IngredientChild;
import cs169.project.thepantry.IngredientGroup;


public class BaseListActivityTest extends ActivityInstrumentationTestCase2<BaseListActivity> {

	private String table = "ingredients";
	private String database = "testdatabase";
	private String amount = "1";
	private BaseListActivity act;
	
	protected void setUp() throws Exception {
		super.setUp();
		act = getActivity();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public BaseListActivityTest() {
		super(BaseListActivity.class);
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
		act.table = this.table;
		act.fillArrays();
		int itemCount = act.groupItems.size();
		int childrenCount = act.children.size();
		int nameCount = act.groupNames.size();
		assertFalse("Item array should not be empty", itemCount == 0);
		assertFalse("Children array should not be empty", childrenCount == 0);
		assertFalse("Name array should not be empty", nameCount == 0);
	}

	public void testGetTypes() {
		ArrayList<IngredientGroup> types = act.getTypes(table);
		String[] names = (String[]) groupToName(types).toArray();
		String[] known = {"Produce", "Dairy", "Poultry", "Grain"};
		assertEquals(known, names);
	}

	public void testGetItems() {
		String type = "Dairy";
		ArrayList<IngredientChild> types = act.getItems(table, type);
		String item = types.get(0).getName();
		assertEquals("Milk", item);
	}

	public void testAddItem() {
		String item = "Pasta";
		String type = "Grain";
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
		String type = "Other";
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
		String type = "Other";
		boolean thrown = false;
		try {
			act.addItem(table, item, type, amount);
		} catch (IOException e) {
			thrown = true;
		}
		assertTrue("Expecting IOException, spaces input not allowed", thrown);
	}

	public void testUpdateInventory() {
		fail("Not yet implemented");
	}
}
