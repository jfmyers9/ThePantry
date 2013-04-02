package cs169.project.thepantry.test;

import cs169.project.thepantry.DatabaseModel;
import android.test.AndroidTestCase;

public class DatabaseModelFailTest extends AndroidTestCase {

	DatabaseModel faildm;
	
	protected void setUp() throws Exception {
		faildm = new DatabaseModel(getContext(), "");
	}

	protected void tearDown() throws Exception {
		faildm = null;
	}

	public void testAdd() {
		fail("Not yet implemented");
	}

	public void testRemove() {
		fail("Not yet implemented");
	}

	public void testFindAllItems() {
		fail("Not yet implemented");
	}

	public void testFindItem() {
		fail("Not yet implemented");
	}

	public void testFindTypeItems() {
		fail("Not yet implemented");
	}

	public void testFindType() {
		fail("Not yet implemented");
	}

	public void testFindAmount() {
		fail("Not yet implemented");
	}

	public void testFindAllTypes() {
		fail("Not yet implemented");
	}

	public void testChecked() {
		fail("Not yet implemented");
	}

	public void testCheckedItems() {
		fail("Not yet implemented");
	}

	public void testIsItemChecked() {
		fail("Not yet implemented");
	}

}
