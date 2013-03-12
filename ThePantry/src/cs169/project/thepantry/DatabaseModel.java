package cs169.project.thepantry;

import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

/** Interface class called by the activities to interact with our database.
 *  Uses the DatabaseModelHelper class to create the database.
 * @author amyzhang
 */
public class DatabaseModel {

	// incomplete but this is essentially the helper class for this model
	// should it be a class variable?
	 //private (static final) DatabaseModelHelper dbModel = new DatabaseModelHelper();
	 //private (static final) SQLiteDatabase readDatabase = dbModel.getReadableDatabase();
	 //private (static final) SQLiteDatabase writeDatabase = dbModel.getWriteableDatabase();
	/** Constructor for the DatabaseModel class. */
	private DatabaseModel() { //changed to private to make it static class	
		// could delete this if we don't want to do anything special?
		// can initialize and call DatabaseModelHelper() here
		// also could getWritableDatabase() or getReadableDatabase() here
	}
	
	/** Adds the ITEM, its TYPE and given AMOUNT to the specified TABLE.
	 * Returns true if the modification was successful, false otherwise.
	 */
	public static boolean add(String table, String item, String type, float amount) {
	    ContentValues values = new ContentValues();
	    values.put(dbModel.COLUMN_, comment);
	    long insertId = database.insert(MySQLiteHelper.TABLE_COMMENTS, null,
	        values);
	    Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENTS,
	        allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
	        null, null, null);
	    cursor.moveToFirst();
	    Comment newComment = cursorToComment(cursor);
	    cursor.close();
	    return newComment;
		return true;
	}
	
	/** Removes the ITEM from the specified TABLE.
	 * Returns true if the modification was successful, false otherwise.
	 */
	public static boolean remove(String table, String item) {
		return true;
	}
	
	/** Returns all items from the specified TABLE. */
	public static List<String> findAllItem(String table) {
		return null;
	}
	
	/** Returns one item from the specified TABLE. */
	public static String findItem(String table, String item) {
		return null;
	}
	
	/** Returns all items of the TYPE from the specified TABLE. */
	public static List<String> findTypeItems(String table, String type) {
		return null;
	}
	
	/** Returns the type of the ITEM from the specified TABLE. */
	public static String findType(String table, String item) {
		return null;
	}
	
	/** Returns the amount of the ITEM from the specified TABLE. */
	public String findAmount(String table, String item) {
		return null;
	}
	
	/** Returns all types from the specified TABLE. */
	public static List<String> findAllTypes(String table) {
		return null;
	}
	
	// TODO -- Figure out if we can use android's UI to reference checked items instead of database
	// TODO -- Figure out way to extract an entire entry and add it to a new table
	
	/** Finds the current value of checked for the ITEM from the
	 *  specified TABLE and sets it to the opposite.
	 *  Returns true on success, false otherwise. */
	public static boolean checked(String table, String item) {
		return true;
	}
	
	/** Returns list of all items (preferably entries) checked */
	public static List<String> checkedItems() {
		return null;
	}
}