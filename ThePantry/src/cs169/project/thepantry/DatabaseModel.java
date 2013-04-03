package cs169.project.thepantry;


import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;

/** Interface class called by the activities to interact with our database.
* Uses the DatabaseModelHelper class to create the database.
* @author amyzhang
*/
public class DatabaseModel extends SQLiteAssetHelper {

    //private static final String DATABASE_NAME = "thepantry";
    private static final int DATABASE_VERSION = 1;

    public DatabaseModel(Context context, String databaseName) {
        super(context, databaseName, null, DATABASE_VERSION);
    }

	/** Adds the ITEM, its TYPE and given AMOUNT to the specified TABLE.
	 * Returns true if the modification was successful, false otherwise.
	 */
	public boolean add(String table, String item, String type, String amount) {
		try {
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
			if (!findItem(table, item)) {
				try {
					newRowId = db.insertOrThrow(table, null, values);
					//db.close();
					if (newRowId != -1) {
						return true;
					}
				} catch (SQLiteException e) {
					//db.close();
					System.out.println(e.getMessage());
					//do something
				}
			} else {
				// increment amount
				// add a popup to ask if they want amount to be incremented?
				return true;
			}
		} catch (SQLiteException e) {
			System.out.println(e.getMessage());
			// add more stuff
		}
		return false;
	}

	/** Removes the ITEM from the specified TABLE.
	 * Returns true if the modification was successful, false otherwise.
	 */
	public boolean remove(String table, String item) {
		try {
			SQLiteDatabase db = getWritableDatabase();
			String selection = ThePantryContract.ITEM + " = ?";
			String[] selectionArgs = {item};
			
			int val = db.delete(table, selection, selectionArgs);
			//db.close();
			
			if (val != 0) {
				return true;
			}
		} catch (SQLiteException e) {
			System.out.println(e.getMessage());
			// add more stuff
		}
		return false;
	}
	
	/** Returns all items from the specified TABLE. */
	public Cursor findAllItems(String table) {
		try {
			SQLiteDatabase db = getReadableDatabase();
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(table);
			
			Cursor c = qb.query(db, null, null, null, null, null, null, null);
			//db.close();
			
			if (c.moveToFirst()) {
				return c;
			}
		} catch (SQLiteException e) {
			System.out.println(e.getMessage());
			// add more stuff
		}
		return null;
	}

	/** Finds all items is in the specified TABLE that contain given text */
	public Cursor search(String table, String query) {
		try {
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(table);
		
		String selection = ThePantryContract.ITEM + " LIKE ?";
		String[] selectionArgs = {"%"+query+"%"};
		
		Cursor c = qb.query(db, null, selection, selectionArgs, null, null, null);
		if (c.moveToFirst()) {
			return c;
		} else {
			return null;
		}
		}catch (SQLiteException e) {
			System.out.println(e.getMessage());
			// add more stuff
		}
		return null;
	}
	
	/** Finds if an item is in the specified TABLE. */
	public boolean findItem(String table, String item) {
		try {
			SQLiteDatabase db = getReadableDatabase();
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(table);
			
			String selection = ThePantryContract.ITEM + " = ?";
			String[] selectionArgs = {item};
			
			Cursor c = qb.query(db, null, selection, selectionArgs, null, null, null);
			//db.close();
			
			if(c.moveToFirst()) {
				return true;
			}
		} catch (SQLiteException e) {
			System.out.println(e.getMessage());
			// add more stuff
		}
		return false;
	}

	/** Returns all items of the TYPE from the specified TABLE. */
	public Cursor findTypeItems(String table, String type) {
		try {
			SQLiteDatabase db = getReadableDatabase();
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(table);
			
			String[] columns = {ThePantryContract.ITEM};
			String selection = ThePantryContract.TYPE + " = ?";
			String[] selectionArgs = {type};
			
			Cursor c = qb.query(db, columns, selection, selectionArgs, null, null, null);
			//db.close();
			
			if (c.moveToFirst()) {
				return c;
			}
		} catch (SQLiteException e) {
			System.out.println(e.getMessage());
			// add more stuff
		}
		return null;
	}

