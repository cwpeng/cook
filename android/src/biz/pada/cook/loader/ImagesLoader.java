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
public class ImagesLoader extends AsyncTask<String, Integer, Boolean>{
	private Start startActivity;
	public ImagesLoader(Start startActivity){
		this.startActivity=startActivity;
	}
	@Override
	protected void onPostExecute(Boolean result){
		this.startActivity.loadImagesFinished(result);
	}
	@Override
	protected void onProgressUpdate(Integer... values /* completed percentage */){
		this.startActivity.loadImagesProgress(values[0]);
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
			// Convert the InputStream into a JSON Array and Download images
			JsonArray list=this.readAsJsonArray(in);
			int downloaded=0;
			int total=list.size();
			this.publishProgress(0);
			for(int i=0;i<list.size();i++){
				if(this.downloadImage(list.get(i).getAsJsonObject())){
					downloaded++;
					this.publishProgress((int)(100*(downloaded/(double)total)));
				}else{
					return false;
				}
			}
			return true;
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
	private boolean downloadImage(JsonObject image){
		InputStream in=null;
		FileOutputStream out=null;
		try{
			URL url=new URL(image.get("url").getAsString());
			HttpURLConnection conn=(HttpURLConnection)url.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(10000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// Start download
			conn.connect();
			in=conn.getInputStream();
			out=new FileOutputStream(new File(this.startActivity.getFilesDir(), "images"+File.separator+image.get("name").getAsString()));
			byte[] buffer=new byte[4096];
			int size;
			while((size=in.read(buffer, 0, buffer.length))!=-1){
				out.write(buffer, 0, size);
			}
			out.flush();
			return true;
		}catch(IOException e){
			e.printStackTrace();
			return false;
		}finally{
			try{
				if(in != null){
					in.close();
				}
				if(out != null){
					out.close();
				}
			}catch(IOException e){
				e.printStackTrace();
				return false;
			}
		}
	}
}