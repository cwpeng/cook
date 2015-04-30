package pada.servlet.data;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
public class GetMaterialBases extends HttpServlet{
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		request.setCharacterEncoding("utf-8");
		// Make Output
		response.setCharacterEncoding("utf-8");
		PrintWriter out=response.getWriter();
		out.print(pada.data.MaterialBase.getMaterialBasesJson());
		out.flush(); out.close();
	}
}