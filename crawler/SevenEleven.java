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
public class SevenEleven{
	private static String[][] cities=new String[][]{
		{"台北市", "121.517166", "25.048055", "01"},
		{"基隆市", "121.768104", "25.151627", "02"},
		{"新北市", "121.459043", "25.009605", "03"},
		{"桃園市", "121.301782", "24.993918", "04"},
		{"新竹市", "120.973664", "24.805210", "05"},
		{"新竹縣", "121.004279", "24.839621", "06"},
		{"苗栗縣", "120.819108", "24.561589", "07"},
		{"台中市", "120.680172", "24.144180", "08"},
		{"台中縣", "120.718156", "24.241698", "09"},
		{"彰化縣", "120.542278", "24.081021", "10"},
		{"南投縣", "120.687516", "23.911253", "11"},
		{"雲林縣", "120.527389", "23.696933", "12"},
		{"嘉義市", "120.453670", "23.479000", "13"},
		{"嘉義縣", "120.332480", "23.458862", "14"},
		{"台南市", "120.224423", "22.980075", "15"},
		{"台南縣", "120.316702", "23.310117", "16"},
		{"高雄市", "120.282797", "22.622129", "17"},
		{"高雄縣", "120.357287", "22.627150", "18"},
		{"屏東縣", "120.490142", "22.674804", "19"},
		{"宜蘭縣", "121.753479", "24.752216", "20"},
		{"花蓮縣", "121.607039", "23.981993", "21"},
		{"台東縣", "121.146825", "22.754697", "22"},
		{"澎湖縣", "119.566815", "23.566252", "23"},
		{"金門縣", "118.375352", "24.457632", "25"},
		{"連江縣", "119.925572", "26.156777", "24"}
	};
	public static void main(String[] args) throws Exception{
		if(args.length==1){
			if(args[0].equals("towns")){
				getTowns();
			}else if(args[0].equals("stores")){
				getStores();
			}
		}else{
			getTowns();
			getStores();
		}
	}
	private static void getTowns() throws Exception{
		Pattern globalPtn = Pattern.compile("<GeoPosition>.+?</GeoPosition>");
		Matcher globalMtr;
		String townXML;
		int index, count=0;
		StringBuilder result=new StringBuilder();
		for(int i=0;i<cities.length;i++){
			System.out.println(i);
			globalMtr = globalPtn.matcher(getTownsByCity(cities[i][3]));
			while(globalMtr.find()){
				townXML = globalMtr.group();
				index=townXML.indexOf("<TownName>");
				if(index<0){
					continue;
				}
				if(count>0){
					result.append("\r\n");
				}
				result.append(cities[i][0]+"\t"+townXML.substring(index+10, townXML.indexOf("</TownName>", index+10)));
				// latitude
				index=townXML.indexOf("<Y>");
				result.append("\t"+townXML.substring(index+3, townXML.indexOf("</Y>", index+3)));
				// longitude
				index=townXML.indexOf("<X>");
				result.append("\t"+townXML.substring(index+3, townXML.indexOf("</X>", index+3)));
				count++;
			}
		}
		PrintWriter writer=new PrintWriter("crawler\\result\\seven\\towns", "utf-8");
		writer.print(result);
		writer.flush();
		writer.close();
	}
	private static String getTownsByCity(String id) throws Exception{
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost post = new HttpPost("http://emap.pcsc.com.tw/EMapSDK.aspx");
		post.setHeader("Accept", "*/*");
		post.setHeader("Accept-Encoding", "gzip, deflate");
		post.setHeader("Accept-Language", "en-US,en;q=0.8,zh-TW;q=0.6,zh;q=0.4,de;q=0.2,es;q=0.2,id;q=0.2,ja;q=0.2,pt;q=0.2,zh-CN;q=0.2,fr;q=0.2");
		post.setHeader("Connection", "keep-alive");
		post.setHeader("Host", "emap.pcsc.com.tw");
		post.setHeader("Origin", "http://emap.pcsc.com.tw");
		post.setHeader("Referer", "http://emap.pcsc.com.tw/emap.aspx");
		post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36");
		post.setHeader("X-Requested-With", "XMLHttpRequest");

		java.util.List <NameValuePair> nvps = new java.util.ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("commandid", "GetTown"));
		nvps.add(new BasicNameValuePair("cityid", id));
		nvps.add(new BasicNameValuePair("isDining", "False"));
		nvps.add(new BasicNameValuePair("isParking", "False"));
		nvps.add(new BasicNameValuePair("isLavatory", "False"));
		nvps.add(new BasicNameValuePair("isATM", "False"));
		nvps.add(new BasicNameValuePair("is7WiFi", "False"));
		nvps.add(new BasicNameValuePair("isIce", "False"));
		nvps.add(new BasicNameValuePair("isHotDog", "False"));
		nvps.add(new BasicNameValuePair("isHealthStations", "False"));
		nvps.add(new BasicNameValuePair("isIceCream", "False"));
		nvps.add(new BasicNameValuePair("isOpenStore", "False"));
		nvps.add(new BasicNameValuePair("isFruit", "False"));
		nvps.add(new BasicNameValuePair("isCityCafe", "False"));
		post.setEntity(new org.apache.http.client.entity.UrlEncodedFormEntity(nvps, "UTF-8"));
		CloseableHttpResponse response = httpclient.execute(post);
		try{
			System.out.println(response.getStatusLine().toString());
			return EntityUtils.toString(response.getEntity(), "utf-8");
		}finally{
			response.close();
		}
	}
	private static void getStores() throws Exception{
		BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream("crawler\\result\\seven\\towns"), "utf-8"));
		String town;
		String[] townData;
		Pattern globalPtn = Pattern.compile("<GeoPosition>.+?</GeoPosition>");
		Matcher globalMtr;
		String storeXML, storeLatitude, storeLongitude;
		int index, count=0;
		StringBuilder result=new StringBuilder();
		while((town=reader.readLine())!=null){
			townData=town.split("\t");
			globalMtr = globalPtn.matcher(getStoresByTown(townData[0], townData[1]));
			while(globalMtr.find()){
				storeXML = globalMtr.group();
				index=storeXML.indexOf("<POIName>");
				if(index<0){
					continue;
				}
				if(count>0){
					result.append("\r\n");
				}
				result.append(townData[0]+"\t"+townData[1]+"\t"+storeXML.substring(index+9, storeXML.indexOf("</POIName>", index+9)));
				// latitude
				index=storeXML.indexOf("<Y>");
				storeLatitude=storeXML.substring(index+3, storeXML.indexOf("</Y>", index+3));
				result.append("\t"+(storeLatitude.indexOf(".")>-1?storeLatitude.substring(0, storeLatitude.indexOf(".")):storeLatitude));
				// longitude
				index=storeXML.indexOf("<X>");
				storeLongitude=storeXML.substring(index+3, storeXML.indexOf("</X>", index+3));
				result.append("\t"+(storeLongitude.indexOf(".")>-1?storeLongitude.substring(0, storeLongitude.indexOf(".")):storeLongitude));
				// address
				index=storeXML.indexOf("<Address>");
				result.append("\t"+storeXML.substring(index+9, storeXML.indexOf("</Address>", index+9)));
				count++;
				System.out.println(count);
			}
		}
		reader.close();
		PrintWriter writer=new PrintWriter("crawler\\result\\seven\\stores", "utf-8");
		writer.print(result);
		writer.flush();
		writer.close();
	}
	private static String getStoresByTown(String city, String town) throws Exception{
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost post = new HttpPost("http://emap.pcsc.com.tw/EMapSDK.aspx");
		post.setHeader("Accept", "*/*");
		post.setHeader("Accept-Encoding", "gzip, deflate");
		post.setHeader("Accept-Language", "en-US,en;q=0.8,zh-TW;q=0.6,zh;q=0.4,de;q=0.2,es;q=0.2,id;q=0.2,ja;q=0.2,pt;q=0.2,zh-CN;q=0.2,fr;q=0.2");
		post.setHeader("Connection", "keep-alive");
		post.setHeader("Host", "emap.pcsc.com.tw");
		post.setHeader("Origin", "http://emap.pcsc.com.tw");
		post.setHeader("Referer", "http://emap.pcsc.com.tw/emap.aspx");
		post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36");
		post.setHeader("X-Requested-With", "XMLHttpRequest");

		java.util.List <NameValuePair> nvps = new java.util.ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("commandid", "SearchStore"));
		nvps.add(new BasicNameValuePair("city", city));
		nvps.add(new BasicNameValuePair("town", town));
		nvps.add(new BasicNameValuePair("roadname", ""));
		nvps.add(new BasicNameValuePair("ID", ""));
		nvps.add(new BasicNameValuePair("StoreName", ""));
		nvps.add(new BasicNameValuePair("SpecialStore_Kind", ""));
		nvps.add(new BasicNameValuePair("isDining", "False"));
		nvps.add(new BasicNameValuePair("isParking", "False"));
		nvps.add(new BasicNameValuePair("isLavatory", "False"));
		nvps.add(new BasicNameValuePair("isATM", "False"));
		nvps.add(new BasicNameValuePair("is7WiFi", "False"));
		nvps.add(new BasicNameValuePair("isIce", "False"));
		nvps.add(new BasicNameValuePair("isHotDog", "False"));
		nvps.add(new BasicNameValuePair("isHealthStations", "False"));
		nvps.add(new BasicNameValuePair("isIceCream", "False"));
		nvps.add(new BasicNameValuePair("isOpenStore", "False"));
		nvps.add(new BasicNameValuePair("isFruit", "False"));
		nvps.add(new BasicNameValuePair("isCityCafe", "False"));
		nvps.add(new BasicNameValuePair("address", ""));
		post.setEntity(new org.apache.http.client.entity.UrlEncodedFormEntity(nvps, "UTF-8"));
		CloseableHttpResponse response = httpclient.execute(post);
		try{
			System.out.println(response.getStatusLine().toString());
			return EntityUtils.toString(response.getEntity(), "utf-8");
		}finally{
			response.close();
		}
	}
}