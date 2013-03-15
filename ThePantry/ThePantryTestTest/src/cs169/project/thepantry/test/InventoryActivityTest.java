package cs169.project.thepantry.test;

import com.slidingmenu.lib.SlidingMenu;
import com.actionbarsherlock.app.ActionBar;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import cs169.project.thepantry.BasicMenuActivity;
import cs169.project.thepantry.InventoryActivity;

import junit.framework.TestCase;

public class InventoryActivityTest extends ActivityInstrumentationTestCase2<InventoryActivity> {
	
	 private BasicMenuActivity mActivity;
	 private SlidingMenu sm;
	 private ActionBar actionBar;
	 private EditText eText;
	 private ListView lView;
	 private Button sButton;

	public InventoryActivityTest(Class<InventoryActivity> activity) {
		super(activity);
	}
	
	public InventoryActivityTest() {
		super(InventoryActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
	    setActivityInitialTouchMode(false);

	    mActivity = getActivity();
	    actionBar = mActivity.getSupportActionBar();
	    sm = mActivity.getSlidingMenu();
	    eText = (EditText) mActivity.findViewById(cs169.project.thepantry.R.id.search_text);
	    lView = (ListView) mActivity.findViewById(cs169.project.thepantry.R.id.recsList);
	    sButton = (Button) mActivity.findViewById(cs169.project.thepantry.R.id.search);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testPreConditions() {
		assertEquals(eText.getText().toString(), "");
		assertTrue(lView.getAdapter() != null);
		assertTrue(lView.getOnItemClickListener() != null);
	}

	public void testEdit() {
		mActivity.runOnUiThread(
				new Runnable() {
					public void run() {
						eText.requestFocus();
						eText.setText("Waffles");
					}
		});
		assertNotNull(eText.getText());
	}

	public void testSearch() {
		mActivity.runOnUiThread(
				new Runnable() {
					public void run() {
						sButton.requestFocus();
						sButton.setText("Search");
					}
		});
		assertNotNull(sButton.getText());
	}

}
