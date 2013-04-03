package cs169.project.thepantry;


import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import cs169.project.thepantry.ThePantryContract.Ingredients;

/** Interface class called by the activities to interact with our database.
* Uses the DatabaseModelHelper class to create the database.
* @author amyzhang
*/
public class DatabaseModel extends SQLiteAssetHelper {

    private static final int DATABASE_VERSION = 1;

    public DatabaseModel(Context context, String databaseName) {
        super(context, databaseName, null, DATABASE_VERSION);
    }

	/** Adds the ITEM, its TYPE and given AMOUNT to the specified TABLE.
	 * Returns true if the modification was successful, false otherwise
	 * If a SQLiteException is thrown, returns false and prints out the error
	 * message to System.err (could be due to no table or no column).
	 */
	public boolean add(String table, String item, String type, String amount) {
		try {
			SQLiteDatabase db = getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(ThePantryContract.ITEM, item);
			values.put(ThePantryContract.TYPE, type);
			values.put(ThePantryContract.AMOUNT, amount);
			if (table.equals(ThePantryContract.ShoppingList.TABLE_NAME)
					|| table.equals(ThePantryContract.Inventory.TABLE_NAME)) {
				values.put(ThePantryContract.ADDFLAG, "true");
				values.put(ThePantryContract.REMOVEFLAG, "false");
			}
			
			long newRowId;
			if (!findItem(table, item)) {
				try {
					newRowId = db.insertOrThrow(table, null, values);
					if (newRowId != -1) {
						return true;
					}
				} catch (SQLiteException e) {
					//do something
				}
				return false;
			} else {
				// increment amount
				// add a popup to ask if they want amount to be incremented?
				return true;
			}
		} catch (SQLiteException e) {
			System.err.println(e.getMessage());
			return false;
		}
	}
	
	
	/** Removes the ITEM from the specified TABLE.
	 * Returns true if the modification was successful, false otherwise.
	 * If a SQLiteException is thrown, returns false and prints out the error
	 * message to System.err (could be due to no table or no column).
	 */
	public boolean remove(String table, String item) {
		try {
			SQLiteDatabase db = getWritableDatabase();
			String selection = ThePantryContract.ITEM + " = ?";
			String[] selectionArgs = {item};
			
			int val = db.delete(table, selection, selectionArgs);			
			if (val != 0) {
				return true;
			} else {
				return false;
			}
		} catch (SQLiteException e) {
			System.err.println(e.getMessage());
			return false;
		}
	}
	
	/** Returns all items from the specified TABLE, null if none can be found.
	 * If a SQLiteException is thrown, returns null and prints out the error
	 * message to System.err (could be due to no table or no column). */
	public Cursor findAllItems(String table) {
		try {
			SQLiteDatabase db = getReadableDatabase();
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(table);
			
			String selection;
			String[] selectionArgs = new String[1];
			if(table == Ingredients.TABLE_NAME) {
				selection = null;
				selectionArgs = null;
			} else {
				selection = ThePantryContract.REMOVEFLAG + " = ?";
				selectionArgs[0] = "false";
			}
			
			Cursor c = qb.query(db, null, selection, selectionArgs, null, null, null, null);
			if (c.moveToFirst()) {
				return c;
			} else {
				return null;
			}
		} catch (SQLiteException e) {
			System.err.println(e.getMessage());
			return null;
		}
	}

	/** Finds all items in the specified TABLE that contain given text
	 * If a SQLiteException is thrown, returns null and prints out the error
	 * message to System.err (could be due to no table or no column). */
	public Cursor search(String table, String query) {
		try {
			SQLiteDatabase db = getReadableDatabase();
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(table);
	
			String selection;
			String[] columns = {ThePantryContract.ITEM};
			ArrayList<String> selectionArgsList = new ArrayList<String>();
			if(table == Ingredients.TABLE_NAME) {
				selection = ThePantryContract.ITEM + " LIKE ?";
				selectionArgsList.add("%"+query+"%");
			} else {
				selection = ThePantryContract.ITEM + " LIKE ?" + " AND " + ThePantryContract.REMOVEFLAG + " = ?";
				selectionArgsList.add("%"+query+"%");
				selectionArgsList.add("false");
			}
			String[] selectionArgs = selectionArgsList.toArray(new String[0]);
			
			Cursor c = qb.query(db, columns, selection, selectionArgs, null, null, null);
			if (c.moveToFirst()) {
				System.out.println("XXXXXXXXXXXXXXX");
				System.out.println(c.getString(0));
				return c;
			} else {
				return null;
			}
		} catch (SQLiteException e) {
			System.err.println(e.getMessage());
			return null;
		}
	}
	
	/** Finds if an item is in the specified TABLE.
	 *  If a SQLiteException is thrown, returns false and prints out the error
	 *  message to System.err (could be due to no table or no column). */
	public boolean findItem(String table, String item) {
		try {
			SQLiteDatabase db = getReadableDatabase();
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(table);

			System.out.println("*************");
				String selection = ThePantryContract.ITEM + " = ?";
				String[] selectionArgs = {item};
				
				Cursor c = qb.query(db, null, selection, selectionArgs, null, null, null);
				if(c.moveToFirst()) {
					if(table != Ingredients.TABLE_NAME) {	
						if(isItemChecked(table, item, ThePantryContract.REMOVEFLAG)){
							return false;
						}
					}
					return true;
				}else {
					return false;
				}
		} catch (SQLiteException e) {
			System.err.println(e.getMessage());
			return false;
		}
	}