	/** Returns the type of the ITEM from the specified TABLE. */
	public Cursor findType(String table, String item) {
		try {
			SQLiteDatabase db = getReadableDatabase();
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(table);
			
			String[] columns = {ThePantryContract.TYPE};
			String selection = ThePantryContract.ITEM + " = ?";
			String[] selectionArgs = {item};
			
			Cursor c = qb.query(db, columns, selection, selectionArgs, null, null, null);
			//db.close();
			
			if (c.moveToFirst()) {
				return c;
			}
		} catch (SQLiteException e) {
			System.out.println(e.getMessage());
			// add more stuff
		}
		return null;
	}

	/** Returns the amount of the ITEM from the specified TABLE. */
	public Cursor findAmount(String table, String item) {
		try {
			SQLiteDatabase db = getReadableDatabase();
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(table);
			
			String[] columns = {ThePantryContract.AMOUNT};
			String selection = ThePantryContract.ITEM + " = ?";
			String[] selectionArgs = {item};
			
			Cursor c = qb.query(db, columns, selection, selectionArgs, null, null, null);
			//db.close();
			
			if (c.moveToFirst()) {
				return c;
			}
		} catch (SQLiteException e) {
			System.out.println(e.getMessage());
			// add more stuff
		}
		return null;
	}

	/** Returns all types from the specified TABLE. */
	public Cursor findAllTypes(String table) {
		try {
			SQLiteDatabase db = getReadableDatabase();
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(table);
			qb.setDistinct(true);
			
			String[] columns = {ThePantryContract.TYPE};
			
			Cursor c = qb.query(db, columns, null, null, null, null, null);
			//db.close();
			
			if (c.moveToFirst()) {
				return c;
			}
		} catch (SQLiteException e) {
			System.out.println(e.getMessage());
			// add more stuff
		}
		return null;
	}

	// TODO -- Figure out if we can use android's UI to reference checked items instead of database
	// TODO -- Figure out way to extract an entire entry and add it to a new table

	/** Sets the check value of the item to whatever checked it */
	public boolean checked(String table, String item, String col, boolean checked) {
		try {
			SQLiteDatabase db = getWritableDatabase();
			String selection;
			if (table.equals(ThePantryContract.Recipe.TABLE_NAME)) {
				selection = ThePantryContract.Recipe.RECIPE + " = ?";
			} else {
				selection = ThePantryContract.ITEM + " = ?";	
			}
			String[] selectionArgs = {item};
			ContentValues values = new ContentValues();
			
			String newCheck;
			if (checked) {
				newCheck = "true";
			} else {
				newCheck="false";
			}
			values.put(col, newCheck);
			int rows = db.update(table, values, selection, selectionArgs);
			//db.close();
			
			if (rows != 0) {
				return true;
			}
		} catch (SQLiteException e) {
			System.out.println(e.getMessage());
			// add more stuff
		}
		return false;
	}
	

	/** Returns list of all items (preferably entries) checked */
	public Cursor checkedItems(String table, String col) {
		try {
			SQLiteDatabase db = getReadableDatabase();
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(table);
			
			String selection = col + " = ?";
			String[] selectionArgs = {"true"};


			Cursor c = qb.query(db, null, selection, selectionArgs, null, null, null);
			//db.close();
			
			if (c.moveToFirst()) {
				return c;
			}
		} catch (SQLiteException e) {
			System.out.println(e.getMessage());
			// add more stuff
		}
		return null;
	}
	
	
	/** Returns false if item is not checked and true if item is checked for
	 * favorited and cooked recipe
	 */
	public boolean isItemChecked(String table, String recipe_name, String col) {
		try {
			SQLiteDatabase db = getReadableDatabase();
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(table);
			
			String[] columns = {col};
			String selection = ThePantryContract.Recipe.RECIPE + " = ?";
			String[] selectionArgs = {recipe_name};
			
			Cursor c = qb.query(db, columns, selection, selectionArgs, null, null, null);
			//db.close();
			
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
		} catch (SQLiteException e) {
			System.out.println(e.getMessage());
			// add more stuff
		}
		return false;
	}

}