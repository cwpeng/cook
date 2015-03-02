package pada.servlet.data;
import pada.data.Cookbook;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
public class GetCookbooks extends HttpServlet{
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		request.setCharacterEncoding("utf-8");
		Cookbook[] cookbooks=Cookbook.getCookbooks();
		StringBuilder json=new StringBuilder("[");
		for(int i=0;i<cookbooks.length;i++){
			if(i>0){
				json.append(",");
			}
			json.append("{\"id\":"+cookbooks[i].id+",\"name\":\""+cookbooks[i].name+"\",\"description\":\""+cookbooks[i].description+"\",\"materials\":[");
			for(int j=0;j<cookbooks[i].materials.length;j++){
				if(j>0){
					json.append(",");
				}
				json.append("{\"id\":"+cookbooks[i].materials[j][0]+",\"number\":"+cookbooks[i].materials[j][1]+"}");
			}
			json.append("]}");
		}
		json.append("]");
		// Make Output
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json");
		PrintWriter out=response.getWriter();
		out.print(json.toString());
		out.flush(); out.close();
	}
}