package cs169.project.thepantry;

import android.provider.BaseColumns;

/** Contract/Schema class for our database table entries. */
public class ThePantryContract {
	
	/** Private constructor method to make class static. */
	private ThePantryContract() {}
	 
	/** Need to figure out if this is chill -- may need specific id for each table*/
	public static final String ID = "_id";
	
	/** Inner class that defines the Ingredients table */
	public static abstract class Ingredients implements BaseColumns {
	    public static final String TABLE_NAME = "ingredients";
	    public static final String ITEM = "ingredients_item";
	    public static final String TYPE = "ingredients_type";
	    public static final String AMOUNT = "ingredients_amount";
	    public static final String CHECKED = "ingredients_checked";
	}
	
	/** Inner class that defines the Inventory table */
	public static abstract class Inventory implements BaseColumns {
	    public static final String TABLE_NAME = "inventory";
	    public static final String ITEM = "inventory_item";
	    public static final String TYPE = "inventory_type";
	    public static final String AMOUNT = "inventory_amount";
	}
	
	/** Inner class that defines the Shopping List table */
	public static abstract class ShoppingList implements BaseColumns {
	    public static final String TABLE_NAME = "shoppinglist";
	    public static final String ITEM = "shoppinglist_item";
	    public static final String TYPE = "shoppinglist_type";
	    public static final String AMOUNT = "shoppinglist_amount";
	    public static final String CHECKED = "shoppinglist_checked";
	}
}