package pada.servlet.data;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
public class GenerateMaterialBases extends HttpServlet{
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		request.setCharacterEncoding("utf-8");
		pada.data.MaterialBase[] bases=pada.data.MaterialBase.generateAll();
		/*
		int count=0;
		StringBuilder result=new StringBuilder();
		for(int i=0;i<bases.length;i++){
			if(bases[i]==null){
				count++;
				result.append("<br/>null");
			}else{
				result.append("<br/>"+bases[i].lat+","+bases[i].lng);
				for(int j=0;j<bases[i].materials.length;j++){
					result.append(","+bases[i].materials[j]);
				}
			}
		}
		// Make Output
		response.setCharacterEncoding("utf-8");
		PrintWriter out=response.getWriter();
		out.print(count+"/"+bases.length+result.toString());
		out.flush(); out.close();
		*/
	}
}