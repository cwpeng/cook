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
public class Login extends AsyncTask<String, Void, Player>{
	private Start startActivity;
	public Login(Start startActivity){
		this.startActivity=startActivity;
	}
	@Override
	protected void onPostExecute(Player player){ // Player data from server
		this.startActivity.loginCallback(player);
	}
	@Override
	protected Player doInBackground(String... args){
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
			writer.write("imei="+args[1]+"&password="+args[2]);
			writer.close();
			// Starts the query
			conn.connect();
			in=conn.getInputStream();
			// Convert the InputStream into a JSON Array and Save to local SQLite database
			return this.getPlayer(in);
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
	private Player getPlayer(InputStream stream) throws IOException, UnsupportedEncodingException{
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
		return new Player(jsonPlayer.get("id").getAsLong(), jsonPlayer.get("token").getAsString(),
			jsonPlayer.get("name").isJsonNull()?null:jsonPlayer.get("name").getAsString());
	}
}