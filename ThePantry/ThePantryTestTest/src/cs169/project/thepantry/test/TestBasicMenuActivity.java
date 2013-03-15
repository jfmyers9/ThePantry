package cs169.project.thepantry.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.slidingmenu.lib.SlidingMenu;

import cs169.project.thepantry.BasicMenuActivity;
import cs169.project.thepantry.HomePageActivity;

public class TestBasicMenuActivity extends ActivityInstrumentationTestCase2<HomePageActivity> {
	
	 private BasicMenuActivity mActivity;
	 private ActionBar actionBar;
	 private SlidingMenu sm;
	 private EditText eText;
	 private ListView lView;

	public TestBasicMenuActivity(Class<HomePageActivity> activityClass) {
		super(activityClass);
	}
	
	public TestBasicMenuActivity() {
		super("cs169.project.thepantry.HomePageActivity", HomePageActivity.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
	    setActivityInitialTouchMode(false);

	    mActivity = getActivity();
	    actionBar = mActivity.getSupportActionBar();
	    sm = mActivity.getSlidingMenu();
	    eText = (EditText) mActivity.findViewById(cs169.project.thepantry.R.id.search_text);
	    lView = (ListView) mActivity.findViewById(cs169.project.thepantry.R.id.recsList);
	}
	
	public void testPreConditions() {
		assertEquals(mActivity.getActTitle(), mActivity.getString(cs169.project.thepantry.R.string.app_name));
		assertEquals(eText.getText().toString(), "");
		assertTrue(lView.getAdapter() != null);
		assertTrue(lView.getOnItemClickListener() != null);
	}
	
	public void testEditTextUI() {
		mActivity.runOnUiThread(
				new Runnable() {
					public void run() {
						eText.requestFocus();
						eText.setText("Bacon");
					}
		});
	    assertEquals("Bacon",eText.getText().toString());
	}
	
	public void testListView() {
		mActivity.runOnUiThread(
				new Runnable() {
					public void run() {
						lView.requestFocus();
					}
		});
	}

}