	/** Returns all items of the TYPE from the specified TABLE.
	 *  If a SQLiteException is thrown, returns false and prints out the error
	 *  message to System.err (could be due to no table or no column). */
	public Cursor findTypeItems(String table, String type) {
		try {
			SQLiteDatabase db = getReadableDatabase();
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(table);
			
			String[] columns = {ThePantryContract.ITEM};
			String selection = ThePantryContract.TYPE + " = ?";
			String[] selectionArgs = {type};
			
			Cursor c = qb.query(db, columns, selection, selectionArgs, null, null, null);
			if (c.moveToFirst()) {
				return c;
			} else {
				return null;
			}
		} catch (SQLiteException e) {
			System.err.println(e.getMessage());
			return null;
		}
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
			if (c.moveToFirst()) {
				if(table != Ingredients.TABLE_NAME) {	
					if(isItemChecked(table, item, ThePantryContract.REMOVEFLAG)){
						return null;
					}
				}
				return c;
			} else {
				return null;
			}
		} catch (SQLiteException e) {
			System.err.println(e.getMessage());
			return null;
		}
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
			if (c.moveToFirst()) {
				if(table != Ingredients.TABLE_NAME) {	
					if(isItemChecked(table, item, ThePantryContract.REMOVEFLAG)){
						return null;
					}
				}
				return c;
			} else {
				return null;
			}
		} catch (SQLiteException e) {
			System.err.println(e.getMessage());
			return null;
		}
	}

	/** Returns all types from the specified TABLE. */
	public Cursor findAllTypes(String table) {
		try {
			SQLiteDatabase db = getReadableDatabase();
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(table);
			qb.setDistinct(true);
			
			String selection;
			String[] selectionArgs = new String[1];
			if(table == Ingredients.TABLE_NAME) {
				selection = null;
				selectionArgs = null;
			} else {
				selection = ThePantryContract.REMOVEFLAG + " = ?";
				selectionArgs[0] = "false";
			}
			
			String[] columns = {ThePantryContract.TYPE};
			
			Cursor c = qb.query(db, columns, selection, selectionArgs, null, null, null);
			if (c.moveToFirst()) {
				return c;
			} else {
				return null;
			}
		} catch (SQLiteException e) {
			System.err.println(e.getMessage());
			return null;
		}
	}


	/** Sets the check value of the item to whatever checked it */
	public boolean check(String table, String item, String col, boolean checked) {
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
			if (rows != 0) {
				return true;
			} else {
				return false;
			}
		} catch (SQLiteException e) {
			System.err.println(e.getMessage());
			return false;
		}
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
			if (c.moveToFirst()) {
				return c;
			} else {
				return null;
			}
		} catch (SQLiteException e) {
			System.err.println(e.getMessage());
			return null;
		}
	}
	
	
	/** Returns false if item is not checked and true if item is checked for
	 * favorited and cooked recipe
	 */
	public boolean isItemChecked(String table, String name, String col) {
			
			SQLiteDatabase db = getReadableDatabase();
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(table);
			
			
			String[] columns = {col};
			String selection;
			if (table == ThePantryContract.Recipe.TABLE_NAME) {
				selection = ThePantryContract.Recipe.RECIPE + " = ?";
			} else {
				selection = ThePantryContract.ITEM + " = ?";
			}
			String[] selectionArgs = {name};
			Cursor c = qb.query(db, columns, selection, selectionArgs, null, null, null);
			
			if (c.moveToFirst()) {
				System.out.println("XXXXXXXXXXXXXXXXXX");
				String data = c.getString(0);
				System.out.println(data);
				if (data.equals("true")) {
					c.close();
					return true;
				}
				c.close();
				return false;
			} else {
				return false;
			}		
	}
	
	public Cursor findItemNames(String table) {
		try {
			SQLiteDatabase db = getReadableDatabase();
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(table);
			
			String[] columns = {"item"};
			
			Cursor c = qb.query(db, columns, null, null, null, null, null, null);
			if (c.moveToFirst()) {
				return c;
			} else {
				return null;
			}
		} catch (SQLiteException e) {
			System.err.println(e.getMessage());
			return null;
		}
	}
	
	public boolean removeAllBut(String table, ArrayList<String> ingredients) {
		boolean success = false;
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(table);
		
		ArrayList<String> result = new ArrayList<String>();
		Cursor currentItems = findItemNames(table);
		if (currentItems != null) {
			while(!currentItems.isAfterLast()) {
				String item = currentItems.getString(0);
				result.add(item);
				currentItems.moveToNext();
			}
			currentItems.close();
		}
		for (String item : result) {
			if (!ingredients.contains(item)) {
				success = remove(table, item);
			}
		}
		return success;
	}
	
	public boolean clear(String table) {
		try {
			SQLiteDatabase db = getWritableDatabase();
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(table);
			
			int val = db.delete(table, null, null);			
			if (val != 0) {
				return true;
			} else {
				return false;
			}
		} catch(SQLiteException e) {
			System.err.println(e.getMessage());
			return false;
		}
	}
}