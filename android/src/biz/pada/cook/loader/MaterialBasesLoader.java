package biz.pada.cook.loader;
import biz.pada.cook.Start;
import biz.pada.cook.db.CookDBHelper;
import java.io.*;
import java.net.*;
import android.os.AsyncTask;
import android.content.ContentValues;
import android.database.sqlite.*;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.stream.JsonReader;
public class MaterialBasesLoader extends AsyncTask<String, Void, Boolean>{
	private Start startActivity;
	public MaterialBasesLoader(Start startActivity){
		this.startActivity=startActivity;
	}
	@Override
	protected void onPostExecute(Boolean result){
		this.startActivity.loadResourcesCallback(result);
	}
	@Override
	protected Boolean doInBackground(String... urls){
		InputStream in=null;
		try{
			URL url=new URL(urls[0]);
			HttpURLConnection conn=(HttpURLConnection)url.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(10000);
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			// Starts the query
			conn.connect();
			in=conn.getInputStream();
			// Convert the InputStream into a JSON Array and Save to local SQLite database
			return this.saveToDB(this.readAsJsonArray(in));
		}catch(IOException e){
			e.printStackTrace();
			return false;
		}finally{
			try{
				if(in != null){
					in.close();
				}
			}catch(IOException e){
				e.printStackTrace();
				return false;
			}
		}
	}
	private JsonArray readAsJsonArray(InputStream stream) throws IOException, UnsupportedEncodingException{
		Reader reader=null;
		reader=new InputStreamReader(stream, "utf-8");
		int len=2048, size;
		char[] buffer=new char[len];
		StringBuilder result=new StringBuilder();
		while((size=reader.read(buffer, 0, len))>-1){
			result.append(new String(buffer, 0, size));
		}
		// Read as JSON Array
		JsonReader jsonReader=new JsonReader(new java.io.StringReader(result.toString()));
		jsonReader.setLenient(true);
		return (new JsonParser()).parse(jsonReader).getAsJsonArray();
	}
	private boolean saveToDB(JsonArray bases){
		SQLiteDatabase db=(new CookDBHelper(this.startActivity)).getWritableDatabase();
		// Create a new map of values, where column names are the keys
		JsonObject base;
		JsonArray materials;
		StringBuilder materialsStr;
		ContentValues values;
		db.beginTransaction();
		for(int i=0;i<bases.size();i++){
			base=bases.get(i).getAsJsonObject();
			values=new ContentValues();
			values.put("id", base.get("id").getAsLong());
			values.put("lat", base.get("la").getAsDouble());
			values.put("lng", base.get("ln").getAsDouble());
			materials=base.get("ms").getAsJsonArray();
			materialsStr=new StringBuilder();
			for(int j=0;j<materials.size();j++){
				if(j>0){
					materialsStr.append(",");
				}
				materialsStr.append(materials.get(j).getAsLong());
			}
			values.put("materials", materialsStr.toString());
			// Insert the new row, returning the primary key value of the new row
			db.insert("material_base", null, values);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		return true;
	}
}