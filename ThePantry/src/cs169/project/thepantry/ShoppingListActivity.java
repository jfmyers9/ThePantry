package cs169.project.thepantry;

import java.io.IOException;
import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;

import cs169.project.thepantry.ThePantryContract.ShoppingList;

public class ShoppingListActivity extends BaseListActivity {

	private Spinner spinner;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.ShoppingListTitle));
		setContentView(R.layout.activity_shopping_list);
		table = ShoppingList.TABLE_NAME;
		eView = (ExpandableListView) findViewById(R.id.exp_shop_list);
		eView.setFocusable(true);
		eView.setDividerHeight(2);
		eView.setClickable(true);;
		
		groupItems = new ArrayList<IngredientGroup>();
		groupNames = new ArrayList<String>();
		children = new ArrayList<IngredientChild>();
		
		fillArrays();
		eAdapter = new BaseListAdapter(getApplicationContext(), groupItems, table);
		eView.setAdapter(eAdapter);
		System.out.println(groupItems.get(0));
		
		// Creates and populates the ingredient type drop-down menu
		spinner = (Spinner) findViewById(R.id.add_sl_types);
		ArrayAdapter<CharSequence> adapter = 
				ArrayAdapter.createFromResource(this,
												R.array.ingredient_type_array,
												android.R.layout.simple_spinner_item);
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, groupNames);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.shopping_list, menu);
		return true;
	}
	
	/** Retrieves user input and adds the ingredient to the shopping list and database */
	public void addShopItem(View view) {
		EditText eText = (EditText) findViewById(R.id.shopping_list_text);
		try {
			addItem(eText.getText().toString(), spinner.getSelectedItem().toString(), "1");
			((BaseExpandableListAdapter)eView.getExpandableListAdapter()).notifyDataSetChanged();
		} catch (IOException e) {
			Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
			toast.show();
		}
		eText.setText("");
	}

}
