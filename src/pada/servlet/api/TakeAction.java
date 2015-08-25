package pada.servlet.api;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
public class TakeAction extends HttpServlet{
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		request.setCharacterEncoding("utf-8");
		if(request.getParameter("token")==null||request.getParameter("action")==null||request.getParameter("args")==null){
			return;
		}else{
			
			// Make Output
			/*
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/json");
			PrintWriter out=response.getWriter();
			out.print("{\"id\":"+player.id+",\"token\":\""+player.token+"\",\"name\":"+(player.name==null?"null":"\""+player.name+"\"")+"}");
			out.flush(); out.close();
			*/
		}
	}
}