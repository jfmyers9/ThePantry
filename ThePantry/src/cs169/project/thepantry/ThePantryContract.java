package cs169.project.thepantry;

import android.provider.BaseColumns;

/** Contract/Schema class for our database table entries. */
public class ThePantryContract {
	
	/** Private constructor method to make class static. */
	private ThePantryContract() {}
	
	/** The name of the database */
    public static final String DATABASE_NAME = "thepantry";
	
	/** Column names that are central to every table (but recipes) */
	public static final String ITEM = "item"; 
	public static final String TYPE = "type";
	public static final String AMOUNT = "amount";
	public static final String ADDFLAG = "addFlag";
	public static final String REMOVEFLAG = "removeFlag";
	public static final String CHECKED = "checked";
	
	/** String used to separate different strings in database
	 * that will be parsed when it is retrieved
	 */
	public static final String SEPERATOR = ";;;;";
	
	public static final String DEFAULTAMOUNT = "1";
	
	/** Need to figure out if this is chill -- may need specific id for each table*/
	public static final String ID = "_id";
	
	public static final String STORAGE = "storage";
	public static final String STORAGELIST = "storagelist";
	public static final String CHILD = "child";
	public static final String CHILDLIST = "childlist";
	public static final String GROUP = "group";
	public static final String GROUPLIST = "grouplist";
	public static final String AMOUNTVAL = "amountval";
	public static final String STRINGLIST = "stringlist";
	
	/** Fragment types */
	public static final int RECENT = 0;
	public static final int FAVORITED = 1;
	public static final int COOKED = 2;
	public static final int RECOMMENDED = 3;
	public static final int RESULTS = 4;
	public static final int USERRESULTS = 5;
	
	/** Inner class that defines the CookBook table */
	public static abstract class CookBook extends Storage implements BaseColumns {
		
		public static final String TABLE_NAME = "cookbook";
	    public static final String INGLINES = "ingredientLines";
	    public static final String IMAGE = "image";
	    
	    // DATABASE COLUMN INDEXS
	    public static final int REMOVEFLAGIND = 0;
	    public static final int ADDFLAGIND = 1;
	    public static final int DIRECTIONSIND = 2;
		public static final int RECIPEIND = 3;
	    public static final int IDIND = 4;
	    public static final int COOKEDIND = 5;
		public static final int FAVORITEIND = 6;	
	    public static final int INGLINESIND = 7;
	    public static final int IMGIND= 8;
	}
	
	/** Inner class that defines the Ingredients table */
	public static abstract class Ingredients implements BaseColumns {
	    public static final String TABLE_NAME = "ingredients";
	    
	    // DATABASE COLUMN INDEXS
	    public static final int ITEMIND = 0;
	    public static final int TYPEIND = 1;
	    public static final int AMOUNTIND = 2;
	    public static final int CHECKEDIND = 3;
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
	
	/** Inner class that defines the Recipe table */
	public static abstract class Recipe extends Storage implements BaseColumns {
	    public static final String TABLE_NAME = "recipes";
	    
	    // DATABASE COLUMN NAMES
	    public static final String INGLINES = "ingredientLines";
	    public static final String IMAGE = "image";
	    public static final String ATTRIBUTE = "attribute";
	    public static final String SOURCE = "source";
	    public static final String DIRECTIONS = "directions";
		
		// DATABASE COLUMN INDEXS
		public static final int RECIPEIND = 0;
		public static final int IDIND = 1;
		public static final int INGLINESIND = 2;
		public static final int IMGIND = 3;
		public static final int ATTIND = 4;
		public static final int SOURCEIND = 5;
		public static final int DIRECTIONSIND = 6;
		public static final int COOKEDIND = 7;
		public static final int FAVORITEIND = 8;
	    public static final int ADDFLAGIND = 9;
	    public static final int REMOVEFLAGIND = 10;
	}
	
	/** Inner class that defines the Search Match table */
	public static abstract class SearchMatch extends Storage implements BaseColumns {
	    public static final String TABLE_NAME = "searchMatch";
	    
	    // DATABASE COLUMN NAMES
	    public static final String INGREDIENTS = "ingredients";
	    public static final String IMAGEURL = "imageUrl";
	    public static final String SOURCENAME = "sourceName";
		
		// DATABASE COLUMN INDEXS
		public static final int RECIPEIND = 0;
		public static final int IDIND = 1;
		public static final int INGREDIENTSIND = 2;
		public static final int IMGIND = 3;
		public static final int SOURCEIND = 5;
		public static final int COOKEDIND = 6;
		public static final int FAVORITEIND = 7;
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
	
	public static abstract class Storage implements BaseColumns {
		public static final String RECIPE = "recipe";
	    public static final String ID = "id";
	    public static final String COOKED = "cooked";
		public static final String FAVORITE = "favorite";	
	}
}