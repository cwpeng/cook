package crawler;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.*;
import java.net.*;
import org.apache.http.message.*;
import org.apache.http.*;
import org.apache.http.util.*;
import org.apache.http.client.utils.*;
import org.apache.http.client.*;
import org.apache.http.impl.client.*;
import org.apache.http.client.methods.*;
public class OKMart{
	private static String[][] cities=new String[][]{
		{"台北市", "121.517166", "25.048055"},
		{"基隆市", "121.768104", "25.151627"},
		{"新北市", "121.459043", "25.009605"},
		{"桃園市", "121.301782", "24.993918"},
		{"新竹市", "120.973664", "24.805210"},
		{"新竹縣", "121.004279", "24.839621"},
		{"苗栗縣", "120.819108", "24.561589"},
		{"台中市", "120.680172", "24.144180"},
		{"彰化縣", "120.542278", "24.081021"},
		{"南投縣", "120.687516", "23.911253"},
		{"雲林縣", "120.527389", "23.696933"},
		{"嘉義市", "120.453670", "23.479000"},
		{"嘉義縣", "120.332480", "23.458862"},
		{"台南市", "120.224423", "22.980075"},
		{"高雄市", "120.282797", "22.622129"},
		{"屏東縣", "120.490142", "22.674804"},
		{"宜蘭縣", "121.753479", "24.752216"},
		{"花蓮縣", "121.607039", "23.981993"},
		{"台東縣", "121.146825", "22.754697"}
	};
	public static void main(String[] args) throws Exception{
		if(args.length==1){
			if(args[0].equals("latlng")){
				getStores();
			}
		}else{
			getStoresWithoutLatlng();
		}
	}
	private static void getStores() throws Exception{
		BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream("crawler\\result\\okmart\\stores_without_latlng"), "utf-8"));
		String store, storeLatlng;
		String[] storeData;
		int index, count=0;
		StringBuilder result=new StringBuilder();
		while((store=reader.readLine())!=null){
			storeData=store.split("\t");
			storeLatlng = getStoreLatlng(storeData[2]);
			if(count>0){
				result.append("\r\n");
			}
			result.append(storeData[0]+"\t"+storeData[1]+"\t"+storeLatlng+"\t"+storeData[2]);
			count++;
			if(count%5==0){
				Thread.sleep(1000);
			}
			System.out.println(count);
		}
		reader.close();
		PrintWriter writer=new PrintWriter("crawler\\result\\okmart\\stores", "utf-8");
		writer.print(result);
		writer.flush();
		writer.close();
	}
	private static String getStoreLatlng(String address) throws Exception{
		String latlngXML=sendHttpRequest("https://maps.googleapis.com/maps/api/geocode/xml",
			"address="+URLEncoder.encode(address, "utf-8")+"&region=tw&key=AIzaSyCM3BBLiGGT3xaz6vJmIqKlSzu52Y36mlM",
			false);
		int index=latlngXML.indexOf("<lat>");
		if(index<-1){
			return "null\tnull";
		}
		String lat=latlngXML.substring(index+5, latlngXML.indexOf("</lat>", index+5));
		index=latlngXML.indexOf("<lng>");
		return lat+"\t"+latlngXML.substring(index+5, latlngXML.indexOf("</lng>", index+5));
	}
	private static void getStoresWithoutLatlng() throws Exception{
		Pattern globalPtn = Pattern.compile("<li>.+?</li>");
		Matcher globalMtr;
		String storeHTML;
		int index, count=0;
		StringBuilder result=new StringBuilder();
		for(int i=0;i<cities.length;i++){
			globalMtr = globalPtn.matcher(getStoresByCity(cities[i][0]));
			while(globalMtr.find()){
				storeHTML=globalMtr.group();
				index=storeHTML.indexOf("<h2>"); // Name
				if(index<0){
					continue;
				}
				if(count>0){
					result.append("\r\n");
				}
				result.append(cities[i][0]+"\t"+storeHTML.substring(index+4, storeHTML.indexOf(" ", index+4)));
				// Address
				index=storeHTML.indexOf("<span>");
				result.append("\t"+storeHTML.substring(index+6, storeHTML.indexOf("</span>", index+6)));
				count++;
			}
		}
		PrintWriter writer=new PrintWriter("crawler\\result\\okmart\\stores_without_latlng", "utf-8");
		writer.print(result);
		writer.flush();
		writer.close();
	}
	private static String getStoresByCity(String city) throws Exception{
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet get = new HttpGet("http://www.okmart.com.tw/convenient_shopSearch_Result.asp?city="+URLEncoder.encode(city, "utf-8")+"&zipcode=&key=&service=undefined&_=1426769553747");
		get.setHeader("Accept", "*/*");
		get.setHeader("Accept-Encoding", "gzip, deflate, sdch");
		get.setHeader("Accept-Language", "en-US,en;q=0.8,zh-TW;q=0.6,zh;q=0.4,de;q=0.2,es;q=0.2,id;q=0.2,ja;q=0.2,pt;q=0.2,zh-CN;q=0.2,fr;q=0.2");
		get.setHeader("Connection", "keep-alive");
		get.setHeader("Host", "www.okmart.com.tw");
		get.setHeader("Origin", "http://www.okmart.com.tw");
		get.setHeader("Referer", "http://www.okmart.com.tw/convenient_shopSearch.asp");
		get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36");
		get.setHeader("X-Requested-With", "XMLHttpRequest");
		CloseableHttpResponse response = httpclient.execute(get);
		try{
			System.out.println(response.getStatusLine().toString());
			return EntityUtils.toString(response.getEntity(), "utf-8");
		}finally{
			response.close();
		}
	}
	private static String sendHttpRequest(String source, String args, boolean isPost){
		URL url = null;
		try{
			if(isPost){
				url = new URL(source);
			}else{
				url = new URL(source + "?" + args);
			}
		}catch(MalformedURLException e){
			e.printStackTrace();
			return null;
		}

		HttpURLConnection con = null;
		OutputStreamWriter writer = null;
		InputStream in = null;
		ByteArrayOutputStream out = null;
		try{
			con = (HttpURLConnection)url.openConnection();
			if(isPost){
				con.setDoOutput(true);
				con.setRequestMethod("POST");
				writer = new OutputStreamWriter(con.getOutputStream());
				writer.write(args);
				writer.close();
			}
			if(con.getResponseCode() == HttpURLConnection.HTTP_OK){
				in = con.getInputStream();
				out = new ByteArrayOutputStream();
				byte[] buffer = new byte[512];
				int len;
				while((len = in.read(buffer)) > -1){
					out.write(buffer, 0, len);
				}
				String encoding = con.getContentEncoding();
				if(encoding == null){
					encoding = "utf-8";
				}
				return out.toString(encoding);
			}else{
				return null;
			}
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}finally{
			try{
				if(writer != null){
					writer.close();
				}
				if(in != null){
					in.close();
				}
				if(out != null){
					out.close();
				}
			}catch(IOException e){
				e.printStackTrace();
				return null;
			}
		}
	}
}