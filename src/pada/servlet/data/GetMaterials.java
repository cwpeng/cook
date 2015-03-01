package pada.servlet.data;
import pada.data.Material;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
public class GetMaterials extends HttpServlet{
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		request.setCharacterEncoding("utf-8");
		Material[] materials=Material.getMaterials();
		StringBuilder json=new StringBuilder("[");
		for(int i=0;i<materials.length;i++){
			if(i>0){
				json.append(",");
			}
			json.append("{\"id\":"+materials[i].id+",\"name\":\""+materials[i].name+"\",\"description\":\""+materials[i].description+"\",\"cookbook\":"+materials[i].cookbook+"}");
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