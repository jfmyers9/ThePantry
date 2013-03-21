package cs169.project.thepantry.test;

import android.test.AndroidTestCase;
import android.test.ViewAsserts;
import android.widget.ListView;
import cs169.project.thepantry.SearchResultsActivity;


public class SearchResultsActivityTest extends AndroidTestCase {

	SearchResultsActivity srAct;
	ListView lView;
	
	protected void setUp() throws Exception {
		super.setUp();
		srAct = new SearchResultsActivity();
	}

	protected void tearDown() throws Exception {
		srAct = null;
	}

	public void testOnCreateBundle() {
		srAct = new SearchResultsActivity();
		assertNotNull("Error: sm null", srAct.sm);
	}

	public void testUI1() {
		srAct.runOnUiThread(
				new Runnable() {
					public void run() {
						lView.requestFocus();
					}
		});
	}

}
