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
public class HiLife{
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
		{"金門縣", "118.375352", "24.457632"}
	};
	public static void main(String[] args) throws Exception{
		getStores();
	}
	private static void getStores() throws Exception{
		// 直接使用 7-11 的地區資料
		BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream("crawler\\result\\seven\\towns"), "utf-8"));
		String town;
		String[] townData;
		String[] storeJSONs;
		int index, count=0;
		StringBuilder result=new StringBuilder();
		while((town=reader.readLine())!=null){
			townData=town.split("\t");
			storeJSONs=getStoresByTown(townData[0], townData[1]).split("},");
			for(int i=0;i<storeJSONs.length;i++){
				index=storeJSONs[i].indexOf("\"名稱\": \"");
				if(index<0){
					continue;
				}
				if(count>0){
					result.append("\r\n");
				}
				result.append(townData[0]+"\t"+townData[1]+"\t"+storeJSONs[i].substring(index+7, storeJSONs[i].indexOf("\"", index+7)));
				// latitude
				index=storeJSONs[i].indexOf("\"緯度\": ");
				result.append("\t"+storeJSONs[i].substring(index+6, storeJSONs[i].indexOf(",", index+6)));
				// longitude
				index=storeJSONs[i].indexOf("\"經度\": ");
				result.append("\t"+storeJSONs[i].substring(index+6, storeJSONs[i].indexOf(",", index+6)));
				// address
				index=storeJSONs[i].indexOf("地址：");
				result.append("\t"+storeJSONs[i].substring(index+3, storeJSONs[i].indexOf("<br/>", index+3)));
				count++;
				System.out.println(count);
			}
		}
		reader.close();
		PrintWriter writer=new PrintWriter("crawler\\result\\hilife\\stores", "utf-8");
		writer.print(result);
		writer.flush();
		writer.close();
	}
	private static String getStoresByTown(String city, String town) throws Exception{
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost post = new HttpPost("http://www.hilife.com.tw/getGoogleSpot.ashx");
		post.setHeader("Accept", "*/*");
		post.setHeader("Accept-Encoding", "gzip, deflate");
		post.setHeader("Accept-Language", "en-US,en;q=0.8,zh-TW;q=0.6,zh;q=0.4,de;q=0.2,es;q=0.2,id;q=0.2,ja;q=0.2,pt;q=0.2,zh-CN;q=0.2,fr;q=0.2");
		post.setHeader("Connection", "keep-alive");
		post.setHeader("Host", "www.hilife.com.tw");
		post.setHeader("Origin", "http://www.hilife.com.tw");
		post.setHeader("Referer", "http://www.hilife.com.tw/storeInquiry_map.aspx");
		post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36");
		post.setHeader("X-Requested-With", "XMLHttpRequest");

		java.util.List <NameValuePair> nvps = new java.util.ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("city_name", city));
		nvps.add(new BasicNameValuePair("town_name", town));
		nvps.add(new BasicNameValuePair("shop_id", ""));
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