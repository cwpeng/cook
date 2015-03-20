package crawler;
import java.io.*;
public class GeometryExtractor{
	public static void main(String[] args) throws Exception{
		if(args.length==1){
			extractGeometry(args[0]);
		}
	}
	private static void extractGeometry(String srcFilePath) throws Exception{
		File srcFile=new File(srcFilePath);
		BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(srcFile), "utf-8"));
		String line;
		String[] data;
		StringBuilder result=new StringBuilder();
		int count=0;
		while((line=reader.readLine())!=null){
			data=line.split("\t");
			if(count>0){
				result.append("\r\n");
			}
			if(data[data.length-3].indexOf(".")<0){
				data[data.length-3]=data[data.length-3].substring(0, 2)+"."+data[data.length-3].substring(2);
			}
			if(data[data.length-2].indexOf(".")<0){
				data[data.length-2]=data[data.length-2].substring(0, 3)+"."+data[data.length-2].substring(3);
			}
			result.append(data[data.length-3]+","+data[data.length-2]);
			count++;
		}
		reader.close();
		File desFile=new File(srcFile.getParentFile(), "latlng");
		PrintWriter out=new PrintWriter(desFile, "utf-8");
		out.print(result.toString());
		out.flush();
		out.close();
	}
}