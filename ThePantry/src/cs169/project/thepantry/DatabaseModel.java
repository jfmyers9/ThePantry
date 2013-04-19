package cs169.project.thepantry;


import java.util.ArrayList;
import java.util.Arrays;

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
	public boolean addIngredient(String table, String item, String type, String amount) {
		try {
			ContentValues values = new ContentValues();

			item = item.toLowerCase().trim();
			type = type.toLowerCase().trim();
			values.put(ThePantryContract.ITEM, item);
			values.put(ThePantryContract.TYPE, type);
			values.put(ThePantryContract.AMOUNT, amount);
			if (table.equals(ThePantryContract.ShoppingList.TABLE_NAME)
					|| table.equals(ThePantryContract.Inventory.TABLE_NAME)) {
				values.put(ThePantryContract.ADDFLAG, "true");
				values.put(ThePantryContract.REMOVEFLAG, "false");
			} if(!table.equals(ThePantryContract.Inventory.TABLE_NAME)){
				values.put(ThePantryContract.CHECKED, "false");
			}
			return addToDatabase(table, item, values);
		} catch (SQLiteException e) {
			System.err.println("1");
			System.err.println(e.getMessage());
			return false;
		} //Make this ThePantryException, change later
	}


	/**
	 * Adds a given storage to the database
	 * @param table specifies the table to add storage to
	 * @param storage a recipe object
	 * @return boolean signifies successful database insert
	 */
	public boolean addStorage(String table, Storage storage) {
		String id = storage.id;
		ContentValues values = makeStorageValue(table, storage);
		
		try {
			addToDatabase(table, id, values);
		} catch (SQLiteException e) {
			System.err.println("2");
			System.err.println(e.getMessage());
			return false;
		} //also catch ThePantryException
		return true;
	}

	/** Adds either a recipe, ingredient, or searchMatch to the given table.
	 * @param table Database Table
	 * @param query Recipe ID or an Item
	 * @param values Information to be added as a row in the table
	 * @return true or false based on success of addition
	 */
	public boolean addToDatabase(String table, String query, ContentValues values) {
		SQLiteDatabase db = getWritableDatabase();
		long newRowId;
		if (!findItem(table, query)) {
			try {
				newRowId = db.insertOrThrow(table, null, values);
				if (newRowId != -1) {
					return true;
				}
			} catch (SQLiteException e) {
				//throw ThePantryException
				System.err.println("3");
			}
			return false;
		} else {
			if(table != Ingredients.TABLE_NAME) {	
				if(isItemChecked(table, query, ThePantryContract.REMOVEFLAG)){
					check(table, query, ThePantryContract.ADDFLAG, true);
					check(table, query, ThePantryContract.REMOVEFLAG, false);
					return true;
				}
			}
			// increment amount
			// add a popup to ask if they want amount to be incremented?
			return true;
		}
	}

	/** Sets the check value of the item to whatever checked it
	 *  @param query is either an item or a recipe id. */
	public boolean check(String table, String query, String col, boolean checked) {
		try {
			SQLiteDatabase db = getWritableDatabase();
			String selection;
			if (table.equals(ThePantryContract.Recipe.TABLE_NAME) || table.equals(ThePantryContract.CookBook.TABLE_NAME)) {
				selection = ThePantryContract.Recipe.ID + " = ?";
				check(ThePantryContract.SearchMatch.TABLE_NAME, query, col, checked);
				// if we want to check the SearchMatch, we can add it in here later
			} else {
				selection = ThePantryContract.ITEM + " = ?";	
			}
			String[] selectionArgs = {query};
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
			System.err.println("4");
			System.err.println(e.getMessage());
			return false;
		}
	}

	/** Returns list of all items checked */
	public ArrayList<IngredientChild> checkedItems(String table, String select) {
		try {
			SQLiteDatabase db = getReadableDatabase();

			String selection = select + " = ?";
			String[] selectionArgs = {"true"};

			// CursorToObject -> ObjectType == ??
			Cursor cursor = queryToCursor(db, false, table, null, selection, selectionArgs);
			return (ArrayList<IngredientChild>)cursorToObject(cursor, table, ThePantryContract.CHILDLIST);
		} catch (SQLiteException e) {
			System.err.println("5");
			System.err.println(e.getMessage());
			return null;
		}
	}

	public ArrayList<String> cursorToStringList(Cursor cursor) {
		ArrayList<String> result = new ArrayList<String>();
		if (cursor != null) {
			while (!cursor.isAfterLast()) {
				result.add(cursor.getString(0));
				cursor.moveToNext();
			}
			cursor.close();
		}
		return result;
	}

	public boolean clear(String table) {
		try {
			SQLiteDatabase db = getWritableDatabase();

			int val = db.delete(table, null, null);			
			if (val != 0) {
				return true;
			} else {
				return false;
			}
		} catch(SQLiteException e) {
			System.err.println("6");
			System.err.println(e.getMessage());
			return false;
		}
	}
	
	public String cursorToAmount(Cursor cursor) {
		String amount = ThePantryContract.DEFAULTAMOUNT;
		if (cursor != null) {
			amount = cursor.getString(0);
		}
		cursor.close();
		return amount;
	}
	
	public Object cursorToObject(Cursor cursor, String table, String objectType) {
		Object result = new Object();
		int[] indices = setIndices(table);

		if (objectType.equals(ThePantryContract.STORAGELIST) || objectType.equals(ThePantryContract.STORAGE)) {
			if (table.equals(ThePantryContract.Recipe.TABLE_NAME) || table.equals(ThePantryContract.CookBook.TABLE_NAME)) {
				ArrayList<Storage> recipes = makeRecipe(cursor, table);
				result = recipes;
			} else {
				ArrayList<Storage> searchMatches = makeSearchMatch(cursor);
				result = searchMatches;
			}
		} if (objectType.equals(ThePantryContract.STORAGE)) {
			result = ((ArrayList<Storage>)result).get(0);
		} else if (objectType.equals(ThePantryContract.CHILDLIST)) {
			result = makeIngredientChildren(cursor, indices, table);
			// can also add case for just one IngredientChild later if we need to 
		} else if (objectType.equals(ThePantryContract.GROUPLIST)) {
			result = makeIngredientGroups(cursor, indices);
		} else if (objectType.equals(ThePantryContract.GROUP)) {
			result = makeIngredientGroups(cursor, indices).get(0);
		} else if (objectType.equals(ThePantryContract.STRINGLIST)) {
			result = cursorToStringList(cursor);
		} else if (objectType.equals(ThePantryContract.AMOUNTVAL)) {
			result = cursorToAmount(cursor);
		}
		return result;
	}

	/** Returns all items from the specified TABLE, null if none can be found.
	 * If a SQLiteException is thrown, returns null and prints out the error
	 * message to System.err (could be due to no table or no column). */
	public ArrayList<IngredientChild> findAllItems(String table) {
		try {
			SQLiteDatabase db = getReadableDatabase();

			String selection;
			String[] selectionArgs = new String[1];
			if(table == Ingredients.TABLE_NAME) {
				selection = null;
				selectionArgs = null;
			} else {
				selection = ThePantryContract.REMOVEFLAG + " = ?";
				selectionArgs[0] = "false";
			}

			Cursor cursor = queryToCursor(db, false, table, null, selection, selectionArgs);
			return ((ArrayList<IngredientChild>)cursorToObject(cursor, table, ThePantryContract.CHILDLIST));
		} catch (SQLiteException e) {
			System.err.println("7");
			System.err.println(e.getMessage());
			return null;
		}
	}

	/** Returns all types from the specified TABLE. */
	public ArrayList<IngredientGroup> findAllTypes(String table) {
		try {
			SQLiteDatabase db = getReadableDatabase();

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

			Cursor cursor = queryToCursor(db, true, table, columns, selection, selectionArgs);
			return ((ArrayList<IngredientGroup>)cursorToObject(cursor, table, ThePantryContract.GROUPLIST));
			
		} catch (SQLiteException e) {
			System.err.println("8");
			System.err.println(e.getMessage());
			return null;
		}
	}

	/** Returns the amount of the ITEM from the specified TABLE. */
	public String findAmount(String table, String item) {
		try {
			SQLiteDatabase db = getReadableDatabase();

			String[] columns = {ThePantryContract.AMOUNT};
			String selection = ThePantryContract.ITEM + " = ?" + " AND " + ThePantryContract.REMOVEFLAG + " = ?";
			item = item.toLowerCase().trim();
			String[] selectionArgs = {item, "false"};

			Cursor cursor = queryToCursor(db, false, table, columns, selection, selectionArgs);
			return ((String)cursorToObject(cursor, table, ThePantryContract.AMOUNTVAL));
		} catch (SQLiteException e) {
			System.err.println("9");
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

			String selection;
			if (table.equals(ThePantryContract.Recipe.TABLE_NAME) || table.equals(ThePantryContract.SearchMatch.TABLE_NAME) || table.equals(ThePantryContract.CookBook.TABLE_NAME)) {
				selection = ThePantryContract.Recipe.ID + " = ?";
			} else {
				item = item.toLowerCase().trim();
				selection = ThePantryContract.ITEM + " = ?";
			}
			String[] selectionArgs = {item};

			Cursor c = queryToCursor(db, false, table, null, selection, selectionArgs);
			if(c != null) {
				return true;
			}else {
				return false;
			}
		} catch (SQLiteException e) {
			System.err.println("10");
			System.err.println(e.getMessage());
			return false;
		}
	}

	public ArrayList<String> findItemNames(String table) {
		try {
			SQLiteDatabase db = getReadableDatabase();

			String[] columns = {"item"};

			Cursor cursor = queryToCursor(db, false, table, columns, null, null);
			return ((ArrayList<String>)cursorToObject(cursor, table, ThePantryContract.STRINGLIST));
		} catch (SQLiteException e) {
			System.err.println("11");
			System.err.println(e.getMessage());
			return null;
		}
	}

	/** Returns the type of the ITEM from the specified TABLE. */
	public IngredientGroup findType(String table, String item) {
		try {
			SQLiteDatabase db = getReadableDatabase();

			String[] columns = {ThePantryContract.TYPE};
			String selection = ThePantryContract.ITEM + " = ?" + " AND " + ThePantryContract.REMOVEFLAG + " = ?";
			item = item.toLowerCase().trim();
			String[] selectionArgs = {item, "false"};

			Cursor cursor = queryToCursor(db, false, table, columns, selection, selectionArgs);
			return ((IngredientGroup)cursorToObject(cursor, table, ThePantryContract.GROUP));
		} catch (SQLiteException e) {
			System.err.println("12");
			System.err.println(e.getMessage());
			return null;
		}
	}

	/** Returns all items of the TYPE from the specified TABLE.
	 *  If a SQLiteException is thrown, returns false and prints out the error
	 *  message to System.err (could be due to no table or no column). */
	public ArrayList<IngredientChild> findTypeItems(String table, String type) {
		try {
			SQLiteDatabase db = getReadableDatabase();

			String selection;
			type = type.toLowerCase().trim();
			ArrayList<String> selectionArgsList = new ArrayList<String>();
			if(table == Ingredients.TABLE_NAME) {
				selection = ThePantryContract.TYPE + " = ?";
				selectionArgsList.add(type);
			} else {
				selection = ThePantryContract.TYPE + " = ?" + " AND " + ThePantryContract.REMOVEFLAG + " = ?";
				selectionArgsList.add(type);
				selectionArgsList.add("false");
			}
			String[] selectionArgs = selectionArgsList.toArray(new String[0]);

			Cursor cursor = queryToCursor(db, false, table, null, selection, selectionArgs);
			return ((ArrayList<IngredientChild>)cursorToObject(cursor, table, ThePantryContract.CHILDLIST));
		} catch (SQLiteException e) {
			System.err.println("13");
			System.err.println(e.getMessage());
			return null;
		}
	}

	public ArrayList<Recipe> getAllRecipes(String table) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = queryToCursor(db, false, table, null, null, null);
		return ((ArrayList<Recipe>)cursorToObject(cursor, table, ThePantryContract.STORAGELIST));
	}

	/**
	 * Returns all cooked or favorited recipes in recipe table
	 * Will need to cast to specific type when object is retrieved
	 * @param column -- cooked or favorited
	 * @return ArrayList of recipes
	 */
	public ArrayList<Storage> getCookOrFav(String table, String column) {
		SQLiteDatabase db = getReadableDatabase();

		String selection = column + " = ?";
		String[] selectionArgs = {"true"};
		Cursor cursor = queryToCursor(db, false, table, null, selection, selectionArgs);
		return (ArrayList<Storage>)cursorToObject(cursor, table, ThePantryContract.STORAGELIST);
	}

	/**
	 * Finds the recipe with the associated ID
	 * @param id -- ID of a specific recipe
	 * @return Recipe object
	 */
	public Storage getStorage(String table, String id) {
		SQLiteDatabase db = getReadableDatabase();

		String selection = ThePantryContract.Recipe.ID + " = ?";
		String[] selectionArgs = {id};

		Cursor cursor = queryToCursor(db, false, table, null, selection, selectionArgs);
		return (Storage)cursorToObject(cursor, table, ThePantryContract.STORAGE);
	}

	/** Returns false if item is not checked and true if item is checked for
	 * favorited and cooked recipe
	 */
	public boolean isItemChecked(String table, String name, String col) {
		SQLiteDatabase db = getReadableDatabase();

		String[] columns = {col};
		String selection;
		if (table == ThePantryContract.Recipe.TABLE_NAME) {
			selection = ThePantryContract.Recipe.RECIPE + " = ?";
		} else {
			name = name.toLowerCase().trim();
			selection = ThePantryContract.ITEM + " = ?";
		}
		String[] selectionArgs = {name};
		return queryToChecked(db, false, table, columns, selection, selectionArgs);
		
	}
	
	public boolean queryToChecked(SQLiteDatabase db, boolean distinct, String table,
								  String[] columns, String selection, String[] selectionArgs) {
		Cursor cursor = db.query(distinct, table, columns, selection, selectionArgs, null, null, null, null);
		if (cursor.moveToFirst()) {
			String data = cursor.getString(0);
			if (data.equals("true")) {
				cursor.close();
				return true;
			}
			cursor.close();
			return false;
		} else {
			return false;
		}		
	}

	/** Creates an ArrayList<IngredientChild> from a cursor and specifies indices 
	 * @param table TODO*/
	public ArrayList<IngredientChild> makeIngredientChildren(Cursor cursor, int[] indices, String table) {
		ArrayList<IngredientChild> children = new ArrayList<IngredientChild>();
		if (cursor != null) {
			while(!cursor.isAfterLast()){
				String data = cursor.getString(indices[0]);
				String type = cursor.getString(indices[1]);
				IngredientChild child = new IngredientChild(data,type);
				if(table != Ingredients.TABLE_NAME) {
					if(!isItemChecked(table, child.getName(), ThePantryContract.REMOVEFLAG)) {
						children.add(child);
					}
				} else {
					children.add(child);
				}
				cursor.moveToNext();
			}
			cursor.close();
		}
		return children;
	}

	/** Creates an ArrayList<IngredientGroup> from a cursor and specifies indices */
	public ArrayList<IngredientGroup> makeIngredientGroups(Cursor cursor, int[] indices) {
		ArrayList<IngredientGroup> groups = new ArrayList<IngredientGroup>();
		if (cursor != null) {
			while(!cursor.isAfterLast()){
				//Might need indices
				String data = cursor.getString(0);
				groups.add(new IngredientGroup(data, new ArrayList<IngredientChild>()));
				cursor.moveToNext();
			}
			cursor.close();
		}
		return groups;
	}

	/** Parses the given cursor into a Recipe object
	 * @param table TODO
	 */
	public ArrayList<Storage> makeRecipe(Cursor cursor, String table) {
		ArrayList<Storage> recipes = new ArrayList<Storage>();

		if (cursor != null) {
			while(!cursor.isAfterLast()){
				
				if (table.equals(ThePantryContract.CookBook.TABLE_NAME)) {
					Recipe recipe = new Recipe();
					recipe.name = cursor.getString(ThePantryContract.CookBook.RECIPEIND);
					recipe.id = cursor.getString(ThePantryContract.CookBook.IDIND);

					ArrayList<String> ingredientLines = new ArrayList<String>(Arrays.asList(cursor.getString(ThePantryContract.CookBook.INGLINESIND).split(ThePantryContract.SEPERATOR)));
					recipe.ingredientLines = ingredientLines;

					String[] imgArray = cursor.getString(ThePantryContract.CookBook.IMGIND).split(ThePantryContract.SEPERATOR);
					RecipeImages img;
					if (imgArray.length == 1) {
						img = new RecipeImages(imgArray[0], imgArray[0]);
					} else {
						img = new RecipeImages(imgArray[0], imgArray[1]);
					}
					recipe.images = img;
					
					recipes.add(recipe);

					cursor.moveToNext();
					
				} else if (table.equals(ThePantryContract.Recipe.TABLE_NAME)) {
				
					Recipe recipe = new Recipe();
					recipe.name = cursor.getString(ThePantryContract.Recipe.RECIPEIND);
					recipe.id = cursor.getString(ThePantryContract.Recipe.IDIND);
	
					ArrayList<String> ingredientLines = new ArrayList<String>(Arrays.asList(cursor.getString(ThePantryContract.Recipe.INGLINESIND).split(ThePantryContract.SEPERATOR)));
					recipe.ingredientLines = ingredientLines;
	
					String[] imgArray = cursor.getString(ThePantryContract.Recipe.IMGIND).split(ThePantryContract.SEPERATOR);
					RecipeImages img;
					if (imgArray.length == 1) {
						img = new RecipeImages(imgArray[0], imgArray[0]);
					} else {
						img = new RecipeImages(imgArray[0], imgArray[1]);
					}
					recipe.images = img;
	
					if (table.equals(ThePantryContract.Recipe.TABLE_NAME)) {
						String[] srcArray = cursor.getString(ThePantryContract.Recipe.SOURCEIND).split(ThePantryContract.SEPERATOR);
						RecipeSource src = new RecipeSource(srcArray[0], srcArray[1], srcArray[2]);
						recipe.source = src;
	
						String[] attArray = cursor.getString(ThePantryContract.Recipe.ATTIND).split(ThePantryContract.SEPERATOR);
						Attribution att = new Attribution(attArray[0], attArray[1], attArray[2]);
						recipe.attribution = att;
					}
	
					recipes.add(recipe);
	
					cursor.moveToNext();
				}
			}
			cursor.close();
		}
		return recipes;
	}

	/**
	 * Parses the given cursor into a SearchMatch object
	 */
	public ArrayList<Storage> makeSearchMatch(Cursor cursor) {
		ArrayList<Storage> searchMatches = new ArrayList<Storage>();

		if (cursor != null) {
			while(!cursor.isAfterLast()){
				SearchMatch searchMatch = new SearchMatch();

				searchMatch.name = cursor.getString(ThePantryContract.SearchMatch.RECIPEIND);
				searchMatch.id = cursor.getString(ThePantryContract.SearchMatch.IDIND);
				searchMatch.smallImageUrl = cursor.getString(ThePantryContract.SearchMatch.IMGIND);
				searchMatch.sourceDisplayName = cursor.getString(ThePantryContract.SearchMatch.SOURCEIND);

				ArrayList<String> ingredients = new ArrayList<String>(Arrays.asList(cursor.getString(ThePantryContract.SearchMatch.INGREDIENTSIND)
						.split(ThePantryContract.SEPERATOR)));
				searchMatch.ingredients = ingredients;
			}
			cursor.close();
		}
		return searchMatches;
	}

	public ContentValues makeStorageValue(String table, Storage storage) {
		String id = storage.id;
		String name = storage.name;
		ContentValues values = new ContentValues();
		values.put(ThePantryContract.Storage.ID, id);
		values.put(ThePantryContract.Storage.RECIPE, name);
		values.put(ThePantryContract.Storage.FAVORITE, "false");
		values.put(ThePantryContract.Storage.COOKED, "false");
		values.put(ThePantryContract.ADDFLAG, "true");
		values.put(ThePantryContract.REMOVEFLAG, "false");

		if (table.equals(ThePantryContract.Recipe.TABLE_NAME) || table.equals(ThePantryContract.CookBook.TABLE_NAME)) {


			String ingredientLines = "";
			for (String ingredient : ((Recipe) storage).ingredientLines) {
				if (!ingredientLines.equals("")) {
					ingredientLines += ThePantryContract.SEPERATOR;
				}
				ingredientLines += ingredient;
			}

			String image = "";
			if (((Recipe) storage).images.hostedLargeUrl != null) {
				image = ((Recipe) storage).images.hostedLargeUrl;
				if (((Recipe) storage).images.hostedSmallUrl != null) {
					image += ThePantryContract.SEPERATOR;
				}
			} if (((Recipe) storage).images.hostedSmallUrl != null) {
				image += ((Recipe) storage).images.hostedSmallUrl; 
			}

			// Recipes don't have directionLines yet so cooked and favorite functionalities will fail
			String directions = "";
			for (String direction : ((Recipe) storage).directionLines) {
				if (!directions.equals("")) {
					directions += ThePantryContract.SEPERATOR;
				}
				directions += direction;
			}

			if (table.equals(ThePantryContract.Recipe.TABLE_NAME)) {
				String source = ((Recipe) storage).source.sourceDisplayName + ThePantryContract.SEPERATOR
						+ ((Recipe) storage).source.sourceRecipeUrl + ThePantryContract.SEPERATOR
						+ ((Recipe) storage).source.sourceSiteUrl;
				String attribution = ((Recipe) storage).attribution.url + ThePantryContract.SEPERATOR
						+ ((Recipe) storage).attribution.text 
						+ ThePantryContract.SEPERATOR + ((Recipe) storage).attribution.logo;
				values.put(ThePantryContract.Recipe.ATTRIBUTE, attribution);
				values.put(ThePantryContract.Recipe.SOURCE, source);
			}

			values.put(ThePantryContract.Recipe.IMAGE, image);
			values.put(ThePantryContract.Recipe.INGLINES, ingredientLines);
			values.put(ThePantryContract.Recipe.DIRECTIONS, directions);
		} else {
			String image = ((SearchMatch) storage).smallImageUrl;
			String source = ((SearchMatch) storage).sourceDisplayName;

			String ingredients = "";
			for (String ingredient : ((SearchMatch) storage).ingredients) {
				if (!ingredients.equals("")) {
					ingredients += ThePantryContract.SEPERATOR;
				}
				ingredients += ingredient;
			}

			values.put(ThePantryContract.SearchMatch.INGREDIENTS, ingredients);
			values.put(ThePantryContract.SearchMatch.IMAGEURL, image);
			values.put(ThePantryContract.SearchMatch.SOURCENAME, source);
		}
		return values;
	}

	/** Queries the given table following COLUMNS, SELECTION, SELECTIONARGS
	 * @param distinct TODO
	 * @return a Cursor object or null if it failed */
	public Cursor queryToCursor(SQLiteDatabase db, boolean distinct, String table,
			String[] columns, String selection, String[] selectionArgs) {
		Cursor c = db.query(distinct, table, columns, selection, selectionArgs, null, null, null, null);
		if (c.moveToFirst()) {
			return c;
		} else {
			return null;
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
			item = item.toLowerCase().trim();
			String[] selectionArgs = {item};

			int val = db.delete(table, selection, selectionArgs);			
			if (val != 0) {
				return true;
			} else {
				return false;
			}
		} catch (SQLiteException e) {
			System.err.println("15");
			System.err.println(e.getMessage());
			return false;
		}
	}

	/** Finds all items in the specified TABLE that contain given text
	 * If a SQLiteException is thrown, returns null and prints out the error
	 * message to System.err (could be due to no table or no column). */
	public ArrayList<IngredientChild> search(String table, String query) {
		try {
			SQLiteDatabase db = getReadableDatabase();

			String selection;
			ArrayList<String> selectionArgsList = new ArrayList<String>();
			query = query.toLowerCase().trim();
			if(table == Ingredients.TABLE_NAME) {
				selection = ThePantryContract.ITEM + " LIKE ?";
				selectionArgsList.add("%"+query+"%");
			} else {
				selection = ThePantryContract.ITEM + " LIKE ?" + " AND " + ThePantryContract.REMOVEFLAG + " = ?";
				selectionArgsList.add("%"+query+"%");
				selectionArgsList.add("false");
			}
			String[] selectionArgs = selectionArgsList.toArray(new String[0]);

			Cursor cursor = queryToCursor(db, false, table, null, selection, selectionArgs);
			return (ArrayList<IngredientChild>)cursorToObject(cursor, table, ThePantryContract.CHILDLIST);
			
		} catch (SQLiteException e) {
			System.err.println("16");
			System.err.println(e.getMessage());
			return null;
		}
	}

	/** Sets the item and type indices for cursorToObject method*/
	public int[] setIndices(String table) {
		int[] indices = new int[2];

		if (table.equals(ThePantryContract.Ingredients.TABLE_NAME)) {
			indices[0] = ThePantryContract.Ingredients.ITEMIND;
			indices[1] = ThePantryContract.Ingredients.TYPEIND;
		} else if (table.equals(ThePantryContract.Ingredients.TABLE_NAME)) {
			indices[0] = ThePantryContract.Inventory.ITEMIND;
			indices[1] = ThePantryContract.Inventory.TYPEIND;
		} else {
			indices[0] = ThePantryContract.ShoppingList.ITEMIND;
			indices[1] = ThePantryContract.ShoppingList.TYPEIND;
		}
		return indices;
	}

}