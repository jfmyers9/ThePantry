package cs169.project.thepantry;

import android.provider.BaseColumns;

/** Contract/Schema class for our database table entries. */
public class ThePantryContract {
	
	/** The name of the database */
    public static final String DATABASE_NAME = "thepantry";
	
	/** Column names that are central to every table (but recipes) */
	public static final String ITEM = "item";
	public static final String TYPE = "type";
	public static final String AMOUNT = "amount";
	public static final String ADDFLAG = "addFlag";
	public static final String REMOVEFLAG = "removeFlag";
	public static final String CHECKED = "checked";
	
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
	    
	    // DATABASE COLUMN INDEXS
	    public static final int REMOVEFLAGIND = 0;
	    public static final int ADDFLAGIND = 1;
	    public static final int ITEMIND = 2;
	    public static final int TYPEIND = 3;
	    public static final int AMOUNTIND = 4;
	}
	
	/** Inner class that defines the Shopping List table */
	public static abstract class ShoppingList implements BaseColumns {
	    public static final String TABLE_NAME = "shoppinglist";
	    
	 // DATABASE COLUMN INDEXS
	    public static final int CHECKEDIND = 0;
	    public static final int REMOVEFLAGIND = 1;
	    public static final int ITEMIND = 2;
	    public static final int TYPEIND = 3;
	    public static final int AMOUNTIND = 4;
	    public static final int ADDFLAGIND = 5;
	}
	/** Inner class that defines the Shopping List table */
	public static abstract class Recipe implements BaseColumns {
	    public static final String TABLE_NAME = "recipes";
	    // DATABASE COLUMN NAMES
	    public static final String RECIPE = "recipe";
	    public static final String ID = "id";
	    public static final String INGLINES = "ingredientLines";
	    public static final String IMAGE = "image";
	    public static final String ATTRIBUTE = "attribute";
	    public static final String SOURCE = "source";
	    public static final String COOKED = "cooked";
		public static final String FAVORITE = "favorite";	
		
		// DATABASE COLUMN INDEXS
		public static final int RECIPEIND = 0;
		public static final int IDIND = 1;
		public static final int INGLINESIND = 2;
		public static final int IMGIND = 3;
		public static final int ATTIND = 4;
		public static final int SOURCEIND = 5;
		public static final int COOKEDIND = 6;
		public static final int FAVORITEIND = 7;
	}
}