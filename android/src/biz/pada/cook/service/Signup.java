package biz.pada.cook.service;
import biz.pada.cook.Start;
import biz.pada.cook.core.Player;
import java.io.*;
import java.net.*;
import android.os.AsyncTask;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.stream.JsonReader;
public class Signup extends AsyncTask<String, Void, Long>{
	private Start startActivity;
	public Signup(Start startActivity){
		this.startActivity=startActivity;
	}
	@Override
	protected void onPostExecute(Long id){ // Player id from server
		this.startActivity.signupCallback(id);
	}
	@Override
	protected Long doInBackground(String... args){
		OutputStreamWriter writer=null;
		InputStream in=null;
		try{
			URL url=new URL(args[0]);
			HttpURLConnection conn=(HttpURLConnection)url.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(10000);
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			writer=new OutputStreamWriter(conn.getOutputStream());
			writer.write("imei="+args[1]+"&password="+args[2]+"&name="+URLEncoder.encode(args[3], "utf-8"));
			writer.close();
			// Starts the query
			conn.connect();
			in=conn.getInputStream();
			// Convert the InputStream into a JSON Array and Save to local SQLite database
			return this.getPlayerId(in);
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}finally{
			try{
				if(writer!=null){
					writer.close();
				}
				if(in!=null){
					in.close();
				}
			}catch(IOException e){
				e.printStackTrace();
				return null;
			}
		}
	}
	private Long getPlayerId(InputStream stream) throws IOException, UnsupportedEncodingException{
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
		JsonObject jsonPlayer=(new JsonParser()).parse(jsonReader).getAsJsonObject();
		// Create Player Object
		return jsonPlayer.get("id").getAsLong();
	}
}