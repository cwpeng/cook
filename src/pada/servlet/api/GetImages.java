package pada.servlet.api;
import pada.data.Image;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
public class GetImages extends HttpServlet{
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		request.setCharacterEncoding("utf-8");
		Image[] imgs=Image.getImages();
		StringBuilder json=new StringBuilder("[");
		for(int i=0;i<imgs.length;i++){
			if(i>0){
				json.append(",");
			}
			json.append("{\"name\":\""+imgs[i].name+"\",\"url\":\""+imgs[i].publicURL+"\"}");
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