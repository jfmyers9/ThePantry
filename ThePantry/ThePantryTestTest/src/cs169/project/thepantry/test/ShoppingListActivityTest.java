package cs169.project.thepantry.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import cs169.project.thepantry.ShoppingListActivity;

public class ShoppingListActivityTest extends
		ActivityInstrumentationTestCase2<ShoppingListActivity> {

	private ShoppingListActivity listAct;
	private Spinner mSpinner;
	private SpinnerAdapter mPlanetData;
	public static final int ADAPTER_COUNT = 9;  
	
	public ShoppingListActivityTest() {
		super(ShoppingListActivity.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		setActivityInitialTouchMode(false);
	    listAct = getActivity();
	    mSpinner =(Spinner) listAct.findViewById(cs169.project.thepantry.R.id.add_sl_types);
	    mPlanetData = mSpinner.getAdapter();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testPreConditions() {
	    assertTrue(mSpinner.getOnItemSelectedListener() != null);
	    assertTrue(mPlanetData != null);
	    assertEquals(mPlanetData.getCount(),ADAPTER_COUNT);
	  } // end of testPreConditions() method definition
	
	public void testOnCreateBundle() {
		fail("Not yet implemented");
	}

	public void testOnCreateOptionsMenuMenu() {
		fail("Not yet implemented");
	}

	public void testAddShopItem() {
		fail("Not yet implemented");
	}

	public void testFillArrays() {
		fail("Not yet implemented");
	}

	public void testGetTypes() {
		fail("Not yet implemented");
	}

	public void testGetItems() {
		fail("Not yet implemented");
	}

	public void testAddItem() {
		fail("Not yet implemented");
	}

	public void testRemoveItem() {
		fail("Not yet implemented");
	}

	public void testSwipeToRemove() {
		fail("Not yet implemented");
	}

	public void testUpdateInventory() {
		fail("Not yet implemented");
	}

	public void testCheck() {
		fail("Not yet implemented");
	}

}
