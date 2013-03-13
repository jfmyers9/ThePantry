package cs169.project.thepantry;

import java.util.List;

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
		//Should use the two commands below to make queries to the db
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		ContentValues values = new ContentValues();
		values.put(table + "_item", item);
		values.put(table + "_type", type);
		values.put(table + "_amount", amount);
		if (table.equals("shoppinglist") || table.equals("inventory")) {
			values.put(table + "_checked", false);
		}
		long newRowId;
		// TODO - consider using insertOrThrow and add try/catch block?
		
		/*
		newRowId = writeDatabase.insert(table, null, values);
		if (newRowId != -1) {
			return true;
		} else {
			return false;
		}*/
		return true;
	}

	/** Removes the ITEM from the specified TABLE.
	 * Returns true if the modification was successful, false otherwise.
	 */
	public boolean remove(String table, String item) {
		String selection = table + "_item = ?";
		String[] selectionArgs = {item};
		//int val = writeDatabase.delete(table, selection, selectionArgs);
		// TODO - do a check on val to determine success
		return true;
	}

	/** Returns all items from the specified TABLE. */
	public List<String> findAllItems(String table) {
		//Cursor cursor = readDatabase.query(table, null, null, null, null, null, null);
		// TODO - parse through the cursor object to return a List<String>?
		return null;
	}

	/** Returns one item from the specified TABLE. */
	public String findItem(String table, String item) {
		// Not quite sure what this is supposed to do
		// Right now hypothetically returns type, amount, and checked of the item -Amy
		String selection = table + "_item = ?";
		String[] selectionArgs = {item};
		//Cursor cursor = readDatabase.query(table, null, selection, selectionArgs, null, null, null);
		// TODO - parse the cursor into a String
		return null;
	}

	/** Returns all items of the TYPE from the specified TABLE. */
	public List<String> findTypeItems(String table, String type) {
		String[] columns = {table + "_item"};
		String selection = table + "_type = ?";
		String[] selectionArgs = {type};
		//Cursor cursor = readDatabase.query(table, columns, selection, selectionArgs, null, null, null);
		// TODO - parse cursor into a List<String>
		return null;
	}

	/** Returns the type of the ITEM from the specified TABLE. */
	public String findType(String table, String item) {
		String[] columns = {table + "_type"};
		String selection = table + "_item = ?";
		String[] selectionArgs = {item};
		//Cursor cursor = readDatabase.query(table, columns, selection, selectionArgs, null, null, null);
		// TODO - parse cursor into a String
		return null;
	}

	/** Returns the amount of the ITEM from the specified TABLE. */
	public String findAmount(String table, String item) {
		String[] columns = {table + "_amount"};
		String selection = table + "_item = ?";
		String[] selectionArgs = {item};
		//Cursor cursor = readDatabase.query(table, columns, selection, selectionArgs, null, null, null);
		// TODO - parse cursor into a String
		return null;
	}

	/** Returns all types from the specified TABLE. */
	public List<String> findAllTypes(String table) {
		String[] columns = {table + "_type"};
		//Cursor cursor = readDatabase.query(true, table, columns, null, null, null, null, null, null);
		// TODO - parse cursor into a List<String>
		return null;
	}

	// TODO -- Figure out if we can use android's UI to reference checked items instead of database
	// TODO -- Figure out way to extract an entire entry and add it to a new table

	/** Finds the current value of checked for the ITEM from the
	 *  specified TABLE and sets it to the opposite.
	 *  Returns true on success, false otherwise. */
	public boolean checked(String table, String item) {
		String[] columns = {table + "_checked"};
		String selection = table + "_item = ?";
		String[] selectionArgs = {item};
		//Cursor cursor = readDatabase.query(table, columns, selection, selectionArgs, null, null, null);
		// TODO - parse cursor to extract checkval
		boolean checkval = false;
		boolean newval = !checkval;
		ContentValues values = new ContentValues();
		values.put(table + "_checked", newval);
		//int rows = writeDatabase.update(table, values, selection, selectionArgs);
		// rows is the number of rows updated, should only affect one
		/*
		if (rows == 1) {
			return true;
		} else {
			return false;
		}*/
		return true;
	}

	/** Returns list of all items (preferably entries) checked */
	public List<String> checkedItems(String table) {
		String[] columns = {table + "_item"}; //for entries, omit this line
		String selection = table + "_checked = ?";
		String[] selectionArgs = {"true"}; //not quite right...
		// if we want the entry, can replace columns with null?
		//Cursor cursor = readDatabase.query(table, columns, selection, selectionArgs, null, null, null);
		// TODO - parse cursor into a List<String>
		return null;
	}
}