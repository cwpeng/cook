package pada.servlet.api;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
public class GetResourcesVersion extends HttpServlet{
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		request.setCharacterEncoding("utf-8");
		// Make Output
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json");
		PrintWriter out=response.getWriter();
		out.print("{\"version\":"+System.getProperty("cook.resources.version")+"}");
		out.flush(); out.close();
	}
}