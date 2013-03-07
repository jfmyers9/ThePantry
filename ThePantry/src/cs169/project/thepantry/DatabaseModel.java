package cs169.project.thepantry;

import java.util.List;

/** Interface class called by the activities to interact with our database.
 *  Uses the DatabaseModelHelper class to create the database.
 * @author amyzhang
 */
public class DatabaseModel {

	// incomplete but this is essentially the helper class for this model
	// should it be a class variable?
	//private (static final) DatabaseModelHelper dbModel = new DatabaseModelHelper();
	
	/** Constructor for the DatabaseModel class. */
	public DatabaseModel() {
		// could delete this if we don't want to do anything special?
		// can initialize and call DatabaseModelHelper() here
		// also could getWritableDatabase() or getReadableDatabase() here
	}
	
	/** Adds the ITEM, its TYPE and given AMOUNT to the specified TABLE.
	 * Returns true if the modification was successful, false otherwise.
	 */
	public boolean add(String table, String item, String type, float amount) {
		return true;
	}
	
	/** Removes the ITEM from the specified TABLE.
	 * Returns true if the modification was successful, false otherwise.
	 */
	public boolean remove(String table, String item) {
		return true;
	}
	
	/** Returns all items from the specified TABLE. */
	public List<String> findAllItem(String table) {
		return null;
	}
	
	/** Returns all items of the TYPE from the specified TABLE. */
	public List<String> findTypeItems(String table, String type) {
		return null;
	}
	
	/** Returns the type of the ITEM from the specified TABLE. */
	public String findType(String table, String item) {
		return null;
	}
	
	/** Returns the amount of the ITEM from the specified TABLE. */
	public String findAmount(String table, String item) {
		return null;
	}
	
	/** Returns all types from the specified TABLE. */
	public String findAllTypes(String table) {
		return null;
	}
	
	/** Finds the current value of checked for the ITEM from the
	 *  specified TABLE and sets it to the opposite.
	 *  Returns true on success, false otherwise. */
	public boolean checked(String table, String item) {
		return true;
	}
}