package cs169.project.thepantry;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cs169.project.thepantry.Storage;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class CookBookActivity extends BasicMenuActivity {
	
	private final String TITLE = "Cook Book";
	private final String DATABASE = ThePantryContract.DATABASE_NAME;
	private String tableName;
	private ArrayList<Storage> recipes;
	private DatabaseModel dm;
	private CookbookListAdapter cbAdapter;
	private Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cook_book);
		
		tableName = ThePantryContract.CookBook.TABLE_NAME;
		recipes = new ArrayList<Storage>();
		context = this;
		
		recipes = getRecipes();
		
		setTitle(TITLE);
		
		ListView listView = (ListView) findViewById(R.id.cookbook_list);
		cbAdapter = new CookbookListAdapter(this, recipes);
		listView.setAdapter(cbAdapter);
		cbAdapter.notifyDataSetChanged();
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				System.out.println("HI");
				Recipe rec = (Recipe) view.getTag();
				Intent intent = new Intent(context, RecipeActivity.class);
				intent.putExtra("result", rec);
				intent.putExtra("type", "cookbook");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				
			}
		});
	}
	
	private ArrayList<Storage> getRecipes() {
		dm = new DatabaseModel(this, DATABASE);
		ArrayList<Storage> recipes = dm.getAllStorage(tableName);
		dm.close();
		return recipes;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.cook_book, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		switch(item.getItemId()) {
			case R.id.action_new_recipe:
				intent = new Intent(this, AddRecipeActivity.class);
				break;
			case R.id.action_settings:
				intent = new Intent(this, SettingsActivity.class);
				break;
			case android.R.id.home:
				toggle();
				return true;
			case R.id.menu_add:
				intent = new Intent(this, AddRecipeActivity.class);
				break;
			default:
				System.out.println(item.getItemId());
				System.out.println(R.id.action_new_recipe);
		}
		
		if (intent != null) {
			startActivity(intent);
			return true;
		}
		
		return false;		
	}

}
