package biz.pada.cook.loader;
import biz.pada.cook.Start;
import java.io.*;
import java.net.*;
import android.os.AsyncTask;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.stream.JsonReader;
public class ResourcesVersionLoader extends AsyncTask<String, Void, Integer>{
	private Start startActivity;
	public ResourcesVersionLoader(Start startActivity){
		this.startActivity=startActivity;
	}
	@Override
	protected void onPostExecute(Integer version){ // Version number from server
		this.startActivity.checkResourcesVersionCallback(version);
	}
	@Override
	protected Integer doInBackground(String... urls){
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
			return this.getVersionNumber(in);
		}catch(IOException e){
			e.printStackTrace();
			return -1;
		}finally{
			try{
				if(in != null){
					in.close();
				}
			}catch(IOException e){
				e.printStackTrace();
				return -1;
			}
		}
	}
	private int getVersionNumber(InputStream stream) throws IOException, UnsupportedEncodingException{
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
		JsonObject manifest=(new JsonParser()).parse(jsonReader).getAsJsonObject();
		// Get version number
		return manifest.get("version").getAsInt();
	}
}