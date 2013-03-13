package cs169.project.thepantry;

import cs169.project.thepantry.ThePantryContract;
import cs169.project.thepantry.ThePantryContract.Ingredients;
import cs169.project.thepantry.ThePantryContract.Inventory;
import cs169.project.thepantry.ThePantryContract.ShoppingList;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** Helper class for creating and handling our local database. */
public class DatabaseModelHelper extends SQLiteOpenHelper {
	/** Database constant */
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "thePantry";
	
	
	private static final String CREATE_INVENTORY_TABLE = "CREATE TABLE "+Inventory.TABLE_NAME+"("+ThePantryContract.ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+Inventory.ITEM+" TEXT UNIQUE, "+Inventory.TYPE+" TEXT, "+Inventory.AMOUNT+" Integer)";
	private static final String CREATE_SHOPPING_LIST_TABLE = "CREATE TABLE "+ShoppingList.TABLE_NAME+"("+ThePantryContract.ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+ShoppingList.ITEM+" TEXT UNIQUE, "+ShoppingList.TYPE+" TEXT, "+ShoppingList.AMOUNT+" Integer, "+ShoppingList.CHECKED+" Integer)";
	private static final String CREATE_INGREDIENTS_TABLE = "CREATE TABLE "+Ingredients.TABLE_NAME+"("+ThePantryContract.ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+Ingredients.ITEM+" TEXT UNIQUE, "+Ingredients.TYPE+" TEXT, "+Ingredients.AMOUNT+" Integer, "+Ingredients.CHECKED+" Integer)";
	
	
	/** Constructor method.
	 *  Note: can insert 'SQLiteDatabase.CursorFactor factory' into
	 *  the method header if we want a customized factory. Otherwise,
	 *  leave as is. */
	public DatabaseModelHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_INVENTORY_TABLE);
		db.execSQL(CREATE_SHOPPING_LIST_TABLE);
		db.execSQL(CREATE_INGREDIENTS_TABLE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// does stuff when database needs to be upgraded
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// does stuff when database gets downgraded
	}
}