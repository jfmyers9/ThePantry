package cs169.project.thepantry;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** Helper class for creating and handling our local database. */
public class DatabaseModelHelper extends SQLiteOpenHelper {
	
	/** Constructor method */
	public DatabaseModelHelper(Context context, String name, 
						 SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		//calls db.execSQL(PREDEFINED_CREATE_TABLE_SQL)
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int newVersion, int oldVersion) {
		// does stuff when database needs to be upgraded
	}
}