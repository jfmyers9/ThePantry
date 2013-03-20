package cs169.project.thepantry.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import cs169.project.thepantry.ShoppingListActivity;
import android.test.ActivityUnitTestCase;

public class ShoppingListActivityTest extends ActivityInstrumentationTestCase2<ShoppingListActivity> {

	private ShoppingListActivity listAct;
	private Spinner mSpinner;
	private SpinnerAdapter mPlanetData;
	private EditText mText;
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
	    mText = (EditText) listAct.findViewById(cs169.project.thepantry.R.id.shopping_list_text);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testPreConditions() {
	    assertTrue(mSpinner.getOnItemSelectedListener() != null);
	    assertTrue(mPlanetData != null);
	    assertEquals(mPlanetData.getCount(),ADAPTER_COUNT);
	  } // end of testPreConditions() method definition

	public void testAddShopItem() {
		listAct.addShopItem(mText);
		assertNull(mText.getText());
	}

}
