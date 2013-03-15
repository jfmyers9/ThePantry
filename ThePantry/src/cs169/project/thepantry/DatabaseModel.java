package cs169.project.thepantry;

import java.util.ArrayList;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

/** Interface class called by the activities to interact with our database.
 *  Uses the DatabaseModelHelper class to create the database.
 * @author amyzhang
 */
public class DatabaseModel extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "thepantry";
    private static final int DATABASE_VERSION = 1;

    public DatabaseModel(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }

    public Cursor tester() {
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		String [] sqlSelect = {"0 _id", "item", "type", "amount", "checked"}; 
		String sqlTables = "ingredients";

		qb.setTables(sqlTables);
		Cursor c = qb.query(db, sqlSelect, null, null,
				null, null, null);

		c.moveToFirst();
		

		return c;

	}
    
    
	/** Adds the ITEM, its TYPE and given AMOUNT to the specified TABLE.
	 * Returns true if the modification was successful, false otherwise.
	 */
	public boolean add(String table, String item, String type, float amount) {
		//TODO - try/catch block all the SQLiteDatabase calls, throws SQLiteException if open failed
		SQLiteDatabase db = getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(ThePantryContract.ITEM, item);
		values.put(ThePantryContract.TYPE, type);
		values.put(ThePantryContract.AMOUNT, amount);
		if (table.equals(ThePantryContract.ShoppingList.TABLE_NAME)
				|| table.equals(ThePantryContract.Ingredients.TABLE_NAME)) {
			values.put(ThePantryContract.CHECKED, false);
		}
		long newRowId;
		// TODO - consider using insertOrThrow and add try/catch block?
		newRowId = db.insert(table, null, values);
		if (newRowId != -1) {
			return true;
		} else {
			return false;
		}
	}

	/** Removes the ITEM from the specified TABLE.
	 * Returns true if the modification was successful, false otherwise.
	 */
	public boolean remove(String table, String item) {
		SQLiteDatabase db = getWritableDatabase();
		
		String selection = ThePantryContract.ITEM + " = ?";
		String[] selectionArgs = {item};
		int val = db.delete(table, selection, selectionArgs);
		// TODO - do a check on val to determine success
		return true;
	}
	
	/** Returns all items from the specified TABLE. */
	public Cursor findAllItems(String table) {
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(table);
		
		Cursor c = qb.query(db, null, null, null, null, null, null, null);
		c.moveToFirst();
		return c;
	}

	/** Returns one item from the specified TABLE. */
	public Cursor findItem(String table, String item) {
		// Not quite sure what this is supposed to do
		// Right now hypothetically returns type, amount, and checked of the item -Amy
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(table);
		
		String selection = ThePantryContract.ITEM + " = ?";
		String[] selectionArgs = {item};
		
		Cursor c = qb.query(db, null, selection, selectionArgs, null, null, null);
		c.moveToFirst();
		return c;
	}

	/** Returns all items of the TYPE from the specified TABLE. */
	public Cursor findTypeItems(String table, String type) {
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(table);
		
		String[] columns = {ThePantryContract.ITEM};
		String selection = ThePantryContract.TYPE + " = ?";
		String[] selectionArgs = {type};
		
		Cursor c = qb.query(db, columns, selection, selectionArgs, null, null, null);
		c.moveToFirst();
		return c;
	}

	/** Returns the type of the ITEM from the specified TABLE. */
	public Cursor findType(String table, String item) {
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(table);
		
		String[] columns = {ThePantryContract.TYPE};
		String selection = ThePantryContract.ITEM + " = ?";
		String[] selectionArgs = {item};
		
		Cursor c = qb.query(db, columns, selection, selectionArgs, null, null, null);
		c.moveToFirst();
		return c;
	}

	/** Returns the amount of the ITEM from the specified TABLE. */
	public Cursor findAmount(String table, String item) {
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(table);
		
		String[] columns = {ThePantryContract.AMOUNT};
		String selection = ThePantryContract.ITEM + " = ?";
		String[] selectionArgs = {item};
		
		Cursor c = qb.query(db, columns, selection, selectionArgs, null, null, null);
		c.moveToFirst();
		return c;
	}

	/** Returns all types from the specified TABLE. */
	public Cursor findAllTypes(String table) {
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(table);
		qb.setDistinct(true);
		
		String[] columns = {ThePantryContract.TYPE};
		
		Cursor c = qb.query(db, columns, null, null, null, null, null);
		c.moveToFirst();
		return c;
	}

	// TODO -- Figure out if we can use android's UI to reference checked items instead of database
	// TODO -- Figure out way to extract an entire entry and add it to a new table

	/** Sets the check value of the item to whatever checked it */
	public boolean checked(String table, String item, String col, boolean checked) {
		SQLiteDatabase db = getWritableDatabase();
		
		String selection = ThePantryContract.ITEM + " = ?";
		String[] selectionArgs = {item};
		
		ContentValues values = new ContentValues();
		
		int newCheck;
		if (checked) {
			newCheck = 1;
		} else {
			newCheck=0;
		}
		values.put(col, newCheck);
		int rows = db.update(table, values, selection, selectionArgs);
		// TODO - add some check using rows to see if the update was successful
		return true;
	}
	

	/** Returns list of all items (preferably entries) checked */
	public Cursor checkedItems(String table, String col) {
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(table);
		
		String selection = col + " = ?";
		String[] selectionArgs = {"true"}; //not quite right...
		
		// if we want the entry, can replace columns with null?
		

		Cursor c = qb.query(db, null, selection, selectionArgs, null, null, null);
		c.moveToFirst();
		return c;
	}
	
	
	/** Returns 0 if item is not checked and 1 if item is checked for
	 * favorited and cooked recipe
	 */
	public boolean isItemChecked(String table, String recipe_name, String col) {
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(table);
		
		String[] columns = {col};
		String selection = ThePantryContract.RECIPE + " = ?";
		String[] selectionArgs = {recipe_name};
		
		Cursor c = qb.query(db, columns, selection, selectionArgs, null, null, null);
		if (c.moveToFirst()) {
			String data = c.getString(0);
			System.out.println(data);
			if (data.equals("true")) {
				c.close();
				return true;
			}
			c.close();
			return false;
		}
		c.close();
		return false;
	}
	
	//TODO - consider creating a private method to construct queries
	// The code is so repetitive and we could probably get rid of some
	// of that repetition by making a constructQuery class or something.

}