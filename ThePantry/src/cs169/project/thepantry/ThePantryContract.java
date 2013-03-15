package cs169.project.thepantry;

import android.provider.BaseColumns;

/** Contract/Schema class for our database table entries. */
public class ThePantryContract {
	
	/** Column names that are central to every table */
	public static final String ITEM = "item";
	public static final String TYPE = "type";
	public static final String AMOUNT = "amount";
	public static final String CHECKED = "checked";
	public static final String COOKED = "cooked";
	public static final String FAVORITE = "favorite";
	
	/** Private constructor method to make class static. */
	private ThePantryContract() {}
	 
	/** Need to figure out if this is chill -- may need specific id for each table*/
	public static final String ID = "_id";
	
	/** Inner class that defines the Ingredients table */
	public static abstract class Ingredients implements BaseColumns {
	    public static final String TABLE_NAME = "ingredients";
	}
	
	/** Inner class that defines the Inventory table */
	public static abstract class Inventory implements BaseColumns {
	    public static final String TABLE_NAME = "inventory";
	}
	
	/** Inner class that defines the Shopping List table */
	public static abstract class ShoppingList implements BaseColumns {
	    public static final String TABLE_NAME = "shoppinglist";
	}
	/** Inner class that defines the Shopping List table */
	public static abstract class Recipe implements BaseColumns {
	    public static final String TABLE_NAME = "recipe";
	}
}