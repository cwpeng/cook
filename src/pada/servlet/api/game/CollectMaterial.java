package pada.servlet.api.game;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
public class CollectMaterial extends HttpServlet{
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		request.setCharacterEncoding("utf-8");
		if(request.getParameter("base_id")==null||request.getParameter("material_id")==null){
			return;
		}else{
			pada.data.Game.collectMaterial(
				(pada.data.Player)request.getAttribute("player"),
				Long.parseLong(request.getParameter("base_id")),
				Long.parseLong(request.getParameter("material_id"))
			);
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