package cs169.project.thepantry.test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.ViewAsserts;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.slidingmenu.lib.SlidingMenu;

import cs169.project.thepantry.HomePageActivity;

public class TestBasicMenuActivity extends ActivityInstrumentationTestCase2<HomePageActivity> {
	
	 private HomePageActivity mActivity;
	 private ActionBar actionBar;
	 private SlidingMenu sm;
	 private EditText eText;
	 private ListView lView;
	
	public TestBasicMenuActivity(String name) {
		super("cs169.project.thepantry", HomePageActivity.class);
		setName(name);
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
		TouchUtils.tapView(this, eText);
		eText.performClick();
		this.sendKeys("1");
		this.sendKeys("2");
	    assertEquals("1",eText.getText().toString());
	}
	
	public void testListView() {
		mActivity.runOnUiThread(
				new Runnable() {
					public void run() {
						lView.requestFocus();
					}
		});
		this.sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
		this.sendKeys(KeyEvent.KEYCODE_3D_MODE);
		ViewAsserts.assertOnScreen(lView.getRootView(), eText);
		assertEquals(lView, (ListView) mActivity.findViewById(cs169.project.thepantry.R.id.recsList));
	}
	
	public void testSlidingMenu() {
		mActivity.runOnUiThread(
				new Runnable() {
					public void run() {
						lView.requestFocus();
					}
		});
		this.sendKeys("l");
		ViewAsserts.assertOnScreen(eText.getRootView(), lView);
	}
	
	

}
