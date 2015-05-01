package biz.pada.cook.loader;
import java.io.*;
import java.net.*;
import android.app.Activity;
import android.os.AsyncTask;
import android.database.sqlite.*;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.stream.JsonReader;
public class MaterialBasesLoader extends AsyncTask<String, Void, Boolean>{
	private Activity activity;
	public MaterialBasesLoader(Activity activity){
		this.activity=activity;
	}
	@Override
	protected void onPostExecute(Boolean result){
		//this.activity.updateResourcesCallback(result);
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
		// Read as JSON Object
		JsonReader jsonReader=new JsonReader(new java.io.StringReader(result.toString()));
		jsonReader.setLenient(true);
		return (new JsonParser()).parse(jsonReader).getAsJsonArray();
	}
	private boolean saveToDB(JsonArray bases){
		return true;
	}
}