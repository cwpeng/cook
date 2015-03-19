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
public class FamilyMart{
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
		{"台東縣", "121.146825", "22.754697"},
		{"澎湖縣", "119.566815", "23.566252"},
		{"金門縣", "118.375352", "24.457632"}
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
		Pattern globalPtn = Pattern.compile("[\\{][^.]+?[\\}]");
		Matcher globalMtr;
		String townJSON;
		int index, count=0;
		StringBuilder result=new StringBuilder();
		for(int i=0;i<cities.length;i++){
			System.out.println(i);
			globalMtr = globalPtn.matcher(getTownsByCity(cities[i][0]));
			while(globalMtr.find()){
				townJSON = globalMtr.group();
				index=townJSON.indexOf("\"town\": \"");
				if(index<0){
					continue;
				}
				if(count>0){
					result.append("\r\n");
				}
				result.append(cities[i][0]+"\t"+townJSON.substring(index+9, townJSON.indexOf("\"", index+9)));
				count++;
			}
		}
		PrintWriter writer=new PrintWriter("crawler\\result\\family\\towns", "utf-8");
		writer.print(result);
		writer.flush();
		writer.close();
	}
	private static String getTownsByCity(String city) throws Exception{
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet get = new HttpGet("http://api.map.com.tw/net/familyShop.aspx?searchType=ShowTownList&type=&city="+URLEncoder.encode(city, "utf-8")+"&fun=storeTownList");
		get.setHeader("Accept", "*/*");
		get.setHeader("Accept-Encoding", "gzip, deflate, sdch");
		get.setHeader("Accept-Language", "en-US,en;q=0.8,zh-TW;q=0.6,zh;q=0.4,de;q=0.2,es;q=0.2,id;q=0.2,ja;q=0.2,pt;q=0.2,zh-CN;q=0.2,fr;q=0.2");
		get.setHeader("Connection", "keep-alive");
		get.setHeader("Host", "api.map.com.tw");
		get.setHeader("Origin", "http://emap.pcsc.com.tw");
		get.setHeader("Referer", "http://www.family.com.tw/marketing/inquiry.aspx");
		get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36");
		CloseableHttpResponse response = httpclient.execute(get);
		try{
			System.out.println(response.getStatusLine().toString());
			return EntityUtils.toString(response.getEntity(), "utf-8");
		}finally{
			response.close();
		}
	}
	private static void getStores() throws Exception{
		BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream("crawler\\result\\family\\towns"), "utf-8"));
		String town;
		String[] townData;
		String[] storeJSONs;
		int index, count=0;
		StringBuilder result=new StringBuilder();
		while((town=reader.readLine())!=null){
			townData=town.split("\t");
			storeJSONs = getStoresByTown(townData[0], townData[1]).split("},");
			for(int i=0;i<storeJSONs.length;i++){
				index=storeJSONs[i].indexOf("\"NAME\": \"");
				if(index<0){
					continue;
				}
				if(count>0){
					result.append("\r\n");
				}
				result.append(townData[0]+"\t"+townData[1]+"\t"+storeJSONs[i].substring(index+9, storeJSONs[i].indexOf("\"", index+9)));
				// latitude
				index=storeJSONs[i].indexOf("\"py\": ");
				result.append("\t"+storeJSONs[i].substring(index+6, storeJSONs[i].indexOf(",", index+6)));
				// longitude
				index=storeJSONs[i].indexOf("\"px\": ");
				result.append("\t"+storeJSONs[i].substring(index+6, storeJSONs[i].indexOf(",", index+6)));
				// address
				index=storeJSONs[i].indexOf("\"addr\": \"");
				result.append("\t"+storeJSONs[i].substring(index+9, storeJSONs[i].indexOf("\"", index+9)));
				count++;
				System.out.println(count);
			}
		}
		reader.close();
		PrintWriter writer=new PrintWriter("crawler\\result\\family\\stores", "utf-8");
		writer.print(result);
		writer.flush();
		writer.close();
	}
	private static String getStoresByTown(String city, String town) throws Exception{
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet get = new HttpGet("http://api.map.com.tw/net/familyShop.aspx?searchType=ShopList&type=&city="+URLEncoder.encode(city, "utf-8")+"&area="+URLEncoder.encode(town, "utf-8")+"&road=&fun=showStoreList");
		get.setHeader("Accept", "*/*");
		get.setHeader("Accept-Encoding", "gzip, deflate, sdch");
		get.setHeader("Accept-Language", "en-US,en;q=0.8,zh-TW;q=0.6,zh;q=0.4,de;q=0.2,es;q=0.2,id;q=0.2,ja;q=0.2,pt;q=0.2,zh-CN;q=0.2,fr;q=0.2");
		get.setHeader("Connection", "keep-alive");
		get.setHeader("Host", "api.map.com.tw");
		get.setHeader("Origin", "http://emap.pcsc.com.tw");
		get.setHeader("Referer", "http://www.family.com.tw/marketing/inquiry.aspx");
		get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36");
		CloseableHttpResponse response = httpclient.execute(get);
		try{
			System.out.println(response.getStatusLine().toString());
			return EntityUtils.toString(response.getEntity(), "utf-8");
		}finally{
			response.close();
		}
	}
}