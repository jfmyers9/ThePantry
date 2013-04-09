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
	//TODO: MAKE IT SO IT IS CASE INSENSITIVE (.toLowerCase().trim())

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
			} if(!table.equals(ThePantryContract.Inventory.TABLE_NAME)){
				values.put(ThePantryContract.CHECKED, "false");
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
				if(table != Ingredients.TABLE_NAME) {	
					if(isItemChecked(table, item, ThePantryContract.REMOVEFLAG)){
						check(table, item, ThePantryContract.ADDFLAG, true);
						check(table, item, ThePantryContract.REMOVEFLAG, false);
						return true;
					}
				}
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
			String selection;
			if (table == ThePantryContract.Recipe.TABLE_NAME) {
				selection = ThePantryContract.Recipe.ID + " = ?";
			} else {
				selection = ThePantryContract.ITEM + " = ?";
			}
			String[] selectionArgs = {item};

			Cursor c = qb.query(db, null, selection, selectionArgs, null, null, null);
			if(c.moveToFirst()) {
				/*
					if(table != Ingredients.TABLE_NAME) {	
						if(isItemChecked(table, item, ThePantryContract.REMOVEFLAG)){
							return false;
						}
					}*/
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

			String selection;
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


	/** Returns list of all items checked */
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
			String data = c.getString(0);
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
		if (currentItems.moveToFirst()) {
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

	/**
	 * Returns all cooked or favorited recipes in recipe table
	 * @param column -- cooked or favorited
	 * @return ArrayList of recipes
	 */
	public ArrayList<Recipe> getCookOrFav(String column) {
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(ThePantryContract.Recipe.TABLE_NAME);

		String selection = column + " = ?";
		String[] selectionArgs = {"true"};
		Cursor cRecipe = qb.query(db, null, selection, selectionArgs, null, null, null, null);

		ArrayList<Recipe> recipes = makeRecipe(cRecipe);
		return recipes;
	}
	
	/**
	 * Finds the recipe with the associated ID
	 * @param id -- ID of a specific recipe
	 * @return Recipe object
	 */
	public Recipe getRecipe(String id) {
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(ThePantryContract.Recipe.TABLE_NAME);

		String selection = ThePantryContract.Recipe.ID + " = ?";
		String[] selectionArgs = {id};
		
		Cursor cRecipe = qb.query(db, null, selection, selectionArgs, null, null, null, null);
		ArrayList<Recipe> recipes = makeRecipe(cRecipe);
		
		Recipe recipe = recipes.get(0);
		return recipe;
	}

	/**
	 * Adds a given recipe to the database
	 * @param recipe -- a recipe object
	 * @return boolean -- signifies successful database insert
	 */
	public boolean addRecipe(Recipe recipe) {
		String id = recipe.id;
		String name = recipe.name;

		String attribution = recipe.attribution.url + ","+ recipe.attribution.text + "," + recipe.attribution.logo;

		String ingredientLines = "";
		for (String ingredient : recipe.ingredientLines) {
			if (!ingredientLines.equals("")) {
				ingredientLines += ",";
			}
			ingredientLines += ingredient;
		}

		String image = recipe.images.hostedLargeUrl + "," + recipe.images.hostedSmallUrl; //not always present
		String source = recipe.source.sourceDisplayName + "," + recipe.source.sourceRecipeUrl + "," + recipe.source.sourceSiteUrl;

		try {
			SQLiteDatabase db = getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(ThePantryContract.Recipe.ID, id);
			values.put(ThePantryContract.Recipe.RECIPE, name);
			values.put(ThePantryContract.Recipe.ATTRIBUTE, attribution);
			values.put(ThePantryContract.Recipe.INGLINES, ingredientLines);
			values.put(ThePantryContract.Recipe.IMAGE, image);
			values.put(ThePantryContract.Recipe.SOURCE, source);

			long newRowId;
			if (!findItem(ThePantryContract.Recipe.TABLE_NAME, recipe.id)) {
				try {
					newRowId = db.insertOrThrow(ThePantryContract.Recipe.TABLE_NAME, null, values);
					if (newRowId != -1) {
						return true;
					}
				} catch (SQLiteException e) {
					//do something
				}
				return false;
			} 
		} catch (SQLiteException e) {
			System.err.println(e.getMessage());
			return false;
		}
		return true;
	}

	private ArrayList<Recipe> makeRecipe(Cursor cursRecipe) {
		ArrayList<Recipe> recipes = new ArrayList<Recipe>();

		if (cursRecipe.moveToFirst()) {
			while(!cursRecipe.isAfterLast()){
				Recipe recipe = new Recipe();
				recipe.name = cursRecipe.getString(ThePantryContract.Recipe.RECIPEIND);
				recipe.id = cursRecipe.getString(ThePantryContract.Recipe.IDIND);

				String[] attArray = cursRecipe.getString(ThePantryContract.Recipe.ATTIND).split(",");
				Attribution att = new Attribution(attArray[0], attArray[1], attArray[2]);
				recipe.attribution = att;

				ArrayList<String> ingredientLines = new ArrayList<String>(Arrays.asList(cursRecipe.getString(ThePantryContract.Recipe.INGLINESIND).split(",")));
				recipe.ingredientLines = ingredientLines;

				String[] imgArray = cursRecipe.getString(ThePantryContract.Recipe.IMGIND).split(",");
				RecipeImages img = new RecipeImages(imgArray[0], imgArray[1]);
				recipe.images = img;

				String[] srcArray = cursRecipe.getString(ThePantryContract.Recipe.SOURCEIND).split(",");
				RecipeSource src = new RecipeSource(srcArray[0], srcArray[1], srcArray[2]);
				recipe.source = src;

				recipes.add(recipe);

				cursRecipe.moveToNext();
			}
		}
		return recipes;
	}
}