package biz.pada.cook.core;
import java.util.HashMap;
import android.content.Context;
import biz.pada.cook.db.CookDBHelper;
import android.database.Cursor;
import android.database.sqlite.*;
public class Material{
	private static HashMap<Long, Material> materials=null;
	public static Material getMaterial(Context context, long id){
		if(materials==null){
			Material.getMaterials(context);
		}
		return Material.materials.get(id);
	}
		private static void getMaterials(Context context){
			SQLiteDatabase db=(new CookDBHelper(context)).getReadableDatabase();
			Cursor cursor=db.query("material", new String[]{"id", "name", "description"},
				null, null, null, null, null);
			if(cursor.moveToFirst()){
				Material.materials=new HashMap<Long, Material>();
				int idIndex=cursor.getColumnIndex("id");
				int nameIndex=cursor.getColumnIndex("name");
				int descriptionIndex=cursor.getColumnIndex("description");
				long id;
				do{
					id=cursor.getLong(idIndex);
					Material.materials.put(id, new Material(id, cursor.getString(nameIndex), cursor.getString(descriptionIndex)));
				}while(cursor.moveToNext());
				cursor.close();
			}
		}
	// instance members
	public long id;
	public String name;
	public String description;
	public Material(long id, String name, String description){
		this.id=id;
		this.name=name;
		this.description=description;
	}
}