package pada.servlet.api;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
public class Signup extends HttpServlet{
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		request.setCharacterEncoding("utf-8");
		if(request.getParameter("imei")==null&&request.getParameter("password")==null&&request.getParameter("name")==null){
			return;
		}else{
			long id=pada.data.Player.signup(request.getParameter("imei"), request.getParameter("password"), request.getParameter("name"));
			// Make Output
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/json");
			PrintWriter out=response.getWriter();
			out.print("{\"id\":"+id+"}");
			out.flush(); out.close();
		}
	}
}