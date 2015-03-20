package pada.servlet.data;
import pada.data.GeometrySet;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
public class GetGeometrySets extends HttpServlet{
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		request.setCharacterEncoding("utf-8");
		GeometrySet[] sets=GeometrySet.getGeometrySets();
		StringBuilder json=new StringBuilder("[");
		for(int i=0;i<sets.length;i++){
			if(i>0){
				json.append(",");
			}
			json.append("{\"id\":"+sets[i].id+",\"name\":\""+sets[i].name+"\",\"description\":\""+sets[i].description+"\"}");
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