package cs169.project.thepantry;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** Helper class for creating and handling our local database. */
public class DatabaseModelHelper extends SQLiteOpenHelper {
	/** Database constant */
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "thePantry";
	//private static final String CREATE_INVENTORY_TABLE = "SQL code";
	//private static final String CREATE_SHOPPING_LIST_TABLE = "SQL code";
	//private static final String CREATE_INGREDIENTS_TABLE = "SQL code";
	
	
	/** Constructor method */
	public DatabaseModelHelper(Context context, SQLiteDatabase.CursorFactory factory) {
		// might not need factory since we can use default
		// if that's the case, the replace factory with null
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// db.execSQL(CREATE_INVENTORY_TABLE);
		// db.execSQL(CREATE_SHOPPING_LIST_TABLE);
		// db.execSQL(CREATE_INGREDIENTS_TABLE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// does stuff when database needs to be upgraded
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// does stuff when database gets downgraded
	}
}