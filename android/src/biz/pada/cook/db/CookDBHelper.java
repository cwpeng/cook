package biz.pada.cook.db;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class CookDBHelper extends SQLiteOpenHelper{
	// After changing the database schema, database version must be incremented.
    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_NAME="cook.db";
	public CookDBHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	public void onCreate(SQLiteDatabase db){
		db.execSQL("CREATE TABLE material_base (id INTEGER PRIMARY KEY,lat REAL,lng REAL,materials TEXT)");
		db.execSQL("CREATE UNIQUE INDEX geolocation ON material_base (lat,lng)");
	}
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		// This database is only a cache for online data, so its upgrade policy is
		// to simply to discard the data and start over
		db.execSQL("DROP TABLE IF EXISTS material_base");
		onCreate(db);
	}
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
		onUpgrade(db, oldVersion, newVersion);
	}
}